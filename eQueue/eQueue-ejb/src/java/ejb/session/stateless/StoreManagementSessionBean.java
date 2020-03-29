package ejb.session.stateless;

import entity.Store;
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
    public Long storeInitialization(Store newStore) throws InputDataValidationException, UnknownPersistenceException {

        try {
            Set<ConstraintViolation<Store>> constraintViolations = validator.validate(newStore);

            if (constraintViolations.isEmpty()) {
                em.persist(newStore);
                em.flush();

                return newStore.getStoreId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
        }

    }

    @Override
    public Store retrieveStore() throws StoreNotInitializedException {
        Store store = em.find(Store.class, 1l);

        if (store != null) {
            return store;
        } else {
            throw new StoreNotInitializedException("Store is not initialized!");
        }
    }

    @Override
    public void updateStore(Store store) throws InputDataValidationException, StoreNotInitializedException {
        if (store != null && store.getStoreId() != null) {

            Set<ConstraintViolation<Store>> constraintViolations = validator.validate(store);

            if (constraintViolations.isEmpty()) {
                
                Store storeToUpdate = retrieveStore();
                
                storeToUpdate.setStoreName(store.getStoreName());
                storeToUpdate.setStoreEmail(store.getStoreEmail());
                storeToUpdate.setStoreContact(store.getStoreContact());
                storeToUpdate.setStoreAddress(store.getStoreAddress());
                storeToUpdate.setAllocationGraceWaitingMinutes(store.getAllocationGraceWaitingMinutes());
                storeToUpdate.setEstimatedQueueUnitWaitingMinutes(store.getEstimatedQueueUnitWaitingMinutes());
                storeToUpdate.setMessageOfTheDay(store.getMessageOfTheDay());
                
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Store>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
