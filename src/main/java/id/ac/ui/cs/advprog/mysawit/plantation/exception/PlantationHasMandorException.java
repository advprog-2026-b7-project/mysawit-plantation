package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class PlantationHasMandorException extends ApiException {

    private final String mandorName;

    public PlantationHasMandorException(String mandorName) {
        super(
                "PLANTATION_HAS_MANDOR",
                "Plantation cannot be deleted while it still has a mandor assigned.",
                HttpStatus.CONFLICT
        );
        this.mandorName = mandorName;
    }

    public String getMandorName() {
        return mandorName;
    }
}
