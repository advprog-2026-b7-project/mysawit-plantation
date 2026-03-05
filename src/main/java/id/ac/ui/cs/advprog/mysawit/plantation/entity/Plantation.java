package id.ac.ui.cs.advprog.mysawit.plantation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    private String id;

    @Column(unique = true, nullable = false)
    private String plantationCode;

    @Column(nullable = false)
    private String plantationName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double areaSize;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}