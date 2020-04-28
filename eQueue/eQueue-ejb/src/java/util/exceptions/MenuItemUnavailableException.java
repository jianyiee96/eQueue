/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exceptions;

/**
 *
 * @author User
 */
public class MenuItemUnavailableException extends Exception {

    /**
     * Creates a new instance of <code>MenuItemUnavailableException</code>
     * without detail message.
     */
    public MenuItemUnavailableException() {
    }

    /**
     * Constructs an instance of <code>MenuItemUnavailableException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public MenuItemUnavailableException(String msg) {
        super(msg);
    }
}
