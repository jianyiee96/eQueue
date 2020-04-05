package util.exceptions;

public class CustomerOrderNotFoundException extends Exception {

    public CustomerOrderNotFoundException() {
    }

    public CustomerOrderNotFoundException(String msg) {
        super(msg);
    }
}
