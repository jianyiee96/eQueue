package jsf.managedBean;

import ejb.session.stateless.MenuCategorySessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import entity.MenuCategory;
import entity.MenuItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuCategoryNotFoundException;
import util.exceptions.MenuItemNotFoundException;

@Named(value = "updateMenuItemManagedBean")
@ViewScoped
public class UpdateMenuItemManagedBean implements Serializable {

    @EJB
    private MenuCategorySessionBeanLocal menuCategorySessionBean;

    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;

    private Long menuItemIdToUpdate;
    private MenuItem menuItemToUpdate;
    private Long categoryId;
    private List<MenuCategory> menuCategories;

    public UpdateMenuItemManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        menuItemIdToUpdate = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("menuItemIdToUpdate");

        try {
            menuItemToUpdate = menuItemSessionBean.retrieveMenuItemById(menuItemIdToUpdate);
            categoryId = menuItemToUpdate.getMenuCategory().getMenuCategoryId();

            menuCategories = menuCategorySessionBean.retrieveAllLeafMenuCategories();
        } catch (MenuItemNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the menu item details: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void back(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("menuItemIdToView", menuItemIdToUpdate);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewMenuItemDetails.xhtml");
    }

    public void foo() {
    }

    public void updateMenuItem() {
        if (categoryId == 0) {
            categoryId = null;
        }

        try {
            menuItemSessionBean.updateMenuItem(menuItemToUpdate, categoryId);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Menu item updated successfully", null));
        } catch (InputDataValidationException | MenuCategoryNotFoundException | MenuItemNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating menu item: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public Long getMenuItemIdToUpdate() {
        return menuItemIdToUpdate;
    }

    public void setMenuItemIdToUpdate(Long menuItemIdToUpdate) {
        this.menuItemIdToUpdate = menuItemIdToUpdate;
    }

    public MenuItem getMenuItemToUpdate() {
        return menuItemToUpdate;
    }

    public void setMenuItemToUpdate(MenuItem menuItemToUpdate) {
        this.menuItemToUpdate = menuItemToUpdate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

}
