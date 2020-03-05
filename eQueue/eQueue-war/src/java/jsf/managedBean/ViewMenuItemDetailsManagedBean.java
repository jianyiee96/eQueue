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
import util.exceptions.MenuItemNotFoundException;

@Named(value = "viewMenuItemDetailsManagedBean")
@ViewScoped
public class ViewMenuItemDetailsManagedBean implements Serializable{

    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;

    private Long menuItemIdToView;
    private String backMode;
    private MenuItem menuItemToView;

    public ViewMenuItemDetailsManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        menuItemIdToView = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("menuItemIdToView");
        backMode = (String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("backMode");

        try {
            menuItemToView = menuItemSessionBean.retrieveMenuItemById(menuItemIdToView);
        } catch (MenuItemNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the menu item details: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void foo() {
    }

    public void back(ActionEvent event) throws IOException {
        if (backMode == null || backMode.trim().length() == 0) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("viewAllMenuItems.xhtml");
        } else {
            FacesContext.getCurrentInstance().getExternalContext().redirect(backMode + ".xhtml");
        }
    }

    public void updateMenuItem(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("menuItemIdToUpdate", menuItemIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("updateMenuItem.xhtml");
    }

    public void deleteMenuItem(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("menuItemIdToDelete", menuItemIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("deleteMenuItem.xhtml");
    }

    public MenuItem getMenuItemToView() {
        return menuItemToView;
    }

    public void setMenuItemToView(MenuItem menuItemToView) {
        this.menuItemToView = menuItemToView;
    }

}
