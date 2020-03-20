package ejb.session.stateless;

import entity.MenuCategory;
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
import util.exceptions.CreateNewMenuCategoryException;
import util.exceptions.DeleteMenuCategoryException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuCategoryNotFoundException;
import util.exceptions.UpdateMenuCategoryException;

@Stateless
public class MenuCategorySessionBean implements MenuCategorySessionBeanLocal {

    @PersistenceContext(unitName = "eQueue-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public MenuCategorySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewMenuCategory(MenuCategory newMenuCategory, Long parentMenuCategoryId) throws InputDataValidationException, CreateNewMenuCategoryException {
        Set<ConstraintViolation<MenuCategory>> constraintViolations = validator.validate(newMenuCategory);

        if (constraintViolations.isEmpty()) {
            try {
                if (parentMenuCategoryId != null) {
                    MenuCategory parentMenuCategory = retrieveMenuCategoryById(parentMenuCategoryId);

                    if (!parentMenuCategory.getMenuItems().isEmpty()) {
                        throw new CreateNewMenuCategoryException("Parent category cannot be associated with any product");
                    }

                    newMenuCategory.setParentMenuCategory(parentMenuCategory);
                }

                em.persist(newMenuCategory);
                em.flush();

                return newMenuCategory.getMenuCategoryId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null
                        && ex.getCause().getCause() != null
                        && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new CreateNewMenuCategoryException("Category with same name already exist");
                } else {
                    throw new CreateNewMenuCategoryException("An unexpected error has occurred: " + ex.getMessage());
                }
            } catch (Exception ex) {
                throw new CreateNewMenuCategoryException("An unexpected error has occurred: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<MenuCategory> retrieveAllMenuCategories() {
        Query query = em.createQuery("SELECT c FROM MenuCategory c ORDER BY c.categoryName ASC");
        List<MenuCategory> MenuCategories = query.getResultList();

        for (MenuCategory menuCategory : MenuCategories) {
            menuCategory.getParentMenuCategory();
            menuCategory.getSubMenuCategories().size();
            menuCategory.getMenuItems().size();
        }

        return MenuCategories;
    }

    @Override
    public List<MenuCategory> retrieveAllRootMenuCategories() {
        Query query = em.createQuery("SELECT c FROM MenuCategory c WHERE c.parentMenuCategory IS NULL ORDER BY c.categoryName ASC");
        List<MenuCategory> rootMenuCategories = query.getResultList();

        for (MenuCategory rootMenuCategory : rootMenuCategories) {
            lazilyLoadSubMenuCategories(rootMenuCategory);

            rootMenuCategory.getMenuItems().size();
        }

        return rootMenuCategories;
    }

    @Override
    public List<MenuCategory> retrieveAllLeafMenuCategories() {
        Query query = em.createQuery("SELECT c FROM MenuCategory c WHERE c.subMenuCategories IS EMPTY ORDER BY c.categoryName ASC");
        List<MenuCategory> leafMenuCategories = query.getResultList();

        for (MenuCategory leafMenuCategory : leafMenuCategories) {
            leafMenuCategory.getMenuItems().size();
        }

        return leafMenuCategories;
    }

    @Override
    public List<MenuCategory> retrieveAllMenuCateogoriesWithoutMenuItems() {
        Query query = em.createQuery("SELECT c FROM MenuCategory c WHERE c.menuItems IS EMPTY ORDER BY c.categoryName ASC");
        List<MenuCategory> rootMenuCategories = query.getResultList();

        for (MenuCategory rootMenuCategory : rootMenuCategories) {
            rootMenuCategory.getParentMenuCategory();
        }

        return rootMenuCategories;
    }

    @Override
    public MenuCategory retrieveMenuCategoryById(Long menuCategoryId) throws MenuCategoryNotFoundException {
        MenuCategory menuCategory = em.find(MenuCategory.class, menuCategoryId);

        if (menuCategory != null) {
            return menuCategory;
        } else {
            throw new MenuCategoryNotFoundException("Category ID " + menuCategoryId + " does not exist!");
        }
    }

    @Override
    public void updateMenuCategory(MenuCategory menuCategory, Long parentMenuCategoryId) throws InputDataValidationException, MenuCategoryNotFoundException, UpdateMenuCategoryException {
        Set<ConstraintViolation<MenuCategory>> constraintViolations = validator.validate(menuCategory);

        if (constraintViolations.isEmpty()) {
            if (menuCategory.getMenuCategoryId() != null) {
                MenuCategory menuCategoryToUpdate = retrieveMenuCategoryById(menuCategory.getMenuCategoryId());

                Query query = em.createQuery("SELECT c FROM MenuCategory c WHERE c.categoryName = :inName AND c.menuCategoryId <> :inMenuCategoryId");
                query.setParameter("inName", menuCategory.getCategoryName());
                query.setParameter("inMenuCategoryId", menuCategory.getMenuCategoryId());

                if (!query.getResultList().isEmpty()) {
                    throw new UpdateMenuCategoryException("The name of the menu category to be updated is duplicated");
                }

                menuCategoryToUpdate.setCategoryName(menuCategory.getCategoryName());

                if (parentMenuCategoryId != null) {
                    if (menuCategoryToUpdate.getMenuCategoryId().equals(parentMenuCategoryId)) {
                        throw new UpdateMenuCategoryException("Category cannot be its own parent");
                    } else if (menuCategoryToUpdate.getParentMenuCategory() == null || (!menuCategoryToUpdate.getParentMenuCategory().getMenuCategoryId().equals(parentMenuCategoryId))) {
                        MenuCategory parentMenuCategoryToUpdate = retrieveMenuCategoryById(parentMenuCategoryId);

                        if (!parentMenuCategoryToUpdate.getMenuItems().isEmpty()) {
                            throw new UpdateMenuCategoryException("Parent category cannot have any product associated with it");
                        }

                        lazilyLoadSubMenuCategories(menuCategoryToUpdate);
                        if (isOwnChild(menuCategory.getMenuCategoryId(), parentMenuCategoryId)) {
                            throw new UpdateMenuCategoryException("Parent category cannot be its own child");
                        }

                        menuCategoryToUpdate.setParentMenuCategory(parentMenuCategoryToUpdate);
                    }
                } else {
                    menuCategoryToUpdate.setParentMenuCategory(null);
                }
            } else {
                throw new MenuCategoryNotFoundException("Category ID not provided for category to be updated");
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    private boolean isOwnChild(Long menuCategoryId, Long compareId) throws MenuCategoryNotFoundException {
        System.out.println("Entered isOwnChild");
        MenuCategory menuCategory = retrieveMenuCategoryById(menuCategoryId);
        for (MenuCategory mc : menuCategory.getSubMenuCategories()) {
            System.out.println("mc: " + mc);
            if (mc.getMenuCategoryId().equals(compareId)) {
                return true;
            } else {
                return isOwnChild(menuCategoryId, mc.getMenuCategoryId());
            }
        }
        System.out.println("Exit isOwnChild");
        return false;
    }

    @Override
    public void deleteMenuCategory(Long menuCategoryId) throws MenuCategoryNotFoundException, DeleteMenuCategoryException {
        MenuCategory menuCategoryToRemove = retrieveMenuCategoryById(menuCategoryId);

        if (!menuCategoryToRemove.getSubMenuCategories().isEmpty()) {
            throw new DeleteMenuCategoryException("Menu Category ID " + menuCategoryId + " is associated with existing sub-categories and cannot be deleted!");
        } else if (!menuCategoryToRemove.getMenuItems().isEmpty()) {
            throw new DeleteMenuCategoryException("Menu Category ID " + menuCategoryId + " is associated with existing products and cannot be deleted!");
        } else {
            menuCategoryToRemove.setParentMenuCategory(null);

            em.remove(menuCategoryToRemove);
        }
    }

    private void lazilyLoadSubMenuCategories(MenuCategory menuCategory) {
        for (MenuCategory mc : menuCategory.getSubMenuCategories()) {
            lazilyLoadSubMenuCategories(mc);
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<MenuCategory>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
