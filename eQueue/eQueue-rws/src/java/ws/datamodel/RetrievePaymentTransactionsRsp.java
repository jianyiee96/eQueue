package ws.datamodel;

import entity.PaymentTransaction;
import java.util.List;

public class RetrievePaymentTransactionsRsp {

    List<PaymentTransaction> paymentTransactions;

    public RetrievePaymentTransactionsRsp() {
    }

    public RetrievePaymentTransactionsRsp(List<PaymentTransaction> paymentTransactions) {
        this.paymentTransactions = paymentTransactions;
    }

    public List<PaymentTransaction> getPaymentTransactions() {
        return paymentTransactions;
    }

    public void setPaymentTransactions(List<PaymentTransaction> paymentTransactions) {
        this.paymentTransactions = paymentTransactions;
    }

}
