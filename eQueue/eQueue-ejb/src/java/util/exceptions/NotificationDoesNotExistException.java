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
public class NotificationDoesNotExistException extends Exception {

    /**
     * Creates a new instance of <code>NotificationDoesNotExistException</code>
     * without detail message.
     */
    public NotificationDoesNotExistException() {
    }

    /**
     * Constructs an instance of <code>NotificationDoesNotExistException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NotificationDoesNotExistException(String msg) {
        super(msg);
    }
}
