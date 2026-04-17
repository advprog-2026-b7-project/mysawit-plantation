package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class CodeAlreadyExistsException extends ApiException {

    public CodeAlreadyExistsException(String code) {
        super(
                "CODE_ALREADY_EXISTS",
                "Plantation code already exists: " + code,
                HttpStatus.CONFLICT
        );
    }
}
