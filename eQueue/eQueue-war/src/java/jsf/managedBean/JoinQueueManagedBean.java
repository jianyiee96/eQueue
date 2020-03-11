/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.QueueSessionBeanLocal;
import entity.Customer;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.UnableToJoinQueueException;

/**
 *
 * @author User
 */
@Named(value = "joinQueueManagedBean")
@ViewScoped
public class JoinQueueManagedBean implements Serializable {

    @EJB
    private QueueSessionBeanLocal queueSessionBeanLocal;

    private Long numberOfPax;
    private Boolean joinable = true;
    private Customer currentCustomer;

    public JoinQueueManagedBean() {
        numberOfPax = 1l;
    }
    
    @PostConstruct
    public void postConstruct(){
        currentCustomer = (Customer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentCustomer");
    }

    public void foo(){}
    
    public void join() {

        try {

            if (queueSessionBeanLocal.retrieveQueueByCustomerId(currentCustomer.getCustomerId())== null) {
                queueSessionBeanLocal.joinQueue(currentCustomer.getCustomerId(), numberOfPax);
                numberOfPax = 1l;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucessfully Joined Queue!", null));
            } else {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Unable to join queue: Customer has exisitng queue", null));
            }

        } catch (UnableToJoinQueueException ex) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Customer does not exist", null));
        }

    }

    public Long getNumberOfPax() {
        return numberOfPax;
    }

    public void setNumberOfPax(Long numberOfPax) {
        this.numberOfPax = numberOfPax;
    }

    public Boolean getJoinable() {
        return joinable;
    }

    public void setJoinable(Boolean joinable) {
        this.joinable = joinable;
    }

}
