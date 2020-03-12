package jsf.managedBean;

import ejb.session.stateless.MenuCategorySessionBeanLocal;
import entity.MenuCategory;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exceptions.MenuCategoryNotFoundException;

@Named(value = "viewMenuCategoryDetailsManagedBean")
@ViewScoped
public class ViewMenuCategoryDetailsManagedBean implements Serializable{

    @EJB
    private MenuCategorySessionBeanLocal menuCategorySessionBean;
    
    
    private Long menuCategoryIdToView;
    private String backMode;
    private MenuCategory menuCategoryToView;
    
    public ViewMenuCategoryDetailsManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct() {
        menuCategoryIdToView = (Long) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("menuCategoryIdToView");
        backMode = (String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("backMode");

        try {
            menuCategoryToView = menuCategorySessionBean.retrieveMenuCategoryById(menuCategoryIdToView);
        } catch (MenuCategoryNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while retrieving the menu category details: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void foo() {
    }

    public void back(ActionEvent event) throws IOException {
        if (backMode == null || backMode.trim().length() == 0) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("viewAllMenuCategories.xhtml");
        } else {
            FacesContext.getCurrentInstance().getExternalContext().redirect(backMode + ".xhtml");
        }
    }

    public void updateMenuCategory(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("menuCategoryIdToUpdate", menuCategoryIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("updateMenuCategory.xhtml");
    }

    public void deleteMenuCategory(ActionEvent event) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("menuCategoryIdToDelete", menuCategoryIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("deleteMenuCategory.xhtml");
    }

    public MenuCategory getMenuCategoryToView() {
        return menuCategoryToView;
    }

    public void setMenuCategoryToView(MenuCategory menuCategoryToView) {
        this.menuCategoryToView = menuCategoryToView;
    }
}
