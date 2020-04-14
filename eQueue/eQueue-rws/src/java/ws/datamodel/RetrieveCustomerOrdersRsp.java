/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.CustomerOrder;
import java.util.List;

/**
 *
 * @author User
 */
public class RetrieveCustomerOrdersRsp {
    
    private List<CustomerOrder> customerOrders;

    public RetrieveCustomerOrdersRsp() {
    }

    public RetrieveCustomerOrdersRsp(List<CustomerOrder> customerOrders) {
        this.customerOrders = customerOrders;
    }

    public List<CustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(List<CustomerOrder> customerOrders) {
        this.customerOrders = customerOrders;
    }
    
    
    
}
