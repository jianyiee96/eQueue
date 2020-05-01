package ejb.session.stateless;

import entity.PaymentTransaction;
import javax.ejb.Local;
import util.exceptions.CreateNewPaymentTransactionException;
import util.exceptions.CustomerOrderNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface PaymentTransactionSessionBeanLocal {

    public Long createNewPaymentTransactionByCustomer(PaymentTransaction newPaymentTransaction) throws CreateNewPaymentTransactionException, CustomerOrderNotFoundException, InputDataValidationException, UnknownPersistenceException;

}
