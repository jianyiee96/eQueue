package jsf.managedBean;

import ejb.session.stateless.MenuCategorySessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import entity.MenuCategory;
import entity.MenuItem;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import util.exceptions.CreateNewMenuItemException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuItemNotFoundException;
import util.exceptions.MenuItemNotUniqueException;
import util.exceptions.UnknownPersistenceException;

@Named(value = "createNewMenuItemManagedBean")
@RequestScoped
public class CreateNewMenuItemManagedBean {

    @EJB
    private MenuCategorySessionBeanLocal menuCategorySessionBean;

    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;

    private MenuItem menuItem;
    private List<MenuCategory> leafMenuCategories;
    private Long categoryId;

    @PostConstruct
    public void postConstruct() {
        leafMenuCategories = menuCategorySessionBean.retrieveAllLeafMenuCategories();
    }

    public CreateNewMenuItemManagedBean() {
        menuItem = new MenuItem();
    }

    public void createNewMenuItem() {
        if (categoryId == 0) {
            categoryId = null;
        }

        try {
            Long menuItemId = menuItemSessionBean.createNewMenuItem(menuItem, categoryId);
            menuItem = menuItemSessionBean.retrieveMenuItemById(menuItemId);

            categoryId = null;

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New menu item created successfully (Menu Item ID: " + menuItem.getMenuItemId() + ")", null));
        } catch (InputDataValidationException | CreateNewMenuItemException | UnknownPersistenceException | MenuItemNotFoundException | MenuItemNotUniqueException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new product: " + ex.getMessage(), null));
        }

        menuItem = new MenuItem();
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public List<MenuCategory> getLeafMenuCategories() {
        return leafMenuCategories;
    }

    public void setLeafMenuCategories(List<MenuCategory> leafMenuCategories) {
        this.leafMenuCategories = leafMenuCategories;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

}
