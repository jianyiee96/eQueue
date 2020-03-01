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

@Named(value = "viewEmployeeDetailsManagedBean")
@ViewScoped
public class ViewEmployeeDetailsManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private Long employeeIdToView;
    private Employee employeeToView;

    public ViewEmployeeDetailsManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        employeeIdToView = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("employeeIdToView");

        try {
            employeeToView = employeeSessionBeanLocal.retrieveEmployeeById(employeeIdToView);
        } catch (EmployeeNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the employee details: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
    public void back(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewAllEmployees.xhtml");
    }
    
    public void updateEmployee(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("employeeIdToUpdate", employeeIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("updateEmployee.xhtml");
    }

    public void deleteEmployee(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("employeeIdToDelete", employeeIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("deleteEmployee.xhtml");
    }

    public Long getEmployeeIdToView() {
        return employeeIdToView;
    }

    public void setEmployeeIdToView(Long employeeIdToView) {
        this.employeeIdToView = employeeIdToView;
    }

    public Employee getEmployeeToView() {
        return employeeToView;
    }

    public void setEmployeeToView(Employee employeeToView) {
        this.employeeToView = employeeToView;
    }

}
