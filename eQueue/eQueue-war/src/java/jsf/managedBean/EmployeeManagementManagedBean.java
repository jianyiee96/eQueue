package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.model.UploadedFile;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.EmployeeUsernameExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Named(value = "employeeManagementManagedBean")
@ViewScoped
public class EmployeeManagementManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private List<Employee> employees;
    private List<Employee> filteredEmployees;

//    private UploadedFile file;

    private Employee employeeToCreate;
    private Employee employeeToView;

    public EmployeeManagementManagedBean() {
        employeeToCreate = new Employee();
        employeeToView = new Employee();
    }

    @PostConstruct
    public void postConstruct() {
        employees = employeeSessionBeanLocal.retrieveAllEmployees();
    }

    public void createNewEmployee() {
        try {
            Long employeeId = employeeSessionBeanLocal.createNewEmployee(employeeToCreate);
            employeeToCreate = employeeSessionBeanLocal.retrieveEmployeeById(employeeId);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New employee created successfully (Employee ID: " + employeeToCreate.getEmployeeId() + ")", null));

            postConstruct();

            employeeToCreate = new Employee();
        } catch (EmployeeUsernameExistException | EmployeeNotFoundException | InputDataValidationException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new employee: " + ex.getMessage(), null));
        }
    }

//    public void upload() {
//        if (file != null) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, file.getFileName() + " is uploaded successfully", null));
//        }
//    }

    public Employee getEmployeeToCreate() {
        return employeeToCreate;
    }

    public void setEmployeeToCreate(Employee employeeToCreate) {
        this.employeeToCreate = employeeToCreate;
    }

    public Employee getEmployeeToView() {
        return employeeToView;
    }

    public void setEmployeeToView(Employee employeeToView) {
        this.employeeToView = employeeToView;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Employee> getFilteredEmployees() {
        return filteredEmployees;
    }

    public void setFilteredEmployees(List<Employee> filteredEmployees) {
        this.filteredEmployees = filteredEmployees;
    }

//    public UploadedFile getFile() {
//        return file;
//    }
//
//    public void setFile(UploadedFile file) {
//        this.file = file;
//    }

}
