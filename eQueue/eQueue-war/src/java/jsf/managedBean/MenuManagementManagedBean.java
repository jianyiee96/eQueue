package jsf.managedBean;

import ejb.session.stateless.MenuCategorySessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import entity.MenuCategory;
import entity.MenuItem;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import util.enumeration.MenuItemAvailabilityEnum;
import util.exceptions.CreateNewMenuCategoryException;
import util.exceptions.CreateNewMenuItemException;
import util.exceptions.DeleteMenuCategoryException;
import util.exceptions.DeleteMenuItemException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuCategoryNotFoundException;
import util.exceptions.MenuItemNotFoundException;
import util.exceptions.MenuItemNotUniqueException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateMenuCategoryException;

@Named(value = "menuManagementManagedBean")
@ViewScoped
public class MenuManagementManagedBean implements Serializable {

    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;
    @EJB
    private MenuCategorySessionBeanLocal menuCategorySessionBean;

    @Inject
    private ViewMenuItemManagedBean viewMenuItemManagedBean;

    @Inject
    private ViewMenuCategoryManagedBean viewMenuCategoryManagedBean;

    private List<MenuItem> menuItems;
    private List<MenuItem> filteredMenuItems;
    private List<MenuCategory> menuCategories;
    private List<MenuCategory> leafMenuCategories;
    private List<MenuCategory> menuCategoriesWithoutItems;
    private List<MenuCategory> filteredMenuCategories;
    private List<MenuCategory> rootMenuCategories;
    private MenuItemAvailabilityEnum[] menuItemAvailabilities;

    private MenuItem menuItemToCreate;
    private Long menuCategoryIdToCreate;

    private MenuItem menuItemToUpdate;
    private Long menuCategoryIdToUpdate;

    private MenuCategory menuCategoryToCreate;
    private Long parentMenuCategoryIdToCreate;

    private MenuCategory menuCategoryToUpdate;
    private Long parentMenuCategoryIdToUpdate;

    private TreeNode treeNode;
    private TreeNode selectedTreeNode;

    public MenuManagementManagedBean() {
        menuItemToCreate = new MenuItem();
        menuItemToUpdate = new MenuItem();
        menuCategoryToCreate = new MenuCategory();
    }

    @PostConstruct
    public void postConstruct() {
        menuItems = menuItemSessionBean.retrieveAllMenuItems();
        menuItemAvailabilities = getMenuItemAvailabilities();
        menuCategories = menuCategorySessionBean.retrieveAllMenuCategories();

        //To choose as category for menu item
        leafMenuCategories = menuCategorySessionBean.retrieveAllLeafMenuCategories();

        //To choose as parent category
        menuCategoriesWithoutItems = menuCategorySessionBean.retrieveAllMenuCateogoriesWithoutMenuItems();
        rootMenuCategories = menuCategorySessionBean.retrieveAllRootMenuCategories();

        processTree();
    }

    private void processTree() {
        treeNode = new DefaultTreeNode("Root", null);
        for (MenuCategory menuCategory : rootMenuCategories) {
            createTreeNode(menuCategory, treeNode);
        }
    }

    private void createTreeNode(MenuCategory menuCategory, TreeNode parentTreeNode) {
        TreeNode treeNode = new DefaultTreeNode("menuCategory", menuCategory, parentTreeNode);

        if (!menuCategory.getMenuItems().isEmpty()) {
            for (MenuItem mi : menuCategory.getMenuItems()) {
                TreeNode menuItemTreeNode = new DefaultTreeNode("menuItem", mi, treeNode);
            }
        }

        for (MenuCategory mc : menuCategory.getSubMenuCategories()) {
            createTreeNode(mc, treeNode);
        }
    }

