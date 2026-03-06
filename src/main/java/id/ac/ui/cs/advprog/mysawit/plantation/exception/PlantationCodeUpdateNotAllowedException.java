package id.ac.ui.cs.advprog.mysawit.plantation.exception;

public class PlantationCodeUpdateNotAllowedException extends RuntimeException {

    public PlantationCodeUpdateNotAllowedException() {
        super("Plantation code cannot be updated");
    }
}
