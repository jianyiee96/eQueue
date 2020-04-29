package ejb.session.stateless;

import entity.OrderLineItem;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateNewOrderLineItemException;
import util.exceptions.DeleteOrderLineItemException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuItemUnavailableException;
import util.exceptions.OrderLineItemNotFoundException;
import util.exceptions.OrderStateMismatchException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateOrderLineItemException;

@Local
public interface OrderLineItemSessionBeanLocal {

    public OrderLineItem retrieveOrderLineItemById(Long orderLineItemId) throws OrderLineItemNotFoundException;

    public Long createNewOrderLineItem(OrderLineItem newOrderLineItem, Long menuItemId) throws UnknownPersistenceException, InputDataValidationException, CreateNewOrderLineItemException, MenuItemUnavailableException;

    public void updateOrderLineItemByCustomer(OrderLineItem orderLineItem) throws OrderLineItemNotFoundException, UpdateOrderLineItemException, InputDataValidationException;

    public void updateOrderLineItemByEmployee(OrderLineItem orderLineItem) throws OrderLineItemNotFoundException, UpdateOrderLineItemException, InputDataValidationException;

    public void deleteOrderLineItem(Long orderLineItemId) throws OrderLineItemNotFoundException, DeleteOrderLineItemException;
    
    public void cancelOrderLineItem(Long orderLineItemId) throws OrderLineItemNotFoundException, OrderStateMismatchException;

    public List<OrderLineItem> retrieveOrderLineItemsByMenuItemId(Long menuItemId);
    
}
