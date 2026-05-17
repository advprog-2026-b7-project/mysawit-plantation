package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationDetailResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import java.util.List;
import java.util.UUID;

public interface PlantationQueryService {
    List<PlantationResponse> getAll(String name, String code);
    PlantationDetailResponse getById(UUID plantationId);
}
