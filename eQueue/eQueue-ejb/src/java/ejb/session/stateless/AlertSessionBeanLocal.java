package ejb.session.stateless;

import entity.Alert;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.AlertNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Local
public interface AlertSessionBeanLocal {

    public Long createNewAlert(Alert newAlert) throws UnknownPersistenceException, InputDataValidationException;

    public List<Alert> retrieveAllAlerts();

    public Alert retrieveAlertById(Long alertId) throws AlertNotFoundException;

    public void deleteAlert(Long alertId) throws AlertNotFoundException;

}
