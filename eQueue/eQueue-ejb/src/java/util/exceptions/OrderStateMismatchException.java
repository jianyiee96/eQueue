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
public class OrderStateMismatchException extends Exception {

    /**
     * Creates a new instance of <code>OrderStateMismatchException</code>
     * without detail message.
     */
    public OrderStateMismatchException() {
    }

    /**
     * Constructs an instance of <code>OrderStateMismatchException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public OrderStateMismatchException(String msg) {
        super(msg);
    }
}
