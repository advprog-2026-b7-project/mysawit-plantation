package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.CodeAlreadyExistsException;
import id.ac.ui.cs.advprog.mysawit.plantation.mapper.PlantationMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.security.JwtAdminGuard;
import id.ac.ui.cs.advprog.mysawit.plantation.service.validation.CoordinateNormalizationService;
import id.ac.ui.cs.advprog.mysawit.plantation.service.validation.NormalizedCoordinates;
import id.ac.ui.cs.advprog.mysawit.plantation.service.validation.OverlapValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlantationService {

    private final PlantationRepository plantationRepository;
    private final CoordinateNormalizationService coordinateNormalizationService;
    private final OverlapValidationService overlapValidationService;
    private final JwtAdminGuard jwtAdminGuard;
    private final PlantationMapper plantationMapper;

    public PlantationService(
            PlantationRepository plantationRepository,
            CoordinateNormalizationService coordinateNormalizationService,
            OverlapValidationService overlapValidationService,
            JwtAdminGuard jwtAdminGuard,
            PlantationMapper plantationMapper
    ) {
        this.plantationRepository = plantationRepository;
        this.coordinateNormalizationService = coordinateNormalizationService;
        this.overlapValidationService = overlapValidationService;
        this.jwtAdminGuard = jwtAdminGuard;
        this.plantationMapper = plantationMapper;
    }

    @Transactional
    public PlantationResponse create(String authorizationHeader, CreatePlantationRequest request) {
        jwtAdminGuard.requireAdmin(authorizationHeader);

        if (plantationRepository.existsByCode(request.getCode())) {
            throw new CodeAlreadyExistsException(request.getCode());
        }

        NormalizedCoordinates normalized = coordinateNormalizationService
            .normalize(request.getCoordinates());
        overlapValidationService.validateNoOverlap(
                normalized.minX(),
                normalized.minY(),
                normalized.maxX(),
                normalized.maxY()
        );

        Plantation plantation = new Plantation();
        plantation.setName(request.getName());
        plantation.setCode(request.getCode());
        plantation.setArea(request.getArea());
        plantation.setMinX(normalized.minX());
        plantation.setMinY(normalized.minY());
        plantation.setMaxX(normalized.maxX());
        plantation.setMaxY(normalized.maxY());
        plantation.setCoordinatesJson(plantationMapper.toCoordinatesJson(normalized.points()));
        plantation.setMandorId(null);

        Plantation saved = plantationRepository.save(plantation);
        return plantationMapper.toResponse(saved);
    }
}
