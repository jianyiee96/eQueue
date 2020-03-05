package jsf.managedBean;

import ejb.session.stateless.MenuItemSessionBeanLocal;
import entity.MenuItem;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

@Named(value = "viewAllMenuItemsManagedBean")
@RequestScoped

public class ViewAllMenuItemsManagedBean {

    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;

    private List<MenuItem> menuItems;

    public ViewAllMenuItemsManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        menuItems = menuItemSessionBean.retrieveAllMenuItems();
    }

    public void viewMenuItemDetails(ActionEvent event) throws IOException {
        Long menuItemIdToView = (Long) event.getComponent().getAttributes().get("menuItemId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("menuItemIdToView", menuItemIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewMenuItemDetails.xhtml");
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

}
