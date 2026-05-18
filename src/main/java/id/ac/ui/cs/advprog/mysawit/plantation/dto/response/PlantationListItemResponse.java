package id.ac.ui.cs.advprog.mysawit.plantation.dto.response;

import java.time.Instant;

public class PlantationListItemResponse {

    private String id;
    private String name;
    private String code;
    private Double area;
    private String mandorName;
    private long driverCount;
    private Instant createdAt;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getMandorName() {
        return mandorName;
    }

    public void setMandorName(String mandorName) {
        this.mandorName = mandorName;
    }

    public long getDriverCount() {
        return driverCount;
    }

    public void setDriverCount(long driverCount) {
        this.driverCount = driverCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
