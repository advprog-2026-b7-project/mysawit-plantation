package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignDriverRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.ReassignPlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.UpdatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.DriverAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PageResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationDetailResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationListItemResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationUpdateResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.security.JwtAdminGuard;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlantationService {

    private final JwtAdminGuard jwtAdminGuard;
    private final PlantationMutationService mutationService;
    private final MandorAssignmentService mandorAssignmentService;
    private final DriverAssignmentService driverAssignmentService;
    private final PlantationQueryService queryService;

    public PlantationResponse create(
            String authorizationHeader,
            CreatePlantationRequest request
    ) {
        jwtAdminGuard.requireAdmin(authorizationHeader);
        return mutationService.create(request);
    }

    public PlantationUpdateResponse update(
            String authorizationHeader,
            UUID plantationId,
            UpdatePlantationRequest request
    ) {
        jwtAdminGuard.requireAdmin(authorizationHeader);
        return mutationService.update(plantationId, request);
    }

    public String delete(String authorizationHeader, UUID plantationId) {
        jwtAdminGuard.requireAdmin(authorizationHeader);
        return mutationService.delete(plantationId);
    }

    public MandorAssignmentResponse assignMandor(
            String authorizationHeader,
            UUID plantationId,
            AssignMandorRequest request
    ) {
        jwtAdminGuard.requireAdmin(authorizationHeader);
        return mandorAssignmentService.assign(plantationId, request);
    }

    public MandorAssignmentResponse unassignMandor(
            String authorizationHeader,
            UUID plantationId,
            ReassignPlantationRequest request
    ) {
        jwtAdminGuard.requireAdmin(authorizationHeader);
        return mandorAssignmentService.unassign(plantationId, request);
    }

    public DriverAssignmentResponse assignDriver(
            String authorizationHeader,
            UUID plantationId,
            AssignDriverRequest request
    ) {
        jwtAdminGuard.requireAdmin(authorizationHeader);
        return driverAssignmentService.assign(plantationId, request);
    }

    public DriverAssignmentResponse unassignDriver(
            String authorizationHeader,
            UUID plantationId,
            UUID driverId,
            ReassignPlantationRequest request
    ) {
        jwtAdminGuard.requireAdmin(authorizationHeader);
        return driverAssignmentService.unassign(plantationId, driverId, request);
    }

    public PageResponse<PlantationListItemResponse> getAll(
            String authorizationHeader,
            String name,
            String code,
            Pageable pageable
    ) {
        jwtAdminGuard.requireAdmin(authorizationHeader);
        return queryService.getAll(name, code, pageable);
    }

    public PlantationDetailResponse getById(
            String authorizationHeader,
            UUID plantationId,
            String driverName,
            Pageable driverPageable
    ) {
        jwtAdminGuard.requireAdmin(authorizationHeader);
        return queryService.getById(plantationId, driverName, driverPageable);
    }
}
