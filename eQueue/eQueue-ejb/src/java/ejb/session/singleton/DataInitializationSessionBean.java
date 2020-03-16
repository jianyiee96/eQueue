package ejb.session.singleton;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.MenuCategorySessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import ejb.session.stateless.StoreManagementSessionBeanLocal;
import entity.Customer;
import entity.DiningTable;
import entity.Employee;
import entity.MenuCategory;
import entity.MenuItem;
import entity.StoreVariables;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeRoleEnum;
import util.enumeration.MenuItemAvailabilityEnum;
import util.exceptions.CreateNewMenuCategoryException;
import util.exceptions.CreateNewMenuItemException;
import util.exceptions.CustomerNotUniqueException;
import util.exceptions.EmployeeUsernameExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuItemNotUniqueException;
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
    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;
    @EJB
    private MenuCategorySessionBeanLocal menuCategorySessionBean;

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
            employeeSessionBeanLocal.createNewEmployee(new Employee("Manager", "Default", "manager@eQueue.com", "manager", "password", EmployeeRoleEnum.MANAGER));
            diningTableSessionBean.allocateTableToCustomer(1l, 1l);
            diningTableSessionBean.allocateTableToCustomer(2l, 2l);
            diningTableSessionBean.seatCustomerToDiningTable(2l, 2l);
            diningTableSessionBean.seatCustomerToDiningTable(1l, 4l);

            Long mc1Id = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Asian"), null);
            Long mc2Id = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Chinese"), mc1Id);
            Long mc3Id = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Malay"), mc1Id);
            Long mc4Id = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Peranakan"), mc3Id);
            Long mc5Id = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Western"), null);
            menuItemSessionBean.createNewMenuItem(new MenuItem("MI001", "Chicken Rice", 3.50, 10L, MenuItemAvailabilityEnum.AVAILABLE), mc2Id);
            menuItemSessionBean.createNewMenuItem(new MenuItem("MI002", "Char Kway Teow", 4.00, 15L, MenuItemAvailabilityEnum.AVAILABLE), mc2Id);
            menuItemSessionBean.createNewMenuItem(new MenuItem("MI003", "Hokkien Noodles", 4.00, 15L, MenuItemAvailabilityEnum.AVAILABLE), mc2Id);
            menuItemSessionBean.createNewMenuItem(new MenuItem("MI004", "Ayam Buah Keluak", 3.50, 20L, MenuItemAvailabilityEnum.AVAILABLE), mc4Id);
            menuItemSessionBean.createNewMenuItem(new MenuItem("MI005", "Chap Chai", 3.50, 20L, MenuItemAvailabilityEnum.AVAILABLE), mc4Id);
            menuItemSessionBean.createNewMenuItem(new MenuItem("MI006", "Chicken Chop", 6.00, 10L, MenuItemAvailabilityEnum.AVAILABLE), mc5Id);
            menuItemSessionBean.createNewMenuItem(new MenuItem("MI007", "Fish and Chips", 6.50, 10L, MenuItemAvailabilityEnum.AVAILABLE), mc5Id);
            menuItemSessionBean.createNewMenuItem(new MenuItem("MI008", "Fries", 2.00, 10L, MenuItemAvailabilityEnum.AVAILABLE), mc5Id);
        } catch (EmployeeUsernameExistException | CustomerNotUniqueException | InputDataValidationException | UnknownPersistenceException | CreateNewMenuCategoryException | CreateNewMenuItemException | MenuItemNotUniqueException ex) {
            ex.printStackTrace();
        }

    }

}
