package ejb.session.stateless;

import entity.StoreVariables;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.InputDataValidationException;
import util.exceptions.StoreNotInitializedException;
import util.exceptions.UnknownPersistenceException;

@Stateless
public class StoreManagementSessionBean implements StoreManagementSessionBeanLocal {

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public StoreManagementSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long storeInitialization(StoreVariables newStoreVariables) throws InputDataValidationException, UnknownPersistenceException {

        try {
            Set<ConstraintViolation<StoreVariables>> constraintViolations = validator.validate(newStoreVariables);

            if (constraintViolations.isEmpty()) {
                em.persist(newStoreVariables);
                em.flush();

                return newStoreVariables.getStoreVariableId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
        }

    }

    @Override
    public StoreVariables retrieveStoreVariables() throws StoreNotInitializedException {
        StoreVariables storeVariables = em.find(StoreVariables.class, 1l);

        if (storeVariables != null) {
            return storeVariables;
        } else {
            throw new StoreNotInitializedException("Store is not initialized!");
        }
    }

    @Override
    public void updateStoreVariables(StoreVariables storeVariables) throws InputDataValidationException, StoreNotInitializedException {
        if (storeVariables != null && storeVariables.getStoreVariableId() != null) {

            Set<ConstraintViolation<StoreVariables>> constraintViolations = validator.validate(storeVariables);

            if (constraintViolations.isEmpty()) {
                
                StoreVariables storeVariablesToUpdate = retrieveStoreVariables();
                
                storeVariablesToUpdate.setStoreName(storeVariables.getStoreName());
                storeVariablesToUpdate.setStoreEmail(storeVariables.getStoreEmail());
                storeVariablesToUpdate.setStoreContact(storeVariables.getStoreContact());
                storeVariablesToUpdate.setStoreAddress(storeVariables.getStoreAddress());
                storeVariablesToUpdate.setAllocationGraceWaitingMinutes(storeVariables.getAllocationGraceWaitingMinutes());
                storeVariablesToUpdate.setEstimatedQueueUnitWaitingMinutes(storeVariables.getEstimatedQueueUnitWaitingMinutes());
                storeVariablesToUpdate.setMessageOfTheDay(storeVariables.getMessageOfTheDay());
                
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<StoreVariables>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
