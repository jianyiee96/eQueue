package ejb.session.stateless;

import entity.PaymentTransaction;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateNewPaymentTransactionException;
import util.exceptions.CustomerOrderNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface PaymentTransactionSessionBeanLocal {

    public Long createNewPaymentTransactionByCustomer(PaymentTransaction newPaymentTransaction) throws CreateNewPaymentTransactionException, CustomerOrderNotFoundException, InputDataValidationException, UnknownPersistenceException;

    public List<PaymentTransaction> retrieveAllPastTransactions();

}
