package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.DeleteEmployeeException;
import util.exceptions.EmployeeInvalidEnteredCurrentPasswordException;
import util.exceptions.EmployeeInvalidLoginCredentialException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.EmployeeUsernameExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateEmployeeException;
import util.security.CryptographicHelper;

@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanLocal {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    public EmployeeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewEmployee(Employee newEmployee) throws InputDataValidationException, EmployeeUsernameExistException, UnknownPersistenceException {
        try {
            Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(newEmployee);

            if (constraintViolations.isEmpty()) {
                em.persist(newEmployee);
                em.flush();
                return newEmployee.getEmployeeId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }

        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new EmployeeUsernameExistException("Employee username or image name exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Employee> retrieveAllEmployees() {
        Query query = em.createQuery("SELECT e FROM Employee e");
        List<Employee> employees = query.getResultList();

        for (Employee employee : employees) {
            employee.getPaymentTransactions().size();
        }

        return employees;
    }

    @Override
    public Employee retrieveEmployeeById(Long employeeId) throws EmployeeNotFoundException {
        Employee employee = em.find(Employee.class, employeeId);

        if (employee != null) {
            employee.getPaymentTransactions().size();
            return employee;
        } else {
            throw new EmployeeNotFoundException("Employee ID " + employeeId + " does not exist!");
        }
    }

    @Override
    public Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException {

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.username = :inUsername");
        query.setParameter("inUsername", username);

        try {
            return (Employee) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Employee Username " + username + " does not exist!");
        }
    }

    @Override
    public Employee employeeLogin(String username, String password) throws EmployeeInvalidLoginCredentialException {
        try {
            Employee employee = retrieveEmployeeByUsername(username);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + employee.getSalt()));

            if (employee.getPassword().equals(passwordHash)) {
                employee.getPaymentTransactions().size();
                return employee;
            } else {
                throw new EmployeeInvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeInvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    @Override
    public void updateEmployeePassword(String username, String currentPassword, String newPassword) throws EmployeeInvalidEnteredCurrentPasswordException, EmployeeNotFoundException {
        Employee employee = retrieveEmployeeByUsername(username);
        String currentPasswordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(currentPassword + employee.getSalt()));

        if (employee.getPassword().equals(currentPasswordHash)) {
            employee.setPassword(newPassword);
        } else {
            throw new EmployeeInvalidEnteredCurrentPasswordException("The entered password does not match!");
        }
    }

    @Override
    public void updateEmployee(Employee employee) throws EmployeeNotFoundException, UpdateEmployeeException, InputDataValidationException {
        if (employee != null && employee.getEmployeeId() != null) {
            Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(employee);

            if (constraintViolations.isEmpty()) {
                Employee employeeToUpdate = retrieveEmployeeById(employee.getEmployeeId());
                if (employeeToUpdate.getUsername().equals(employee.getUsername())) {
                    employeeToUpdate.setEmail(employee.getEmail());
                    employeeToUpdate.setEmployeeRole(employee.getEmployeeRole());
                    employeeToUpdate.setFirstName(employee.getFirstName());
                    employeeToUpdate.setLastName(employee.getLastName());
                    employeeToUpdate.setImagePath(employee.getImagePath());
                } else {
                    throw new UpdateEmployeeException("Username of employee record to be updated does not match the existing record!");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new EmployeeNotFoundException("Employee ID not provided for employee to be updated!");
        }
    }

    @Override
    public void deleteEmployee(Long employeeId) throws EmployeeNotFoundException, DeleteEmployeeException {
        Employee employeeToRemove = retrieveEmployeeById(employeeId);

        if (employeeToRemove.getPaymentTransactions().isEmpty()) {
            em.remove(employeeToRemove);
        } else {
            throw new DeleteEmployeeException("Employee ID " + employeeId + " is associated with existing payment transaction(s) and cannot be deleted!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Employee>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
