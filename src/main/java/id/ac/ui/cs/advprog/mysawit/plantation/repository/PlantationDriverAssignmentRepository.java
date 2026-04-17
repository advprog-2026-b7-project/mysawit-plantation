package id.ac.ui.cs.advprog.mysawit.plantation.repository;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.PlantationDriverAssignment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantationDriverAssignmentRepository
        extends JpaRepository<PlantationDriverAssignment, UUID> {

    boolean existsByPlantationIdAndDriverId(UUID plantationId, UUID driverId);
}
