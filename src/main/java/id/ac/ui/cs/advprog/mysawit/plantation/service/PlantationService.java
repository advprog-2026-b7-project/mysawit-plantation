package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.UpdatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationUpdateResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.CodeAlreadyExistsException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.MandorAlreadyAssignedException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.MandorInOtherPlantationException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationHasMandorException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundApiException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.ValidationFailedException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UserNotMandorException;
import id.ac.ui.cs.advprog.mysawit.plantation.mapper.PlantationMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.gateway.UserProfile;
import id.ac.ui.cs.advprog.mysawit.plantation.gateway.UserProfileGateway;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.security.JwtAdminGuard;
import id.ac.ui.cs.advprog.mysawit.plantation.service.validation.CoordinateNormalizationService;
import id.ac.ui.cs.advprog.mysawit.plantation.service.validation.NormalizedCoordinates;
import id.ac.ui.cs.advprog.mysawit.plantation.service.validation.OverlapValidationService;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlantationService {

    private final PlantationRepository plantationRepository;
    private final CoordinateNormalizationService coordinateNormalizationService;
    private final OverlapValidationService overlapValidationService;
    private final JwtAdminGuard jwtAdminGuard;
    private final PlantationMapper plantationMapper;
    private final UserProfileGateway userProfileGateway;

    public PlantationService(
            PlantationRepository plantationRepository,
            CoordinateNormalizationService coordinateNormalizationService,
            OverlapValidationService overlapValidationService,
            JwtAdminGuard jwtAdminGuard,
            PlantationMapper plantationMapper,
            UserProfileGateway userProfileGateway
    ) {
        this.plantationRepository = plantationRepository;
        this.coordinateNormalizationService = coordinateNormalizationService;
        this.overlapValidationService = overlapValidationService;
        this.jwtAdminGuard = jwtAdminGuard;
        this.plantationMapper = plantationMapper;
        this.userProfileGateway = userProfileGateway;
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

    @Transactional
    public PlantationUpdateResponse update(
            String authorizationHeader,
            UUID plantationId,
            UpdatePlantationRequest request
    ) {
        jwtAdminGuard.requireAdmin(authorizationHeader);

        Plantation plantation = plantationRepository.findById(plantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(plantationId));

        if (request.getName() != null) {
            String name = request.getName().trim();
            if (name.isEmpty()) {
                throw new ValidationFailedException("name must be non-empty");
            }
            plantation.setName(name);
        }

        if (request.getArea() != null) {
            if (request.getArea() <= 0) {
                throw new ValidationFailedException("area must be greater than 0");
            }
            plantation.setArea(request.getArea());
        }

        if (request.getCoordinates() != null) {
            NormalizedCoordinates normalized = coordinateNormalizationService
                    .normalize(request.getCoordinates());
            overlapValidationService.validateNoOverlapExcludingPlantation(
                    normalized.minX(),
                    normalized.minY(),
                    normalized.maxX(),
                    normalized.maxY(),
                    plantationId
            );
            plantation.setMinX(normalized.minX());
            plantation.setMinY(normalized.minY());
            plantation.setMaxX(normalized.maxX());
            plantation.setMaxY(normalized.maxY());
            plantation.setCoordinatesJson(plantationMapper.toCoordinatesJson(normalized.points()));
        }

        plantation.setUpdatedAt(Instant.now());
        Plantation saved = plantationRepository.save(plantation);
        return plantationMapper.toUpdateResponse(saved);
    }

    @Transactional
    public String delete(String authorizationHeader, UUID plantationId) {
        jwtAdminGuard.requireAdmin(authorizationHeader);

        Plantation plantation = plantationRepository.findById(plantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(plantationId));

        if (plantation.getMandorId() != null) {
            throw new PlantationHasMandorException(plantation.getMandorName());
        }

        String plantationCode = plantation.getCode();
        plantationRepository.delete(plantation);
        return plantationCode;
    }

    @Transactional
    public MandorAssignmentResponse assignMandor(
            String authorizationHeader,
            UUID plantationId,
            AssignMandorRequest request
    ) {
        jwtAdminGuard.requireAdmin(authorizationHeader);

        Plantation plantation = plantationRepository.findById(plantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(plantationId));

        if (plantation.getMandorId() != null) {
            throw new MandorAlreadyAssignedException();
        }

        UserProfile userProfile = userProfileGateway.findById(request.getMandorId())
                .orElseThrow(() -> new UserNotFoundException(request.getMandorId()));

        if (!"MANDOR".equalsIgnoreCase(userProfile.role())) {
            throw new UserNotMandorException();
        }

        plantationRepository.findByMandorIdAndIdNot(request.getMandorId(), plantationId)
                .ifPresent(existing -> {
                    throw new MandorInOtherPlantationException(existing.getId());
                });

        Instant assignedAt = Instant.now();
        plantation.setMandorId(userProfile.id());
        plantation.setMandorName(userProfile.name());
        plantation.setMandorCertificationNumber(userProfile.certificationNumber());
        plantation.setUpdatedAt(assignedAt);

        Plantation saved = plantationRepository.save(plantation);
        return plantationMapper.toMandorAssignmentResponse(saved, userProfile, assignedAt);
    }
}
