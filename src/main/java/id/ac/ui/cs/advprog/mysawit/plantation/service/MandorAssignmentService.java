package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.ReassignPlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorAssignmentResponse;
import java.util.UUID;

public interface MandorAssignmentService {
    MandorAssignmentResponse assign(UUID plantationId, AssignMandorRequest request);
    MandorAssignmentResponse unassign(UUID plantationId, ReassignPlantationRequest request);
}
