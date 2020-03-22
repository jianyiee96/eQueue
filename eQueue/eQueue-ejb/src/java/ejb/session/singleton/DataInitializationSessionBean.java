package ejb.session.singleton;

import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.MenuCategorySessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import ejb.session.stateless.OrderLineItemSessionBean;
import ejb.session.stateless.OrderLineItemSessionBeanLocal;
import ejb.session.stateless.StoreManagementSessionBeanLocal;
import entity.Customer;
import entity.CustomerOrder;
import entity.DiningTable;
import entity.Employee;
import entity.MenuCategory;
import entity.MenuItem;
import entity.OrderLineItem;
import entity.StoreVariables;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeRoleEnum;
import util.enumeration.MenuItemAvailabilityEnum;
import util.enumeration.OrderLineItemStatusEnum;
import util.exceptions.CreateNewCustomerOrderException;
import util.exceptions.CreateNewMenuCategoryException;
import util.exceptions.CreateNewMenuItemException;
import util.exceptions.CreateNewOrderLineItemException;
import util.exceptions.CustomerNotUniqueException;
import util.exceptions.EmployeeUsernameExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuItemNotUniqueException;
import util.exceptions.OrderLineItemNotFoundException;
import util.exceptions.StoreNotInitializedException;
import util.exceptions.UnknownPersistenceException;

