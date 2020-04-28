package ejb.session.stateless;

import entity.Customer;
import entity.DiningTable;
import entity.Queue;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.TableStatusEnum;
import util.exceptions.DeleteDiningTableException;
import util.exceptions.DiningTableNotFoundException;
import util.exceptions.EditTableException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

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
    public Long createNewDiningTable(DiningTable diningTable) throws UnknownPersistenceException, InputDataValidationException {
        
        diningTable.setQrCode(UUID.randomUUID().toString().substring(0, 8));
        
        try {
            Set<ConstraintViolation<DiningTable>> constraintViolations = validator.validate(diningTable);

            if (constraintViolations.isEmpty()) {

                em.persist(diningTable);
                em.flush();

                return diningTable.getDiningTableId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }

        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }

  
    @Override
    public List<DiningTable> retrieveAllTables() {
        Query query = em.createQuery("SELECT d FROM DiningTable d");

        return query.getResultList();
    }

    @Override
    public List<DiningTable> retrieveAllUnfrozenUnoccupiedTables() {

        Query query = em.createQuery("SELECT d FROM DiningTable d where d.tableStatus = :inStatus ORDER BY d.seatingCapacity ASC");
        query.setParameter("inStatus", TableStatusEnum.UNFROZEN_UNOCCUPIED);

        return query.getResultList();
    }

    @Override
    public DiningTable retrieveDiningTableById(Long diningTableId) throws DiningTableNotFoundException {
        DiningTable diningTable = em.find(DiningTable.class, diningTableId);

        if (diningTable != null) {
            return diningTable;
        } else {
            throw new DiningTableNotFoundException("Dining Table ID " + diningTableId + " does not exist!");
        }
    }
    
    @Override
    public DiningTable retrieveDiningTableByCustomerId(Long customerId) {
        Customer customer = em.find(Customer.class, customerId);
        DiningTable diningTableToRetrieved = customer.getAllocatedDiningTable();
        return diningTableToRetrieved;
    }

    @Override
    public void updateDiningTableInformation(DiningTable diningTable) throws DiningTableNotFoundException, EditTableException, InputDataValidationException {
        if (diningTable != null && diningTable.getDiningTableId() != null) {
            Set<ConstraintViolation<DiningTable>> constraintViolations = validator.validate(diningTable);

            if (constraintViolations.isEmpty()) {
                DiningTable diningTableToUpdate = retrieveDiningTableById(diningTable.getDiningTableId());
                diningTableToUpdate.setQrCode(diningTable.getQrCode());
                diningTableToUpdate.setSeatingCapacity(diningTable.getSeatingCapacity());
                diningTableToUpdate.setTableStatus(diningTable.getTableStatus());
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }

        } else {
            throw new DiningTableNotFoundException("Invalid Dining Table ID provided for updating");
        }
    }

    @Override
    public void deleteDiningTable(Long diningTableId) throws DiningTableNotFoundException, DeleteDiningTableException {
        DiningTable diningTableToRemove = retrieveDiningTableById(diningTableId);

        if (diningTableToRemove.getCustomer() == null) {
            em.remove(diningTableToRemove);
        } else {
            throw new DeleteDiningTableException("Dining Table ID " + diningTableId + " is currently associated with Customer and cannot be deleted!");
        }
    }

    @Override
    public void allocateTableToCustomer(Long diningTableId, Long customerId) {
        DiningTable diningTable = em.find(DiningTable.class, diningTableId);
        Customer customer = em.find(Customer.class, customerId);
        diningTable.setCustomer(customer);
        diningTable.setTableStatus(TableStatusEnum.UNFROZEN_ALLOCATED);
    }

    @Override // when customer did not seat at table in time or when customer leaves table
    public void removeCustomerTableRelationship(Long customerId) {

        Customer customer = em.find(Customer.class, customerId);
        DiningTable diningTable = customer.getAllocatedDiningTable();
        
        if(diningTable == null) {
            return;
        }

        if (diningTable.getTableStatus() == TableStatusEnum.UNFROZEN_ALLOCATED) {
            diningTable.setTableStatus(TableStatusEnum.UNFROZEN_UNOCCUPIED);
        } else if (diningTable.getTableStatus() == TableStatusEnum.FROZEN_ALLOCATED) {
            diningTable.setTableStatus(TableStatusEnum.FROZEN_UNOCCUPIED);
        } else if (diningTable.getTableStatus() == TableStatusEnum.UNFROZEN_OCCUPIED) {
            diningTable.setTableStatus(TableStatusEnum.UNFROZEN_UNOCCUPIED);
        } else if (diningTable.getTableStatus() == TableStatusEnum.FROZEN_OCCUPIED) {
            diningTable.setTableStatus(TableStatusEnum.FROZEN_UNOCCUPIED);
        }
        
        
        diningTable.setSeatedTime(null);
        diningTable.setCustomer(null);

    }

    @Override
    public void seatCustomerToDiningTable(Long diningTableId) {
        DiningTable diningTable = em.find(DiningTable.class, diningTableId);
        diningTable.setTableStatus(TableStatusEnum.UNFROZEN_OCCUPIED);
        diningTable.setSeatedTime(new Date());
        
        Queue queue = diningTable.getCustomer().getCurrentQueue();
        diningTable.getCustomer().setCurrentQueue(null);
        em.remove(queue);
    }


    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<DiningTable>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
