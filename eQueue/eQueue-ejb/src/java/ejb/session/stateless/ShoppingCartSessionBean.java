package ejb.session.stateless;

import entity.Customer;
import entity.MenuItem;
import entity.OrderLineItem;
import entity.Queue;
import entity.ShoppingCart;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.OrderLineItemStatusEnum;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.MenuItemNotFoundException;
import util.exceptions.ShoppingCartNotFoundException;

@Stateless
public class ShoppingCartSessionBean implements ShoppingCartSessionBeanLocal {

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    MenuItemSessionBeanLocal menuItemSessionBeanLocal;

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
    public ShoppingCart retrieveShoppingCartByCustomerId(Long customerId) {

        try {
            ShoppingCart s = customerSessionBeanLocal.retrieveCustomerById(customerId).getShoppingCart();

            return s;
        } catch (CustomerNotFoundException ex) {
            return null;
        }
    }

    @Override
    public void saveShoppingCart(Long customerId, ShoppingCart shoppingCart) throws CustomerNotFoundException, MenuItemNotFoundException {

        Customer c = customerSessionBeanLocal.retrieveCustomerById(customerId);
        ShoppingCart cart = c.getShoppingCart();

        cart.getOrderLineItems().forEach(o -> em.remove(o));

        cart.setOrderLineItems(new ArrayList<>());
        em.flush();

        cart.setTotalAmount(shoppingCart.getTotalAmount());

        for (OrderLineItem o : shoppingCart.getOrderLineItems()) {

            OrderLineItem newOrderLineItem = new OrderLineItem(o.getQuantity(), o.getRemarks(), OrderLineItemStatusEnum.IN_CART);
            MenuItem m = menuItemSessionBeanLocal.retrieveMenuItemById(o.getMenuItem().getMenuItemId());
            newOrderLineItem.setMenuItem(m);
            cart.getOrderLineItems().add(newOrderLineItem);
            em.persist(newOrderLineItem);
        }
        em.persist(cart);
        em.flush();

    }
}
