/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.OrderLineItem;
import java.util.List;

/**
 *
 * @author User
 */
public class RetrieveOrderLineItemsRsp {
    
    List<OrderLineItem> orderLineItems;

    public RetrieveOrderLineItemsRsp() {
    }

    public RetrieveOrderLineItemsRsp(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
    }

    public List<OrderLineItem> getOrderLineItems() {
        return orderLineItems;
    }

    public void setOrderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
    }    
    
}
