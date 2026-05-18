package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PageResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationDetailResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationListItemResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.PlantationDriverAssignment;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundApiException;
import id.ac.ui.cs.advprog.mysawit.plantation.mapper.PlantationMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationDriverAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlantationQueryServiceImpl implements PlantationQueryService {

    private final PlantationRepository plantationRepository;
    private final PlantationDriverAssignmentRepository plantationDriverAssignmentRepository;
    private final PlantationMapper plantationMapper;

    @Override
    public PageResponse<PlantationListItemResponse> getAll(
            String name,
            String code,
            Pageable pageable
    ) {
        String nameFilter = (name == null || name.isBlank()) ? "" : name.trim();
        String codeFilter = (code == null || code.isBlank()) ? "" : code.trim();
        Page<Plantation> plantations =
                plantationRepository.findByFilters(nameFilter, codeFilter, pageable);
        return plantationMapper.toListPageResponse(
                plantations,
                plantation -> plantationDriverAssignmentRepository
                        .countByPlantationId(plantation.getId())
        );
    }

    @Override
    public PlantationDetailResponse getById(
            UUID plantationId,
            String driverName,
            Pageable driverPageable
    ) {
        Plantation plantation = plantationRepository.findById(plantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(plantationId));
        Page<PlantationDriverAssignment> drivers =
                findDrivers(plantationId, driverName, driverPageable);
        return plantationMapper.toDetailResponse(plantation, drivers);
    }

    private Page<PlantationDriverAssignment> findDrivers(
            UUID plantationId,
            String driverName,
            Pageable driverPageable
    ) {
        if (driverName == null || driverName.isBlank()) {
            return plantationDriverAssignmentRepository.findByPlantationId(
                    plantationId,
                    driverPageable
            );
        }
        return plantationDriverAssignmentRepository
                .findByPlantationIdAndDriverNameContainingIgnoreCase(
                        plantationId,
                        driverName.trim(),
                        driverPageable
                );
    }
}
