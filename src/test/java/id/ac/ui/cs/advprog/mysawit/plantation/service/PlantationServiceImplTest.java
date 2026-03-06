package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.UpdatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Coordinate;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationCodeUpdateNotAllowedException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.PlantationNotFoundException;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlantationServiceImplTest {

    @Mock
    private PlantationRepository plantationRepository;

    @InjectMocks
    private PlantationServiceImpl plantationService;

    private UUID plantationId;
    private Plantation existingPlantation;

    @BeforeEach
    void setUp() {
        plantationId = UUID.randomUUID();
        existingPlantation = Plantation.builder()
                .id(plantationId)
                .plantationCode("PLT-001")
                .plantationName("Kebun Lama")
                .areaSize(10.0)
                .coordinates(List.of(
                        new Coordinate(0, 0),
                        new Coordinate(1, 0),
                        new Coordinate(1, 1),
                        new Coordinate(0, 1)
                ))
                .build();
    }

    @Test
    void updatePlantationShouldSucceedWhenCodeUnchanged() {
        UpdatePlantationRequest request = new UpdatePlantationRequest();
        request.setPlantationCode("PLT-001");
        request.setPlantationName("Kebun Baru");
        request.setAreaSize(20.5);
        request.setCoordinates(List.of(
                new Coordinate(2, 2),
                new Coordinate(3, 2),
                new Coordinate(3, 3),
                new Coordinate(2, 3)
        ));

        when(plantationRepository.findById(plantationId)).thenReturn(Optional.of(existingPlantation));
        when(plantationRepository.save(any(Plantation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlantationResponse response = plantationService.updatePlantation(plantationId, request);

        assertEquals("PLT-001", response.getPlantationCode());
        assertEquals("Kebun Baru", response.getPlantationName());
        assertEquals(20.5, response.getAreaSize());
        assertEquals(4, response.getCoordinates().size());
        verify(plantationRepository).save(existingPlantation);
    }

    @Test
    void updatePlantationShouldFailWhenCodeChanged() {
        UpdatePlantationRequest request = new UpdatePlantationRequest();
        request.setPlantationCode("PLT-999");
        request.setPlantationName("Kebun Baru");
        request.setAreaSize(20.5);
        request.setCoordinates(List.of(
                new Coordinate(2, 2),
                new Coordinate(3, 2),
                new Coordinate(3, 3),
                new Coordinate(2, 3)
        ));

        when(plantationRepository.findById(plantationId)).thenReturn(Optional.of(existingPlantation));

        assertThrows(
                PlantationCodeUpdateNotAllowedException.class,
                () -> plantationService.updatePlantation(plantationId, request)
        );
    }

    @Test
    void updatePlantationShouldFailWhenPlantationNotFound() {
        UpdatePlantationRequest request = new UpdatePlantationRequest();
        request.setPlantationName("Kebun Baru");
        request.setAreaSize(20.5);
        request.setCoordinates(List.of(
                new Coordinate(2, 2),
                new Coordinate(3, 2),
                new Coordinate(3, 3),
                new Coordinate(2, 3)
        ));

        when(plantationRepository.findById(plantationId)).thenReturn(Optional.empty());

        assertThrows(
                PlantationNotFoundException.class,
                () -> plantationService.updatePlantation(plantationId, request)
        );
    }
}
