package util.exceptions;

/**
 *
 * @author tanwk
 */
public class CreditCardNotFoundException extends Exception {

    public CreditCardNotFoundException() {
    }

    public CreditCardNotFoundException(String msg) {
        super(msg);
    }
}
