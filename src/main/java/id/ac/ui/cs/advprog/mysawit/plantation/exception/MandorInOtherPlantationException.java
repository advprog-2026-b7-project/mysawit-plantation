package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;

public class MandorInOtherPlantationException extends ApiException {

    public MandorInOtherPlantationException(UUID plantationId) {
        super(
                "MANDOR_IN_OTHER_PLANTATION",
                "Mandor is already assigned to plantation " + plantationId,
                HttpStatus.CONFLICT
        );
    }
}
