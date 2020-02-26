package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.DeleteEmployeeException;
import util.exceptions.EmployeeNotFoundException;

@Named(value = "deleteEmployeeManagedBean")
@ViewScoped
public class DeleteEmployeeManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private Long employeeIdToDelete;
    private Employee employeeToDelete;

    public DeleteEmployeeManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        employeeIdToDelete = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("employeeIdToDelete");

        try {
            employeeToDelete = employeeSessionBeanLocal.retrieveEmployeeById(employeeIdToDelete);
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the employee details: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void deleteProduct(ActionEvent event) {
        try {
            employeeSessionBeanLocal.deleteEmployee(employeeIdToDelete);
            employeeToDelete = null;

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee deleted successfully", null));
        } catch (EmployeeNotFoundException | DeleteEmployeeException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting employee: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void back(ActionEvent event) throws IOException {
        if (employeeToDelete == null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("viewAllEmployees.xhtml");
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("employeeIdToView", employeeIdToDelete);
            FacesContext.getCurrentInstance().getExternalContext().redirect("viewEmployeeDetails.xhtml");
        }
    }

    public Long getEmployeeIdToDelete() {
        return employeeIdToDelete;
    }

    public void setEmployeeIdToDelete(Long employeeIdToDelete) {
        this.employeeIdToDelete = employeeIdToDelete;
    }

    public Employee getEmployeeToDelete() {
        return employeeToDelete;
    }

    public void setEmployeeToDelete(Employee employeeToDelete) {
        this.employeeToDelete = employeeToDelete;
    }
}
