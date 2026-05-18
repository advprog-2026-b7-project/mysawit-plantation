package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.ReassignPlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.MandorAlreadyAssignedException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.MandorInOtherPlantationException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.MandorNotInPlantationException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundApiException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.ReassignPlantationRequiredException;
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
        plantation.setMandorEmail(userProfile.email());
        plantation.setMandorCertificationNumber(userProfile.certificationNumber());
        plantation.setUpdatedAt(assignedAt);
        Plantation saved = plantationRepository.save(plantation);
        return plantationMapper.toMandorAssignmentResponse(saved, userProfile, assignedAt);
    }

    @Override
    @Transactional
    public MandorAssignmentResponse unassign(
            UUID plantationId,
            ReassignPlantationRequest request
    ) {
        Plantation source = plantationRepository.findById(plantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(plantationId));
        if (source.getMandorId() == null) {
            throw new MandorNotInPlantationException();
        }
        UUID targetPlantationId = requireTargetPlantationId(plantationId, request);
        Plantation target = plantationRepository.findById(targetPlantationId)
                .orElseThrow(() -> new PlantationNotFoundApiException(targetPlantationId));
        if (target.getMandorId() != null) {
            throw new MandorAlreadyAssignedException();
        }

        UUID mandorId = source.getMandorId();
        String mandorName = source.getMandorName();
        String mandorEmail = source.getMandorEmail();
        String certificationNumber = source.getMandorCertificationNumber();
        Instant assignedAt = Instant.now();

        source.setMandorId(null);
        source.setMandorName(null);
        source.setMandorEmail(null);
        source.setMandorCertificationNumber(null);
        source.setUpdatedAt(assignedAt);

        target.setMandorId(mandorId);
        target.setMandorName(mandorName);
        target.setMandorEmail(mandorEmail);
        target.setMandorCertificationNumber(certificationNumber);
        target.setUpdatedAt(assignedAt);

        plantationRepository.save(source);
        Plantation savedTarget = plantationRepository.save(target);
        UserProfile reassignedMandor = new UserProfile(
                mandorId,
                mandorName,
                mandorEmail,
                "MANDOR",
                certificationNumber
        );
        return plantationMapper.toMandorAssignmentResponse(
                savedTarget,
                reassignedMandor,
                assignedAt
        );
    }

    private UUID requireTargetPlantationId(
            UUID sourcePlantationId,
            ReassignPlantationRequest request
    ) {
        if (request == null || request.getReassignToPlantationId() == null
                || sourcePlantationId.equals(request.getReassignToPlantationId())) {
            throw new ReassignPlantationRequiredException();
        }
        return request.getReassignToPlantationId();
    }
}
