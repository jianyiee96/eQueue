package ejb.session.stateless;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLineItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.CreateNewCustomerOrderException;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.OrderLineItemNotFoundException;
import util.exceptions.UnknownPersistenceException;

@Stateless
public class CustomerOrderSessionBean implements CustomerOrderSessionBeanLocal {

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;

    @EJB
    private OrderLineItemSessionBeanLocal orderLineItemSessionBean;

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
                Customer customer = customerSessionBean.retrieveCustomerById(customerId);
                List<OrderLineItem> newOrderLineItems = new ArrayList<>();
                for (OrderLineItem oli : orderLineItems) {
                    OrderLineItem newOrderLineItem = orderLineItemSessionBean.retrieveOrderLineItemById(oli.getOrderLineItemId());
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
    public List<CustomerOrder> retrieveOngoingOrders() {
        Query query = em.createQuery(
                "SELECT DISTINCT o " + 
                "FROM CustomerOrder o " +
                "JOIN o.orderLineItems li " +
                "WHERE li.status = util.enumeration.OrderLineItemStatusEnum.ORDERED " +
                "OR li.status = util.enumeration.OrderLineItemStatusEnum.PREPARING " + 
                "ORDER BY o.orderDate ASC, o.orderId ASC"
        );

        List<CustomerOrder> ongoingOrders = query.getResultList();
        
        for (CustomerOrder o : ongoingOrders) {
            o.getOrderLineItems().size();
        }
        
        return ongoingOrders;
    }
    
    @Override
    public List<CustomerOrder> retrieveOrdersWithOrderedLineItems() {
        Query query = em.createQuery(
                "SELECT DISTINCT o " + 
                "FROM CustomerOrder o " +
                "JOIN o.orderLineItems li " +
                "WHERE li.status = util.enumeration.OrderLineItemStatusEnum.ORDERED " +
                "ORDER BY o.orderDate ASC, o.orderId ASC"
        );
        
        List<CustomerOrder> ordersWithOrderedLineItems = query.getResultList();
        
        for (CustomerOrder o : ordersWithOrderedLineItems) {
            o.getOrderLineItems().size();
        }    
    
        return ordersWithOrderedLineItems;
    }
    
    @Override
    public List<CustomerOrder> retrieveOrdersWithPreparingLineItems() {
        Query query = em.createQuery(
                "SELECT DISTINCT o " + 
                "FROM CustomerOrder o " +
                "JOIN o.orderLineItems li " +
                "WHERE li.status = util.enumeration.OrderLineItemStatusEnum.PREPARING " +
                "ORDER BY o.orderDate ASC, o.orderId ASC"
        );
        
        List<CustomerOrder> ordersWithPreparingLineItems = query.getResultList();
        
        for (CustomerOrder o : ordersWithPreparingLineItems) {
            o.getOrderLineItems().size();
        }
        
        return ordersWithPreparingLineItems;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CustomerOrder>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
