package ws.datamodel;

import entity.Customer;

public class RegisterCustomerReq {

    private Customer customer;

    public RegisterCustomerReq() {
    }

    public RegisterCustomerReq(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
