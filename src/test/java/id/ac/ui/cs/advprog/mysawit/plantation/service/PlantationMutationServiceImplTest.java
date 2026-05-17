package id.ac.ui.cs.advprog.mysawit.plantation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.UpdatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationUpdateResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.CodeAlreadyExistsException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationHasMandorException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundApiException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.ValidationFailedException;
import id.ac.ui.cs.advprog.mysawit.plantation.mapper.PlantationMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.service.validation.CoordinateNormalizationService;
import id.ac.ui.cs.advprog.mysawit.plantation.service.validation.NormalizedCoordinates;
import id.ac.ui.cs.advprog.mysawit.plantation.service.validation.OverlapValidationService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlantationMutationServiceImplTest {

    @Mock
    private PlantationRepository plantationRepository;

    @Mock
    private CoordinateNormalizationService coordinateNormalizationService;

    @Mock
    private OverlapValidationService overlapValidationService;

    @Mock
    private PlantationMapper plantationMapper;

    @InjectMocks
    private PlantationMutationServiceImpl mutationService;

    private NormalizedCoordinates stubNormalized() {
        List<List<Integer>> pts = List.of(
                List.of(0, 0), List.of(100, 0), List.of(100, 100), List.of(0, 100)
        );
        return new NormalizedCoordinates(0, 0, 100, 100, pts);
    }

    @Test
    void create_success() {
        CreatePlantationRequest request = new CreatePlantationRequest();
        request.setCode("PLT-001");
        request.setName("Kebun A");
        request.setArea(50.0);
        request.setCoordinates(List.of(
                List.of(0, 0), List.of(100, 0), List.of(100, 100), List.of(0, 100)
        ));
        NormalizedCoordinates normalized = stubNormalized();
        Plantation saved = new Plantation();
        PlantationResponse expected = new PlantationResponse();
        when(plantationRepository.existsByCode("PLT-001")).thenReturn(false);
        when(coordinateNormalizationService.normalize(request.getCoordinates()))
                .thenReturn(normalized);
        when(plantationMapper.toCoordinatesJson(normalized.points())).thenReturn("[]");
        when(plantationRepository.save(any(Plantation.class))).thenReturn(saved);
        when(plantationMapper.toResponse(saved)).thenReturn(expected);
        PlantationResponse result = mutationService.create(request);
        assertSame(expected, result);
        verify(overlapValidationService).validateNoOverlap(0, 0, 100, 100);
    }

    @Test
    void create_duplicateCode_throwsCodeAlreadyExists() {
        CreatePlantationRequest request = new CreatePlantationRequest();
        request.setCode("PLT-001");
        when(plantationRepository.existsByCode("PLT-001")).thenReturn(true);
        assertThrows(CodeAlreadyExistsException.class, () -> mutationService.create(request));
    }

    @Test
    void update_nameOnly_updatesName() {
        UUID id = UUID.randomUUID();
        Plantation plantation = new Plantation();
        plantation.setName("Old");
        plantation.setArea(10.0);
        UpdatePlantationRequest request = new UpdatePlantationRequest();
        request.setName("New Name");
        PlantationUpdateResponse expected = new PlantationUpdateResponse();
        when(plantationRepository.findById(id)).thenReturn(Optional.of(plantation));
        when(plantationRepository.save(plantation)).thenReturn(plantation);
        when(plantationMapper.toUpdateResponse(plantation)).thenReturn(expected);
        PlantationUpdateResponse result = mutationService.update(id, request);
        assertSame(expected, result);
        assertEquals("New Name", plantation.getName());
    }

    @Test
    void update_emptyName_throwsValidationFailed() {
        UUID id = UUID.randomUUID();
        Plantation plantation = new Plantation();
        UpdatePlantationRequest request = new UpdatePlantationRequest();
        request.setName("   ");
        when(plantationRepository.findById(id)).thenReturn(Optional.of(plantation));
        assertThrows(
                ValidationFailedException.class,
                () -> mutationService.update(id, request)
        );
    }

    @Test
    void update_areaZero_throwsValidationFailed() {
        UUID id = UUID.randomUUID();
        Plantation plantation = new Plantation();
        UpdatePlantationRequest request = new UpdatePlantationRequest();
        request.setArea(0.0);
        when(plantationRepository.findById(id)).thenReturn(Optional.of(plantation));
        assertThrows(
                ValidationFailedException.class,
                () -> mutationService.update(id, request)
        );
    }

    @Test
    void update_coordinatesOnly_runsOverlapCheck() {
        UUID id = UUID.randomUUID();
        Plantation plantation = new Plantation();
        List<List<Integer>> coords = List.of(
                List.of(0, 0), List.of(100, 0), List.of(100, 100), List.of(0, 100)
        );
        UpdatePlantationRequest request = new UpdatePlantationRequest();
        request.setCoordinates(coords);
        NormalizedCoordinates normalized = stubNormalized();
        PlantationUpdateResponse expected = new PlantationUpdateResponse();
        when(plantationRepository.findById(id)).thenReturn(Optional.of(plantation));
        when(coordinateNormalizationService.normalize(coords)).thenReturn(normalized);
        when(plantationMapper.toCoordinatesJson(normalized.points())).thenReturn("[]");
        when(plantationRepository.save(plantation)).thenReturn(plantation);
        when(plantationMapper.toUpdateResponse(plantation)).thenReturn(expected);
        mutationService.update(id, request);
        verify(overlapValidationService)
                .validateNoOverlapExcludingPlantation(0, 0, 100, 100, id);
    }

    @Test
    void update_notFound_throwsPlantationNotFound() {
        UUID id = UUID.randomUUID();
        when(plantationRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(
                PlantationNotFoundApiException.class,
                () -> mutationService.update(id, new UpdatePlantationRequest())
        );
    }

    @Test
    void delete_noMandor_returnsCode() {
        UUID id = UUID.randomUUID();
        Plantation plantation = new Plantation();
        plantation.setCode("PLT-001");
        plantation.setMandorId(null);
        when(plantationRepository.findById(id)).thenReturn(Optional.of(plantation));
        String result = mutationService.delete(id);
        assertEquals("PLT-001", result);
        verify(plantationRepository).delete(plantation);
    }

    @Test
    void delete_hasMandor_throwsPlantationHasMandor() {
        UUID id = UUID.randomUUID();
        Plantation plantation = new Plantation();
        plantation.setMandorId(UUID.randomUUID());
        plantation.setMandorName("Budi");
        when(plantationRepository.findById(id)).thenReturn(Optional.of(plantation));
        assertThrows(
                PlantationHasMandorException.class,
                () -> mutationService.delete(id)
        );
    }

    @Test
    void delete_notFound_throwsPlantationNotFound() {
        UUID id = UUID.randomUUID();
        when(plantationRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(
                PlantationNotFoundApiException.class,
                () -> mutationService.delete(id)
        );
    }
}
