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
public class CustomerNotUniqueException extends Exception {

    /**
     * Creates a new instance of <code>CustomerExistException</code> without
     * detail message.
     */
    public CustomerNotUniqueException() {
    }

    /**
     * Constructs an instance of <code>CustomerExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerNotUniqueException(String msg) {
        super(msg);
    }
}
