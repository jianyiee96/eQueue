/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.MenuCategory;
import java.util.List;

/**
 *
 * @author User
 */
public class RetrieveAllCategoryRsp {
    
    List<MenuCategory> menuCategories;

    public RetrieveAllCategoryRsp() {
    }

    public RetrieveAllCategoryRsp(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }
    
    
    
}
