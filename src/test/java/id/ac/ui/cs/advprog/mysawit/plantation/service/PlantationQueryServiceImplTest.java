package id.ac.ui.cs.advprog.mysawit.plantation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationDetailResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.PlantationDriverAssignment;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundApiException;
import id.ac.ui.cs.advprog.mysawit.plantation.mapper.PlantationMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationDriverAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlantationQueryServiceImplTest {

    @Mock
    private PlantationRepository plantationRepository;

    @Mock
    private PlantationDriverAssignmentRepository plantationDriverAssignmentRepository;

    @Mock
    private PlantationMapper plantationMapper;

    @InjectMocks
    private PlantationQueryServiceImpl queryService;

    @Test
    void getAll_noFilter_returnsAll() {
        Plantation p = new Plantation();
        PlantationResponse response = new PlantationResponse();
        when(plantationRepository.findByFilters("", "")).thenReturn(List.of(p));
        when(plantationMapper.toResponse(p)).thenReturn(response);
        List<PlantationResponse> result = queryService.getAll("", "");
        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    void getAll_withNameFilter_passesFilter() {
        Plantation p = new Plantation();
        PlantationResponse response = new PlantationResponse();
        when(plantationRepository.findByFilters("kebun", "")).thenReturn(List.of(p));
        when(plantationMapper.toResponse(p)).thenReturn(response);
        List<PlantationResponse> result = queryService.getAll("kebun", "");
        assertEquals(1, result.size());
        verify(plantationRepository).findByFilters("kebun", "");
    }

    @Test
    void getAll_nullFilter_treatedAsEmpty() {
        when(plantationRepository.findByFilters("", "")).thenReturn(List.of());
        List<PlantationResponse> result = queryService.getAll(null, null);
        assertEquals(0, result.size());
        verify(plantationRepository).findByFilters("", "");
    }

    @Test
    void getAll_blankFilter_treatedAsEmpty() {
        when(plantationRepository.findByFilters("", "")).thenReturn(List.of());
        queryService.getAll("  ", "  ");
        verify(plantationRepository).findByFilters("", "");
    }

    @Test
    void getById_found_returnsDetail() {
        UUID id = UUID.randomUUID();
        Plantation plantation = new Plantation();
        List<PlantationDriverAssignment> drivers = List.of();
        PlantationDetailResponse expected = new PlantationDetailResponse();
        when(plantationRepository.findById(id)).thenReturn(Optional.of(plantation));
        when(plantationDriverAssignmentRepository.findByPlantationId(id))
                .thenReturn(drivers);
        when(plantationMapper.toDetailResponse(plantation, drivers)).thenReturn(expected);
        PlantationDetailResponse result = queryService.getById(id);
        assertSame(expected, result);
    }

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(plantationRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(
                PlantationNotFoundApiException.class,
                () -> queryService.getById(id)
        );
    }
}
