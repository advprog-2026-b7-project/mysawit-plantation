package id.ac.ui.cs.advprog.mysawit.plantation.repository;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.PlantationDriverAssignment;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantationDriverAssignmentRepository
        extends JpaRepository<PlantationDriverAssignment, UUID> {

    boolean existsByPlantationIdAndDriverId(UUID plantationId, UUID driverId);

    java.util.List<PlantationDriverAssignment> findByPlantationId(UUID plantationId);

    Page<PlantationDriverAssignment> findByPlantationId(UUID plantationId, Pageable pageable);

    Page<PlantationDriverAssignment> findByPlantationIdAndDriverNameContainingIgnoreCase(
            UUID plantationId,
            String driverName,
            Pageable pageable
    );

    long countByPlantationId(UUID plantationId);

    java.util.Optional<PlantationDriverAssignment> findByPlantationIdAndDriverId(
            UUID plantationId, UUID driverId);
}
