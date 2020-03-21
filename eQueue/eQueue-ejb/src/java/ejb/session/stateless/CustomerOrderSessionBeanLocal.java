package ejb.session.stateless;

import entity.CustomerOrder;
import entity.OrderLineItem;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateNewCustomerOrderException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface CustomerOrderSessionBeanLocal {

    public Long createCustomerOrder(CustomerOrder newCustomerOrder, Long customerId, List<OrderLineItem> orderLineItems) throws InputDataValidationException, CreateNewCustomerOrderException, UnknownPersistenceException;
    
    
}
