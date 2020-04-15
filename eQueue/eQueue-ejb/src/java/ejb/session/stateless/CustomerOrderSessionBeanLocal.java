package ejb.session.stateless;

import entity.CustomerOrder;
import entity.OrderLineItem;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateNewCustomerOrderException;
import util.exceptions.CreateNewOrderLineItemException;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.CustomerOrderNotFoundException;
import util.exceptions.EmptyCartException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuItemNotFoundException;
import util.exceptions.OrderLineItemNotFoundException;
import util.exceptions.PriceMismatchException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface CustomerOrderSessionBeanLocal {

    public Long createCustomerOrder(CustomerOrder newCustomerOrder, Long customerId, List<OrderLineItem> orderLineItems) throws InputDataValidationException, CreateNewCustomerOrderException, UnknownPersistenceException;

    public List<CustomerOrder> retrieveIncompleteOrders();

    public void updateCustomerOrder(CustomerOrder customerOrder) throws CustomerOrderNotFoundException, InputDataValidationException;

    public CustomerOrder retrieveCustomerOrderById(Long customerOrderId) throws CustomerOrderNotFoundException;
    
    public List<CustomerOrder> retrieveAllCustomerOrdersByCustomerId(Long customerId);
    
    public void processOrderFromCart(Long customerId) throws CustomerNotFoundException, EmptyCartException, MenuItemNotFoundException, InputDataValidationException, CreateNewCustomerOrderException, UnknownPersistenceException, CreateNewOrderLineItemException, OrderLineItemNotFoundException, PriceMismatchException;

    public void recalculateTotalAmount(Long customerOrderId);
    
    public void updateOrderStatus(Long customerOrderId);
}
