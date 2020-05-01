package jsf.managedBean;

import ejb.session.stateless.DiningTableSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Customer;
import entity.CustomerOrder;
import entity.DiningTable;
import entity.Employee;
import entity.OrderLineItem;
import entity.PaymentTransaction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.OrderStatusEnum;
import util.enumeration.TableStatusEnum;
import util.exceptions.EmployeeNotFoundException;

@Named(value = "transactionManagementManagedBean")
@ViewScoped
public class TransactionManagementManagedBean implements Serializable {

    @EJB(name = "DiningTableSessionBeanLocal")
    private DiningTableSessionBeanLocal diningTableSessionBeanLocal;
    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    private List<DiningTable> diningTables;
    private List<CustomerOrder> customerUnpaidOrders;
    private List<OrderLineItem> customerUnpaidOrderLineItems;

    private DiningTable selectedDiningTable;
    private Customer selectedCustomer;

    private PaymentTransaction newPaymentTransaction;
    private Boolean isDiningTableSelected;
    private Double cashAmount;
    private Double change;

    public TransactionManagementManagedBean() {
        this.diningTables = new ArrayList<>();
        this.customerUnpaidOrders = new ArrayList<>();
        this.customerUnpaidOrderLineItems = new ArrayList<>();

        this.selectedDiningTable = new DiningTable();
        this.selectedCustomer = new Customer();

        this.newPaymentTransaction = new PaymentTransaction();

        this.isDiningTableSelected = false;
    }

    @PostConstruct
    public void postConstruct() {
        this.diningTables = diningTableSessionBeanLocal.retrieveAllTables();
    }

    public void checkout(ActionEvent event) {
        if (!this.isDiningTableSelected) {
            Double transactionValue = 0.00;
            Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");

            try {
                currentEmployee = employeeSessionBeanLocal.retrieveEmployeeById(currentEmployee.getEmployeeId());
                this.newPaymentTransaction.setEmployee(currentEmployee);
            } catch (EmployeeNotFoundException ex) {
            }

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
                        transactionValue += oli.getMenuItem().getMenuItemPrice() * oli.getQuantity();
                    }
                }
            }

            sortOrderLineItems(customerUnpaidOrderLineItems);

            this.newPaymentTransaction.setTransactionDate(new Date(Calendar.getInstance(TimeZone.getDefault().getTimeZone("GMT+8:00")).getTimeInMillis()));
            this.newPaymentTransaction.setCustomerOrders(this.customerUnpaidOrders);
            this.newPaymentTransaction.setTransactionValue(transactionValue);

            Double gst = transactionValue - (transactionValue / 1.07);
            String formattedGst = String.format("%.2f", gst);
            this.newPaymentTransaction.setGst(Double.parseDouble(formattedGst));
        } else {
            this.isDiningTableSelected = false;

            this.customerUnpaidOrders.clear();
            this.customerUnpaidOrderLineItems.clear();

            this.newPaymentTransaction = new PaymentTransaction();
        }
    }

    public void computeChange() {
        this.change = this.cashAmount - this.newPaymentTransaction.getTransactionValue();
    }

    public void voidChange() {
        this.cashAmount = 0.00;
        this.change = 0.00;
    }

    public void confirmPayment() {
        System.out.println("*****************************************************");
        System.out.println("Emply  - " + newPaymentTransaction.getEmployee());
        System.out.println("Date   - " + newPaymentTransaction.getTransactionDate());
        System.out.println("Type   - " + newPaymentTransaction.getPaymentType());
        System.out.println("Value  - " + newPaymentTransaction.getTransactionValue());
        System.out.println("Gst    - " + newPaymentTransaction.getGst());

        System.out.println("=== Orders ===");
        for (CustomerOrder co : newPaymentTransaction.getCustomerOrders()) {
            System.out.println("Order - " + co);
        }
        System.out.println("*****************************************************");
    }

    public void cancel() {
        this.isDiningTableSelected = false;

        this.customerUnpaidOrders.clear();
        this.customerUnpaidOrderLineItems.clear();

        this.newPaymentTransaction = new PaymentTransaction();
    }

    private void sortOrderLineItems(List<OrderLineItem> orderLineItems) {
        Collections.sort(orderLineItems, (i1, i2) -> i1.compareTo_MenuItem_Quantity(i2));
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

    public Double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }
}
