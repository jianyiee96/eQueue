package util.exceptions;

public class EmployeeInvalidLoginCredentialException extends Exception {

    public EmployeeInvalidLoginCredentialException() {
    }

    public EmployeeInvalidLoginCredentialException(String msg) {
        super(msg);
    }
}
