/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.MenuCategory;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateNewMenuCategoryException;
import util.exceptions.DeleteMenuCategoryException;
import util.exceptions.InputDataValidationException;
import util.exceptions.MenuCategoryNotFoundException;
import util.exceptions.UpdateMenuCategoryException;

/**
 *
 * @author Bryan
 */
@Local
public interface MenuCategorySessionBeanLocal {

    public void deleteMenuCategory(Long menuCategoryId) throws MenuCategoryNotFoundException, DeleteMenuCategoryException;

    public void updateMenuCategory(MenuCategory menuCategory, Long parentMenuCategoryId) throws InputDataValidationException, MenuCategoryNotFoundException, UpdateMenuCategoryException;

    public MenuCategory retrieveMenuCategoryById(Long menuCategoryId) throws MenuCategoryNotFoundException;

    public List<MenuCategory> retrieveAllMenuCateogoriesWithoutMenuItems();

    public List<MenuCategory> retrieveAllLeafMenuCategories();

    public List<MenuCategory> retrieveAllRootMenuCategories();

    public List<MenuCategory> retrieveAllMenuCategories();

    public Long createNewMenuCategory(MenuCategory newMenuCategory, Long parentMenuCategoryId) throws InputDataValidationException, CreateNewMenuCategoryException;
    
}
