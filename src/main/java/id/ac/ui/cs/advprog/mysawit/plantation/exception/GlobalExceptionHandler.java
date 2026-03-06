package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlantationAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlePlantationExists(PlantationAlreadyExistsException ex) {
        return Map.of(
                "error", ex.getMessage()
        );
    }

    @ExceptionHandler(PlantationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlePlantationNotFound(PlantationNotFoundException ex) {
        return Map.of(
                "error", ex.getMessage()
        );
    }

    @ExceptionHandler(PlantationCodeUpdateNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCodeUpdateNotAllowed(PlantationCodeUpdateNotAllowedException ex) {
        return Map.of(
                "error", ex.getMessage()
        );
    }
}