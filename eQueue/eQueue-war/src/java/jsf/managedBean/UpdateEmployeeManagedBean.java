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
import util.exceptions.EmployeeNotFoundException;

@Named(value = "updateEmployeeManagedBean")
@ViewScoped
public class UpdateEmployeeManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private Long employeeIdToUpdate;
    private Employee employeeToUpdate;

    public UpdateEmployeeManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        employeeIdToUpdate = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("employeeIdToUpdate");

        try {
            employeeToUpdate = employeeSessionBeanLocal.retrieveEmployeeById(employeeIdToUpdate);
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the employee details: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void back(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("employeeIdToView", employeeIdToUpdate);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewEmployeeDetails.xhtml");
    }

    public void updateEmployee(ActionEvent event) {
        try {
            employeeSessionBeanLocal.updateEmployee(employeeToUpdate);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Employee updated successfully", null));
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating employee: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public Long getEmployeeIdToUpdate() {
        return employeeIdToUpdate;
    }

    public void setEmployeeIdToUpdate(Long employeeIdToUpdate) {
        this.employeeIdToUpdate = employeeIdToUpdate;
    }

    public Employee getEmployeeToUpdate() {
        return employeeToUpdate;
    }

    public void setEmployeeToUpdate(Employee employeeToUpdate) {
        this.employeeToUpdate = employeeToUpdate;
    }

}
