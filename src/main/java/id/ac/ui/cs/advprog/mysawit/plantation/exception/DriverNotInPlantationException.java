package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class DriverNotInPlantationException extends ApiException {

    public DriverNotInPlantationException() {
        super(
                "DRIVER_NOT_IN_PLANTATION",
                "Driver is not assigned to this plantation",
                HttpStatus.NOT_FOUND
        );
    }
}
