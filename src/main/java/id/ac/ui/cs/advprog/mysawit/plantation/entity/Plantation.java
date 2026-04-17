package id.ac.ui.cs.advprog.mysawit.plantation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "plantations")
public class Plantation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Double area;

    @Column(name = "min_x", nullable = false)
    private Integer minX;

    @Column(name = "min_y", nullable = false)
    private Integer minY;

    @Column(name = "max_x", nullable = false)
    private Integer maxX;

    @Column(name = "max_y", nullable = false)
    private Integer maxY;

    @Column(name = "coordinates_json", nullable = false, columnDefinition = "TEXT")
    private String coordinatesJson;

    @Column(name = "mandor_id")
    private UUID mandorId;

    @Column(name = "mandor_name")
    private String mandorName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getMinX() {
        return minX;
    }

    public void setMinX(Integer minX) {
        this.minX = minX;
    }

    public Integer getMinY() {
        return minY;
    }

    public void setMinY(Integer minY) {
        this.minY = minY;
    }

    public Integer getMaxX() {
        return maxX;
    }

    public void setMaxX(Integer maxX) {
        this.maxX = maxX;
    }

    public Integer getMaxY() {
        return maxY;
    }

    public void setMaxY(Integer maxY) {
        this.maxY = maxY;
    }

    public String getCoordinatesJson() {
        return coordinatesJson;
    }

    public void setCoordinatesJson(String coordinatesJson) {
        this.coordinatesJson = coordinatesJson;
    }

    public UUID getMandorId() {
        return mandorId;
    }

    public void setMandorId(UUID mandorId) {
        this.mandorId = mandorId;
    }

    public String getMandorName() {
        return mandorName;
    }

    public void setMandorName(String mandorName) {
        this.mandorName = mandorName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
