package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {

    public UserNotFoundException(UUID userId) {
        super("USER_NOT_FOUND", "User not found: " + userId, HttpStatus.NOT_FOUND);
    }
}
