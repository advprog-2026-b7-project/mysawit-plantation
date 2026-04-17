package id.ac.ui.cs.advprog.mysawit.plantation.dto.response;

import java.util.List;

public class ApiErrorResponse {

    private final String status;
    private final String message;
    private final List<ApiErrorDetail> errors;

    public ApiErrorResponse(String message, List<ApiErrorDetail> errors) {
        this.status = "error";
        this.message = message;
        this.errors = errors;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<ApiErrorDetail> getErrors() {
        return errors;
    }
}
