package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class ReassignPlantationRequiredException extends ApiException {

    public ReassignPlantationRequiredException() {
        super(
                "REASSIGN_PLANTATION_REQUIRED",
                "Reassign plantation id is required.",
                HttpStatus.BAD_REQUEST
        );
    }
}
