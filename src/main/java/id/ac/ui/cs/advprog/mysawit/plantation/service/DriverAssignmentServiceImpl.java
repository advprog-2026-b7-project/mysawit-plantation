package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignDriverRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.ReassignPlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.DriverAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.PlantationDriverAssignment;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.DriverAlreadyInPlantationException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.DriverNotInPlantationException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundApiException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.ReassignPlantationRequiredException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UserNotDriverException;
import id.ac.ui.cs.advprog.mysawit.plantation.gateway.UserProfile;
import id.ac.ui.cs.advprog.mysawit.plantation.gateway.UserProfileGateway;
import id.ac.ui.cs.advprog.mysawit.plantation.mapper.PlantationMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationDriverAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverAssignmentServiceImpl implements DriverAssignmentService {

    private final PlantationRepository plantationRepository;
    private final PlantationDriverAssignmentRepository plantationDriverAssignmentRepository;
    private final UserProfileGateway userProfileGateway;
    private final PlantationMapper plantationMapper;

    @Override
    @Transactional
    public DriverAssignmentResponse assign(UUID plantationId, AssignDriverRequest request) {
        plantationRepository.findById(plantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(plantationId));
        UserProfile userProfile = userProfileGateway.findById(request.getDriverId())
                .orElseThrow(() -> new UserNotFoundException(request.getDriverId()));
        if (!"SUPIR".equalsIgnoreCase(userProfile.role())) {
            throw new UserNotDriverException();
        }
        if (plantationDriverAssignmentRepository.existsByPlantationIdAndDriverId(
                plantationId,
                request.getDriverId()
        )) {
            throw new DriverAlreadyInPlantationException();
        }
        PlantationDriverAssignment assignment = new PlantationDriverAssignment();
        assignment.setPlantationId(plantationId);
        assignment.setDriverId(request.getDriverId());
        assignment.setDriverName(userProfile.name());
        assignment.setDriverEmail(userProfile.email());
        assignment.setAssignedAt(Instant.now());
        PlantationDriverAssignment saved =
                plantationDriverAssignmentRepository.save(assignment);
        return plantationMapper.toDriverAssignmentResponse(saved, userProfile);
    }

    @Override
    @Transactional
    public DriverAssignmentResponse unassign(
            UUID plantationId,
            UUID driverId,
            ReassignPlantationRequest request
    ) {
        plantationRepository.findById(plantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(plantationId));
        PlantationDriverAssignment assignment =
                plantationDriverAssignmentRepository
                        .findByPlantationIdAndDriverId(plantationId, driverId)
                        .orElseThrow(DriverNotInPlantationException::new);
        UUID targetPlantationId = requireTargetPlantationId(plantationId, request);
        plantationRepository.findById(targetPlantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(targetPlantationId));
        if (plantationDriverAssignmentRepository.existsByPlantationIdAndDriverId(
                targetPlantationId,
                driverId
        )) {
            throw new DriverAlreadyInPlantationException();
        }

        Instant assignedAt = Instant.now();
        assignment.setPlantationId(targetPlantationId);
        assignment.setAssignedAt(assignedAt);
        PlantationDriverAssignment saved =
                plantationDriverAssignmentRepository.save(assignment);
        UserProfile reassignedDriver = new UserProfile(
                saved.getDriverId(),
                saved.getDriverName(),
                saved.getDriverEmail(),
                "SUPIR",
                null
        );
        return plantationMapper.toDriverAssignmentResponse(saved, reassignedDriver);
    }

    private UUID requireTargetPlantationId(
            UUID sourcePlantationId,
            ReassignPlantationRequest request
    ) {
        if (request == null || request.getReassignToPlantationId() == null
                || sourcePlantationId.equals(request.getReassignToPlantationId())) {
            throw new ReassignPlantationRequiredException();
        }
        return request.getReassignToPlantationId();
    }
}
