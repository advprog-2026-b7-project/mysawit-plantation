package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class MandorAlreadyAssignedException extends ApiException {

    public MandorAlreadyAssignedException() {
        super(
                "MANDOR_ALREADY_ASSIGNED",
                "Plantation already has a mandor assigned.",
                HttpStatus.CONFLICT
        );
    }
}
