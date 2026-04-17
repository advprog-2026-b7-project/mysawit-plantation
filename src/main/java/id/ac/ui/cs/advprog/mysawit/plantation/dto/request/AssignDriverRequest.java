package id.ac.ui.cs.advprog.mysawit.plantation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class AssignDriverRequest {

    @NotNull(message = "driverId is required")
    private UUID driverId;

    public UUID getDriverId() {
        return driverId;
    }

    public void setDriverId(UUID driverId) {
        this.driverId = driverId;
    }
}
