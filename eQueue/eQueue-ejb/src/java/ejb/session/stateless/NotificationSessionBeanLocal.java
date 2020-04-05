/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Notification;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.UnableToCreateNotificationException;

/**
 *
 * @author User
 */
@Local
public interface NotificationSessionBeanLocal {
    public List<Notification> retrieveNotificationsByCustomerId(Long customerId);
    
    public Long createNewNotification(Notification newNotification, Long customerId) throws UnableToCreateNotificationException;
    
    public Boolean readNotification(Long notificationId);
    
    public Boolean deleteNotification(Long notificationId);
}
