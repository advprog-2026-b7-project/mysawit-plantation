package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import java.util.UUID;

public class PlantationNotFoundException extends RuntimeException {

    public PlantationNotFoundException(UUID id) {
        super("Plantation with id " + id + " not found");
    }
}
