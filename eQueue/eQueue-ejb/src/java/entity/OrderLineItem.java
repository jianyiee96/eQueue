package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.OrderLineItemStatusEnum;

@Entity
public class OrderLineItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderLineItemId;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Long quantity;
    
    @Column(nullable = true, length = 128)
    @Size(max = 128)
    private String remarks;
    
    @Column(nullable = false)
    @NotNull
    private boolean isEdited;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private OrderLineItemStatusEnum status;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private MenuItem menuItem;
    
    public OrderLineItem(){
        
        this.isEdited = false;
        
    }
    
    public OrderLineItem(Long quantity, String remarks, OrderLineItemStatusEnum status){
        this();
        this.quantity = quantity;
        this.remarks = remarks;
        this.status = status;
    }
    
    public Long getOrderLineItemId() {
        return orderLineItemId;
    }

    public void setOrderLineItemId(Long orderLineItemId) {
        this.orderLineItemId = orderLineItemId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isIsEdited() {
        return isEdited;
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public OrderLineItemStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OrderLineItemStatusEnum status) {
        this.status = status;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderLineItemId != null ? orderLineItemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the orderLineItemId fields are not set
        if (!(object instanceof OrderLineItem)) {
            return false;
        }
        OrderLineItem other = (OrderLineItem) object;
        if ((this.orderLineItemId == null && other.orderLineItemId != null) || (this.orderLineItemId != null && !this.orderLineItemId.equals(other.orderLineItemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OrderLineItem[ id=" + orderLineItemId + " ]";
    }
    
}
