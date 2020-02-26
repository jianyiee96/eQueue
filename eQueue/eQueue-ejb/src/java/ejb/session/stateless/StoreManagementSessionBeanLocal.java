package ejb.session.stateless;

import entity.StoreVariables;
import javax.ejb.Local;
import util.exceptions.InputDataValidationException;
import util.exceptions.StoreNotInitializedException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface StoreManagementSessionBeanLocal {
    
    public Long storeInitialization(StoreVariables newStoreVariables) throws InputDataValidationException, UnknownPersistenceException;
    
    public StoreVariables retrieveStoreVariables() throws StoreNotInitializedException;
    
    public void updateStoreVariables(StoreVariables storeVariables) throws InputDataValidationException, StoreNotInitializedException;
    
}
