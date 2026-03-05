package id.ac.ui.cs.advprog.mysawit.plantation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import jakarta.persistence.ElementCollection;
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

    @Column(unique = true, nullable = false)
    private String plantationCode;

    @Column(nullable = false)
    private String plantationName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double areaSize;

    @ElementCollection
    @Size(min = 4, max = 4)
    private List<Coordinate> coordinates;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}