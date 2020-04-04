package jsf.managedBean;

import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import ejb.session.stateless.OrderLineItemSessionBeanLocal;
import entity.CustomerOrder;
import entity.OrderLineItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.enumeration.OrderLineItemStatusEnum;
import util.exceptions.CustomerOrderNotFoundException;
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

    private List<CustomerOrder> currentDayCustomerOrders;
//    private List<CustomerOrder> filteredCustomerOrders;

    private OrderLineItem orderItemToView;
    private OrderLineItem orderItemToPrepare;
    private OrderLineItem orderItemToServe;

    private CustomerOrder orderToComplete;

    public KitchenManagementManagedBean() {
        orderItemToPrepare = new OrderLineItem();
        orderItemToServe = new OrderLineItem();
    }

    // To sort the current day order line items
    public void sortCurrentDayOrderLineItems() {
        currentDayCustomerOrders = customerOrderSessionBeanLocal.retrieveCurrentDayOrders();
        
        for (int i = 0; currentDayCustomerOrders.size() > i; i++) {
            List<OrderLineItem> items = currentDayCustomerOrders.get(i).getOrderLineItems();
            Collections.sort(items, (i1, i2) -> i1.compareTo(i2));
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
    }

    public void completeOrder(ActionEvent event) {
        orderToComplete = (CustomerOrder) event.getComponent().getAttributes().get("orderToComplete");
        orderToComplete.setIsCompleted(Boolean.TRUE);
        try {
            customerOrderSessionBeanLocal.updateCustomerOrder(orderToComplete);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, orderToComplete.getCustomer().getFirstName() + "'s order (ID - " + orderToComplete.getOrderId() + ") is completed!", null));
            orderToComplete = new CustomerOrder();
        } catch (CustomerOrderNotFoundException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating customer order: ", ex.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public List<CustomerOrder> getCurrentDayCustomerOrders() {
        sortCurrentDayOrderLineItems();
        return currentDayCustomerOrders;
    }

    public void setCurrentDayCustomerOrders(List<CustomerOrder> currentDayCustomerOrders) {
        this.currentDayCustomerOrders = currentDayCustomerOrders;
    }

    public OrderLineItem getOrderItemToView() {
        return orderItemToView;
    }

    public void setOrderItemToView(OrderLineItem orderItemToView) {
        this.orderItemToView = orderItemToView;
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

    public CustomerOrder getOrderToComplete() {
        return orderToComplete;
    }

    public void setOrderToComplete(CustomerOrder orderToComplete) {
        this.orderToComplete = orderToComplete;
    }

}
