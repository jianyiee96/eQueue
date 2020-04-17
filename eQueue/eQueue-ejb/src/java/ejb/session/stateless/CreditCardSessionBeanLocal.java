package ejb.session.stateless;

import entity.CreditCard;
import javax.ejb.Local;
import util.exceptions.CreateNewCreditCardException;
import util.exceptions.CreditCardNotFoundException;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.DeleteCreditCardException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface CreditCardSessionBeanLocal {

    public Long createNewCreditCard(String customerEmail, CreditCard newCreditCard) throws CreateNewCreditCardException, InputDataValidationException, CustomerNotFoundException, UnknownPersistenceException;

    public void updateCreditCard(CreditCard creditCard) throws InputDataValidationException, CreditCardNotFoundException;

    public CreditCard retrieveCreditCardById(Long creditCardId) throws CreditCardNotFoundException;

    public void deleteCreditCard(Long creditCardId) throws DeleteCreditCardException;

}
