package util.exceptions;

public class CustomerInvalidPasswordException extends Exception {

    public CustomerInvalidPasswordException() {
    }

    public CustomerInvalidPasswordException(String msg) {
        super(msg);
    }
}
