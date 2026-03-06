package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.UpdatePlantationRequest;

import java.util.UUID;

public interface PlantationService {
    PlantationResponse createPlantation(CreatePlantationRequest request);

    PlantationResponse updatePlantation(UUID id, UpdatePlantationRequest request);
}