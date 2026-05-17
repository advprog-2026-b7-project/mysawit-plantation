package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationDetailResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.PlantationDriverAssignment;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundApiException;
import id.ac.ui.cs.advprog.mysawit.plantation.mapper.PlantationMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationDriverAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlantationQueryServiceImpl implements PlantationQueryService {

    private final PlantationRepository plantationRepository;
    private final PlantationDriverAssignmentRepository plantationDriverAssignmentRepository;
    private final PlantationMapper plantationMapper;

    @Override
    public List<PlantationResponse> getAll(String name, String code) {
        String nameFilter = (name == null || name.isBlank()) ? "" : name.trim();
        String codeFilter = (code == null || code.isBlank()) ? "" : code.trim();
        return plantationRepository.findByFilters(nameFilter, codeFilter).stream()
                .map(plantationMapper::toResponse)
                .toList();
    }

    @Override
    public PlantationDetailResponse getById(UUID plantationId) {
        Plantation plantation = plantationRepository.findById(plantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(plantationId));
        List<PlantationDriverAssignment> drivers =
                plantationDriverAssignmentRepository.findByPlantationId(plantationId);
        return plantationMapper.toDetailResponse(plantation, drivers);
    }
}
