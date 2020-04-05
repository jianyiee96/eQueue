/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Notification;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.NotificationTypeEnum;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.NotificationDoesNotExistException;
import util.exceptions.UnableToCreateNotificationException;

/**
 *
 * @author User
 */
@Stateless
public class NotificationSessionBean implements NotificationSessionBeanLocal {

    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public NotificationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewNotification(Notification newNotification, Long customerId) throws UnableToCreateNotificationException {

        try {
            Customer c = customerSessionBeanLocal.retrieveCustomerById(customerId);
            newNotification.setCustomer(c);
            em.persist(newNotification);
            em.flush();

            return newNotification.getNotificationId();

        } catch (CustomerNotFoundException ex) {
            throw new UnableToCreateNotificationException(ex.getMessage());

        }
    }

    @Override
    public List<Notification> retrieveNotificationsByCustomerId(Long customerId) {
        Query query = em.createQuery("SELECT n FROM Notification n WHERE n.customer.customerId = :inCustomerId ORDER BY n.notificationDate DESC");
        query.setParameter("inCustomerId", customerId);

        return query.getResultList();
    }

    @Override
    public Boolean readNotification(Long notificationId){
        Notification n = em.find(Notification.class, notificationId);

        if (n != null) {
            
            if(n.getIsRead()){
                return false;
            }
            n.setIsRead(true);
            return true;
            
        } else {
            return false;
        }

    }
    
    @Override
    public Boolean deleteNotification(Long notificationId){
        Notification n = em.find(Notification.class, notificationId);

        if (n != null) {
            n.getCustomer().getNotifications().remove(n);
            em.remove(n);
            return true;
        } else {
            return false;
        }

    }

}
