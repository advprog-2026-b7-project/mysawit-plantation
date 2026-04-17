package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException() {
        super("UNAUTHORIZED", "Missing or invalid JWT token.", HttpStatus.UNAUTHORIZED);
    }
}
