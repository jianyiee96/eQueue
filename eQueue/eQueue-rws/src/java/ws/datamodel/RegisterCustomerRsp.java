package ws.datamodel;

public class RegisterCustomerRsp {

    private Long customerId;

    public RegisterCustomerRsp() {
    }

    public RegisterCustomerRsp(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

}
