package ejb.session.stateless;

import entity.DiningTable;
import javax.ejb.Local;
import util.exceptions.InputDataValidationException;

@Local
public interface DiningTableSessionBeanLocal {
    
    public Long createNewDiningTable(DiningTable diningTable) throws InputDataValidationException;
            
}
