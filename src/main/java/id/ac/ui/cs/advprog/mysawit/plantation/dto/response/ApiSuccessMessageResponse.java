package id.ac.ui.cs.advprog.mysawit.plantation.dto.response;

public class ApiSuccessMessageResponse {

    private final String status;
    private final String message;

    public ApiSuccessMessageResponse(String message) {
        this.status = "success";
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
