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
public class UnableToJoinQueueException extends Exception {

    /**
     * Creates a new instance of <code>UnableToJoinQueueException</code> without
     * detail message.
     */
    public UnableToJoinQueueException() {
    }

    /**
     * Constructs an instance of <code>UnableToJoinQueueException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToJoinQueueException(String msg) {
        super(msg);
    }
}
