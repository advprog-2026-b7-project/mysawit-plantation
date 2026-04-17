package id.ac.ui.cs.advprog.mysawit.plantation.dto.response;

public class ApiErrorDetail {

    private final String code;
    private final String detail;

    public ApiErrorDetail(String code, String detail) {
        this.code = code;
        this.detail = detail;
    }

    public String getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }
}
