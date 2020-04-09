/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.ShoppingCart;

/**
 *
 * @author User
 */
public class SaveShoppingCartReq {
    
    Long customerId;
    ShoppingCart shoppingCart;

    public SaveShoppingCartReq() {
    }

    public SaveShoppingCartReq(Long customerId, ShoppingCart shoppingCart) {
        this.customerId = customerId;
        this.shoppingCart = shoppingCart;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
    
    
    
}
