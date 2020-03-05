package util.exceptions;

public class MenuItemNotFoundException extends Exception {

    public MenuItemNotFoundException() {
    }

    public MenuItemNotFoundException(String msg) {
        super(msg);
    }
}
