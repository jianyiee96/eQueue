package jsf.managedBean;

import ejb.session.stateless.DiningTableSessionBeanLocal;
import entity.Customer;
import entity.CustomerOrder;
import entity.DiningTable;
import entity.OrderLineItem;
import entity.PaymentTransaction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.OrderStatusEnum;
import util.enumeration.TableStatusEnum;

@Named(value = "transactionManagementManagedBean")
@ViewScoped
public class TransactionManagementManagedBean implements Serializable {

    @EJB(name = "DiningTableSessionBeanLocal")
    private DiningTableSessionBeanLocal diningTableSessionBeanLocal;

    private List<DiningTable> diningTables;
    private List<CustomerOrder> customerUnpaidOrders;
//    private Map<OrderLineItem, Integer> customerUnpaidOrderLineItems;
    private List<OrderLineItem> customerUnpaidOrderLineItems;

    private DiningTable selectedDiningTable;
    private Customer selectedCustomer;

    private Double transactionValue;
    private PaymentTransaction newPaymentTransaction;

    private Boolean isDiningTableSelected;

    public TransactionManagementManagedBean() {
        this.diningTables = new ArrayList<>();
        this.customerUnpaidOrders = new ArrayList<>();
//        this.customerUnpaidOrderLineItems = new TreeMap<>();
        this.customerUnpaidOrderLineItems = new ArrayList<>();

        this.selectedDiningTable = new DiningTable();
        this.selectedCustomer = new Customer();

        this.transactionValue = 0.00;
        this.newPaymentTransaction = new PaymentTransaction();

        this.isDiningTableSelected = false;
    }

    @PostConstruct
    public void postConstruct() {
        this.diningTables = diningTableSessionBeanLocal.retrieveAllTables();
    }

    public void checkout(ActionEvent event) {
        if (!this.isDiningTableSelected) {
            this.isDiningTableSelected = true;

            this.selectedDiningTable = (DiningTable) event.getComponent().getAttributes().get("diningTableToCheckout");
            this.selectedCustomer = this.selectedDiningTable.getCustomer();

            for (CustomerOrder co : this.selectedCustomer.getCustomerOrders()) {
                // should be paying for all unpaid orders...
                // otherwise it's weird for the customer to make many round trips...
                if (co.getStatus() == OrderStatusEnum.UNPAID) {
                    this.customerUnpaidOrders.add(co);

                    for (OrderLineItem oli : co.getOrderLineItems()) {
                        customerUnpaidOrderLineItems.add(oli);
                        this.transactionValue += oli.getMenuItem().getMenuItemPrice() * oli.getQuantity();
                    }
                }
            }
        } else {
            this.isDiningTableSelected = false;

            this.customerUnpaidOrders.clear();
            this.customerUnpaidOrderLineItems.clear();
        }

//        orderItemToPrepare = (OrderLineItem) event.getComponent().getAttributes().get("orderItemToPrepare");
//        CustomerOrder currentOrder = (CustomerOrder) event.getComponent().getAttributes().get("order");
//        orderItemToPrepare.setStatus(OrderLineItemStatusEnum.PREPARING);
//
//        try {
//            orderLineItemSessionBeanLocal.updateOrderLineItemByEmployee(orderItemToPrepare);
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, orderItemToPrepare.getMenuItem().getMenuItemName() + " is being prepared.", null));
//            sortOrderLineItems(currentOrder);
//            numOrdered--;
//            numPreparing++;
//            orderItemToPrepare = new OrderLineItem();
//        } catch (OrderLineItemNotFoundException | UpdateOrderLineItemException | InputDataValidationException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating order line item: ", ex.getMessage()));
//        } catch (Exception ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
//        }
    }

    public String getDiningTableStatus(TableStatusEnum status) {

        if (status == TableStatusEnum.FROZEN_ALLOCATED || status == TableStatusEnum.UNFROZEN_ALLOCATED) {
            return "Allocated";
        } else if (status == TableStatusEnum.FROZEN_OCCUPIED || status == TableStatusEnum.UNFROZEN_OCCUPIED) {
            return "Occupied";
        } else if (status == TableStatusEnum.FROZEN_UNOCCUPIED || status == TableStatusEnum.UNFROZEN_UNOCCUPIED) {
            return "Unoccupied";
        }

        return "Unknown";
    }

    public List<DiningTable> getDiningTables() {
        return diningTables;
    }

    public void setDiningTables(List<DiningTable> diningTables) {
        this.diningTables = diningTables;
    }

    public List<CustomerOrder> getCustomerUnpaidOrders() {
        return customerUnpaidOrders;
    }

    public void setCustomerUnpaidOrders(List<CustomerOrder> customerUnpaidOrders) {
        this.customerUnpaidOrders = customerUnpaidOrders;
    }

    public List<OrderLineItem> getCustomerUnpaidOrderLineItems() {
        return customerUnpaidOrderLineItems;
    }

    public void setCustomerUnpaidOrderLineItems(List<OrderLineItem> customerUnpaidOrderLineItems) {
        this.customerUnpaidOrderLineItems = customerUnpaidOrderLineItems;
    }

    public DiningTable getSelectedDiningTable() {
        return selectedDiningTable;
    }

    public void setSelectedDiningTable(DiningTable selectedDiningTable) {
        this.selectedDiningTable = selectedDiningTable;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public Double getTransactionValue() {
        return transactionValue;
    }

    public void setTransactionValue(Double transactionValue) {
        this.transactionValue = transactionValue;
    }

    public PaymentTransaction getNewPaymentTransaction() {
        return newPaymentTransaction;
    }

    public void setNewPaymentTransaction(PaymentTransaction newPaymentTransaction) {
        this.newPaymentTransaction = newPaymentTransaction;
    }

    public Boolean getIsDiningTableSelected() {
        return isDiningTableSelected;
    }

    public void setIsDiningTableSelected(Boolean isDiningTableSelected) {
        this.isDiningTableSelected = isDiningTableSelected;
    }
}
