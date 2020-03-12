package jsf.managedBean;

import entity.MenuItem;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "viewMenuItemManagedBean")
@ViewScoped
public class ViewMenuItemManagedBean implements Serializable {

    private MenuItem menuItemToView;

    public ViewMenuItemManagedBean() {
        menuItemToView = new MenuItem();
    }

    @PostConstruct
    public void postConstruct() {
    }

    public MenuItem getMenuItemToView() {
        return menuItemToView;
    }

    public void setMenuItemToView(MenuItem menuItemToView) {
        this.menuItemToView = menuItemToView;
    }

}
