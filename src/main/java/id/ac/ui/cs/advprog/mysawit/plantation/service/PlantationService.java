package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.PlantationResponse;

public interface PlantationService {
    PlantationResponse createPlantation(CreatePlantationRequest request);
}