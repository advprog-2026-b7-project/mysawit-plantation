package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationAlreadyExistsException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
                .location(request.getLocation())
                .areaSize(request.getAreaSize())
                .coordinates(request.getCoordinates())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Plantation savedPlantation = plantationRepository.save(plantation);

        return PlantationResponse.builder()
                .id(savedPlantation.getId().toString())
                .plantationCode(savedPlantation.getPlantationCode())
                .plantationName(savedPlantation.getPlantationName())
                .location(savedPlantation.getLocation())
                .areaSize(savedPlantation.getAreaSize())
                .coordinates(savedPlantation.getCoordinates())
                .build();
    }
}