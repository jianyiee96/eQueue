package jsf.managedBean;

import ejb.session.stateless.MenuCategorySessionBeanLocal;
import entity.MenuCategory;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import util.exceptions.CreateNewMenuCategoryException;
import util.exceptions.InputDataValidationException;

@Named(value = "createNewMenuCategoryManagedBean")
@RequestScoped
public class CreateNewMenuCategoryManagedBean {

    @EJB
    private MenuCategorySessionBeanLocal menuCategorySessionBean;

    private MenuCategory menuCategory;
    private Long parentCategoryId;
    private List<MenuCategory> menuCategoriesWithoutItems;

    @PostConstruct
    public void postConstruct() {
        menuCategoriesWithoutItems = menuCategorySessionBean.retrieveAllMenuCateogoriesWithoutMenuItems();
    }

    public CreateNewMenuCategoryManagedBean() {
        menuCategory = new MenuCategory();
    }

    public void createNewMenuCategory() {
        if (parentCategoryId == 0) {
            parentCategoryId = null;
        }

        try {
            Long mc = menuCategorySessionBean.createNewMenuCategory(menuCategory, parentCategoryId);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New menu category created successfully (Menu Category ID: " + menuCategory.getMenuCategoryId() + ")", null));

            menuCategory = new MenuCategory();
            parentCategoryId = null;
        } catch (CreateNewMenuCategoryException | InputDataValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new menu category: " + ex.getMessage(), null));
        }
    }

    public MenuCategory getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(MenuCategory menuCategory) {
        this.menuCategory = menuCategory;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public List<MenuCategory> getMenuCategoriesWithoutItems() {
        return menuCategoriesWithoutItems;
    }

    public void setMenuCategoriesWithoutItems(List<MenuCategory> menuCategoriesWithoutItems) {
        this.menuCategoriesWithoutItems = menuCategoriesWithoutItems;
    }
    
    

}
