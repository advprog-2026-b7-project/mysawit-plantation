package id.ac.ui.cs.advprog.mysawit.plantation.exception;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.ApiErrorDetail;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.ApiErrorResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        List<ApiErrorDetail> details = new ArrayList<>();
        if (ex instanceof PlantationOverlapException overlapException) {
            String detail = String.format(
                    "conflict with plantation name='%s', code='%s'",
                    overlapException.getConflictName(),
                    overlapException.getConflictCode()
            );
            details.add(new ApiErrorDetail(overlapException.getCode(), detail));
        } else if (ex instanceof PlantationHasMandorException mandorException) {
            String mandorName = mandorException.getMandorName();
            String detail = mandorName == null || mandorName.isBlank()
                    ? "plantation has a mandor assigned"
                    : "plantation has mandor '" + mandorName + "' assigned";
            details.add(new ApiErrorDetail(mandorException.getCode(), detail));
        } else {
            details.add(new ApiErrorDetail(ex.getCode(), ex.getMessage()));
        }

        ApiErrorResponse response = new ApiErrorResponse(ex.getMessage(), details);
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        List<ApiErrorDetail> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(
                    new ApiErrorDetail(
                            "VALIDATION_FAILED",
                            error.getField() + ": " + error.getDefaultMessage()
                    )
            );
        }
        ApiErrorResponse response = new ApiErrorResponse("Validation failed.", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex
    ) {
        List<ApiErrorDetail> details = List.of(
                new ApiErrorDetail("VALIDATION_FAILED", "Malformed JSON request body.")
        );
        ApiErrorResponse response = new ApiErrorResponse("Validation failed.", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        List<ApiErrorDetail> details = List.of(
                new ApiErrorDetail("INTERNAL_SERVER_ERROR", ex.getMessage())
        );
        ApiErrorResponse response = new ApiErrorResponse("Unexpected server error.", details);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
