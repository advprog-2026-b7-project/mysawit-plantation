package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.MandorAlreadyAssignedException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.MandorInOtherPlantationException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundApiException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UserNotMandorException;
import id.ac.ui.cs.advprog.mysawit.plantation.gateway.UserProfile;
import id.ac.ui.cs.advprog.mysawit.plantation.gateway.UserProfileGateway;
import id.ac.ui.cs.advprog.mysawit.plantation.mapper.PlantationMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MandorAssignmentServiceImpl implements MandorAssignmentService {

    private final PlantationRepository plantationRepository;
    private final UserProfileGateway userProfileGateway;
    private final PlantationMapper plantationMapper;

    @Override
    @Transactional
    public MandorAssignmentResponse assign(UUID plantationId, AssignMandorRequest request) {
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

    @Override
    @Transactional
    public void unassign(UUID plantationId) {
        Plantation plantation = plantationRepository.findById(plantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(plantationId));
        plantation.setMandorId(null);
        plantation.setMandorName(null);
        plantation.setMandorCertificationNumber(null);
        plantation.setUpdatedAt(Instant.now());
        plantationRepository.save(plantation);
    }
}
