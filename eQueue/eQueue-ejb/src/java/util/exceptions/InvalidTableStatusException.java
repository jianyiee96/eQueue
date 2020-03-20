package util.exceptions;

public class InvalidTableStatusException extends Exception {

    public InvalidTableStatusException() {
    }

    public InvalidTableStatusException(String msg) {
        super(msg);
    }
}
