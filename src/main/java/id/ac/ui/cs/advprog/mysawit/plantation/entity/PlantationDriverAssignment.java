package id.ac.ui.cs.advprog.mysawit.plantation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "plantation_driver_assignments")
public class PlantationDriverAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "plantation_id", nullable = false)
    private UUID plantationId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Column(name = "driver_email")
    private String driverEmail;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @PrePersist
    public void prePersist() {
        if (this.assignedAt == null) {
            this.assignedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPlantationId() {
        return plantationId;
    }

    public void setPlantationId(UUID plantationId) {
        this.plantationId = plantationId;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public void setDriverId(UUID driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }
}
