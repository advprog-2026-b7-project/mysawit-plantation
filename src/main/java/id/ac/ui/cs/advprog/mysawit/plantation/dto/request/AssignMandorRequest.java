package id.ac.ui.cs.advprog.mysawit.plantation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class AssignMandorRequest {

    @NotNull(message = "mandorId is required")
    private UUID mandorId;

    public UUID getMandorId() {
        return mandorId;
    }

    public void setMandorId(UUID mandorId) {
        this.mandorId = mandorId;
    }
}
