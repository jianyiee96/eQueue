package jsf.managedBean;

import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import entity.CustomerOrder;
import entity.OrderLineItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import util.enumeration.OrderLineItemStatusEnum;

@Named(value = "kitchenManagementManagedBean")
@ApplicationScoped
public class KitchenManagementManagedBean implements Serializable {

    @EJB(name = "CustomerOrderSessionBeanLocal")
    private CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal;

    private List<CustomerOrder> ongoingCustomerOrders;
    private List<CustomerOrder> filteredCustomerOrders;

    public KitchenManagementManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
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
    }


    public void filterOrderLineItems() {
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

    public List<CustomerOrder> getOngoingCustomerOrders() {
        return ongoingCustomerOrders;
    }

    public void setOngoingCustomerOrders(List<CustomerOrder> ongoingCustomerOrders) {
        this.ongoingCustomerOrders = ongoingCustomerOrders;
    }

    public List<CustomerOrder> getFilteredCustomerOrders() {
        postConstruct();
        filterOrderLineItems();
        return filteredCustomerOrders;
    }

    public void setFilteredCustomerOrders(List<CustomerOrder> filteredCustomerOrders) {
        this.filteredCustomerOrders = filteredCustomerOrders;
    }

}
