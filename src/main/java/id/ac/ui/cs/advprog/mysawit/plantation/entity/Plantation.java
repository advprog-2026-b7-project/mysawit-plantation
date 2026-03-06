package id.ac.ui.cs.advprog.mysawit.plantation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Size;

@Entity
@Table(name = "plantations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plantation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", unique = true, nullable = false)
    private String plantationCode;

    @Column(name = "name", nullable = false)
    private String plantationName;

    @Column(name = "area_hectare", nullable = false)
    private Double areaSize;

    @Convert(converter = CoordinateListConverter.class)
    @Column(name = "coordinates", nullable = false, columnDefinition = "jsonb")
    @Size(min = 4, max = 4)
    private List<Coordinate> coordinates;

    @Column(name = "assigned_mandor_id")
    private UUID assignedMandorId;

    @Column(name = "assigned_driver_id")
    private UUID assignedDriverId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}