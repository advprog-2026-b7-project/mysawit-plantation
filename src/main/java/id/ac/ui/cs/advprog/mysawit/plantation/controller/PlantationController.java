package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.UpdatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.service.PlantationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/plantations")
@RequiredArgsConstructor
public class PlantationController {

    private final PlantationService plantationService;

    @PostMapping
    public PlantationResponse createPlantation(@Valid @RequestBody CreatePlantationRequest request) {
        return plantationService.createPlantation(request);
    }

    @PutMapping("/{id}")
    public PlantationResponse updatePlantation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlantationRequest request
    ) {
        return plantationService.updatePlantation(id, request);
    }
}