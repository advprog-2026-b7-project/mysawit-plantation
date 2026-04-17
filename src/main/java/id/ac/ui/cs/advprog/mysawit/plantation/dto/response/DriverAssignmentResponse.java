package id.ac.ui.cs.advprog.mysawit.plantation.dto.response;

import java.time.Instant;

public class DriverAssignmentResponse {

    private String plantationId;
    private DriverSummaryResponse driver;
    private Instant assignedAt;

    public String getPlantationId() {
        return plantationId;
    }

    public void setPlantationId(String plantationId) {
        this.plantationId = plantationId;
    }

    public DriverSummaryResponse getDriver() {
        return driver;
    }

    public void setDriver(DriverSummaryResponse driver) {
        this.driver = driver;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }
}
