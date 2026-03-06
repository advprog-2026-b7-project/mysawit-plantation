package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.UpdatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationCodeUpdateNotAllowedException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundException;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationAlreadyExistsException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlantationServiceImpl implements PlantationService {

    private final PlantationRepository plantationRepository;

    @Override
    public PlantationResponse createPlantation(CreatePlantationRequest request) {

        if (plantationRepository.existsByPlantationCode(request.getPlantationCode())) {
            throw new PlantationAlreadyExistsException(request.getPlantationCode());
        }

        Plantation plantation = Plantation.builder()
                .plantationCode(request.getPlantationCode())
                .plantationName(request.getPlantationName())
                .areaSize(request.getAreaSize())
                .coordinates(request.getCoordinates())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Plantation savedPlantation = plantationRepository.save(plantation);

        return toResponse(savedPlantation);
    }

    @Override
    public PlantationResponse updatePlantation(UUID id, UpdatePlantationRequest request) {
        Plantation plantation = plantationRepository.findById(id)
                .orElseThrow(() -> new PlantationNotFoundException(id));

        if (request.getPlantationCode() != null
                && !Objects.equals(request.getPlantationCode(), plantation.getPlantationCode())) {
            throw new PlantationCodeUpdateNotAllowedException();
        }

        plantation.setPlantationName(request.getPlantationName());
        plantation.setAreaSize(request.getAreaSize());
        plantation.setCoordinates(request.getCoordinates());
        plantation.setUpdatedAt(LocalDateTime.now());

        Plantation savedPlantation = plantationRepository.save(plantation);

        return toResponse(savedPlantation);
    }

    private PlantationResponse toResponse(Plantation plantation) {
        return PlantationResponse.builder()
                .id(plantation.getId().toString())
                .plantationCode(plantation.getPlantationCode())
                .plantationName(plantation.getPlantationName())
                .areaSize(plantation.getAreaSize())
                .coordinates(plantation.getCoordinates())
                .build();
    }
}