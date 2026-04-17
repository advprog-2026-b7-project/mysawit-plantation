package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class ValidationFailedException extends ApiException {

    public ValidationFailedException(String message) {
        super("VALIDATION_FAILED", message, HttpStatus.BAD_REQUEST);
    }
}
