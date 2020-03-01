package ejb.session.singleton;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import entity.Customer;
import entity.DiningTable;
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
    @EJB
    private DiningTableSessionBeanLocal diningTableSessionBean;
    
    
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
            customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "A", "guestA@equeue.com", "password"));
            customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "B", "guestB@equeue.com", "password"));
            customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "C", "guestC@equeue.com", "password"));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(4L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(4L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(2L));
            
            diningTableSessionBean.allocateTableToCustomer(1l, 1l);
            diningTableSessionBean.allocateTableToCustomer(2l, 2l);
            diningTableSessionBean.seatCustomerToDiningTable(2l, 2l);
            diningTableSessionBean.seatCustomerToDiningTable(1l, 4l);
            
            
        } catch (CustomerNotUniqueException | InputDataValidationException | UnknownPersistenceException ex){
            ex.printStackTrace();
        }

    }

}
