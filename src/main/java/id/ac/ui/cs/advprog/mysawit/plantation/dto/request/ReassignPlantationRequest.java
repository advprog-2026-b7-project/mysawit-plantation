package id.ac.ui.cs.advprog.mysawit.plantation.dto.request;

import java.util.UUID;

public class ReassignPlantationRequest {

    private UUID reassignToPlantationId;

    public UUID getReassignToPlantationId() {
        return reassignToPlantationId;
    }

    public void setReassignToPlantationId(UUID reassignToPlantationId) {
        this.reassignToPlantationId = reassignToPlantationId;
    }
}
