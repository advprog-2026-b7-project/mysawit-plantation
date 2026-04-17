package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {

    public ForbiddenException() {
        super(
                "FORBIDDEN",
                "User does not have permission to access this resource.",
                HttpStatus.FORBIDDEN
        );
    }
}
