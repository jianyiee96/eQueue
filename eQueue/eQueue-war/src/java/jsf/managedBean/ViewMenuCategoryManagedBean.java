package jsf.managedBean;

import entity.MenuCategory;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "viewMenuCategoryManagedBean")
@ViewScoped
public class ViewMenuCategoryManagedBean implements Serializable {

    private MenuCategory menuCategoryToView;

    public ViewMenuCategoryManagedBean() {
        menuCategoryToView = new MenuCategory();
    }

    @PostConstruct
    public void postConstruct() {
    }

    public MenuCategory getMenuCategoryToView() {
        return menuCategoryToView;
    }

    public void setMenuCategoryToView(MenuCategory menuCategoryToView) {
        this.menuCategoryToView = menuCategoryToView;
    }

}
