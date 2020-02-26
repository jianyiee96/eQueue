package jsf.managedBean;

import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Customer;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import util.exceptions.CustomerNotUniqueException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Named(value = "createNewCustomerManagedBean")
@ViewScoped
public class CreateNewCustomerManagedBean implements Serializable {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    private Customer newCustomer;

    public CreateNewCustomerManagedBean() {
        newCustomer = new Customer();
    }

    public void createNewCustomer() {

        try {
            Long customerId = customerSessionBeanLocal.createNewCustomer(newCustomer);
            newCustomer = new Customer();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New customer created successfully (Customer ID: " + customerId + ")", null));
        } catch (CustomerNotUniqueException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating new customer: Customer already exist", null));
        } catch (InputDataValidationException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating new customer: " + ex.getMessage(), null));
        }
    }

    public void foo() {

    }

    public void back(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
    }

//    public void back(ActionEvent event) throws IOException {
//        FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/index.xhtml");
//    }
//    
    
    public Customer getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(Customer newCustomer) {
        this.newCustomer = newCustomer;
    }

}
