package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class CodeUpdateForbiddenException extends ApiException {

    public CodeUpdateForbiddenException() {
        super(
                "CODE_UPDATE_FORBIDDEN",
                "Field 'code' cannot be updated.",
                HttpStatus.BAD_REQUEST
        );
    }
}
