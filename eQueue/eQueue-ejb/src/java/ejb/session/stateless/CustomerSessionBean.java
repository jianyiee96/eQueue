package ejb.session.stateless;

import entity.Customer;
import java.util.Set;
import javax.ejb.EJB;
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
import util.exceptions.CustomerInvalidLoginCredentialException;
import util.exceptions.CustomerInvalidPasswordException;
import util.exceptions.CustomerNotUniqueException;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;
import util.security.CryptographicHelper;

@Stateless
public class CustomerSessionBean implements CustomerSessionBeanLocal {

    @EJB
    private ShoppingCartSessionBeanLocal shoppingCartSessionBean;

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CustomerSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewCustomer(Customer newCustomer) throws InputDataValidationException, CustomerNotUniqueException, UnknownPersistenceException {

        try {
            Set<ConstraintViolation<Customer>> constraintViolations = validator.validate(newCustomer);

            if (constraintViolations.isEmpty()) {
                Long shoppingCartId = shoppingCartSessionBean.createNewShoppingCart(newCustomer);
                em.persist(newCustomer);
                em.flush();
                return newCustomer.getCustomerId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new CustomerNotUniqueException("Email has already been taken.");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }

    }

    @Override
    public Customer retrieveCustomerById(Long customerId) throws CustomerNotFoundException {
        Customer customer = em.find(Customer.class, customerId);

        if (customer != null) {
            return customer;
        } else {
            throw new CustomerNotFoundException("Customer ID " + customerId + " does not exist!");
        }
    }

    @Override
    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :inEmail");
        query.setParameter("inEmail", email);

        try {
            return (Customer) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer email " + email + " does not exist!");
        }
    }

    @Override
    public Customer customerLogin(String email, String password) throws CustomerInvalidLoginCredentialException {
        try {
            Customer customer = retrieveCustomerByEmail(email);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + customer.getSalt()));

            if (customer.getPassword().equals(passwordHash)) {
                return customer;
            } else {
                throw new CustomerInvalidLoginCredentialException("Email does not exist or invalid password!");
            }
        } catch (CustomerNotFoundException ex) {
            throw new CustomerInvalidLoginCredentialException("Email does not exist or invalid password!");
        }
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) throws CustomerInvalidPasswordException, CustomerNotFoundException{
        try {
            
            Customer customer = retrieveCustomerByEmail(email);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(oldPassword + customer.getSalt()));

            if (passwordHash.equals(customer.getPassword())) {
                customer.setPassword(newPassword);
                em.flush();
            } else {
                throw new CustomerInvalidPasswordException("Entered password do not match password associated with account!");
            }
        } catch (CustomerNotFoundException ex) {
            throw new CustomerNotFoundException("Email does not exist!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Customer>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
