package ejb.session.singleton;

import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.MenuCategorySessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import ejb.session.stateless.NotificationSessionBeanLocal;
import ejb.session.stateless.OrderLineItemSessionBeanLocal;
import ejb.session.stateless.StoreManagementSessionBeanLocal;
import entity.Customer;
import entity.CustomerOrder;
import entity.DiningTable;
import entity.Employee;
import entity.MenuCategory;
import entity.MenuItem;
import entity.Notification;
import entity.OrderLineItem;
import entity.Store;
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
import util.enumeration.NotificationTypeEnum;
import util.enumeration.OrderLineItemStatusEnum;
import util.enumeration.OrderStatusEnum;
import util.exceptions.CreateNewCustomerOrderException;
import util.exceptions.CreateNewMenuCategoryException;
import util.exceptions.CreateNewMenuItemException;
import util.exceptions.CreateNewOrderLineItemException;
import util.exceptions.CustomerNotUniqueException;
import util.exceptions.EmployeeUsernameExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuItemNotUniqueException;
import util.exceptions.MenuItemUnavailableException;
import util.exceptions.OrderLineItemNotFoundException;
import util.exceptions.StoreNotInitializedException;
import util.exceptions.UnableToCreateNotificationException;
import util.exceptions.UnknownPersistenceException;

@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {
    
    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;
    
    @EJB
    private CustomerOrderSessionBeanLocal customerOrderSessionBean;
    @EJB
    private OrderLineItemSessionBeanLocal orderLineItemSessionBean;
    @EJB
    private StoreManagementSessionBeanLocal storeManagementSessionBeanLocal;
    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    private DiningTableSessionBeanLocal diningTableSessionBean;
    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;
    @EJB
    private MenuCategorySessionBeanLocal menuCategorySessionBean;
    @EJB
    private NotificationSessionBeanLocal notificationSessionBeanLocal;
    
    public DataInitializationSessionBean() {
        
    }
    
    @PostConstruct
    public void postConstruct() {
        try {
            //customerOrderSessionBean.retrieveIncompleteOrders();
            storeManagementSessionBeanLocal.retrieveStore();
        } catch (StoreNotInitializedException ex) {
            initializeData();
            //customerOrderSessionBean.retrieveIncompleteOrders();
        }
    }
    
    private void initializeData() {
        
        System.out.println("Initialiing Data...");
        try {
            
            storeManagementSessionBeanLocal.storeInitialization(new Store("HamBaoBao", "HamBaoBao@burger.com.yummy", "Kent Ridge Hall, NUS Street 71. #03-21", "Welcome to HamBaoBao", "+65-65410434"));
            Long custId1 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "Account", "guest@equeue.com", "password"));
            Long custId2 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "A", "guestA@equeue.com", "password"));
            Long custId3 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "B", "guestB@equeue.com", "password"));
            Long custId4 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "C", "guestC@equeue.com", "password"));
            Long custId5 = customerSessionBeanLocal.createNewCustomer(new Customer("Hew", "Jian Yiee", "hew1521@hotmail.com", "password"));
            
            notificationSessionBeanLocal.createNewNotification(new Notification("Welcome", "Welcome to eQueue :)", NotificationTypeEnum.GENERAL), custId5);
            notificationSessionBeanLocal.createNewNotification(new Notification("Instruction", "For instructions on how to use this app, please refer to google.com", NotificationTypeEnum.GENERAL), custId5);
