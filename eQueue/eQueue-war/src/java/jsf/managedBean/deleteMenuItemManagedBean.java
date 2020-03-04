package jsf.managedBean;

import ejb.session.stateless.MenuItemSessionBeanLocal;
import entity.MenuItem;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.DeleteMenuItemException;
import util.exceptions.MenuItemNotFoundException;

@Named(value = "deleteMenuItemManagedBean")
@ViewScoped
public class deleteMenuItemManagedBean implements Serializable {

    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;

    private Long menuItemIdToDelete;
    private MenuItem menuItemToDelete;

    public deleteMenuItemManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        menuItemIdToDelete = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("menuItemIdToDelete");

        try {
            menuItemToDelete = menuItemSessionBean.retrieveMenuItemById(menuItemIdToDelete);
        } catch (MenuItemNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the menu item details: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void deleteMenuItem() {
        try {
            menuItemSessionBean.deleteMenuItem(menuItemIdToDelete);

            menuItemIdToDelete = null;
            menuItemToDelete = null;

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product deleted successfully", null));
        } catch (MenuItemNotFoundException | DeleteMenuItemException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting menu item: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void back(ActionEvent event) throws IOException {
        if (menuItemToDelete == null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("viewAllMenuItems.xhtml");
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("menuItemIdToView", menuItemIdToDelete);
            FacesContext.getCurrentInstance().getExternalContext().redirect("viewMenuItemDetails.xhtml");
        }
    }

    public void foo() {
    }

    public Long getMenuItemIdToDelete() {
        return menuItemIdToDelete;
    }

    public void setMenuItemIdToDelete(Long menuItemIdToDelete) {
        this.menuItemIdToDelete = menuItemIdToDelete;
    }

    public MenuItem getMenuItemToDelete() {
        return menuItemToDelete;
    }

    public void setMenuItemToDelete(MenuItem menuItemToDelete) {
        this.menuItemToDelete = menuItemToDelete;
    }
    
    
}
