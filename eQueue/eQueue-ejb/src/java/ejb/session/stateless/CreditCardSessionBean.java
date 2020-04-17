package ejb.session.stateless;

import entity.CreditCard;
import entity.Customer;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.CreateNewCreditCardException;
import util.exceptions.CreditCardNotFoundException;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.DeleteCreditCardException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Stateless
public class CreditCardSessionBean implements CreditCardSessionBeanLocal {

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CreditCardSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewCreditCard(String customerEmail, CreditCard newCreditCard) throws CreateNewCreditCardException, InputDataValidationException, CustomerNotFoundException, UnknownPersistenceException {
        try {
            Set<ConstraintViolation<CreditCard>> constraintViolations = validator.validate(newCreditCard);

            if (constraintViolations.isEmpty()) {

                if (customerEmail == null) {
                    throw new CreateNewCreditCardException("The credit card must be associated with a email!");
                }

                Customer customer = customerSessionBean.retrieveCustomerByEmail(customerEmail);
                newCreditCard.setCustomer(customer);
                customer.setCreditCard(newCreditCard);
                em.persist(newCreditCard);
                em.flush();
                return newCreditCard.getCreditCardId();

            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }

        } catch (CustomerNotFoundException ex) {
            throw new CustomerNotFoundException(ex.getMessage());
        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }

    @Override
    public void updateCreditCard(CreditCard creditCard) throws InputDataValidationException, CreditCardNotFoundException {
        if (creditCard != null && creditCard.getCreditCardId() != null) {
            Set<ConstraintViolation<CreditCard>> constraintViolations = validator.validate(creditCard);

            if (constraintViolations.isEmpty()) {

                CreditCard creditCardToUpdate = retrieveCreditCardById(creditCard.getCreditCardId());

                creditCardToUpdate.setCreditCardName(creditCard.getCreditCardName());
                creditCardToUpdate.setCreditCardNumber(creditCard.getCreditCardNumber());
                creditCardToUpdate.setCvv(creditCard.getCvv());
                creditCardToUpdate.setExpiryMonth(creditCard.getExpiryMonth());
                creditCardToUpdate.setExpiryYear(creditCard.getExpiryYear());

                creditCard.getCustomer().setCreditCard(creditCardToUpdate);

            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new CreditCardNotFoundException("Credit Card ID not provided for credit card to be updated!");
        }
    }

    @Override
    public CreditCard retrieveCreditCardById(Long creditCardId) throws CreditCardNotFoundException {
        CreditCard creditCard = em.find(CreditCard.class, creditCardId);

        if (creditCard != null) {
            creditCard.getCustomer();

            return creditCard;
        } else {
            throw new CreditCardNotFoundException("Credit Card ID " + creditCardId + " does not exist!");
        }
    }

    @Override
    public void deleteCreditCard(Long creditCardId) throws DeleteCreditCardException {
        try {
            CreditCard creditCardToDelete = retrieveCreditCardById(creditCardId);

            creditCardToDelete.getCustomer().setCreditCard(null);

            em.remove(creditCardToDelete);
        } catch (CreditCardNotFoundException ex) {
            throw new DeleteCreditCardException("Cannot delete invalid credit card id!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CreditCard>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
