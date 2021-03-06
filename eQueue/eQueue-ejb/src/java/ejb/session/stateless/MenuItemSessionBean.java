package ejb.session.stateless;

import entity.MenuCategory;
import entity.MenuItem;
import entity.OrderLineItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.CreateNewMenuItemException;
import util.exceptions.DeleteMenuItemException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuCategoryNotFoundException;
import util.exceptions.MenuItemNotFoundException;
import util.exceptions.MenuItemNotUniqueException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateMenuItemException;

@Stateless
public class MenuItemSessionBean implements MenuItemSessionBeanLocal {
    
    @EJB
    private OrderLineItemSessionBeanLocal orderLineItemSessionBean;
    
    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;
    
    @EJB
    private MenuCategorySessionBeanLocal menuCategorySessionBean;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public MenuItemSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewMenuItem(MenuItem newMenuItem, Long menuCategoryId) throws MenuItemNotUniqueException, UnknownPersistenceException, InputDataValidationException, CreateNewMenuItemException {
        Set<ConstraintViolation<MenuItem>> constraintViolations = validator.validate(newMenuItem);
        
        if (constraintViolations.isEmpty()) {
            try {
                if (menuCategoryId == null) {
                    throw new CreateNewMenuItemException("The new menu item must be associated with a leaf category");
                }
                
                MenuCategory menuCategory = menuCategorySessionBean.retrieveMenuCategoryById(menuCategoryId);
                
                if (!menuCategory.getSubMenuCategories().isEmpty()) {
                    throw new CreateNewMenuItemException("Selected menu category for the new item is not a leaf category");
                }
                
                em.persist(newMenuItem);
                newMenuItem.setMenuCategory(menuCategory);
                
                em.flush();
                
                return newMenuItem.getMenuItemId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new MenuItemNotUniqueException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (MenuCategoryNotFoundException ex) {
                throw new CreateNewMenuItemException("An error has occurred while creating the new item: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public MenuItem retrieveMenuItemById(Long menuItemId) throws MenuItemNotFoundException {
        MenuItem menuItem = em.find(MenuItem.class, menuItemId);
        
        if (menuItem != null) {
            return menuItem;
        } else {
            throw new MenuItemNotFoundException("Menu Item ID " + menuItemId + " does not exist!");
        }
    }
    
    @Override
    public List<MenuItem> retrieveAllMenuItems() {
        Query query = em.createQuery("SELECT m FROM MenuItem m ORDER BY m.menuItemName ASC");
        List<MenuItem> menuItems = query.getResultList();
        
        return menuItems;
    }
    
    @Override
    public List<MenuItem> retrievePopularMenuItems(Integer maxItem) {
        Query query = em.createQuery("select c.menuItem from OrderLineItem c GROUP BY c.menuItem.menuItemId ORDER BY COUNT(c.menuItem.menuItemId) DESC");
        List<MenuItem> topMenuItems = query.getResultList();
        
        List<MenuItem> menuItems = new ArrayList<>();
        
        for(MenuItem t : topMenuItems){
            menuItems.add(t);
            if(menuItems.size() >= maxItem) {
                break;
            }
        }
        return menuItems;
    }
    
    @Override
    public List<MenuItem> retrieveAllMenuItemsByCategory(Long categoryId) {
        Query query = em.createQuery("SELECT m FROM MenuItem m WHERE m.menuCategory.menuCategoryId = :inCategoryId ORDER BY m.menuItemName ASC");
        query.setParameter("inCategoryId", categoryId);
        List<MenuItem> menuItems = query.getResultList();
        
        return menuItems;
    }
    
    @Override
    public void updateMenuItem(MenuItem menuItem, Long menuCategoryId) throws MenuItemNotFoundException, MenuCategoryNotFoundException, UpdateMenuItemException, InputDataValidationException {
        if (menuItem != null && menuItem.getMenuItemId() != null) {
            Set<ConstraintViolation<MenuItem>> constraintViolations = validator.validate(menuItem);
            
            if (constraintViolations.isEmpty()) {
                MenuItem menuItemToUpdate = retrieveMenuItemById(menuItem.getMenuItemId());
                
                if (menuItemToUpdate.getMenuItemCode().equals(menuItem.getMenuItemCode())) {
                    if (menuCategoryId != null && (!menuItemToUpdate.getMenuCategory().getMenuCategoryId().equals(menuCategoryId))) {
                        MenuCategory menuCategoryToUpdate = menuCategorySessionBean.retrieveMenuCategoryById(menuCategoryId);
                        
                        if (!menuCategoryToUpdate.getSubMenuCategories().isEmpty()) {
                            throw new UpdateMenuItemException("Selected menu category for the new product is not a leaf category");
                        }
                        menuItemToUpdate.setMenuCategory(menuCategoryToUpdate);
                    }
                    // POINT - POTENTIALLY WORK ON ELSE STATEMENT TO CATCH IF NO CATEGORY HAS BEEN ASSINGED HERE

                    menuItemToUpdate.setMenuItemCode(menuItem.getMenuItemCode());
                    menuItemToUpdate.setMenuItemName(menuItem.getMenuItemName());
                    menuItemToUpdate.setMenuItemDescription(menuItem.getMenuItemDescription());
                    menuItemToUpdate.setMenuItemPrice(menuItem.getMenuItemPrice());
                    menuItemToUpdate.setWaitingTimeMinutes(menuItem.getWaitingTimeMinutes());
                    menuItemToUpdate.setAvailability(menuItem.getAvailability());
                    menuItemToUpdate.setImagePath(menuItem.getImagePath());
                    
                } else {
                    throw new UpdateMenuItemException("Menu Item Code of record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new MenuItemNotFoundException("Menu Item ID not provided for menu item to be updated");
        }
    }
    
    @Override
    public void deleteMenuItem(Long menuItemId) throws MenuItemNotFoundException, DeleteMenuItemException {
        MenuItem menuItemToRemove = retrieveMenuItemById(menuItemId);
        List<OrderLineItem> orderLineItems = orderLineItemSessionBean.retrieveOrderLineItemsByMenuItemId(menuItemId);
        if (orderLineItems.isEmpty()) {
            menuItemToRemove.getMenuCategory().getMenuItems().remove(menuItemToRemove);
            em.remove(menuItemToRemove);
        } else {
            throw new DeleteMenuItemException("Menu Item ID " + menuItemId + " is associated with existing order line items and cannot be deleted!");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<MenuItem>> constraintViolations) {
        String msg = "Input data validation error!:";
        
        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
}

class ItemAndPopularity {
    
    Long id;
    Integer count;
    
    public ItemAndPopularity(Long id, Integer count){
        this.id = id;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    
    
    
}
