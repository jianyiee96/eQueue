package jsf.managedBean;

import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import ejb.session.stateless.OrderLineItemSessionBeanLocal;
import entity.CustomerOrder;
import entity.OrderLineItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.enumeration.OrderLineItemStatusEnum;
import util.exceptions.InputDataValidationException;
import util.exceptions.OrderLineItemNotFoundException;
import util.exceptions.UpdateOrderLineItemException;

@Named(value = "kitchenManagementManagedBean")
@ApplicationScoped
public class KitchenManagementManagedBean implements Serializable {

    @EJB(name = "CustomerOrderSessionBeanLocal")
    private CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal;
    @EJB(name = "OrderLineItemSessionBeanLocal")
    private OrderLineItemSessionBeanLocal orderLineItemSessionBeanLocal;

    private List<CustomerOrder> ongoingCustomerOrders;
    private List<CustomerOrder> filteredCustomerOrders;

    private OrderLineItem orderItemToPrepare;
    private OrderLineItem orderItemToServe;

    public KitchenManagementManagedBean() {
        orderItemToPrepare = new OrderLineItem();
        orderItemToServe = new OrderLineItem();
    }

    @PostConstruct
    public void postConstruct() {
    }

    public void filterOrderLineItems() {
        ongoingCustomerOrders = customerOrderSessionBeanLocal.retrieveOngoingOrders();
        filteredCustomerOrders = customerOrderSessionBeanLocal.retrieveOngoingOrders();

        //        for (CustomerOrder o : ongoingCustomerOrders) {
        //            System.out.println("========== Customer Order " + o.getOrderId() + " ==========");
        //            for (OrderLineItem i : o.getOrderLineItems()) {
        //                System.out.println("       ID - " + i.getOrderLineItemId());
        //                System.out.println("Menu Item - " + i.getMenuItem().getMenuItemName());
        //                System.out.println(" Quantity - " + i.getQuantity());
        //                System.out.println("   Status - " + i.getStatus());
        //                System.out.println("-------------------------------");
        //            }
        //        }
        for (int i = 0; ongoingCustomerOrders.size() > i; i++) {
            List<OrderLineItem> items = ongoingCustomerOrders.get(i).getOrderLineItems();
            List<OrderLineItem> filteredItems = filteredCustomerOrders.get(i).getOrderLineItems();
//            System.out.println("=== filteredCustomerOrder --> " + filteredCustomerOrders.get(i));
            for (int j = 0; items.size() > j; j++) {
//                System.out.println("OrderLineItem --> " + items.get(j).getOrderLineItemId() + " (" + items.get(j).getStatus() + ")");
//                System.out.println(items.get(j).getStatus() != OrderLineItemStatusEnum.ORDERED && items.get(j).getStatus() != OrderLineItemStatusEnum.PREPARING);
                if (items.get(j).getStatus() != OrderLineItemStatusEnum.ORDERED && items.get(j).getStatus() != OrderLineItemStatusEnum.PREPARING) {
                    filteredItems.remove(items.get(j));
                }
            }
            Collections.sort(filteredItems, (i1, i2) -> i1.compareTo(i2));
        }
    }

    public void prepare(ActionEvent event) {
        orderItemToPrepare = (OrderLineItem) event.getComponent().getAttributes().get("orderItemToPrepare");

        orderItemToPrepare.setStatus(OrderLineItemStatusEnum.PREPARING);

        try {
            orderLineItemSessionBeanLocal.updateOrderLineItemByEmployee(orderItemToPrepare);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, orderItemToPrepare.getMenuItem().getMenuItemName() + " is being prepared.", null));

            orderItemToPrepare = new OrderLineItem();
        } catch (OrderLineItemNotFoundException | UpdateOrderLineItemException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating order line item: ", ex.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }

//        System.out.println("orderItemToPrepare --> " + orderItemToPrepare.getMenuItem().getMenuItemName() + " with quantity "+ orderItemToPrepare.getQuantity());
    }

    public void serve(ActionEvent event) {
        orderItemToServe = (OrderLineItem) event.getComponent().getAttributes().get("orderItemToServe");

        orderItemToServe.setStatus(OrderLineItemStatusEnum.SERVED);

        try {
            orderLineItemSessionBeanLocal.updateOrderLineItemByEmployee(orderItemToServe);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, orderItemToServe.getMenuItem().getMenuItemName() + " is being served.", null));

            orderItemToServe = new OrderLineItem();
        } catch (OrderLineItemNotFoundException | UpdateOrderLineItemException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating order line item: ", ex.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
//        System.out.println("  orderItemToServe --> " + orderItemToServe.getMenuItem().getMenuItemName() + " with quantity "+ orderItemToServe.getQuantity());
    }

    public List<CustomerOrder> getOngoingCustomerOrders() {
        return ongoingCustomerOrders;
    }

    public void setOngoingCustomerOrders(List<CustomerOrder> ongoingCustomerOrders) {
        this.ongoingCustomerOrders = ongoingCustomerOrders;
    }

    public List<CustomerOrder> getFilteredCustomerOrders() {
        filterOrderLineItems();
        return filteredCustomerOrders;
    }

    public void setFilteredCustomerOrders(List<CustomerOrder> filteredCustomerOrders) {
        this.filteredCustomerOrders = filteredCustomerOrders;
    }

    public OrderLineItem getOrderItemToPrepare() {
        return orderItemToPrepare;
    }

    public void setOrderItemToPrepare(OrderLineItem orderItemToPrepare) {
        this.orderItemToPrepare = orderItemToPrepare;
    }

    public OrderLineItem getOrderItemToServe() {
        return orderItemToServe;
    }

    public void setOrderItemToServe(OrderLineItem orderItemToServe) {
        this.orderItemToServe = orderItemToServe;
    }

}
