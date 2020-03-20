package util.exceptions;

public class ShoppingCartNotFoundException extends Exception {

    public ShoppingCartNotFoundException() {
    }

    public ShoppingCartNotFoundException(String msg) {
        super(msg);
    }
}
