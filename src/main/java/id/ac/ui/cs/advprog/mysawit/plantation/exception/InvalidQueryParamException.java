package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class InvalidQueryParamException extends ApiException {

    public InvalidQueryParamException(String message) {
        super("INVALID_QUERY_PARAM", message, HttpStatus.BAD_REQUEST);
    }
}
