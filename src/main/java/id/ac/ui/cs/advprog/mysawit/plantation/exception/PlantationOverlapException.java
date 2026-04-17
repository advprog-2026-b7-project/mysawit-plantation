package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;

public class PlantationOverlapException extends ApiException {

    private final String conflictName;
    private final String conflictCode;

    public PlantationOverlapException(String conflictName, String conflictCode) {
        super(
                "PLANTATION_OVERLAP",
                "Plantation overlaps with existing plantation.",
                HttpStatus.BAD_REQUEST
        );
        this.conflictName = conflictName;
        this.conflictCode = conflictCode;
    }

    public String getConflictName() {
        return conflictName;
    }

    public String getConflictCode() {
        return conflictCode;
    }
}
