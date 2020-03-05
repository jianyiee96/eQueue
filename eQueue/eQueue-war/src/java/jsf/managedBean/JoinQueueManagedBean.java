/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.QueueSessionBeanLocal;
import entity.Customer;
import entity.Queue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author User
 */
@Named(value = "joinQueueManagedBean")
@ViewScoped
public class JoinQueueManagedBean implements Serializable {

    @EJB
    private QueueSessionBeanLocal queueSessionBeanLocal;
    
    private Boolean joinable = true;
    
    public JoinQueueManagedBean() {
        
    }
    
    

    public void join(){
        
        Customer customer = (Customer)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentCustomer");
        List<Queue> customerQueue = queueSessionBeanLocal.retrieveQueueByCustomerId(customer.getCustomerId());
        
        System.out.println("Retrieved: "+customerQueue.size() +" queues.");
        for(Queue q : customerQueue) {
            System.out.println(q.toString());
        }
     
    }
    
    public Boolean getJoinable() {
        return joinable;
    }

    public void setJoinable(Boolean joinable) {
        this.joinable = joinable;
    }
    
    
    
}
