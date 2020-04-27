package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Local;
import util.exceptions.CustomerInvalidLoginCredentialException;
import util.exceptions.CustomerInvalidPasswordException;
import util.exceptions.CustomerNotFoundException;
import util.exceptions.CustomerNotUniqueException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;


@Local
public interface CustomerSessionBeanLocal {
    
    public Long createNewCustomer(Customer customer) throws InputDataValidationException, CustomerNotUniqueException, UnknownPersistenceException;
    
    public Customer retrieveCustomerById(Long customerId) throws CustomerNotFoundException;
    
    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException;
    
    public Customer customerLogin(String email, String password) throws CustomerInvalidLoginCredentialException;

    public void changePassword(String email, String oldPassword, String newPassword) throws CustomerInvalidPasswordException, CustomerNotFoundException;
    
    public Long retrieveCustomerIdByOrderLineItemId(Long orderLineItemId) throws CustomerNotFoundException;
    
}