//            notificationSessionBeanLocal.createNewNotification(new Notification("Instruction Pt. 2", "To go google.com first you need a browser and an internet connection", NotificationTypeEnum.GENERAL), custId5);
//            notificationSessionBeanLocal.createNewNotification(new Notification("Instruction Pt. 3", "After installing your browser and connecting to the internet, start the brower", NotificationTypeEnum.GENERAL), custId5);
//            notificationSessionBeanLocal.createNewNotification(new Notification("Instruction Pt. 4", "Now enter google.com on the URL bar", NotificationTypeEnum.GENERAL), custId5);
//            notificationSessionBeanLocal.createNewNotification(new Notification("Instruction Pt. 5", "On google search bar, simply key in eQueue - documentation. Click on the first item in the result", NotificationTypeEnum.GENERAL), custId5);
//            notificationSessionBeanLocal.createNewNotification(new Notification("Instruction Pt. 6", "Now you will have able to access our app's full documentation. ", NotificationTypeEnum.GENERAL), custId5);
//            
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(4L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(4L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(2L));
            
            employeeSessionBeanLocal.createNewEmployee(new Employee("Wee Kek", "Tan", "weekek-tan@eQueue.com", "manager", "password", EmployeeRoleEnum.MANAGER, "1 - tan_wee_kek.jpg"));
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
            
            Long mcId1 = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Asian"), null);
            Long mcId2 = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Chinese"), mcId1);
            Long mcId3 = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Malay"), mcId1);
            Long mcId4 = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Western"), null);
            
            Long mcId5 = menuCategorySessionBean.createNewMenuCategory(new MenuCategory("Original"), mcId4);
            
            Long miId1 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI001", "Chicken Rice", 3.50, 10L, MenuItemAvailabilityEnum.AVAILABLE, "1 - Chicken Rice.jpg", "Fragrant and steamy Chicken Rice"), mcId2);
            Long miId2 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI002", "Char Kway Teow", 4.00, 15L, MenuItemAvailabilityEnum.AVAILABLE, "2 - Char Kway Teow.jpg", "Singapore award winning Char Kway Teow"), mcId2);
            Long miId3 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI003", "Hokkien Noodles", 4.00, 15L, MenuItemAvailabilityEnum.AVAILABLE, "3 - Hokkien Noodles.jpg", "Penang best Hokkien Mee"), mcId2);
            Long miId4 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI004", "Ayam Buah Keluak", 3.50, 20L, MenuItemAvailabilityEnum.AVAILABLE, "4 - Ayam Buah Keluak.jpg", "Made with fresh kampung chicken"), mcId3);
            Long miId5 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI005", "Chap Chai", 3.50, 20L, MenuItemAvailabilityEnum.AVAILABLE, "5 - Chap Chai.JPG", "Chap Chai Chop Ching Chong"), mcId3);
            Long miId6 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI006", "Chicken Chop", 6.00, 10L, MenuItemAvailabilityEnum.AVAILABLE, "6 - Chicken Chop.jpg", "Freshly chopped chicken served with chopsticks"), mcId5);
            Long miId7 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI007", "Fish and Chips", 6.50, 10L, MenuItemAvailabilityEnum.AVAILABLE, "7 - Fish and Chips.jpg", "Original recipe from Sanji"), mcId5);
            Long miId8 = menuItemSessionBean.createNewMenuItem(new MenuItem("MI008", "Fries", 2.00, 10L, MenuItemAvailabilityEnum.AVAILABLE, "8 - Fries.jpg", "Golden Potato Fries, Served with Curry Sauce, BBQ Sauce and Mayo Sauce"), mcId5);
            
            for (int i = 0; i < 20; i++) {
                Boolean orderIsCancelled = true;
                
                List<OrderLineItem> orderLineItems = new ArrayList<>();
                
                Integer numItems = (int) (Math.random() * 3) + 1;
//                System.out.println("numItems --> " + numItems);

                for (int x = 0; numItems > x; x++) {
                    Long quantity = (long) (Math.random() * 10) + 1L;
                    Long menuItemId = (long) (Math.random() * 8) + 1L;
                    
                    OrderLineItemStatusEnum lineItemStatus = lineItemStatusRandomiser();
                    
                    if (lineItemStatus != OrderLineItemStatusEnum.CANCELLED) {
                        orderIsCancelled = false;
                    }
                    
                    Long oliId = orderLineItemSessionBean.createNewOrderLineItem(new OrderLineItem(quantity, remarksRandomiser(), lineItemStatus), menuItemId);
                    OrderLineItem oli = orderLineItemSessionBean.retrieveOrderLineItemById(oliId);
                    orderLineItems.add(oli);
                }
                
                CustomerOrder customerOrder = new CustomerOrder();
                customerOrder.setTotalAmount(calculateTotalPrice(orderLineItems));
                
                if (orderIsCancelled) {
                    customerOrder.setStatus(OrderStatusEnum.CANCELLED);
                    customerOrder.setIsCompleted(true);
                }
                
                customerOrderSessionBean.createCustomerOrder(customerOrder, (long) (Math.random() * 4) + 1L, orderLineItems);
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                
            }
        } catch (EmployeeUsernameExistException | CustomerNotUniqueException | InputDataValidationException
                | UnknownPersistenceException | CreateNewMenuCategoryException | CreateNewMenuItemException
                | MenuItemNotUniqueException | CreateNewOrderLineItemException | OrderLineItemNotFoundException
                | CreateNewCustomerOrderException | MenuItemUnavailableException | UnableToCreateNotificationException ex) {
            ex.printStackTrace();
        }
    }
    
    private String remarksRandomiser() {
        Integer remarksRandomiser = (int) (Math.random() * 5);
        switch (remarksRandomiser) {
            case 0:
                return "Less salty please";
            case 1:
                return "More spicy please";
            case 2:
                return "Less spicy aight?";
            case 3:
                return "Less sweet";
            default:
                return "Faster!!";
        }
    }
    
    private OrderLineItemStatusEnum lineItemStatusRandomiser() {
        Integer lineItemStatusRandomiser = (int) (Math.random() * 4);
        switch (lineItemStatusRandomiser) {
            case 0:
                return OrderLineItemStatusEnum.ORDERED;
            case 1:
                return OrderLineItemStatusEnum.PREPARING;
            case 2:
                return OrderLineItemStatusEnum.SERVED;
            default:
                return OrderLineItemStatusEnum.CANCELLED;
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
