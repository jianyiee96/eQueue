package ejb.session.stateless;

import entity.Customer;
import entity.OrderLineItem;
import entity.ShoppingCart;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exceptions.ShoppingCartNotFoundException;

@Stateless
public class ShoppingCartSessionBean implements ShoppingCartSessionBeanLocal {

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    public ShoppingCartSessionBean() {
    }
    
    @Override
    public Long createNewShoppingCart(Customer customer) {
        if (customer.getShoppingCart() == null) {
            ShoppingCart newShoppingCart = new ShoppingCart();
            em.persist(newShoppingCart);
            em.flush();

            customer.setShoppingCart(newShoppingCart);
            return newShoppingCart.getShoppingCartId();
        } else {
            return customer.getShoppingCart().getShoppingCartId();
        }
    }

    @Override
    public ShoppingCart retrieveShoppingCartById(Long shoppingCartId) throws ShoppingCartNotFoundException {
        ShoppingCart shoppingCart = em.find(ShoppingCart.class, shoppingCartId);
        
        shoppingCart.getOrderLineItems().size();
        shoppingCart.getOrderLineItems().forEach(x -> x.getMenuItem());
        
        if (shoppingCart != null) {
            return shoppingCart;
        } else {
            throw new ShoppingCartNotFoundException("Shopping Cart ID " + shoppingCartId + " does not exist!");
        }
    }
    
    @Override
    public void updateShoppingCart(ShoppingCart shoppingCart) throws ShoppingCartNotFoundException {
        ShoppingCart shoppingCartToUpdate = retrieveShoppingCartById(shoppingCart.getShoppingCartId());
        List<OrderLineItem> orderLineItemsToUpdate = shoppingCartToUpdate.getOrderLineItems();
        
        
    }
}
