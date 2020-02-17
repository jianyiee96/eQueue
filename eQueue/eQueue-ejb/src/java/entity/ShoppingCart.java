package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@Entity
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ShoppingCart;
    
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 9, fraction = 2)
    private Double totalAmount;
    
    @OneToMany
    private List<OrderLineItem> orderLineItems;

    public ShoppingCart(){
        this.orderLineItems = new ArrayList<>();
    }
    
    public Long getShoppingCart() {
        return ShoppingCart;
    }

    public void setShoppingCart(Long ShoppingCart) {
        this.ShoppingCart = ShoppingCart;
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

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ShoppingCart != null ? ShoppingCart.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ShoppingCart fields are not set
        if (!(object instanceof ShoppingCart)) {
            return false;
        }
        ShoppingCart other = (ShoppingCart) object;
        if ((this.ShoppingCart == null && other.ShoppingCart != null) || (this.ShoppingCart != null && !this.ShoppingCart.equals(other.ShoppingCart))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ShoppingCart[ id=" + ShoppingCart + " ]";
    }
    
}
