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
public class CustomerInvalidLoginCredentialException extends Exception {

    /**
     * Creates a new instance of
     * <code>CustomerInvalidLoginCredentialException</code> without detail
     * message.
     */
    public CustomerInvalidLoginCredentialException() {
    }

    /**
     * Constructs an instance of
     * <code>CustomerInvalidLoginCredentialException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public CustomerInvalidLoginCredentialException(String msg) {
        super(msg);
    }
}
