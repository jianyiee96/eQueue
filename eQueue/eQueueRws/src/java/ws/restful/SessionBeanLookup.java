package ws.restful;

import ejb.session.stateless.CustomerSessionBeanLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
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

}
