package id.ac.ui.cs.advprog.mysawit.plantation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignDriverRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.ReassignPlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.DriverAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
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
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DriverAssignmentServiceImplTest {

    @Mock
    private PlantationRepository plantationRepository;

    @Mock
    private PlantationDriverAssignmentRepository plantationDriverAssignmentRepository;

    @Mock
    private UserProfileGateway userProfileGateway;

    @Mock
    private PlantationMapper plantationMapper;

    @InjectMocks
    private DriverAssignmentServiceImpl driverAssignmentService;

    private UserProfile supirProfile(UUID id) {
        return new UserProfile(id, "Ali", "ali@sawit.id", "SUPIR", null);
    }

    @Test
    void assign_success() {
        UUID plantationId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        AssignDriverRequest request = new AssignDriverRequest();
        request.setDriverId(driverId);
        UserProfile profile = supirProfile(driverId);
        PlantationDriverAssignment saved = new PlantationDriverAssignment();
        DriverAssignmentResponse expected = new DriverAssignmentResponse();
        when(plantationRepository.findById(plantationId))
                .thenReturn(Optional.of(new Plantation()));
        when(userProfileGateway.findById(driverId)).thenReturn(Optional.of(profile));
        when(plantationDriverAssignmentRepository.existsByPlantationIdAndDriverId(
                plantationId, driverId)).thenReturn(false);
        when(plantationDriverAssignmentRepository.save(any())).thenReturn(saved);
        when(plantationMapper.toDriverAssignmentResponse(saved, profile)).thenReturn(expected);
        DriverAssignmentResponse result =
                driverAssignmentService.assign(plantationId, request);
        assertSame(expected, result);
    }

    @Test
    void assign_plantationNotFound_throws() {
        UUID plantationId = UUID.randomUUID();
        when(plantationRepository.findById(plantationId)).thenReturn(Optional.empty());
        AssignDriverRequest request = new AssignDriverRequest();
        request.setDriverId(UUID.randomUUID());
        assertThrows(
                PlantationNotFoundApiException.class,
                () -> driverAssignmentService.assign(plantationId, request)
        );
    }

    @Test
    void assign_userNotFound_throws() {
        UUID plantationId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        when(plantationRepository.findById(plantationId))
                .thenReturn(Optional.of(new Plantation()));
        when(userProfileGateway.findById(driverId)).thenReturn(Optional.empty());
        AssignDriverRequest request = new AssignDriverRequest();
        request.setDriverId(driverId);
        assertThrows(
                UserNotFoundException.class,
                () -> driverAssignmentService.assign(plantationId, request)
        );
    }

    @Test
    void assign_userNotSupir_throws() {
        UUID plantationId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        UserProfile notSupir =
                new UserProfile(driverId, "Budi", "budi@mysawit.id", "MANDOR", "CERT-1");
        when(plantationRepository.findById(plantationId))
                .thenReturn(Optional.of(new Plantation()));
        when(userProfileGateway.findById(driverId)).thenReturn(Optional.of(notSupir));
        AssignDriverRequest request = new AssignDriverRequest();
        request.setDriverId(driverId);
        assertThrows(
                UserNotDriverException.class,
                () -> driverAssignmentService.assign(plantationId, request)
        );
    }

    @Test
    void assign_alreadyInPlantation_throws() {
        UUID plantationId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        UserProfile profile = supirProfile(driverId);
        when(plantationRepository.findById(plantationId))
                .thenReturn(Optional.of(new Plantation()));
        when(userProfileGateway.findById(driverId)).thenReturn(Optional.of(profile));
        when(plantationDriverAssignmentRepository.existsByPlantationIdAndDriverId(
                plantationId, driverId)).thenReturn(true);
        AssignDriverRequest request = new AssignDriverRequest();
        request.setDriverId(driverId);
        assertThrows(
                DriverAlreadyInPlantationException.class,
                () -> driverAssignmentService.assign(plantationId, request)
        );
    }

    @Test
    void unassign_success() {
        UUID plantationId = UUID.randomUUID();
        UUID targetPlantationId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        PlantationDriverAssignment assignment = new PlantationDriverAssignment();
        assignment.setDriverId(driverId);
        assignment.setDriverName("Ali");
        assignment.setDriverEmail("ali@sawit.id");
        ReassignPlantationRequest request = reassignRequest(targetPlantationId);
        DriverAssignmentResponse expected = new DriverAssignmentResponse();
        when(plantationRepository.findById(plantationId))
                .thenReturn(Optional.of(new Plantation()));
        when(plantationRepository.findById(targetPlantationId))
                .thenReturn(Optional.of(new Plantation()));
        when(plantationDriverAssignmentRepository
                .findByPlantationIdAndDriverId(plantationId, driverId))
                .thenReturn(Optional.of(assignment));
        when(plantationDriverAssignmentRepository.existsByPlantationIdAndDriverId(
                targetPlantationId,
                driverId
        )).thenReturn(false);
        when(plantationDriverAssignmentRepository.save(assignment)).thenReturn(assignment);
        when(plantationMapper.toDriverAssignmentResponse(any(), any()))
                .thenReturn(expected);

        DriverAssignmentResponse result =
                driverAssignmentService.unassign(plantationId, driverId, request);

        assertSame(expected, result);
        assertEquals(targetPlantationId, assignment.getPlantationId());
        verify(plantationDriverAssignmentRepository).save(assignment);
    }

    @Test
    void unassign_plantationNotFound_throws() {
        UUID plantationId = UUID.randomUUID();
        when(plantationRepository.findById(plantationId)).thenReturn(Optional.empty());
        assertThrows(
                PlantationNotFoundApiException.class,
                () -> driverAssignmentService.unassign(
                        plantationId,
                        UUID.randomUUID(),
                        reassignRequest(UUID.randomUUID())
                )
        );
    }

    @Test
    void unassign_driverNotInPlantation_throws() {
        UUID plantationId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        when(plantationRepository.findById(plantationId))
                .thenReturn(Optional.of(new Plantation()));
        when(plantationDriverAssignmentRepository
                .findByPlantationIdAndDriverId(plantationId, driverId))
                .thenReturn(Optional.empty());
        assertThrows(
                DriverNotInPlantationException.class,
                () -> driverAssignmentService.unassign(
                        plantationId,
                        driverId,
                        reassignRequest(UUID.randomUUID())
                )
        );
    }

    @Test
    void unassign_withoutTarget_throws() {
        UUID plantationId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        PlantationDriverAssignment assignment = new PlantationDriverAssignment();
        when(plantationRepository.findById(plantationId))
                .thenReturn(Optional.of(new Plantation()));
        when(plantationDriverAssignmentRepository
                .findByPlantationIdAndDriverId(plantationId, driverId))
                .thenReturn(Optional.of(assignment));

        assertThrows(
                ReassignPlantationRequiredException.class,
                () -> driverAssignmentService.unassign(
                        plantationId,
                        driverId,
                        new ReassignPlantationRequest()
                )
        );
    }

    @Test
    void unassign_driverAlreadyInTarget_throws() {
        UUID plantationId = UUID.randomUUID();
        UUID targetPlantationId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        PlantationDriverAssignment assignment = new PlantationDriverAssignment();
        when(plantationRepository.findById(plantationId))
                .thenReturn(Optional.of(new Plantation()));
        when(plantationRepository.findById(targetPlantationId))
                .thenReturn(Optional.of(new Plantation()));
        when(plantationDriverAssignmentRepository
                .findByPlantationIdAndDriverId(plantationId, driverId))
                .thenReturn(Optional.of(assignment));
        when(plantationDriverAssignmentRepository.existsByPlantationIdAndDriverId(
                targetPlantationId,
                driverId
        )).thenReturn(true);

        assertThrows(
                DriverAlreadyInPlantationException.class,
                () -> driverAssignmentService.unassign(
                        plantationId,
                        driverId,
                        reassignRequest(targetPlantationId)
                )
        );
    }

    private ReassignPlantationRequest reassignRequest(UUID targetId) {
        ReassignPlantationRequest request = new ReassignPlantationRequest();
        request.setReassignToPlantationId(targetId);
        return request;
    }
}
