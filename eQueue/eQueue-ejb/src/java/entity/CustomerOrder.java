package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import util.enumeration.OrderLineItemStatusEnum;
import util.enumeration.OrderStatusEnum;

@Entity
public class CustomerOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date orderDate;

    @Column(nullable = false)
    @NotNull
    private Boolean isCompleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private OrderStatusEnum status;

    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 9, fraction = 2)
    private Double totalAmount;

    @OneToMany
    private List<OrderLineItem> orderLineItems;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;

    @ManyToOne(optional = true)
    private PaymentTransaction paymentTransaction;

    public CustomerOrder() {

        this.orderDate = new Date();
        this.isCompleted = Boolean.FALSE;
        this.status = OrderStatusEnum.UNPAID;
        this.totalAmount = 0.0;

        this.orderLineItems = new ArrayList<>();
        this.paymentTransaction = null;

    }

    public Boolean getIsAllServed() {
        for (OrderLineItem li : orderLineItems) {
            if (li.getStatus() == OrderLineItemStatusEnum.ORDERED || li.getStatus() == OrderLineItemStatusEnum.PREPARING) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OrderStatusEnum status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderLineItem> getOrderLineItems() {
        return orderLineItems;
    }

    public void setOrderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        if (this.customer != null) {
            this.customer.getCustomerOrders().remove(this);
        }

        this.customer = customer;

        if (this.customer != null) {
            if (!this.customer.getCustomerOrders().contains(this)) {
                this.customer.getCustomerOrders().add(this);
            }
        }
    }

    public PaymentTransaction getPaymentTransaction() {
        return paymentTransaction;
    }

    public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
        if (this.paymentTransaction != null) {
            this.paymentTransaction.getCustomerOrders().remove(this);
        }

        this.paymentTransaction = paymentTransaction;

        if (this.paymentTransaction != null) {
            if (!this.paymentTransaction.getCustomerOrders().contains(this)) {
                this.paymentTransaction.getCustomerOrders().add(this);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderId != null ? orderId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the orderId fields are not set
        if (!(object instanceof CustomerOrder)) {
            return false;
        }
        CustomerOrder other = (CustomerOrder) object;
        if ((this.orderId == null && other.orderId != null) || (this.orderId != null && !this.orderId.equals(other.orderId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Order[ id=" + orderId + " ]";
    }

}
