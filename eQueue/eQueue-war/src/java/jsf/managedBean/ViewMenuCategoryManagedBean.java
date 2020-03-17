package jsf.managedBean;

import entity.MenuCategory;
import entity.MenuItem;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named(value = "viewMenuCategoryManagedBean")
@ViewScoped
public class ViewMenuCategoryManagedBean implements Serializable {

    private MenuCategory menuCategoryToView;
    private TreeNode treeNode;

    public ViewMenuCategoryManagedBean() {
        menuCategoryToView = new MenuCategory();
    }

    @PostConstruct
    public void postConstruct() {

    }

    public void processTree() {
        treeNode = new DefaultTreeNode("Root", null);
        TreeNode currentNode = createParentBranch(menuCategoryToView, treeNode);
        createChildrenBranch(menuCategoryToView, currentNode);
        collapsingORexpanding(treeNode, true);
    }

    private void createChildrenBranch(MenuCategory menuCategory, TreeNode parentTreeNode) {

        if (!menuCategory.getMenuItems().isEmpty()) {
            for (MenuItem mi : menuCategory.getMenuItems()) {
                TreeNode menuItemTreeNode = new DefaultTreeNode("menuItem", mi, parentTreeNode);
            }
        }

        for (MenuCategory mc : menuCategory.getSubMenuCategories()) {
            TreeNode treeNode = new DefaultTreeNode("menuCategory", mc, parentTreeNode);
            createChildrenBranch(mc, treeNode);
        }

    }

    public void collapsingORexpanding(TreeNode n, boolean option) {
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

    private TreeNode createParentBranch(MenuCategory menuCategory, TreeNode currentTreeNode) {
        MenuCategory parentMenuCategory = menuCategory.getParentMenuCategory();
        if (parentMenuCategory == null) {
            return new DefaultTreeNode("menuCategory", menuCategory, currentTreeNode);
        } else {
            TreeNode newTreeNode = createParentBranch(parentMenuCategory, currentTreeNode);
            TreeNode treeNodeToReturn = new DefaultTreeNode("menuCategory", menuCategory, newTreeNode);
            return treeNodeToReturn;
        }
    }

    public MenuCategory getMenuCategoryToView() {
        return menuCategoryToView;
    }

    public void setMenuCategoryToView(MenuCategory menuCategoryToView) {
        this.menuCategoryToView = menuCategoryToView;
        processTree();
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
    }

}
