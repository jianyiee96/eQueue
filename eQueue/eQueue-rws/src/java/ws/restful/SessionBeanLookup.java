package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.QueueSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.NotificationSessionBeanLocal;
import ejb.session.stateless.StoreManagementSessionBeanLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SessionBeanLookup {

    private final String ejbModuleJndiPath;

    public SessionBeanLookup() {
        ejbModuleJndiPath = "java:global/eQueue/eQueue-ejb/";
    }

    public CustomerSessionBeanLocal lookupCustomerSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CustomerSessionBeanLocal) c.lookup(ejbModuleJndiPath + "CustomerSessionBean!ejb.session.stateless.CustomerSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    public NotificationSessionBeanLocal lookupNotificationSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (NotificationSessionBeanLocal) c.lookup(ejbModuleJndiPath + "NotificationSessionBean!ejb.session.stateless.NotificationSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    public QueueSessionBeanLocal lookupQueueSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (QueueSessionBeanLocal) c.lookup(ejbModuleJndiPath + "QueueSessionBean!ejb.session.stateless.QueueSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public DiningTableSessionBeanLocal lookupDiningTableSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (DiningTableSessionBeanLocal) c.lookup(ejbModuleJndiPath + "DiningTableSessionBean!ejb.session.stateless.DiningTableSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    public StoreManagementSessionBeanLocal lookupStoreManagementSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (StoreManagementSessionBeanLocal) c.lookup(ejbModuleJndiPath + "StoreManagementSessionBean!ejb.session.stateless.StoreManagementSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