    public void selectTreeNode() {
        if (selectedTreeNode != null) {
            Object selectedItem = selectedTreeNode.getData();
            MenuCategory mc = null;
            MenuItem mi = null;
            if (selectedItem instanceof MenuCategory) {
                mc = (MenuCategory) selectedItem;
            } else if (selectedItem instanceof MenuItem) {
                mi = (MenuItem) selectedItem;
            }
            if (mc != null) {
                if (viewMenuCategoryManagedBean != null) {
                    viewMenuCategoryManagedBean.setMenuCategoryToView(mc);
                }
                PrimeFaces.current().executeScript("PF('dialogViewMenuCategory').show()");
            } else {
                if (viewMenuItemManagedBean != null) {
                    viewMenuItemManagedBean.setMenuItemToView(mi);
                }
                PrimeFaces.current().executeScript("PF('dialogViewMenuItem').show()");
            }
        }
    }

    private TreeNode searchParentCategoryTreeNode(Long parentCategoryId, TreeNode treeNode) {
        for (TreeNode tn : treeNode.getChildren()) {
            if (tn.getType().equals("menuCategory")) {
                MenuCategory mc = (MenuCategory) tn.getData();

                if (mc.getMenuCategoryId().equals(parentCategoryId)) {
                    return tn;
                } else {
                    return searchParentCategoryTreeNode(parentCategoryId, tn);
                }
            }
        }
        return null;
    }

    private TreeNode searchMenuItemTreeNode(Long menuItemId, Long parentCategoryId, TreeNode treeNode) {
        TreeNode parentTreeNode = searchParentCategoryTreeNode(parentCategoryId, treeNode);
        for (TreeNode tn : treeNode.getChildren()) {
            if (tn.getType().equals("menuItem")) {
                MenuItem mi = (MenuItem) tn.getData();

                if (mi.getMenuItemId().equals(menuItemId)) {
                    return tn;
                }
            }
        }
        return null;
    }

    public void collapsingORexpanding(TreeNode n, boolean option) {
        System.out.println("Called***********************************");
        if (n.getChildren().size() == 0) {
            n.setSelected(false);
        } else {
            for (TreeNode s : n.getChildren()) {
                collapsingORexpanding(s, option);
            }
            n.setExpanded(option);
            n.setSelected(false);
        }
    }

    public void createNewMenuItem() {
        if (menuCategoryIdToCreate == 0) {
            menuCategoryIdToCreate = null;
        }

        try {
            Long menuItemId = menuItemSessionBean.createNewMenuItem(menuItemToCreate, menuCategoryIdToCreate);
            menuItemToCreate = menuItemSessionBean.retrieveMenuItemById(menuItemId);

            menuItems.add(menuItemToCreate);
            if (filteredMenuItems != null) {
                filteredMenuItems.add(menuItemToCreate);
            }

            postConstruct();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New menu item created successfully (Menu Item ID: " + menuItemToCreate.getMenuItemId() + ")", null));

            menuItemToCreate = new MenuItem();
            menuCategoryIdToCreate = null;
        } catch (InputDataValidationException | CreateNewMenuItemException | UnknownPersistenceException | MenuItemNotFoundException | MenuItemNotUniqueException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new menu item: " + ex.getMessage(), null));
        }

    }

    public void createNewMenuCategory() {
        if (parentMenuCategoryIdToCreate == 0) {
            parentMenuCategoryIdToCreate = null;
        }

        try {
            Long menuCategoryId = menuCategorySessionBean.createNewMenuCategory(menuCategoryToCreate, parentMenuCategoryIdToCreate);
            menuCategoryToCreate = menuCategorySessionBean.retrieveMenuCategoryById(menuCategoryId);

            if (filteredMenuCategories != null) {
                filteredMenuCategories.add(menuCategoryToCreate);
            }

            postConstruct();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New menu category created successfully (Menu Category ID: " + menuCategoryToCreate.getMenuCategoryId() + ")", null));

            menuCategoryToCreate = new MenuCategory();
            parentMenuCategoryIdToCreate = null;

        } catch (InputDataValidationException | MenuCategoryNotFoundException | CreateNewMenuCategoryException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new menu Category: " + ex.getMessage(), null));
        }

    }

    public void doUpdateMenuItem(ActionEvent event) {

        menuItemToUpdate = (MenuItem) event.getComponent().getAttributes().get("menuItemToUpdate");

        menuCategoryIdToUpdate = menuItemToUpdate.getMenuCategory().getMenuCategoryId();

    }

