package ws.restful;

import ejb.session.stateless.AlertSessionBeanLocal;
import ejb.session.stateless.CreditCardSessionBeanLocal;
import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.QueueSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.MenuCategorySessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import ejb.session.stateless.NotificationSessionBeanLocal;
import ejb.session.stateless.OrderLineItemSessionBeanLocal;
import ejb.session.stateless.PaymentTransactionSessionBeanLocal;
import ejb.session.stateless.ShoppingCartSessionBeanLocal;
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

    public MenuCategorySessionBeanLocal lookupMenuCategorySessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (MenuCategorySessionBeanLocal) c.lookup(ejbModuleJndiPath + "MenuCategorySessionBean!ejb.session.stateless.MenuCategorySessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public MenuItemSessionBeanLocal lookupMenuItemSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (MenuItemSessionBeanLocal) c.lookup(ejbModuleJndiPath + "MenuItemSessionBean!ejb.session.stateless.MenuItemSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public ShoppingCartSessionBeanLocal lookupShoppingCartSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (ShoppingCartSessionBeanLocal) c.lookup(ejbModuleJndiPath + "ShoppingCartSessionBean!ejb.session.stateless.ShoppingCartSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public CreditCardSessionBeanLocal lookupCreditCardSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (CreditCardSessionBeanLocal) c.lookup(ejbModuleJndiPath + "CreditCardSessionBean!ejb.session.stateless.CreditCardSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public CustomerOrderSessionBeanLocal lookupCustomerOrderSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (CustomerOrderSessionBeanLocal) c.lookup(ejbModuleJndiPath + "CustomerOrderSessionBean!ejb.session.stateless.CustomerOrderSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public OrderLineItemSessionBeanLocal lookupOrderLineItemSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (OrderLineItemSessionBeanLocal) c.lookup(ejbModuleJndiPath + "OrderLineItemSessionBean!ejb.session.stateless.OrderLineItemSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    public AlertSessionBeanLocal lookupAlertSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (AlertSessionBeanLocal) c.lookup(ejbModuleJndiPath + "AlertSessionBean!ejb.session.stateless.AlertSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public PaymentTransactionSessionBeanLocal lookupPaymentTransactionSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (PaymentTransactionSessionBeanLocal) c.lookup(ejbModuleJndiPath + "PaymentTransactionSessionBean!ejb.session.stateless.PaymentTransactionSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
