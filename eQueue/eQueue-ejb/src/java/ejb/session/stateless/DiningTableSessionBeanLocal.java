package ejb.session.stateless;

import entity.DiningTable;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.DeleteDiningTableException;
import util.exceptions.DiningTableNotFoundException;
import util.exceptions.EditTableException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface DiningTableSessionBeanLocal {

    public Long createNewDiningTable(DiningTable diningTable) throws UnknownPersistenceException, InputDataValidationException;

    public List<DiningTable> retrieveAllTables();

    public DiningTable retrieveDiningTableById(Long diningTableId) throws DiningTableNotFoundException;

    public void updateDiningTableInformation(DiningTable diningTable) throws DiningTableNotFoundException, EditTableException, InputDataValidationException;

    public void deleteDiningTable(Long diningTableId) throws DiningTableNotFoundException, DeleteDiningTableException;

    public void allocateTableToCustomer(Long diningTableId, Long customerId);

    public void removeCustomerTableRelationship(Long customerId);

    public void seatCustomerToDiningTable(Long diningTableId);
    
    public List<DiningTable> retrieveAllUnfrozenUnoccupiedTables();

}
