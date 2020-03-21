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
public class QueueDoesNotExistException extends Exception {

    /**
     * Creates a new instance of <code>QueueDoesNotExistException</code> without
     * detail message.
     */
    public QueueDoesNotExistException() {
    }

    /**
     * Constructs an instance of <code>QueueDoesNotExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public QueueDoesNotExistException(String msg) {
        super(msg);
    }
}
