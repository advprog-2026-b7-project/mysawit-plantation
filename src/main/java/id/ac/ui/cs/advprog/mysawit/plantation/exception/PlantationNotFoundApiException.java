package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;

public class PlantationNotFoundApiException extends ApiException {

    public PlantationNotFoundApiException(UUID plantationId) {
        super(
                "PLANTATION_NOT_FOUND",
                "Plantation not found: " + plantationId,
                HttpStatus.NOT_FOUND
        );
    }
}
