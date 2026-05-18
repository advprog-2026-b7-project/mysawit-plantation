package id.ac.ui.cs.advprog.mysawit.plantation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.ReassignPlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.MandorAlreadyAssignedException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.MandorInOtherPlantationException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.MandorNotInPlantationException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundApiException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.ReassignPlantationRequiredException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UserNotMandorException;
import id.ac.ui.cs.advprog.mysawit.plantation.gateway.UserProfile;
import id.ac.ui.cs.advprog.mysawit.plantation.gateway.UserProfileGateway;
import id.ac.ui.cs.advprog.mysawit.plantation.mapper.PlantationMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MandorAssignmentServiceImplTest {

    @Mock
    private PlantationRepository plantationRepository;

    @Mock
    private UserProfileGateway userProfileGateway;

    @Mock
    private PlantationMapper plantationMapper;

    @InjectMocks
    private MandorAssignmentServiceImpl mandorAssignmentService;

    private UserProfile mandorProfile(UUID id) {
        return new UserProfile(id, "Budi", "budi@mysawit.id", "MANDOR", "CERT-001");
    }

    @Test
    void assign_success() {
        UUID plantationId = UUID.randomUUID();
        UUID mandorId = UUID.randomUUID();
        Plantation plantation = new Plantation();
        plantation.setMandorId(null);
        AssignMandorRequest request = new AssignMandorRequest();
        request.setMandorId(mandorId);
        UserProfile profile = mandorProfile(mandorId);
        MandorAssignmentResponse expected = new MandorAssignmentResponse();
        when(plantationRepository.findById(plantationId)).thenReturn(Optional.of(plantation));
        when(userProfileGateway.findById(mandorId)).thenReturn(Optional.of(profile));
        when(plantationRepository.findByMandorIdAndIdNot(mandorId, plantationId))
                .thenReturn(Optional.empty());
        when(plantationRepository.save(any(Plantation.class))).thenReturn(plantation);
        when(plantationMapper.toMandorAssignmentResponse(any(), any(), any()))
                .thenReturn(expected);
        MandorAssignmentResponse result =
                mandorAssignmentService.assign(plantationId, request);
        assertSame(expected, result);
    }

    @Test
    void assign_plantationNotFound_throws() {
        UUID plantationId = UUID.randomUUID();
        when(plantationRepository.findById(plantationId)).thenReturn(Optional.empty());
        AssignMandorRequest request = new AssignMandorRequest();
        request.setMandorId(UUID.randomUUID());
        assertThrows(
                PlantationNotFoundApiException.class,
                () -> mandorAssignmentService.assign(plantationId, request)
        );
    }

    @Test
    void assign_alreadyHasMandor_throws() {
        UUID plantationId = UUID.randomUUID();
        Plantation plantation = new Plantation();
        plantation.setMandorId(UUID.randomUUID());
        when(plantationRepository.findById(plantationId)).thenReturn(Optional.of(plantation));
        AssignMandorRequest request = new AssignMandorRequest();
        request.setMandorId(UUID.randomUUID());
        assertThrows(
                MandorAlreadyAssignedException.class,
                () -> mandorAssignmentService.assign(plantationId, request)
        );
    }

    @Test
    void assign_userNotFound_throws() {
        UUID plantationId = UUID.randomUUID();
        UUID mandorId = UUID.randomUUID();
        Plantation plantation = new Plantation();
        plantation.setMandorId(null);
        when(plantationRepository.findById(plantationId)).thenReturn(Optional.of(plantation));
        when(userProfileGateway.findById(mandorId)).thenReturn(Optional.empty());
        AssignMandorRequest request = new AssignMandorRequest();
        request.setMandorId(mandorId);
        assertThrows(
                UserNotFoundException.class,
                () -> mandorAssignmentService.assign(plantationId, request)
        );
    }

    @Test
    void assign_userNotMandor_throws() {
        UUID plantationId = UUID.randomUUID();
        UUID mandorId = UUID.randomUUID();
        Plantation plantation = new Plantation();
        plantation.setMandorId(null);
        UserProfile notMandor = new UserProfile(mandorId, "Ali", "ali@sawit.id", "SUPIR", null);
        when(plantationRepository.findById(plantationId)).thenReturn(Optional.of(plantation));
        when(userProfileGateway.findById(mandorId)).thenReturn(Optional.of(notMandor));
        AssignMandorRequest request = new AssignMandorRequest();
        request.setMandorId(mandorId);
        assertThrows(
                UserNotMandorException.class,
                () -> mandorAssignmentService.assign(plantationId, request)
        );
    }

    @Test
    void assign_mandorInOtherPlantation_throws() {
        UUID plantationId = UUID.randomUUID();
        UUID mandorId = UUID.randomUUID();
        Plantation plantation = new Plantation();
        plantation.setMandorId(null);
        UserProfile profile = mandorProfile(mandorId);
        Plantation otherPlantation = new Plantation();
        otherPlantation.setId(UUID.randomUUID());
        when(plantationRepository.findById(plantationId)).thenReturn(Optional.of(plantation));
        when(userProfileGateway.findById(mandorId)).thenReturn(Optional.of(profile));
        when(plantationRepository.findByMandorIdAndIdNot(mandorId, plantationId))
                .thenReturn(Optional.of(otherPlantation));
        AssignMandorRequest request = new AssignMandorRequest();
        request.setMandorId(mandorId);
        assertThrows(
                MandorInOtherPlantationException.class,
                () -> mandorAssignmentService.assign(plantationId, request)
        );
    }

    @Test
    void unassign_success_reassignsMandorToTarget() {
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        UUID mandorId = UUID.randomUUID();
        Plantation source = new Plantation();
        source.setMandorId(mandorId);
        source.setMandorName("Budi");
        source.setMandorEmail("budi@mysawit.id");
        source.setMandorCertificationNumber("CERT-001");
        Plantation target = new Plantation();
        target.setId(targetId);
        ReassignPlantationRequest request = reassignRequest(targetId);
        MandorAssignmentResponse expected = new MandorAssignmentResponse();
        when(plantationRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(plantationRepository.findById(targetId)).thenReturn(Optional.of(target));
        when(plantationRepository.save(source)).thenReturn(source);
        when(plantationRepository.save(target)).thenReturn(target);
        when(plantationMapper.toMandorAssignmentResponse(any(), any(), any()))
                .thenReturn(expected);

        MandorAssignmentResponse result = mandorAssignmentService.unassign(sourceId, request);

        assertSame(expected, result);
        assertNull(source.getMandorId());
        assertEquals(mandorId, target.getMandorId());
        verify(plantationRepository).save(source);
        verify(plantationRepository).save(target);
    }

    @Test
    void unassign_plantationNotFound_throws() {
        UUID plantationId = UUID.randomUUID();
        when(plantationRepository.findById(plantationId)).thenReturn(Optional.empty());
        assertThrows(
                PlantationNotFoundApiException.class,
                () -> mandorAssignmentService.unassign(
                        plantationId,
                        reassignRequest(UUID.randomUUID())
                )
        );
    }

    @Test
    void unassign_withoutTarget_throws() {
        UUID sourceId = UUID.randomUUID();
        Plantation source = new Plantation();
        source.setMandorId(UUID.randomUUID());
        when(plantationRepository.findById(sourceId)).thenReturn(Optional.of(source));

        assertThrows(
                ReassignPlantationRequiredException.class,
                () -> mandorAssignmentService.unassign(sourceId, new ReassignPlantationRequest())
        );
    }

    @Test
    void unassign_sourceWithoutMandor_throws() {
        UUID sourceId = UUID.randomUUID();
        Plantation source = new Plantation();
        when(plantationRepository.findById(sourceId)).thenReturn(Optional.of(source));

        assertThrows(
                MandorNotInPlantationException.class,
                () -> mandorAssignmentService.unassign(
                        sourceId,
                        reassignRequest(UUID.randomUUID())
                )
        );
    }

    @Test
    void unassign_targetAlreadyHasMandor_throws() {
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        Plantation source = new Plantation();
        source.setMandorId(UUID.randomUUID());
        Plantation target = new Plantation();
        target.setMandorId(UUID.randomUUID());
        when(plantationRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(plantationRepository.findById(targetId)).thenReturn(Optional.of(target));

        assertThrows(
                MandorAlreadyAssignedException.class,
                () -> mandorAssignmentService.unassign(sourceId, reassignRequest(targetId))
        );
    }

    private ReassignPlantationRequest reassignRequest(UUID targetId) {
        ReassignPlantationRequest request = new ReassignPlantationRequest();
        request.setReassignToPlantationId(targetId);
        return request;
    }
}
