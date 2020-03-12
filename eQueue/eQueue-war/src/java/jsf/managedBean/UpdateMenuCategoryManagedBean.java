package jsf.managedBean;

import ejb.session.stateless.MenuCategorySessionBeanLocal;
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

@Named(value = "udpateMenuCategoryManagedBean")
@ViewScoped
public class UpdateMenuCategoryManagedBean implements Serializable {

    @EJB
    private MenuCategorySessionBeanLocal menuCategorySessionBean;

    private Long menuCategoryIdToUpdate;
    private MenuCategory menuCategoryToUpdate;
    private Long parentCategoryId;
    private List<MenuCategory> menuCategories;

    public UpdateMenuCategoryManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        menuCategoryIdToUpdate = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("menuCategoryIdToUpdate");

        try {
            menuCategoryToUpdate = menuCategorySessionBean.retrieveMenuCategoryById(menuCategoryIdToUpdate);
            parentCategoryId = menuCategoryToUpdate.getParentMenuCategory().getMenuCategoryId();

            menuCategories = menuCategorySessionBean.retrieveAllMenuCateogoriesWithoutMenuItems();
        } catch (MenuCategoryNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the menu category details: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void back(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("menuCategoryIdToView", menuCategoryIdToUpdate);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewMenuCategoryDetails.xhtml");
    }

    public void foo() {
    }

    public void updateMenuCategory() {
        if (parentCategoryId == 0) {
            parentCategoryId = null;
        }

        try {
            menuCategorySessionBean.updateMenuCategory(menuCategoryToUpdate, parentCategoryId);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Menu category updated successfully", null));
        } catch (InputDataValidationException | MenuCategoryNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating menu category: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public MenuCategory getMenuCategoryToUpdate() {
        return menuCategoryToUpdate;
    }

    public void setMenuCategoryToUpdate(MenuCategory menuCategoryToUpdate) {
        this.menuCategoryToUpdate = menuCategoryToUpdate;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

}
