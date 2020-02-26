package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

@Named(value = "viewAllEmployeesManagedBean")
@RequestScoped
public class ViewAllEmployeesManagedBean {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private List<Employee> employees;

    public ViewAllEmployeesManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        employees = employeeSessionBeanLocal.retrieveAllEmployees();
    }

    public void viewEmployeeDetails(ActionEvent event) throws IOException {
        Long employeeIdToView = (Long) event.getComponent().getAttributes().get("employeeId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("employeeIdToView", employeeIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewEmployeeDetails.xhtml");
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

}