    public void updateMenuItem(ActionEvent event) {

        if (menuCategoryIdToUpdate == 0) {
            menuCategoryIdToUpdate = null;
        }

        try {
            MenuItem menuItemBeforeUpdating = menuItemSessionBean.retrieveMenuItemById(menuItemToUpdate.getMenuItemId());
            Long parentCategoryBeforeUpdating = menuItemBeforeUpdating.getMenuCategory().getMenuCategoryId();

            menuItemSessionBean.updateMenuItem(menuItemToUpdate, menuCategoryIdToUpdate);

            for (MenuCategory mc : menuCategories) {
                if (mc.getMenuCategoryId().equals(menuCategoryIdToUpdate)) {
                    menuItemToUpdate.setMenuCategory(mc);
                    break;
                }
            }

            postConstruct();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Menu item updated successfully", null));
        } catch (InputDataValidationException | MenuCategoryNotFoundException | MenuItemNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating menu item: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void doUpdateMenuCategory(ActionEvent event) {

        menuCategoryToUpdate = (MenuCategory) event.getComponent().getAttributes().get("menuCategoryToUpdate");
        if (menuCategoryToUpdate.getParentMenuCategory() != null) {
            parentMenuCategoryIdToUpdate = menuCategoryToUpdate.getParentMenuCategory().getMenuCategoryId();
        }

    }

    public void updateMenuCategory(ActionEvent event) {
        if (parentMenuCategoryIdToUpdate == 0) {
            parentMenuCategoryIdToUpdate = null;
        }

        try {
            menuCategorySessionBean.updateMenuCategory(menuCategoryToUpdate, parentMenuCategoryIdToUpdate);

            postConstruct();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Menu category updated successfully", null));
        } catch (InputDataValidationException | MenuCategoryNotFoundException | UpdateMenuCategoryException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating menu cateogory: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void deleteMenuItem(ActionEvent event) {
        try {
            MenuItem menuItemToDelete = (MenuItem) event.getComponent().getAttributes().get("menuItemToDelete");
            menuItemSessionBean.deleteMenuItem(menuItemToDelete.getMenuItemId());

            menuItems.remove(menuItemToDelete);
            if (filteredMenuItems != null) {
                filteredMenuItems.remove(menuItemToDelete);
            }

            postConstruct();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Menu Item deleted successfully", null));
        } catch (MenuItemNotFoundException | DeleteMenuItemException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting menu item: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public void deleteMenuCategory(ActionEvent event) {
        try {
            MenuCategory menuCategoryToDelete = (MenuCategory) event.getComponent().getAttributes().get("menuCategoryToDelete");
            menuCategorySessionBean.deleteMenuCategory(menuCategoryToDelete.getMenuCategoryId());

            if (filteredMenuCategories != null) {
                filteredMenuCategories.remove(menuCategoryToDelete);
            }

            postConstruct();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Menu Category deleted successfully", null));
        } catch (MenuCategoryNotFoundException | DeleteMenuCategoryException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting menu category: " + ex.getMessage(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

//    private void refreshCategories() {
//        menuCategories = menuCategorySessionBean.retrieveAllMenuCategories();
//        leafMenuCategories = menuCategorySessionBean.retrieveAllLeafMenuCategories();
//        menuCategoriesWithoutItems = menuCategorySessionBean.retrieveAllMenuCateogoriesWithoutMenuItems();
//        rootMenuCategories = menuCategorySessionBean.retrieveAllRootMenuCategories();
//        
//        menuItems = menuItemSessionBean.retrieveAllMenuItems();
//    }
    public ViewMenuItemManagedBean getViewMenuItemManagedBean() {
        return viewMenuItemManagedBean;
    }

    public void setViewMenuItemManagedBean(ViewMenuItemManagedBean viewMenuItemManagedBean) {
        this.viewMenuItemManagedBean = viewMenuItemManagedBean;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public List<MenuItem> getFilteredMenuItems() {
        return filteredMenuItems;
    }

    public void setFilteredMenuItems(List<MenuItem> filteredMenuItems) {
        this.filteredMenuItems = filteredMenuItems;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

    public List<MenuCategory> getLeafMenuCategories() {
        return leafMenuCategories;
    }

    public void setLeafMenuCategories(List<MenuCategory> leafMenuCategories) {
        this.leafMenuCategories = leafMenuCategories;
    }

    public List<MenuCategory> getMenuCategoriesWithoutItems() {
        return menuCategoriesWithoutItems;
    }

    public void setMenuCategoriesWithoutItems(List<MenuCategory> menuCategoriesWithoutItems) {
        this.menuCategoriesWithoutItems = menuCategoriesWithoutItems;
    }

    public MenuItem getMenuItemToCreate() {
        return menuItemToCreate;
    }

    public void setMenuItemToCreate(MenuItem menuItemToCreate) {
        this.menuItemToCreate = menuItemToCreate;
    }

    public Long getMenuCategoryIdToCreate() {
        return menuCategoryIdToCreate;
    }

    public void setMenuCategoryIdToCreate(Long menuCategoryIdToCreate) {
        this.menuCategoryIdToCreate = menuCategoryIdToCreate;
    }

    public MenuItem getMenuItemToUpdate() {
        return menuItemToUpdate;
    }

    public void setMenuItemToUpdate(MenuItem menuItemToUpdate) {
        this.menuItemToUpdate = menuItemToUpdate;
    }

    public Long getMenuCategoryIdToUpdate() {
        return menuCategoryIdToUpdate;
    }

    public void setMenuCategoryIdToUpdate(Long menuCategoryIdToUpdate) {
        this.menuCategoryIdToUpdate = menuCategoryIdToUpdate;
    }

    public ViewMenuCategoryManagedBean getViewMenuCategoryManagedBean() {
        return viewMenuCategoryManagedBean;
    }

    public void setViewMenuCategoryManagedBean(ViewMenuCategoryManagedBean viewMenuCategoryManagedBean) {
        this.viewMenuCategoryManagedBean = viewMenuCategoryManagedBean;
    }

    public List<MenuCategory> getFilteredMenuCategories() {
        return filteredMenuCategories;
    }

    public void setFilteredMenuCategories(List<MenuCategory> filteredMenuCategories) {
        this.filteredMenuCategories = filteredMenuCategories;
    }

    public MenuCategory getMenuCategoryToCreate() {
        return menuCategoryToCreate;
    }

    public void setMenuCategoryToCreate(MenuCategory menuCategoryToCreate) {
        this.menuCategoryToCreate = menuCategoryToCreate;
    }

    public Long getParentMenuCategoryIdToCreate() {
        return parentMenuCategoryIdToCreate;
    }

    public void setParentMenuCategoryIdToCreate(Long parentMenuCategoryIdToCreate) {
        this.parentMenuCategoryIdToCreate = parentMenuCategoryIdToCreate;
    }

    public MenuCategory getMenuCategoryToUpdate() {
        return menuCategoryToUpdate;
    }

    public void setMenuCategoryToUpdate(MenuCategory menuCategoryToUpdate) {
        this.menuCategoryToUpdate = menuCategoryToUpdate;
    }

    public Long getParentMenuCategoryIdToUpdate() {
        return parentMenuCategoryIdToUpdate;
    }

    public void setParentMenuCategoryIdToUpdate(Long parentMenuCategoryIdToUpdate) {
        this.parentMenuCategoryIdToUpdate = parentMenuCategoryIdToUpdate;
    }

    public MenuItemAvailabilityEnum[] getMenuItemAvailabilities() {
        return MenuItemAvailabilityEnum.values();
    }

    public List<MenuCategory> getRootMenuCategories() {
        return rootMenuCategories;
    }

    public void setRootMenuCategories(List<MenuCategory> rootMenuCategories) {
        this.rootMenuCategories = rootMenuCategories;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
    }

    public TreeNode getSelectedTreeNode() {
        return selectedTreeNode;
    }

    public void setSelectedTreeNode(TreeNode selectedTreeNode) {
        this.selectedTreeNode = selectedTreeNode;
    }
}
