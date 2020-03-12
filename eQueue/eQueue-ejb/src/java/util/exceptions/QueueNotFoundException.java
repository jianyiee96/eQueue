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
public class QueueNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>QueueNotFoundException</code> without
     * detail message.
     */
    public QueueNotFoundException() {
    }

    /**
     * Constructs an instance of <code>QueueNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public QueueNotFoundException(String msg) {
        super(msg);
    }
}
