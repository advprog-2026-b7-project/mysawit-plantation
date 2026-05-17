package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorAssignmentResponse;
import java.util.UUID;

public interface MandorAssignmentService {
    MandorAssignmentResponse assign(UUID plantationId, AssignMandorRequest request);
    void unassign(UUID plantationId);
}
