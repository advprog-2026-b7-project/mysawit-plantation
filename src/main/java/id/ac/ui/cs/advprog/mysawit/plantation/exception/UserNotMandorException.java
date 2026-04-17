package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class UserNotMandorException extends ApiException {

    public UserNotMandorException() {
        super("USER_NOT_MANDOR", "User is not a mandor.", HttpStatus.BAD_REQUEST);
    }
}
