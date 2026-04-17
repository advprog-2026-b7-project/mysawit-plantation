package id.ac.ui.cs.advprog.mysawit.plantation.dto.response;

public class MandorSummaryResponse {

    private String id;
    private String name;
    private String certificationNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCertificationNumber() {
        return certificationNumber;
    }

    public void setCertificationNumber(String certificationNumber) {
        this.certificationNumber = certificationNumber;
    }
}
