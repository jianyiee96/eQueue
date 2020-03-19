package ejb.session.stateless;

import entity.MenuItem;
import entity.OrderLineItem;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolation;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.MenuItemAvailabilityEnum;
import util.enumeration.OrderLineItemStatusEnum;
import util.exceptions.CreateNewOrderLineItemException;
import util.exceptions.DeleteOrderLineItemException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuItemNotFoundException;
import util.exceptions.OrderLineItemNotFoundException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateOrderLineItemException;

@Stateless
public class OrderLineItemSessionBean implements OrderLineItemSessionBeanLocal {

    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public OrderLineItemSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewOrderLineItem(OrderLineItem newOrderLineItem, Long menuItemId) throws UnknownPersistenceException, InputDataValidationException, CreateNewOrderLineItemException {
        Set<ConstraintViolation<OrderLineItem>> constraintViolations = validator.validate(newOrderLineItem);
        if (constraintViolations.isEmpty()) {
            try {
                if (menuItemId == null) {
                    throw new CreateNewOrderLineItemException("The new order line item must be associated with a menu item");
                }

                MenuItem menuItem = menuItemSessionBean.retrieveMenuItemById(menuItemId);

                if (menuItem.getAvailability() == MenuItemAvailabilityEnum.UNAVAILABLE) {
                    throw new CreateNewOrderLineItemException("An error has occured while creating the new order line item: " + menuItem.getMenuItemName() + " is currently unavailable.");
                }

                em.persist(newOrderLineItem);
                newOrderLineItem.setMenuItem(menuItem);

                em.flush();
                return newOrderLineItem.getOrderLineItemId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CreateNewOrderLineItemException("Order line item not unique");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (MenuItemNotFoundException ex) {
                throw new CreateNewOrderLineItemException("An error has occured while creating the new order line item: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public OrderLineItem retrieveOrderLineItemById(Long orderLineItemId) throws OrderLineItemNotFoundException {
        OrderLineItem orderLineItem = em.find(OrderLineItem.class, orderLineItemId);

        if (orderLineItem != null) {
            return orderLineItem;
        } else {
            throw new OrderLineItemNotFoundException("Order Line Item ID " + orderLineItemId + " does not exist!");
        }
    }

    // Customer - Can only update the quantity, remkarks when item is in ordered/in_cart status
    @Override
    public void updateOrderLineItemByCustomer(OrderLineItem orderLineItem) throws OrderLineItemNotFoundException, UpdateOrderLineItemException, InputDataValidationException {

        if (orderLineItem != null && orderLineItem.getOrderLineItemId() != null) {
            Set<ConstraintViolation<OrderLineItem>> constraintViolations = validator.validate(orderLineItem);
            if (constraintViolations.isEmpty()) {
                OrderLineItem orderLineItemToUpdate = retrieveOrderLineItemById(orderLineItem.getOrderLineItemId());
                OrderLineItemStatusEnum currentStatus = orderLineItemToUpdate.getStatus();
                if (currentStatus != OrderLineItemStatusEnum.IN_CART && currentStatus != OrderLineItemStatusEnum.ORDERED) {
                    throw new UpdateOrderLineItemException("Order Line Item for " + orderLineItem.getMenuItem().getMenuItemName() + " is currently: " + currentStatus);
                }

                // Extra check to see if menu item suddenly became unavailable while in shopping cart
                if (currentStatus == OrderLineItemStatusEnum.IN_CART) {
                    try {
                        MenuItem menuItem = menuItemSessionBean.retrieveMenuItemById(orderLineItemToUpdate.getMenuItem().getMenuItemId());
                        if (menuItem.getAvailability() == MenuItemAvailabilityEnum.UNAVAILABLE) {
                            throw new UpdateOrderLineItemException("An error has occured while updating the order line item: " + menuItem.getMenuItemName() + " is currently unavailable.");
                        }
                    } catch (MenuItemNotFoundException ex) {
                        throw new UpdateOrderLineItemException("An error has occured while updating the order line item: " + ex.getMessage());
                    }
                }

                orderLineItemToUpdate.setIsEdited(true);
                orderLineItemToUpdate.setQuantity(orderLineItem.getQuantity());
                orderLineItemToUpdate.setRemarks(orderLineItem.getRemarks());

                // When customers wish to cancel their orders while in ordered state
                orderLineItemToUpdate.setStatus(orderLineItem.getStatus());

            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new OrderLineItemNotFoundException("Order Line Item ID not provided for order line item to be updated");
        }
    }

    // Employee - Can update everything, except ID and menu item, in whatever status
    @Override
    public void updateOrderLineItemByEmployee(OrderLineItem orderLineItem) throws OrderLineItemNotFoundException, UpdateOrderLineItemException, InputDataValidationException {

        if (orderLineItem != null && orderLineItem.getOrderLineItemId() != null) {
            Set<ConstraintViolation<OrderLineItem>> constraintViolations = validator.validate(orderLineItem);
            if (constraintViolations.isEmpty()) {
                OrderLineItem orderLineItemToUpdate = retrieveOrderLineItemById(orderLineItem.getOrderLineItemId());

                // Employee - added update functions in whichever status
                // When employee edits, DO NOT change isEdit
                orderLineItemToUpdate.setQuantity(orderLineItem.getQuantity());
                orderLineItemToUpdate.setRemarks(orderLineItem.getRemarks());
                orderLineItemToUpdate.setStatus(orderLineItem.getStatus());

            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new OrderLineItemNotFoundException("Order Line Item ID not provided for order line item to be updated");
        }
    }

    // MAKE SURE TO DISASSOCIATE AT SHOPPING CART AND [CUSTOMER ORDER (This one just set to cancelled instead)]
    // ONLY FOR EMPLOYEE USE & SHOPPING CART USE
    // When customers cancel their orders while in Ordered state, use updateByCustomer method
    @Override
    public void deleteOrderLineItem(Long orderLineItemId) throws OrderLineItemNotFoundException, DeleteOrderLineItemException {
        OrderLineItem orderLineItemToRemove = retrieveOrderLineItemById(orderLineItemId);
        em.remove(orderLineItemToRemove);
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<OrderLineItem>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
