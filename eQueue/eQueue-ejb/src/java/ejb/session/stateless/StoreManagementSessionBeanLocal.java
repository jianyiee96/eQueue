package ejb.session.stateless;

import entity.Store;
import javax.ejb.Local;
import util.exceptions.InputDataValidationException;
import util.exceptions.StoreNotInitializedException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface StoreManagementSessionBeanLocal {

    public Long storeInitialization(Store newStoreVariables) throws InputDataValidationException, UnknownPersistenceException;

    public Store retrieveStore() throws StoreNotInitializedException;

    public void updateStore(Store storeToUpdate) throws InputDataValidationException, StoreNotInitializedException;

}
