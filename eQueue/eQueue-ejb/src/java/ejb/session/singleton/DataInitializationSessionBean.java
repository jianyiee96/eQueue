package ejb.session.singleton;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.StoreManagementSessionBeanLocal;
import entity.Customer;
import entity.DiningTable;
import entity.Employee;
import entity.StoreVariables;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeRoleEnum;
import util.exceptions.CustomerNotUniqueException;
import util.exceptions.EmployeeUsernameExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.StoreNotInitializedException;
import util.exceptions.UnknownPersistenceException;

@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    @EJB
    private StoreManagementSessionBeanLocal storeManagementSessionBeanLocal;
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
            storeManagementSessionBeanLocal.retrieveStoreVariables();
        } catch (StoreNotInitializedException ex) {
            initializeData();
        }
    }

    private void initializeData() {

        try {

            storeManagementSessionBeanLocal.storeInitialization(new StoreVariables("HamBaoBao", "HamBaoBao@burger.com.yummy", "Kent Ridge Hall, NUS Street 71. #03-21", "Welcome to HamBaoBao", "+65-65410434"));
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
