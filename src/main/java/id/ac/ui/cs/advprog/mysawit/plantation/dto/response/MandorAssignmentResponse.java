package id.ac.ui.cs.advprog.mysawit.plantation.dto.response;

import java.time.Instant;

public class MandorAssignmentResponse {

    private String plantationId;
    private MandorSummaryResponse mandor;
    private Instant assignedAt;

    public String getPlantationId() {
        return plantationId;
    }

    public void setPlantationId(String plantationId) {
        this.plantationId = plantationId;
    }

    public MandorSummaryResponse getMandor() {
        return mandor;
    }

    public void setMandor(MandorSummaryResponse mandor) {
        this.mandor = mandor;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }
}
