package util.exceptions;

public class EmployeeUsernameExistException extends Exception {

    public EmployeeUsernameExistException() {
    }

    public EmployeeUsernameExistException(String msg) {
        super(msg);
    }
}
