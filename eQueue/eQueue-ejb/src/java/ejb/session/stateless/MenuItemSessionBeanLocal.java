package ejb.session.stateless;

import entity.MenuItem;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateNewMenuItemException;
import util.exceptions.DeleteMenuItemException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuCategoryNotFoundException;
import util.exceptions.MenuItemNotFoundException;
import util.exceptions.MenuItemNotUniqueException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateMenuItemException;

@Local
public interface MenuItemSessionBeanLocal {
    
    public MenuItem retrieveMenuItemById(Long menuItemId) throws MenuItemNotFoundException;

    public List<MenuItem> retrieveAllMenuItems();
    
    public List<MenuItem> retrieveAllMenuItemsByCategory(Long categoryId);

    public void updateMenuItem(MenuItem menuItem, Long menuCategoryId) throws MenuItemNotFoundException, MenuCategoryNotFoundException, UpdateMenuItemException, InputDataValidationException;

    public void deleteMenuItem(Long menuItemId) throws MenuItemNotFoundException, DeleteMenuItemException;

    public Long createNewMenuItem(MenuItem newMenuItem, Long menuCategoryId) throws MenuItemNotUniqueException, UnknownPersistenceException, InputDataValidationException, CreateNewMenuItemException;
    
}
