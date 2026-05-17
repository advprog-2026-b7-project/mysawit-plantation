package id.ac.ui.cs.advprog.mysawit.plantation.service.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationOverlapException;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OverlapValidationServiceTest {

    @Mock
    private PlantationRepository plantationRepository;

    @InjectMocks
    private OverlapValidationService overlapValidationService;

    private Plantation conflictingPlantation() {
        Plantation p = new Plantation();
        p.setName("Existing");
        p.setCode("PLT-X");
        return p;
    }

    @Test
    void validateNoOverlap_noConflict_passes() {
        when(plantationRepository.findOverlapping(0, 100, 0, 100))
                .thenReturn(List.of());
        assertDoesNotThrow(
                () -> overlapValidationService.validateNoOverlap(0, 0, 100, 100)
        );
    }

    @Test
    void validateNoOverlap_conflict_throwsPlantationOverlap() {
        when(plantationRepository.findOverlapping(0, 100, 0, 100))
                .thenReturn(List.of(conflictingPlantation()));
        assertThrows(
                PlantationOverlapException.class,
                () -> overlapValidationService.validateNoOverlap(0, 0, 100, 100)
        );
    }

    @Test
    void validateNoOverlapExcludingPlantation_noConflict_passes() {
        UUID id = UUID.randomUUID();
        when(plantationRepository.findOverlappingExcludingId(0, 100, 0, 100, id))
                .thenReturn(List.of());
        assertDoesNotThrow(
                () -> overlapValidationService
                        .validateNoOverlapExcludingPlantation(0, 0, 100, 100, id)
        );
    }

    @Test
    void validateNoOverlapExcludingPlantation_conflict_throws() {
        UUID id = UUID.randomUUID();
        when(plantationRepository.findOverlappingExcludingId(0, 100, 0, 100, id))
                .thenReturn(List.of(conflictingPlantation()));
        assertThrows(
                PlantationOverlapException.class,
                () -> overlapValidationService
                        .validateNoOverlapExcludingPlantation(0, 0, 100, 100, id)
        );
    }
}
