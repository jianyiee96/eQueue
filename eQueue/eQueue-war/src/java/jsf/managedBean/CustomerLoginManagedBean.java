/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Customer;
import java.io.IOException;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import util.exceptions.CustomerInvalidLoginCredentialException;

/**
 *
 * @author User
 */
@Named(value = "customerLoginManagedBean")
@RequestScoped
public class CustomerLoginManagedBean {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    
    private String email;
    private String password;

    
    public CustomerLoginManagedBean() {
    
    }

    public void customerLogin(ActionEvent event) throws IOException {
        try {
            Customer currentCustomer = customerSessionBeanLocal.customerLogin(email, password);

            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("customerIsLogin", true);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentCustomer", currentCustomer);
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/index.xhtml");
        } catch (CustomerInvalidLoginCredentialException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid login credential: " + ex.getMessage(), null));
        }
    }

    public void customerLogout(ActionEvent event) throws IOException {
        ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
        FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/index.xhtml");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
