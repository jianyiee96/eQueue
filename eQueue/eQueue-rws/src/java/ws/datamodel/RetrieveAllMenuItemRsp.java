/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.MenuItem;
import java.util.List;

/**
 *
 * @author User
 */
public class RetrieveAllMenuItemRsp {
    
    List<MenuItem> menuItems;

    public RetrieveAllMenuItemRsp() {
    }

    public RetrieveAllMenuItemRsp(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
    
    
    
}
