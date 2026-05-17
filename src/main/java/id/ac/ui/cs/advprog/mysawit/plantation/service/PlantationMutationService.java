package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.UpdatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationUpdateResponse;
import java.util.UUID;

public interface PlantationMutationService {
    PlantationResponse create(CreatePlantationRequest request);
    PlantationUpdateResponse update(UUID plantationId, UpdatePlantationRequest request);
    String delete(UUID plantationId);
}
