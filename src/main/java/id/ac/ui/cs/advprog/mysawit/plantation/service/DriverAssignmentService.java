package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignDriverRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.ReassignPlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.DriverAssignmentResponse;
import java.util.UUID;

public interface DriverAssignmentService {
    DriverAssignmentResponse assign(UUID plantationId, AssignDriverRequest request);
    DriverAssignmentResponse unassign(
            UUID plantationId,
            UUID driverId,
            ReassignPlantationRequest request
    );
}
