/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;


import entity.Notification;
import java.util.List;

/**
 *
 * @author User
 */
public class RetrieveCustomerNotificationsRsp {
    
    private List<Notification> notifications;

    public RetrieveCustomerNotificationsRsp() {
    }

    public RetrieveCustomerNotificationsRsp(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
    
    
    
}
