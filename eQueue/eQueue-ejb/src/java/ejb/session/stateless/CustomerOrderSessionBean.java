package ejb.session.stateless;

import entity.Customer;
import entity.CustomerOrder;
import entity.MenuItem;
import entity.OrderLineItem;
import entity.ShoppingCart;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.OrderLineItemStatusEnum;
import util.enumeration.OrderStatusEnum;
import util.exceptions.CreateNewCustomerOrderException;
import util.exceptions.CreateNewOrderLineItemException;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.CustomerOrderNotFoundException;
import util.exceptions.EmptyCartException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuItemNotFoundException;
import util.exceptions.MenuItemUnavailableException;
import util.exceptions.OrderLineItemNotFoundException;
import util.exceptions.PriceMismatchException;
import util.exceptions.UnknownPersistenceException;

@Stateless
public class CustomerOrderSessionBean implements CustomerOrderSessionBeanLocal {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    private OrderLineItemSessionBeanLocal orderLineItemSessionBeanLocal;
    @EJB
    private ShoppingCartSessionBeanLocal shoppingCartSessionBeanLocal;

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CustomerOrderSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    // Call this at shopping cart checkout [TO DISSASSOCIATE SHOPPING CART AND ORDERLINEITEMS THERE]
    @Override
    public Long createCustomerOrder(CustomerOrder newCustomerOrder, Long customerId, List<OrderLineItem> orderLineItems) throws InputDataValidationException, CreateNewCustomerOrderException, UnknownPersistenceException {
        Set<ConstraintViolation<CustomerOrder>> constraintViolations = validator.validate(newCustomerOrder);
        if (constraintViolations.isEmpty()) {
            try {
                if (customerId == null) {
                    throw new CreateNewCustomerOrderException("The new customer order must be associated with a customer");
                }
                Customer customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
                List<OrderLineItem> newOrderLineItems = new ArrayList<>();
                for (OrderLineItem oli : orderLineItems) {
                    OrderLineItem newOrderLineItem = orderLineItemSessionBeanLocal.retrieveOrderLineItemById(oli.getOrderLineItemId());
                    newOrderLineItems.add(newOrderLineItem);
                }

                em.persist(newCustomerOrder);
                newCustomerOrder.setCustomer(customer);
                newCustomerOrder.setOrderLineItems(newOrderLineItems);

                em.flush();
                return newCustomerOrder.getOrderId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CreateNewCustomerOrderException("Customer order not unique");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (CustomerNotFoundException | OrderLineItemNotFoundException ex) {
                throw new CreateNewCustomerOrderException("An error has occured while creating the new customer order: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<CustomerOrder> retrieveIncompleteOrders() {
//        Date currentDay = new Date();
//        currentDay.setHours(0);
//        currentDay.setMinutes(0);
//        currentDay.setSeconds(0);
        Query query = em.createQuery(
                "SELECT DISTINCT o "
                + "FROM CustomerOrder o "
                + "JOIN o.orderLineItems li "
                //                + "WHERE o.orderDate > :today "
                + "WHERE o.isCompleted = false "
                + "AND li.status <> util.enumeration.OrderLineItemStatusEnum.IN_CART "
                + "ORDER BY o.orderDate ASC, o.orderId ASC"
        );
//        query.setParameter("today", currentDay, TemporalType.TIMESTAMP);

        List<CustomerOrder> currentDayOrders = query.getResultList();

//        for (CustomerOrder o : currentDayOrders) {
//            System.out.println("============================");
//            System.out.println("Order ID   - " + o.getOrderId());
//            System.out.println("Order Date - " + o.getOrderDate());
//            System.out.println("============================");
//            o.getOrderLineItems().size();
//            for (OrderLineItem li : o.getOrderLineItems()) {
//                System.out.println("ID - " + li.getOrderLineItemId() + " ||| Name - " + li.getMenuItem().getMenuItemName() + " ||| Status - " + li.getStatus());
//            }
//            System.out.println();
//        }
        return currentDayOrders;
    }

    @Override
    public CustomerOrder retrieveCustomerOrderById(Long customerOrderId) throws CustomerOrderNotFoundException {
        CustomerOrder customerOrder = em.find(CustomerOrder.class, customerOrderId);

        if (customerOrder != null) {
            return customerOrder;
        } else {
            throw new CustomerOrderNotFoundException("Customer Order ID " + customerOrderId + " does not exist!");
        }
    }

    @Override
    public List<CustomerOrder> retrieveAllCustomerOrdersByCustomerId(Long customerId) {

        try {
            Customer c = customerSessionBeanLocal.retrieveCustomerById(customerId);
            List<CustomerOrder> orders = c.getCustomerOrders();
            orders.forEach(o -> o.getOrderLineItems().size());

            em.detach(c);
            return orders;
        } catch (CustomerNotFoundException ex) {
            return new ArrayList<>();
        }

    }

    @Override
    public void updateCustomerOrder(CustomerOrder customerOrder) throws CustomerOrderNotFoundException, InputDataValidationException {
        if (customerOrder != null && customerOrder.getOrderId() != null) {
            Set<ConstraintViolation<CustomerOrder>> constraintViolations = validator.validate(customerOrder);
            if (constraintViolations.isEmpty()) {
                CustomerOrder customerOrderToUpdate = retrieveCustomerOrderById(customerOrder.getOrderId());

                customerOrderToUpdate.setIsCompleted(customerOrder.getIsCompleted());
                customerOrderToUpdate.setStatus(customerOrder.getStatus());
                customerOrderToUpdate.setTotalAmount(customerOrder.getTotalAmount());
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new CustomerOrderNotFoundException("Customer Order ID not provided for customer order to be updated!");
        }
    }

    //can rename as checkoutFromCart if u guys want OWO/
    @Override
    public void processOrderFromCart(Long customerId) throws CustomerNotFoundException, EmptyCartException, MenuItemNotFoundException, InputDataValidationException, CreateNewCustomerOrderException, UnknownPersistenceException, CreateNewOrderLineItemException, OrderLineItemNotFoundException, PriceMismatchException, MenuItemUnavailableException {

        Customer customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
        ShoppingCart cart = customer.getShoppingCart();

        if (cart.getOrderLineItems().isEmpty()) {
            throw new EmptyCartException();
        }

        List<OrderLineItem> orderLineItems = new ArrayList<>();

        for (OrderLineItem cartItem : cart.getOrderLineItems()) {

            Long oliId = orderLineItemSessionBeanLocal.createNewOrderLineItem(new OrderLineItem(cartItem.getQuantity(), cartItem.getRemarks(), OrderLineItemStatusEnum.ORDERED), cartItem.getMenuItem().getMenuItemId());
            OrderLineItem oli = orderLineItemSessionBeanLocal.retrieveOrderLineItemById(oliId);
            orderLineItems.add(oli);

        }

        //totalPrice validation
        if (calculateTotalPrice(orderLineItems).doubleValue() != cart.getTotalAmount().doubleValue()) {
            throw new PriceMismatchException("Price from client mismatch: " + calculateTotalPrice(orderLineItems) + " != " + cart.getTotalAmount());
        }

        CustomerOrder newOrder = new CustomerOrder();
        newOrder.setTotalAmount(cart.getTotalAmount());
        createCustomerOrder(newOrder, customerId, orderLineItems);

        shoppingCartSessionBeanLocal.saveShoppingCart(customerId, new ShoppingCart());

    }

    @Override
    public void recalculateTotalAmount(Long customerOrderId) {
        try {
            CustomerOrder customerOrder = retrieveCustomerOrderById(customerOrderId);

            customerOrder.setTotalAmount(calculateTotalPrice(customerOrder.getOrderLineItems()));

        } catch (CustomerOrderNotFoundException ex) {

        }
    }

    @Override
    public void updateOrderStatus(Long customerOrderId) {
        try {
            CustomerOrder customerOrder = retrieveCustomerOrderById(customerOrderId);
            boolean toggle = false;
            for (OrderLineItem o : customerOrder.getOrderLineItems()) {
                if (o.getStatus() != OrderLineItemStatusEnum.CANCELLED) {
                    toggle = true;
                }
            }

            if (!toggle) {
                customerOrder.setStatus(OrderStatusEnum.CANCELLED);
                customerOrder.setIsCompleted(true);
            }

        } catch (CustomerOrderNotFoundException ex) {

        }
    }

    private Double calculateTotalPrice(List<OrderLineItem> orderLineItems) {
        double totalPrice = 0.0;
        for (OrderLineItem oli : orderLineItems) {
            if (oli.getStatus() != OrderLineItemStatusEnum.CANCELLED) {
                totalPrice += oli.getQuantity() * oli.getMenuItem().getMenuItemPrice();
            }
        }
        return totalPrice;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CustomerOrder>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
