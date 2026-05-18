package id.ac.ui.cs.advprog.mysawit.plantation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PageResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationDetailResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationListItemResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Pageable pageable = PageRequest.of(0, 20);
        Page<Plantation> page = new PageImpl<>(List.of(p), pageable, 1);
        PageResponse<PlantationListItemResponse> expected =
                new PageResponse<>(List.of(new PlantationListItemResponse()), 0, 20, 1, 1);
        when(plantationRepository.findByFilters("", "", pageable)).thenReturn(page);
        when(plantationMapper.toListPageResponse(eq(page), any())).thenReturn(expected);
        PageResponse<PlantationListItemResponse> result = queryService.getAll("", "", pageable);
        assertSame(expected, result);
    }

    @Test
    void getAll_withNameFilter_passesFilter() {
        Plantation p = new Plantation();
        Pageable pageable = PageRequest.of(0, 20);
        Page<Plantation> page = new PageImpl<>(List.of(p), pageable, 1);
        PageResponse<PlantationListItemResponse> expected =
                new PageResponse<>(List.of(new PlantationListItemResponse()), 0, 20, 1, 1);
        when(plantationRepository.findByFilters("kebun", "", pageable)).thenReturn(page);
        when(plantationMapper.toListPageResponse(eq(page), any())).thenReturn(expected);
        PageResponse<PlantationListItemResponse> result =
                queryService.getAll("kebun", "", pageable);
        assertSame(expected, result);
        verify(plantationRepository).findByFilters("kebun", "", pageable);
    }

    @Test
    void getAll_nullFilter_treatedAsEmpty() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Plantation> page = new PageImpl<>(List.of(), pageable, 0);
        PageResponse<PlantationListItemResponse> expected =
                new PageResponse<>(List.of(), 0, 20, 0, 0);
        when(plantationRepository.findByFilters("", "", pageable)).thenReturn(page);
        when(plantationMapper.toListPageResponse(eq(page), any())).thenReturn(expected);
        PageResponse<PlantationListItemResponse> result =
                queryService.getAll(null, null, pageable);
        assertEquals(0, result.getTotalElements());
        verify(plantationRepository).findByFilters("", "", pageable);
    }

    @Test
    void getAll_blankFilter_treatedAsEmpty() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Plantation> page = new PageImpl<>(List.of(), pageable, 0);
        PageResponse<PlantationListItemResponse> expected =
                new PageResponse<>(List.of(), 0, 20, 0, 0);
        when(plantationRepository.findByFilters("", "", pageable)).thenReturn(page);
        when(plantationMapper.toListPageResponse(eq(page), any())).thenReturn(expected);
        queryService.getAll("  ", "  ", pageable);
        verify(plantationRepository).findByFilters("", "", pageable);
    }

    @Test
    void getById_found_returnsDetail() {
        UUID id = UUID.randomUUID();
        Plantation plantation = new Plantation();
        Pageable pageable = PageRequest.of(0, 20);
        Page<PlantationDriverAssignment> drivers = new PageImpl<>(List.of(), pageable, 0);
        PlantationDetailResponse expected = new PlantationDetailResponse();
        when(plantationRepository.findById(id)).thenReturn(Optional.of(plantation));
        when(plantationDriverAssignmentRepository.findByPlantationId(id, pageable))
                .thenReturn(drivers);
        when(plantationMapper.toDetailResponse(plantation, drivers)).thenReturn(expected);
        PlantationDetailResponse result = queryService.getById(id, null, pageable);
        assertSame(expected, result);
    }

    @Test
    void getById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(plantationRepository.findById(id)).thenReturn(Optional.empty());
        Pageable pageable = PageRequest.of(0, 20);
        assertThrows(
                PlantationNotFoundApiException.class,
                () -> queryService.getById(id, null, pageable)
        );
    }
}
