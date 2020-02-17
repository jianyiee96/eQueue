package ejb.session.stateless;

import entity.Customer;
import entity.DiningTable;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.InputDataValidationException;

@Stateless
public class DiningTableSessionBean implements DiningTableSessionBeanLocal {

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public DiningTableSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewDiningTable(DiningTable diningTable) throws InputDataValidationException {

        Set<ConstraintViolation<DiningTable>> constraintViolations = validator.validate(diningTable);

        if (constraintViolations.isEmpty()) {
            em.persist(diningTable);
            em.flush();

            return diningTable.getDiningTableId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<DiningTable>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
