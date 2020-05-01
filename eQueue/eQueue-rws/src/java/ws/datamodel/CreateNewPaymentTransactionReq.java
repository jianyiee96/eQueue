package ws.datamodel;

import entity.PaymentTransaction;

public class CreateNewPaymentTransactionReq {

    private PaymentTransaction newPaymentTransaction;

    public CreateNewPaymentTransactionReq() {
    }

    public CreateNewPaymentTransactionReq(PaymentTransaction newPaymentTransaction) {
        this.newPaymentTransaction = newPaymentTransaction;
    }

    public PaymentTransaction getNewPaymentTransaction() {
        return newPaymentTransaction;
    }

    public void setNewPaymentTransaction(PaymentTransaction newPaymentTransaction) {
        this.newPaymentTransaction = newPaymentTransaction;
    }

}
