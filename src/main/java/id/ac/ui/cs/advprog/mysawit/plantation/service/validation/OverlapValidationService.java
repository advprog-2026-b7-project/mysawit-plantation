package id.ac.ui.cs.advprog.mysawit.plantation.service.validation;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationOverlapException;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OverlapValidationService {

    private final PlantationRepository plantationRepository;

    public OverlapValidationService(PlantationRepository plantationRepository) {
        this.plantationRepository = plantationRepository;
    }

    public void validateNoOverlap(int minX, int minY, int maxX, int maxY) {
        List<Plantation> overlaps = plantationRepository.findOverlapping(minX, maxX, minY, maxY);
        if (!overlaps.isEmpty()) {
            Plantation conflict = overlaps.get(0);
            throw new PlantationOverlapException(conflict.getName(), conflict.getCode());
        }
    }

    public void validateNoOverlapExcludingPlantation(
            int minX,
            int minY,
            int maxX,
            int maxY,
            UUID plantationId
    ) {
        List<Plantation> overlaps = plantationRepository.findOverlappingExcludingId(
                minX,
                maxX,
                minY,
                maxY,
                plantationId
        );
        if (!overlaps.isEmpty()) {
            Plantation conflict = overlaps.get(0);
            throw new PlantationOverlapException(conflict.getName(), conflict.getCode());
        }
    }
}
