package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class DriverAlreadyInPlantationException extends ApiException {

    public DriverAlreadyInPlantationException() {
        super(
                "DRIVER_ALREADY_IN_PLANTATION",
                "Driver is already assigned to this plantation.",
                HttpStatus.CONFLICT
        );
    }
}
