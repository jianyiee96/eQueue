package ejb.session.singleton;

import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Customer;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.CustomerNotUniqueException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    public DataInitializationSessionBean() {

    }

    @PostConstruct
    public void postConstruct() {
        try {
            customerSessionBeanLocal.retrieveCustomerByEmail("guest@equeue.com");
        } catch (CustomerNotFoundException ex) {
            initializeData();
        }
    }

    private void initializeData() {

        try{
            customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "Account", "guest@equeue.com", "password"));
            
            
            
        } catch (CustomerNotUniqueException | InputDataValidationException | UnknownPersistenceException ex){
            ex.printStackTrace();
        }

    }

}