@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {

    @EJB
    private CustomerOrderSessionBeanLocal customerOrderSessionBean;

    @EJB
    private OrderLineItemSessionBeanLocal orderLineItemSessionBean;

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
            Long custId1 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "Account", "guest@equeue.com", "password"));
            Long custId2 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "A", "guestA@equeue.com", "password"));
            Long custId3 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "B", "guestB@equeue.com", "password"));
            Long custId4 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "C", "guestC@equeue.com", "password"));

            diningTableSessionBean.createNewDiningTable(new DiningTable(8L),true);
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L),true);
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L),true);
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L),true);
            diningTableSessionBean.createNewDiningTable(new DiningTable(4L),true);
            diningTableSessionBean.createNewDiningTable(new DiningTable(4L),true);
            diningTableSessionBean.createNewDiningTable(new DiningTable(2L),true);
            
            
            employeeSessionBeanLocal.createNewEmployee(new Employee("Wee Kek", "Tan", "weekek-tan@eQueue.com", "manager", "password", EmployeeRoleEnum.MANAGER, "tan_wee_kek.jpg"));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Chen Kun Keith", "Lim", "keith-lim@eQueue.com", "keithlim", "password", EmployeeRoleEnum.MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Jia Jin Bryan", "Thum", "bryan-thum@eQueue.com", "bryanthum", "password", EmployeeRoleEnum.MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Jian Yee", "Hew", "jianyiee-hew@eQueue.com", "hewjianyiee", "password", EmployeeRoleEnum.MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Wee Keat", "Tan", "weekeat-tan@eQueue.com", "tanweekeat", "password", EmployeeRoleEnum.MANAGER, "weekeat.JPG"));
            employeeSessionBeanLocal.createNewEmployee(new Employee("One", "Cashier", "cashier-one@eQueue.com", "cashier1", "password", EmployeeRoleEnum.CASHIER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Two", "Cashier", "cashier-two@eQueue.com", "cashier2", "password", EmployeeRoleEnum.CASHIER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Three", "Cashier", "cashier-three@eQueue.com", "cashier3", "password", EmployeeRoleEnum.CASHIER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Four", "Cashier", "cashier-four@eQueue.com", "cashier4", "password", EmployeeRoleEnum.CASHIER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("One", "Staff", "staff-one@eQueue.com", "staff1", "password", EmployeeRoleEnum.DEFAULT));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Two", "Staff", "staff-two@eQueue.com", "staff2", "password", EmployeeRoleEnum.DEFAULT));

            diningTableSessionBean.allocateTableToCustomer(1l, 1l);
            diningTableSessionBean.allocateTableToCustomer(2l, 2l);
            diningTableSessionBean.seatCustomerToDiningTable(2l);
            diningTableSessionBean.seatCustomerToDiningTable(5l);

            Long mcId1 = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Asian"), null);
            Long mcId2 = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Chinese"), mcId1);
            Long mcId3 = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Malay"), mcId1);
            Long mcId4 = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Peranakan"), mcId3);
            Long mcId5 = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Western"), null);

            Long miId1 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI001", "Chicken Rice", 3.50, 10L, MenuItemAvailabilityEnum.AVAILABLE), mcId2);
            Long miId2 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI002", "Char Kway Teow", 4.00, 15L, MenuItemAvailabilityEnum.AVAILABLE), mcId2);
            Long miId3 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI003", "Hokkien Noodles", 4.00, 15L, MenuItemAvailabilityEnum.AVAILABLE), mcId2);
            Long miId4 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI004", "Ayam Buah Keluak", 3.50, 20L, MenuItemAvailabilityEnum.AVAILABLE), mcId4);
            Long miId5 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI005", "Chap Chai", 3.50, 20L, MenuItemAvailabilityEnum.AVAILABLE), mcId4);
            Long miId6 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI006", "Chicken Chop", 6.00, 10L, MenuItemAvailabilityEnum.AVAILABLE), mcId5);
            Long miId7 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI007", "Fish and Chips", 6.50, 10L, MenuItemAvailabilityEnum.AVAILABLE), mcId5);
            Long miId8 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI008", "Fries", 2.00, 10L, MenuItemAvailabilityEnum.AVAILABLE), mcId5);

            for (int i = 0; i < 20; i++) {
                List<OrderLineItem> orderLineItems = new ArrayList<>();
                Long oliId1 = orderLineItemSessionBean.createNewOrderLineItem(new OrderLineItem(2L, "Less salty please", OrderLineItemStatusEnum.ORDERED), miId1);
                Long oliId2 = orderLineItemSessionBean.createNewOrderLineItem(new OrderLineItem(1L, null, OrderLineItemStatusEnum.ORDERED), miId2);
                Long oliId3 = orderLineItemSessionBean.createNewOrderLineItem(new OrderLineItem(1L, null, OrderLineItemStatusEnum.ORDERED), miId5);
                Long oliId4 = orderLineItemSessionBean.createNewOrderLineItem(new OrderLineItem(1L, null, OrderLineItemStatusEnum.ORDERED), miId7);
                OrderLineItem oli1 = orderLineItemSessionBean.retrieveOrderLineItemById(oliId1);
                OrderLineItem oli2 = orderLineItemSessionBean.retrieveOrderLineItemById(oliId2);
                OrderLineItem oli3 = orderLineItemSessionBean.retrieveOrderLineItemById(oliId3);
                OrderLineItem oli4 = orderLineItemSessionBean.retrieveOrderLineItemById(oliId4);
                orderLineItems.add(oli1);
                orderLineItems.add(oli2);
                orderLineItems.add(oli3);
                orderLineItems.add(oli4);
                CustomerOrder customerOrder = new CustomerOrder();
                customerOrder.setTotalAmount(calculateTotalPrice(orderLineItems));
                customerOrderSessionBean.createCustomerOrder(customerOrder, custId1, orderLineItems);
            }
        } catch (EmployeeUsernameExistException | CustomerNotUniqueException | InputDataValidationException
                | UnknownPersistenceException | CreateNewMenuCategoryException | CreateNewMenuItemException
                | MenuItemNotUniqueException | CreateNewOrderLineItemException | OrderLineItemNotFoundException
                | CreateNewCustomerOrderException ex) {
            ex.printStackTrace();
        }

    }

    private Double calculateTotalPrice(List<OrderLineItem> orderLineItems) {
        double totalPrice = 0.0;
        for (OrderLineItem oli : orderLineItems) {
            totalPrice += oli.getQuantity() * oli.getMenuItem().getMenuItemPrice();
        }
        return totalPrice;
    }

}
