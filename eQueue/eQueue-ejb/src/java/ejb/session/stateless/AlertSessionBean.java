package ejb.session.stateless;

import entity.Alert;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.AlertNotFoundException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Stateless
public class AlertSessionBean implements AlertSessionBeanLocal {

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AlertSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewAlert(Alert newAlert) throws UnknownPersistenceException, InputDataValidationException {
        try {

            Set<ConstraintViolation<Alert>> constraintViolations = validator.validate(newAlert);

            if (constraintViolations.isEmpty()) {

                em.persist(newAlert);
                em.flush();

                return newAlert.getAlertId();

            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }

        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }

    @Override
    public List<Alert> retrieveAllAlerts() {
        Query query = em.createQuery("SELECT a FROM Alert a");

        return query.getResultList();
    }

    @Override
    public Alert retrieveAlertById(Long alertId) throws AlertNotFoundException {
        Alert alert = em.find(Alert.class, alertId);

        if (alert != null) {
            return alert;
        } else {
            throw new AlertNotFoundException("Alert ID " + alertId + " does not exist!");
        }
    }

    @Override
    public void deleteAlert(Long alertId) throws AlertNotFoundException {

        try {
            Alert alert = retrieveAlertById(alertId);

            em.remove(alert);

        } catch (AlertNotFoundException ex) {
            throw new AlertNotFoundException("Alert ID " + alertId + " does not exist!");
        }
        
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Alert>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
