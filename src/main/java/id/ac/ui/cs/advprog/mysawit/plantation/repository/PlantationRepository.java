package id.ac.ui.cs.advprog.mysawit.plantation.repository;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlantationRepository extends JpaRepository<Plantation, UUID> {

    boolean existsByCode(String code);

    boolean existsByMandorId(UUID mandorId);

    java.util.Optional<Plantation> findByMandorId(UUID mandorId);

    java.util.Optional<Plantation> findByMandorIdAndIdNot(UUID mandorId, UUID plantationId);

    @Query("SELECT p FROM Plantation p WHERE "
            + "(:name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) "
            + "AND (:code = '' OR LOWER(p.code) LIKE LOWER(CONCAT('%', :code, '%')))")
    Page<Plantation> findByFilters(
            @Param("name") String name,
            @Param("code") String code,
            Pageable pageable
    );

    @Query(
            "SELECT p FROM Plantation p WHERE :minX < p.maxX AND :maxX > p.minX "
                    + "AND :minY < p.maxY AND :maxY > p.minY"
    )
    java.util.List<Plantation> findOverlapping(
            @Param("minX") Integer minX,
            @Param("maxX") Integer maxX,
            @Param("minY") Integer minY,
            @Param("maxY") Integer maxY
    );

    @Query(
            "SELECT p FROM Plantation p WHERE p.id <> :excludeId "
                    + "AND :minX < p.maxX AND :maxX > p.minX "
                    + "AND :minY < p.maxY AND :maxY > p.minY"
    )
    java.util.List<Plantation> findOverlappingExcludingId(
            @Param("minX") Integer minX,
            @Param("maxX") Integer maxX,
            @Param("minY") Integer minY,
            @Param("maxY") Integer maxY,
            @Param("excludeId") UUID excludeId
    );
}
