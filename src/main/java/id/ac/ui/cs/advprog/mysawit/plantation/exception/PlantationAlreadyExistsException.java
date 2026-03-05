package id.ac.ui.cs.advprog.mysawit.plantation.exception;

public class PlantationAlreadyExistsException extends RuntimeException {
    public PlantationAlreadyExistsException(String plantationCode) {
        super("Plantation with code " + plantationCode + " already exists");
    }
}