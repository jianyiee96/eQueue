package ejb.session.stateless;

import entity.Customer;
import entity.ShoppingCart;
import javax.ejb.Local;
import util.exceptions.ShoppingCartNotFoundException;

@Local
public interface ShoppingCartSessionBeanLocal {

    public Long createNewShoppingCart(Customer customer);

    public ShoppingCart retrieveShoppingCartById(Long shoppingCartId) throws ShoppingCartNotFoundException;
    
    public ShoppingCart retrieveShoppingCartByCustomerId(Long customerId);
    
}
