package util.exceptions;

public class OrderLineItemNotFoundException extends Exception {

    public OrderLineItemNotFoundException() {
    }

    public OrderLineItemNotFoundException(String msg) {
        super(msg);
    }
}
