package id.ac.ui.cs.advprog.mysawit.plantation.dto.response;

public class ApiSuccessResponse<T> {

    private final String status;
    private final T data;

    public ApiSuccessResponse(T data) {
        this.status = "success";
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}
