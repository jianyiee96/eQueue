/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exceptions;

/**
 *
 * @author keith
 */
public class UsernameExistsException extends Exception {

    /**
     * Creates a new instance of <code>UsernameExistsException</code> without
     * detail message.
     */
    public UsernameExistsException() {
    }

    /**
     * Constructs an instance of <code>UsernameExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UsernameExistsException(String msg) {
        super(msg);
    }
}
