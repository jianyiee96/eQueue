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
public class PriceMismatchException extends Exception {

    /**
     * Creates a new instance of <code>PriceMismatchException</code> without
     * detail message.
     */
    public PriceMismatchException() {
    }

    /**
     * Constructs an instance of <code>PriceMismatchException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PriceMismatchException(String msg) {
        super(msg);
    }
}
