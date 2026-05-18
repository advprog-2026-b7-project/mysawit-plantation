package id.ac.ui.cs.advprog.mysawit.plantation.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlantationServiceTest {

    @Mock
    private JwtAdminGuard jwtAdminGuard;

    @Mock
    private PlantationMutationService mutationService;

    @Mock
    private MandorAssignmentService mandorAssignmentService;

    @Mock
    private DriverAssignmentService driverAssignmentService;

    @Mock
    private PlantationQueryService queryService;

    @InjectMocks
    private PlantationService plantationService;

    @Test
    void create_callsGuardThenDelegates() {
        String header = "Bearer token";
        CreatePlantationRequest request = new CreatePlantationRequest();
        PlantationResponse expected = new PlantationResponse();
        when(mutationService.create(request)).thenReturn(expected);
        PlantationResponse result = plantationService.create(header, request);
        verify(jwtAdminGuard).requireAdmin(header);
        verify(mutationService).create(request);
        assertSame(expected, result);
    }

    @Test
    void update_callsGuardThenDelegates() {
        String header = "Bearer token";
        UUID id = UUID.randomUUID();
        UpdatePlantationRequest request = new UpdatePlantationRequest();
        PlantationUpdateResponse expected = new PlantationUpdateResponse();
        when(mutationService.update(id, request)).thenReturn(expected);
        PlantationUpdateResponse result = plantationService.update(header, id, request);
        verify(jwtAdminGuard).requireAdmin(header);
        verify(mutationService).update(id, request);
        assertSame(expected, result);
    }

    @Test
    void delete_callsGuardThenDelegates() {
        String header = "Bearer token";
        UUID id = UUID.randomUUID();
        when(mutationService.delete(id)).thenReturn("PLT-001");
        String result = plantationService.delete(header, id);
        verify(jwtAdminGuard).requireAdmin(header);
        verify(mutationService).delete(id);
        assertSame("PLT-001", result);
    }

    @Test
    void assignMandor_callsGuardThenDelegates() {
        String header = "Bearer token";
        UUID id = UUID.randomUUID();
        AssignMandorRequest request = new AssignMandorRequest();
        MandorAssignmentResponse expected = new MandorAssignmentResponse();
        when(mandorAssignmentService.assign(id, request)).thenReturn(expected);
        MandorAssignmentResponse result = plantationService.assignMandor(header, id, request);
        verify(jwtAdminGuard).requireAdmin(header);
        verify(mandorAssignmentService).assign(id, request);
        assertSame(expected, result);
    }

    @Test
    void unassignMandor_callsGuardThenDelegates() {
        String header = "Bearer token";
        UUID id = UUID.randomUUID();
        ReassignPlantationRequest request = new ReassignPlantationRequest();
        MandorAssignmentResponse expected = new MandorAssignmentResponse();
        when(mandorAssignmentService.unassign(id, request)).thenReturn(expected);
        MandorAssignmentResponse result = plantationService.unassignMandor(header, id, request);
        verify(jwtAdminGuard).requireAdmin(header);
        verify(mandorAssignmentService).unassign(id, request);
        assertSame(expected, result);
    }

    @Test
    void assignDriver_callsGuardThenDelegates() {
        String header = "Bearer token";
        UUID id = UUID.randomUUID();
        AssignDriverRequest request = new AssignDriverRequest();
        DriverAssignmentResponse expected = new DriverAssignmentResponse();
        when(driverAssignmentService.assign(id, request)).thenReturn(expected);
        DriverAssignmentResponse result = plantationService.assignDriver(header, id, request);
        verify(jwtAdminGuard).requireAdmin(header);
        verify(driverAssignmentService).assign(id, request);
        assertSame(expected, result);
    }

    @Test
    void unassignDriver_callsGuardThenDelegates() {
        String header = "Bearer token";
        UUID plantationId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        ReassignPlantationRequest request = new ReassignPlantationRequest();
        DriverAssignmentResponse expected = new DriverAssignmentResponse();
        when(driverAssignmentService.unassign(plantationId, driverId, request))
                .thenReturn(expected);
        DriverAssignmentResponse result =
                plantationService.unassignDriver(header, plantationId, driverId, request);
        verify(jwtAdminGuard).requireAdmin(header);
        verify(driverAssignmentService).unassign(plantationId, driverId, request);
        assertSame(expected, result);
    }

    @Test
    void getAll_callsGuardThenDelegates() {
        String header = "Bearer token";
        Pageable pageable = PageRequest.of(0, 20);
        PageResponse<PlantationListItemResponse> expected =
                new PageResponse<>(List.of(new PlantationListItemResponse()), 0, 20, 1, 1);
        when(queryService.getAll("name", "code", pageable)).thenReturn(expected);
        PageResponse<PlantationListItemResponse> result =
                plantationService.getAll(header, "name", "code", pageable);
        verify(jwtAdminGuard).requireAdmin(header);
        verify(queryService).getAll("name", "code", pageable);
        assertSame(expected, result);
    }

    @Test
    void getById_callsGuardThenDelegates() {
        String header = "Bearer token";
        UUID id = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);
        PlantationDetailResponse expected = new PlantationDetailResponse();
        when(queryService.getById(id, "agus", pageable)).thenReturn(expected);
        PlantationDetailResponse result =
                plantationService.getById(header, id, "agus", pageable);
        verify(jwtAdminGuard).requireAdmin(header);
        verify(queryService).getById(id, "agus", pageable);
        assertSame(expected, result);
    }
}
