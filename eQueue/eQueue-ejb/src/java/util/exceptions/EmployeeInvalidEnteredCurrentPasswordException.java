package util.exceptions;

public class EmployeeInvalidEnteredCurrentPasswordException extends Exception {

    public EmployeeInvalidEnteredCurrentPasswordException() {
    }

    public EmployeeInvalidEnteredCurrentPasswordException(String msg) {
        super(msg);
    }
}
