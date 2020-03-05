package util.exceptions;

public class MenuItemNotUniqueException extends Exception {

    public MenuItemNotUniqueException() {
    }

    public MenuItemNotUniqueException(String msg) {
        super(msg);
    }
}
