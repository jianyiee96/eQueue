package ws.datamodel;

import entity.Customer;

public class CustomerLoginRsp {

    private Customer customer;

    public CustomerLoginRsp() {
    }

    public CustomerLoginRsp(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
