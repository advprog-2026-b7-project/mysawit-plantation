package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class UserNotDriverException extends ApiException {

    public UserNotDriverException() {
        super("USER_NOT_DRIVER", "User is not a driver.", HttpStatus.BAD_REQUEST);
    }
}
