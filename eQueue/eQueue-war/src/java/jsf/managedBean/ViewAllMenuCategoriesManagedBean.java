package jsf.managedBean;

import ejb.session.stateless.MenuCategorySessionBeanLocal;
import entity.MenuCategory;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

@Named(value = "viewAllMenuCategoriesManagedBean")
@RequestScoped

public class ViewAllMenuCategoriesManagedBean {

    @EJB
    private MenuCategorySessionBeanLocal menuCategorySessionBean;
    
    private List<MenuCategory> menuCategories;
    
    public ViewAllMenuCategoriesManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct() {
        menuCategories = menuCategorySessionBean.retrieveAllMenuCategories();
    }
    
    public void viewMenuCategoryDetails(ActionEvent event) throws IOException {
        Long menuCategoryIdToView = (Long) event.getComponent().getAttributes().get("menuCategoryId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("menuCategoryIdToView", menuCategoryIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewMenuCategoryDetails.xhtml");
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }
    
}
