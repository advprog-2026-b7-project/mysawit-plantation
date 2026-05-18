package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class MandorNotInPlantationException extends ApiException {

    public MandorNotInPlantationException() {
        super(
                "MANDOR_NOT_IN_PLANTATION",
                "Mandor is not assigned to this plantation.",
                HttpStatus.NOT_FOUND
        );
    }
}
