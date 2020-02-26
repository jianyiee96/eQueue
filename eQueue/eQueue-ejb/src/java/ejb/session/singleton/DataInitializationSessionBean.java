package ejb.session.singleton;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Customer;
import entity.DiningTable;
import entity.Employee;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeRoleEnum;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.CustomerNotUniqueException;
import util.exceptions.EmployeeUsernameExistException;
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
    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

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

        try {

            customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "Account", "guest@equeue.com", "password"));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(4L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(4L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(2L));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Manager", "Default", "manager@eQueue.com", "manager", "password", EmployeeRoleEnum.MANAGER));

        } catch (EmployeeUsernameExistException | CustomerNotUniqueException | InputDataValidationException | UnknownPersistenceException ex) {
            ex.printStackTrace();
        }

    }

}
