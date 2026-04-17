package id.ac.ui.cs.advprog.mysawit.plantation.repository;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;

public interface PlantationRepository extends JpaRepository<Plantation, UUID> {

    boolean existsByCode(String code);

    @Query(
            "SELECT p FROM Plantation p WHERE :minX < p.maxX AND :maxX > p.minX "
                    + "AND :minY < p.maxY AND :maxY > p.minY"
    )
    List<Plantation> findOverlapping(
            @Param("minX") Integer minX,
            @Param("maxX") Integer maxX,
            @Param("minY") Integer minY,
            @Param("maxY") Integer maxY
    );
}
