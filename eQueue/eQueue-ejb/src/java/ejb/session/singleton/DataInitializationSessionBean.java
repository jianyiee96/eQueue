package ejb.session.singleton;

import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.MenuCategorySessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import ejb.session.stateless.NotificationSessionBeanLocal;
import ejb.session.stateless.OrderLineItemSessionBeanLocal;
import ejb.session.stateless.PaymentTransactionSessionBeanLocal;
import ejb.session.stateless.StoreManagementSessionBeanLocal;
import entity.Customer;
import entity.CustomerOrder;
import entity.DiningTable;
import entity.Employee;
import entity.MenuCategory;
import entity.MenuItem;
import entity.Notification;
import entity.OrderLineItem;
import entity.PaymentTransaction;
import entity.Store;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
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
import util.enumeration.TableStatusEnum;
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

    @EJB
    private PaymentTransactionSessionBeanLocal paymentTransactionSessionBean;

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

    private static final Long totalNumOfMenuItems = 38L;
    private static final Long totalNumOfCustomers = 4L;
    private static DecimalFormat df = new DecimalFormat("#.##");

    public DataInitializationSessionBean() {

    }

    @PostConstruct
    public void postConstruct() {
        try {
            storeManagementSessionBeanLocal.retrieveStore();
        } catch (StoreNotInitializedException ex) {
            initializeData();
        }
    }

    private void initializeData() {

        System.out.println("Initializing Data... This might take awhile...");
        try {

            storeManagementSessionBeanLocal.storeInitialization(
                    new Store("The Everything Café", "contact-us@everythingcafe.com", "Kent Ridge Hall, NUS Street 71, #03-21",
                            "In view of COVID-19 situation, we will be giving healthcare workers free coffee! #SGUNITED", "+65-65410434")
            );

            Long custId1 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "One", "guest1@equeue.com", "password"));
            Long custId2 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "Two", "guest2@equeue.com", "password"));
            Long custId3 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "Three", "guest3@equeue.com", "password"));
            Long custId4 = customerSessionBeanLocal.createNewCustomer(new Customer("Guest", "Four", "guest4@equeue.com", "password"));

            notificationSessionBeanLocal.createNewNotification(new Notification("Welcome", "Welcome to eQueue :)", NotificationTypeEnum.GENERAL), custId1);
            notificationSessionBeanLocal.createNewNotification(new Notification("Welcome", "Welcome to eQueue :)", NotificationTypeEnum.GENERAL), custId2);
            notificationSessionBeanLocal.createNewNotification(new Notification("Welcome", "Welcome to eQueue :)", NotificationTypeEnum.GENERAL), custId3);
            notificationSessionBeanLocal.createNewNotification(new Notification("Welcome", "Welcome to eQueue :)", NotificationTypeEnum.GENERAL), custId4);

            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(8L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(4L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(4L));
            diningTableSessionBean.createNewDiningTable(new DiningTable(2L));

            employeeSessionBeanLocal.createNewEmployee(new Employee("Wee Kek", "Tan", "weekek-tan@eQueue.com", "manager", "password", EmployeeRoleEnum.MANAGER, "1 - tan_wee_kek.jpg"));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Chen Kun Keith", "Lim", "keith-lim@eQueue.com", "keithlim", "password", EmployeeRoleEnum.MANAGER, "2 - keith_lim.jpg"));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Jia Jin Bryan", "Thum", "bryan-thum@eQueue.com", "bryanthum", "password", EmployeeRoleEnum.MANAGER, "3 - bryan_thum.jpg"));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Jian Yee", "Hew", "jianyiee-hew@eQueue.com", "hewjianyiee", "password", EmployeeRoleEnum.MANAGER, "4 - jian_yiee.jpg"));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Wee Keat", "Tan", "weekeat-tan@eQueue.com", "tanweekeat", "password", EmployeeRoleEnum.MANAGER, "5 - wee_keat.jpg"));

            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee", "One", "employee-one@eQueue.com", "employee1", "password", EmployeeRoleEnum.EMPLOYEE));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee", "Two", "employee-two@eQueue.com", "employee2", "password", EmployeeRoleEnum.EMPLOYEE));
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee", "Three", "employee-three@eQueue.com", "employee3", "password", EmployeeRoleEnum.EMPLOYEE));

            // Parent Categories
            MenuCategory mc1 = new MenuCategory("Asian");
            MenuCategory mc2 = new MenuCategory("Western");
            MenuCategory mc3 = new MenuCategory("Drinks");
            Long mcId1 = menuCategorySessionBean.createNewMenuCategory(mc1, null);
            Long mcId2 = menuCategorySessionBean.createNewMenuCategory(mc2, null);
            Long mcId3 = menuCategorySessionBean.createNewMenuCategory(mc3, null);

            // Sub Categories
            // --Asian
            MenuCategory mc4 = new MenuCategory("Chinese");
            MenuCategory mc5 = new MenuCategory("Malay");
            MenuCategory mc6 = new MenuCategory("Indian");
            MenuCategory mc7 = new MenuCategory("Thai");
            Long mcId4 = menuCategorySessionBean.createNewMenuCategory(mc4, mcId1);
            Long mcId5 = menuCategorySessionBean.createNewMenuCategory(mc5, mcId1);
            Long mcId6 = menuCategorySessionBean.createNewMenuCategory(mc6, mcId1);
            Long mcId7 = menuCategorySessionBean.createNewMenuCategory(mc7, mcId1);

            // --Western
            MenuCategory mc8 = new MenuCategory("Salads");
            MenuCategory mc9 = new MenuCategory("Burgers");
            MenuCategory mc10 = new MenuCategory("Mains");
            MenuCategory mc11 = new MenuCategory("Pizzas");
            Long mcId8 = menuCategorySessionBean.createNewMenuCategory(mc8, mcId2);
            Long mcId9 = menuCategorySessionBean.createNewMenuCategory(mc9, mcId2);
            Long mcId10 = menuCategorySessionBean.createNewMenuCategory(mc10, mcId2);
            Long mcId11 = menuCategorySessionBean.createNewMenuCategory(mc11, mcId2);

            // --Drinks
            MenuCategory mc12 = new MenuCategory("Cold");
            MenuCategory mc13 = new MenuCategory("Hot");
            MenuCategory mc14 = new MenuCategory("Canned");
            Long mcId12 = menuCategorySessionBean.createNewMenuCategory(mc12, mcId3);
            Long mcId13 = menuCategorySessionBean.createNewMenuCategory(mc13, mcId3);
            Long mcId14 = menuCategorySessionBean.createNewMenuCategory(mc14, mcId3);

            // Menu Items
            // Chinese
            MenuItem mi1 = new MenuItem("MI001", "Chicken Rice", 3.50, 5L, MenuItemAvailabilityEnum.AVAILABLE, "1 - Chicken Rice.jpg", "Fragrant and steamy Chicken Rice");
            MenuItem mi2 = new MenuItem("MI002", "Char Kway Teow", 4.00, 15L, MenuItemAvailabilityEnum.AVAILABLE, "2 - Char Kway Teow.jpg", "Singapore award winning Char Kway Teow");
            MenuItem mi3 = new MenuItem("MI003", "Hokkien Noodles", 4.00, 15L, MenuItemAvailabilityEnum.AVAILABLE, "3 - Hokkien Noodles.jpg", "Penang best Hokkien Mee");
            MenuItem mi4 = new MenuItem("MI004", "Pork Ribs Rice", 4.50, 10L, MenuItemAvailabilityEnum.AVAILABLE, "4 - Pork Ribs Rice.jpg", "Succulent pork ribs rice. Best in Singapore!");
            MenuItem mi5 = new MenuItem("MI005", "Mee Goreng", 5.0, 8L, MenuItemAvailabilityEnum.AVAILABLE, "5 - Mee Goreng.jpg", "Chinese Mee Goreng. A little spicy.");
            MenuItem mi6 = new MenuItem("MI006", "Egg Gravy Crispy Noodle", 5.50, 15L, MenuItemAvailabilityEnum.AVAILABLE, "6 - Egg Gravy Crispy Noodle.jpg", "Crispy Fried Noodles soaked in deliciouos gravy.");
            MenuItem mi7 = new MenuItem("MI007", "Hong Kong Fried Noodles", 5.50, 15L, MenuItemAvailabilityEnum.AVAILABLE, "7 - Hong Kong Fried Noodles.jpg", "Famous Hong Kong dish prepared by our very own Hong Kong chefs.");
            menuItemSessionBean.createNewMenuItem(mi1, mcId4);
            menuItemSessionBean.createNewMenuItem(mi2, mcId4);
            menuItemSessionBean.createNewMenuItem(mi3, mcId4);
            menuItemSessionBean.createNewMenuItem(mi4, mcId4);
            menuItemSessionBean.createNewMenuItem(mi5, mcId4);
            menuItemSessionBean.createNewMenuItem(mi6, mcId4);
            menuItemSessionBean.createNewMenuItem(mi7, mcId4);
            // Malay
            MenuItem mi8 = new MenuItem("MI008", "Mee Siam", 3.50, 15L, MenuItemAvailabilityEnum.AVAILABLE, "8 - Mee Siam.jpg", "Mee siam is a dish of bee hoon with a unique sweet and tart gravy.");
            MenuItem mi9 = new MenuItem("MI009", "Mee Rebus", 3.50, 15L, MenuItemAvailabilityEnum.AVAILABLE, "9 - Mee Rebus.jpg", "Mee rebus is a dish comprising egg noodles in thick, spicy gravy.");
            MenuItem mi10 = new MenuItem("MI010", "Nasi Lemak", 5.50, 10L, MenuItemAvailabilityEnum.AVAILABLE, "10 - Nasi Lemak.jpg", "Coconut rice, chili on the side, slivers of anchovy, nuts, and a boiled egg.");
            MenuItem mi11 = new MenuItem("MI011", "Lontong", 4.00, 20L, MenuItemAvailabilityEnum.AVAILABLE, "11 - Lontong.jpg", "Lontong is an Indonesian dish made of compressed rice cake in the form of a cylinder wrapped inside a banana leaf.");
            menuItemSessionBean.createNewMenuItem(mi8, mcId5);
            menuItemSessionBean.createNewMenuItem(mi9, mcId5);
            menuItemSessionBean.createNewMenuItem(mi10, mcId5);
            menuItemSessionBean.createNewMenuItem(mi11, mcId5);
            // Indian
            MenuItem mi12 = new MenuItem("MI012", "Egg Prata", 2.00, 10L, MenuItemAvailabilityEnum.AVAILABLE, "12 - Egg Prata.jpg", "Roti prata is a fried flatbread that is cooked over a flat grill.");
            MenuItem mi13 = new MenuItem("MI013", "Thosai", 3.00, 15L, MenuItemAvailabilityEnum.AVAILABLE, "13 - Thosai.jpg", "Thosai is a South Indian savoury, thin pancake.");
            MenuItem mi14 = new MenuItem("MI014", "Chicken Curry", 5.50, 15L, MenuItemAvailabilityEnum.AVAILABLE, "14 - Chicken Curry.jpg", "Indian Curry chicken with potatoes is the most common chicken curry recipe in India.");
            menuItemSessionBean.createNewMenuItem(mi12, mcId6);
            menuItemSessionBean.createNewMenuItem(mi13, mcId6);
            menuItemSessionBean.createNewMenuItem(mi14, mcId6);
            // Thai
            MenuItem mi15 = new MenuItem("MI015", "Phad Thai", 4.50, 10L, MenuItemAvailabilityEnum.AVAILABLE, "15 - Phad Thai.jpg", "Pad thai is made with rehydrated dried rice noodles with some tapioca flour mixed in.");
            MenuItem mi16 = new MenuItem("MI016", "Tom Yum Soup", 6.00, 20L, MenuItemAvailabilityEnum.AVAILABLE, "16 - Tom Yum Soup.jpg", "Tom yum is characterised by its distinct hot and sour flavours, with fragrant spices and herbs generously used in the broth.");
            MenuItem mi17 = new MenuItem("MI017", "Basil Chicken Rice", 6.00, 10L, MenuItemAvailabilityEnum.AVAILABLE, "17 - Basil Chicken Rice.jpg", "The chicken is stir fried with Thai holy basil, and served on top of rice with a fried egg on the side.");
            menuItemSessionBean.createNewMenuItem(mi15, mcId7);
            menuItemSessionBean.createNewMenuItem(mi16, mcId7);
            menuItemSessionBean.createNewMenuItem(mi17, mcId7);
            // Salads
            MenuItem mi18 = new MenuItem("MI018", "Caesar Salad", 5.50, 10L, MenuItemAvailabilityEnum.AVAILABLE, "18 - Caesar Salad.jpg", "A Caesar salad is a green salad of romaine lettuce and croutons.");
            MenuItem mi19 = new MenuItem("MI019", "Chef Salad", 5.50, 10L, MenuItemAvailabilityEnum.AVAILABLE, "19 - Chef Salad.jpg", "Chef salad is an American salad consisting of hard-boiled eggs.");
            MenuItem mi20 = new MenuItem("MI020", "Coleslaw", 3.00, 10L, MenuItemAvailabilityEnum.AVAILABLE, "20 - Coleslaw.jpg", "A side dish consisting primarily of finely shredded raw cabbage with a salad dressing.");
            menuItemSessionBean.createNewMenuItem(mi18, mcId8);
            menuItemSessionBean.createNewMenuItem(mi19, mcId8);
            menuItemSessionBean.createNewMenuItem(mi20, mcId8);
            // Burgers
            MenuItem mi21 = new MenuItem("MI021", "Beef Burger", 8.50, 20L, MenuItemAvailabilityEnum.AVAILABLE, "21 - Beef Burger.jpg", "These stacked beef burgers are a BBQ favourite.");
            MenuItem mi22 = new MenuItem("MI022", "Portobello Mushroom Burger", 7.50, 20L, MenuItemAvailabilityEnum.AVAILABLE, "22 - Portobello Mushroom Burger.jpg", "These hamburgers cut through with roasted mushrooms.");
            MenuItem mi23 = new MenuItem("MI023", "Wild Salmon Burger", 10.00, 15L, MenuItemAvailabilityEnum.AVAILABLE, "23 - Wild Salmon Burger.jpg", "Freshest salmon in the sea packed in buns.");
            menuItemSessionBean.createNewMenuItem(mi21, mcId9);
            menuItemSessionBean.createNewMenuItem(mi22, mcId9);
            menuItemSessionBean.createNewMenuItem(mi23, mcId9);
            // Mains
            MenuItem mi24 = new MenuItem("MI024", "Chicken Chop", 8.00, 20L, MenuItemAvailabilityEnum.AVAILABLE, "24 - Chicken Chop.jpg", "Chicken chop is a dish that is prepared with boneless chicken meat.");
            MenuItem mi25 = new MenuItem("MI025", "Chicken Cutlet", 8.00, 20L, MenuItemAvailabilityEnum.AVAILABLE, "25 - Chicken Cutlet.jpg", "Chicken Cutlets is a Fusion recipe, which is loved by people of all age groups.");
            MenuItem mi26 = new MenuItem("MI026", "Fish n Chips", 7.50, 20L, MenuItemAvailabilityEnum.AVAILABLE, "26 - Fish n Chips.jpg", "Fish and chips is a hot dish consisting of fried fish in batter served with chips.");
            menuItemSessionBean.createNewMenuItem(mi24, mcId10);
            menuItemSessionBean.createNewMenuItem(mi25, mcId10);
            menuItemSessionBean.createNewMenuItem(mi26, mcId10);
            // Pizzas
            MenuItem mi27 = new MenuItem("MI027", "New York-Style Pizza", 15.00, 20L, MenuItemAvailabilityEnum.AVAILABLE, "27 - New York-Style Pizza.jpg", "New York-style pizza is pizza made with a characteristically large hand-tossed thin crust.");
            MenuItem mi28 = new MenuItem("MI028", "Chicken Alfredo Pizza", 16.00, 20L, MenuItemAvailabilityEnum.AVAILABLE, "28 - Chicken Alfredo Pizza.jpg", "This Delicious Chicken Alfredo Pizza is made with a homemade pizza crust.");
            MenuItem mi29 = new MenuItem("MI029", "Hawaiian Pizza", 14.00, 20L, MenuItemAvailabilityEnum.AVAILABLE, "29 - Hawaiian Pizza.jpg", "Classic Hawaiian Pizza combines pizza sauce, cheese, cooked ham, and pineapple.");
            menuItemSessionBean.createNewMenuItem(mi27, mcId11);
            menuItemSessionBean.createNewMenuItem(mi28, mcId11);
            menuItemSessionBean.createNewMenuItem(mi29, mcId11);
            // Cold
            MenuItem mi30 = new MenuItem("MI030", "Iced Latte", 3.50, 5L, MenuItemAvailabilityEnum.AVAILABLE, "30 - Iced Latte.jpg", "An iced latte is a simple and straight forward cold espresso based drink.");
            MenuItem mi31 = new MenuItem("MI031", "Iced Mixed Fruit Blended", 4.50, 7L, MenuItemAvailabilityEnum.AVAILABLE, "31 - Iced Mixed Fruit Blended.jpg", "This is a great smoothie consisting of strawberries, banana, peaches, fruit juice and ice.");
            MenuItem mi32 = new MenuItem("MI032", "Milkshake", 5.50, 7L, MenuItemAvailabilityEnum.AVAILABLE, "32 - Milkshake.jpg", "A milkshake made by blending milk, ice cream");
            menuItemSessionBean.createNewMenuItem(mi30, mcId12);
            menuItemSessionBean.createNewMenuItem(mi31, mcId12);
            menuItemSessionBean.createNewMenuItem(mi32, mcId12);
            // Hot
            MenuItem mi33 = new MenuItem("MI033", "Coffee", 1.50, 5L, MenuItemAvailabilityEnum.AVAILABLE, "33 - Coffee.jpg", "Locally produced coffee beans. Authentic.");
            MenuItem mi34 = new MenuItem("MI034", "Tea", 1.50, 5L, MenuItemAvailabilityEnum.AVAILABLE, "34 - Tea.jpg", "Locally produced tea leaves. Authentic.");
            MenuItem mi35 = new MenuItem("MI035", "Ginger Tea", 1.80, 5L, MenuItemAvailabilityEnum.AVAILABLE, "35 - Ginger Tea.jpg", "Homemade ginger tea concoction. Best Seller.");
            menuItemSessionBean.createNewMenuItem(mi33, mcId13);
            menuItemSessionBean.createNewMenuItem(mi34, mcId13);
            menuItemSessionBean.createNewMenuItem(mi35, mcId13);
            // Canned
            MenuItem mi36 = new MenuItem("MI036", "Coke", 1.50, 5L, MenuItemAvailabilityEnum.AVAILABLE, "36 - Coke.jpg", "355ml");
            MenuItem mi37 = new MenuItem("MI037", "Sprite", 1.50, 5L, MenuItemAvailabilityEnum.AVAILABLE, "37 - Sprite.jpg", "355ml");
            MenuItem mi38 = new MenuItem("MI038", "Root Beer", 1.50, 5L, MenuItemAvailabilityEnum.AVAILABLE, "38 - Root Beer.jpg", "355ml");
            menuItemSessionBean.createNewMenuItem(mi36, mcId14);
            menuItemSessionBean.createNewMenuItem(mi37, mcId14);
            menuItemSessionBean.createNewMenuItem(mi38, mcId14);

            Date startDate = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
            Date today = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

            while (startDate.before(today)) {
                int numOfTransForDay = (int) (Math.random() * 15) + 5;
                int openingHours = 12;
                int openingTime = 10;
                for (int i = 0; i < numOfTransForDay; i++) {

                    Long custId = (long) (Math.random() * totalNumOfCustomers) + 1L;
                    Integer numOfOrdersPerTrans = (int) (Math.random() * 5) + 1;

                    List<CustomerOrder> customerOrders = new ArrayList<>();
                    double totalTransAmt = 0.0;
                    int randomHour = (int) (Math.random() * openingHours) + openingTime;
                    int mins = 0;
                    for (int j = 0; j < numOfOrdersPerTrans; j++) {
                        mins += 5;
                        startDate.setHours(randomHour);
                        startDate.setMinutes(mins);
                        CustomerOrder newOrder = customerOrderRandomiser(startDate, custId);
                        customerOrders.add(newOrder);
                        totalTransAmt = Math.round((totalTransAmt + newOrder.getTotalAmount()) * 100.0) / 100.0;
                    }
                    //CREATE TRANSACTION
                    String paymentType = Math.random() <= 0.5 ? "Credit Card" : "Cash";
                    double gst = totalTransAmt - (totalTransAmt / 1.07);
                    gst = Math.round(gst * 100.0) / 100.0;
                    PaymentTransaction newPaymentTransaction = new PaymentTransaction(paymentType, gst, totalTransAmt);
                    newPaymentTransaction.setCustomerOrders(customerOrders);
                    startDate.setMinutes(0);
                    startDate.setHours(++randomHour);
                    newPaymentTransaction.setTransactionDate(startDate);
                    if (paymentType.equals("Cash")) {
                        long employeeIdRand = (long) (Math.random() * 8) + 1L;
                        Employee employee = employeeSessionBeanLocal.retrieveEmployeeById(employeeIdRand);
                        newPaymentTransaction.setEmployee(employee);
                    }
                    try {
                        this.paymentTransactionSessionBean.createNewPaymentTransactionByCustomer(newPaymentTransaction);
                    } catch (Exception ex) {

                    }

                }
                Calendar c = Calendar.getInstance();
                c.setTime(startDate);
                c.add(Calendar.DATE, 1);
                startDate = c.getTime();
            }

            Customer customer1 = customerSessionBeanLocal.retrieveCustomerById(1L);
            DiningTable diningTable1 = diningTableSessionBean.retrieveDiningTableById(2L);
            diningTable1.setSeatedTime(new Date(Calendar.getInstance(TimeZone.getDefault().getTimeZone("GMT+8:00")).getTimeInMillis()));
            diningTable1.setCustomer(customer1);
            diningTable1.setTableStatus(TableStatusEnum.UNFROZEN_OCCUPIED);

            Thread.sleep(1500);

            Customer customer2 = customerSessionBeanLocal.retrieveCustomerById(2L);
            DiningTable diningTable2 = diningTableSessionBean.retrieveDiningTableById(5L);
            diningTable2.setSeatedTime(new Date(Calendar.getInstance(TimeZone.getDefault().getTimeZone("GMT+8:00")).getTimeInMillis()));
            diningTable2.setCustomer(customer2);
            diningTable2.setTableStatus(TableStatusEnum.UNFROZEN_OCCUPIED);

            Thread.sleep(1500);

            for (int i = 0; i < 10; i++) {
                Boolean orderIsCancelled = true;

                List<OrderLineItem> orderLineItems = new ArrayList<>();

                Integer numItems = (int) (Math.random() * 3) + 1;

                for (int x = 0; numItems > x; x++) {
                    Long quantity = (long) (Math.random() * 5) + 1L;
                    Long menuItemId = (long) (Math.random() * 38) + 1L;

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

                Customer customer = customerSessionBeanLocal.retrieveCustomerById((long) (Math.random() * 2) + 1L);

                customerOrderSessionBean.createCustomerOrder(customerOrder, customer.getCustomerId(), orderLineItems);
            }

        } catch (EmployeeUsernameExistException | CustomerNotUniqueException | InputDataValidationException
                | UnknownPersistenceException | CreateNewMenuCategoryException | CreateNewMenuItemException
                | MenuItemNotUniqueException | CreateNewOrderLineItemException | OrderLineItemNotFoundException
                | CreateNewCustomerOrderException | MenuItemUnavailableException | UnableToCreateNotificationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Data initialization complete!!");
    }

    private CustomerOrder customerOrderRandomiser(Date date, Long custId) throws Exception {
        List<OrderLineItem> orderLineItems = orderLineItemsRandomiser();
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setTotalAmount(calculateTotalPrice(orderLineItems));
        customerOrder.setStatus(OrderStatusEnum.PAID);
        customerOrder.setIsCompleted(true);
        customerOrder.setOrderDate(date);

        customerOrderSessionBean.createCustomerOrder(customerOrder, custId, orderLineItems);
        return customerOrder;
    }

    private List<OrderLineItem> orderLineItemsRandomiser() throws Exception {
        Integer numOfItems = (int) (Math.random() * 5) + 1;
        List<OrderLineItem> orderLineItems = new ArrayList<>();

        for (int i = 0; i < numOfItems; i++) {
            Long quantity = (long) (Math.random() * 5) + 1L;
            Long menuItemId = (long) (Math.random() * totalNumOfMenuItems) + 1L;

            OrderLineItem newOli = new OrderLineItem(quantity, enhancedRemarksRandomiser(menuItemId), OrderLineItemStatusEnum.SERVED);
            orderLineItemSessionBean.createNewOrderLineItem(newOli, menuItemId);
            orderLineItems.add(newOli);
        }

        return orderLineItems;
    }

    private String enhancedRemarksRandomiser(Long menuItemId) {
        Integer remarksRandomiser = (int) (Math.random() * 6);

        if (menuItemId < 30) {
            // Food
            switch (remarksRandomiser) {
                case 0:
                    return "Less salty please";
                case 1:
                    return "More spicy please";
                case 2:
                    return "Less spicy please";
                case 3:
                    return "Less sweet";
                case 4:
                    return "In a rush, will need it to be served faster!";
                default:
                    return null;
            }
        } else {
            // Drinks
            switch (remarksRandomiser) {
                case 0:
                    return "Less ice";
                case 1:
                    return "More ice please";
                case 2:
                    return "Less sweet";
                case 3:
                    return "More sweet please";
                case 4:
                    return "Will need a straw";
                default:
                    return null;
            }
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
        Double totalPrice = 0.0;
        for (OrderLineItem oli : orderLineItems) {
            if (oli.getStatus() != OrderLineItemStatusEnum.CANCELLED) {
                Long qty = oli.getQuantity();
                Double price = oli.getMenuItem().getMenuItemPrice();
                Double sum = Math.round((qty * price) * 100.0) / 100.0;
                totalPrice += sum;
            }
        }
        return Math.round((totalPrice) * 100.0) / 100.0;
    }

}
