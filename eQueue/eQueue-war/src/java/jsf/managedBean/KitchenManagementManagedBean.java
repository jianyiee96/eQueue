package jsf.managedBean;

import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import ejb.session.stateless.OrderLineItemSessionBeanLocal;
import entity.CustomerOrder;
import entity.MenuItem;
import entity.OrderLineItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
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
    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;
    @Inject
    private ViewMenuItemManagedBean viewMenuItemManagedBean;

    private List<CustomerOrder> currentDayCustomerOrders;
//    private List<CustomerOrder> filteredCustomerOrders;
    private List<Map.Entry<MenuItem, Integer>> menuItemsOverview;
    private Map.Entry<MenuItem, Integer> menuItemToViewEntry;

    private OrderLineItem selectedOrderItem;
    private OrderLineItem orderItemToPrepare;
    private OrderLineItem orderItemToServe;

    private Integer numOrdered;
    private Integer numPreparing;

    private CustomerOrder orderToComplete;

    public KitchenManagementManagedBean() {
        orderItemToPrepare = new OrderLineItem();
        orderItemToServe = new OrderLineItem();
    }

    @PostConstruct
    public void postConstruct() {
        currentDayCustomerOrders = customerOrderSessionBeanLocal.retrieveCurrentDayOrders();
        sortAllCurrentDayOrderLineItems();

        createMenuItemsOverview();
    }

    // To sort the current day order line items
    public void sortAllCurrentDayOrderLineItems() {
        currentDayCustomerOrders.forEach(order -> {
            sortOrderLineItems(order);
        });
    }

    public void sortOrderLineItems(CustomerOrder order) {
        Collections.sort(order.getOrderLineItems(), (i1, i2) -> i1.compareTo(i2));
    }

    public void createMenuItemsOverview() {
        numOrdered = 0;
        numPreparing = 0;
        Map<MenuItem, Integer> map = new HashMap<>();
        for (CustomerOrder customerOrder : currentDayCustomerOrders) {
            for (OrderLineItem orderLineItem : customerOrder.getOrderLineItems()) {
                OrderLineItemStatusEnum status = orderLineItem.getStatus();
                if (status == OrderLineItemStatusEnum.ORDERED || status == OrderLineItemStatusEnum.PREPARING) {
                    MenuItem menuItem = orderLineItem.getMenuItem();
                    int count = map.containsKey(menuItem) ? map.get(menuItem) : 0;
                    map.put(menuItem, count + Math.toIntExact(orderLineItem.getQuantity()));
                }
                if (status == OrderLineItemStatusEnum.ORDERED) {
                    numOrdered++;
                } else if (status == OrderLineItemStatusEnum.PREPARING) {
                    numPreparing++;
                }
            }
        }
        menuItemsOverview = new ArrayList<>(map.entrySet());
    }

    private void reduceMenuItemOverview(OrderLineItem orderLineItem) {
        for (Map.Entry<MenuItem, Integer> entry : menuItemsOverview) {
            if (entry.getKey() == orderLineItem.getMenuItem()) {
                entry.setValue(entry.getValue() - Math.toIntExact(orderLineItem.getQuantity()));
            }
        }
    }

    public void prepare(ActionEvent event) {
        orderItemToPrepare = (OrderLineItem) event.getComponent().getAttributes().get("orderItemToPrepare");
        CustomerOrder currentOrder = (CustomerOrder) event.getComponent().getAttributes().get("order");
        orderItemToPrepare.setStatus(OrderLineItemStatusEnum.PREPARING);

        try {
            orderLineItemSessionBeanLocal.updateOrderLineItemByEmployee(orderItemToPrepare);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, orderItemToPrepare.getMenuItem().getMenuItemName() + " is being prepared.", null));
            sortOrderLineItems(currentOrder);
            numOrdered--;
            numPreparing++;
            orderItemToPrepare = new OrderLineItem();
        } catch (OrderLineItemNotFoundException | UpdateOrderLineItemException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating order line item: ", ex.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void serve(ActionEvent event) {
        orderItemToServe = (OrderLineItem) event.getComponent().getAttributes().get("orderItemToServe");
        CustomerOrder currentOrder = (CustomerOrder) event.getComponent().getAttributes().get("order");
        orderItemToServe.setStatus(OrderLineItemStatusEnum.SERVED);
        try {
            orderLineItemSessionBeanLocal.updateOrderLineItemByEmployee(orderItemToServe);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, orderItemToServe.getMenuItem().getMenuItemName() + " is being served.", null));
            reduceMenuItemOverview(orderItemToServe);
            sortOrderLineItems(currentOrder);
            numPreparing--;
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
            currentDayCustomerOrders.remove(orderToComplete);
            orderToComplete = new CustomerOrder();
        } catch (CustomerOrderNotFoundException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating customer order: ", ex.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void updateOrderLineItem() {
        try {
            orderLineItemSessionBeanLocal.updateOrderLineItemByEmployee(selectedOrderItem);
            createMenuItemsOverview();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Line Item Updated Successfully", null));
        } catch (OrderLineItemNotFoundException | UpdateOrderLineItemException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating order line item: ", ex.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public Integer getNumOrdered() {
        return numOrdered;
    }

    public Integer getNumPreparing() {
        return numPreparing;
    }

    public List<Map.Entry<MenuItem, Integer>> getMenuItemsOverview() {
        return menuItemsOverview;
    }

    public void viewMenuItem() {
        viewMenuItemManagedBean.setMenuItemToView(menuItemToViewEntry.getKey());
    }

    public Map.Entry<MenuItem, Integer> getMenuItemToViewEntry() {
        return menuItemToViewEntry;
    }

    public void setMenuItemToViewEntry(Map.Entry<MenuItem, Integer> menuItemToViewEntry) {
        this.menuItemToViewEntry = menuItemToViewEntry;
    }

    public ViewMenuItemManagedBean getViewMenuItemManagedBean() {
        return viewMenuItemManagedBean;
    }

    public void setViewMenuItemManagedBean(ViewMenuItemManagedBean viewMenuItemManagedBean) {
        this.viewMenuItemManagedBean = viewMenuItemManagedBean;
    }

    public List<CustomerOrder> getCurrentDayCustomerOrders() {
        return currentDayCustomerOrders;
    }

    public void setCurrentDayCustomerOrders(List<CustomerOrder> currentDayCustomerOrders) {
        this.currentDayCustomerOrders = currentDayCustomerOrders;
    }

    public OrderLineItem getSelectedOrderItem() {
        return selectedOrderItem;
    }

    public void setSelectedOrderItem(OrderLineItem selectedOrderItem) {
        if (selectedOrderItem != null) {
            this.selectedOrderItem = selectedOrderItem;
        }
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

    public List<OrderLineItemStatusEnum> getOrderLineItemStatusEnums() {
        List<OrderLineItemStatusEnum> list = new ArrayList<>(Arrays.asList(OrderLineItemStatusEnum.values()));
        list.remove(OrderLineItemStatusEnum.IN_CART);
        return list;
    }
}
