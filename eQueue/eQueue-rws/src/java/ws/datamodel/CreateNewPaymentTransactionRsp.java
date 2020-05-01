package ws.datamodel;

public class CreateNewPaymentTransactionRsp {

    private Long newPaymentTransactionId;

    public CreateNewPaymentTransactionRsp() {
    }

    public CreateNewPaymentTransactionRsp(Long newPaymentTransactionId) {
        this.newPaymentTransactionId = newPaymentTransactionId;
    }

    public Long getNewPaymentTransactionId() {
        return newPaymentTransactionId;
    }

    public void setNewPaymentTransactionId(Long newPaymentTransactionId) {
        this.newPaymentTransactionId = newPaymentTransactionId;
    }

}
