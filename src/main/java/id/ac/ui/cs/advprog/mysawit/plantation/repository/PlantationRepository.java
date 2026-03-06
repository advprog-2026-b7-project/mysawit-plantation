package id.ac.ui.cs.advprog.mysawit.plantation.repository;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlantationRepository extends JpaRepository<Plantation, UUID> {
    Optional<Plantation> findByPlantationCode(String plantationCode);
    boolean existsByPlantationCode(String plantationCode);
}