package ejb.session.stateless;

import entity.Customer;
import entity.ShoppingCart;
import javax.ejb.Local;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.MenuItemNotFoundException;
import util.exceptions.ShoppingCartNotFoundException;

@Local
public interface ShoppingCartSessionBeanLocal {

    public Long createNewShoppingCart(Customer customer);

    public ShoppingCart retrieveShoppingCartById(Long shoppingCartId) throws ShoppingCartNotFoundException;
    
    public ShoppingCart retrieveShoppingCartByCustomerId(Long customerId);
    
    public void saveShoppingCart(Long customerId, ShoppingCart shoppingCart) throws CustomerNotFoundException, MenuItemNotFoundException;
    
}
