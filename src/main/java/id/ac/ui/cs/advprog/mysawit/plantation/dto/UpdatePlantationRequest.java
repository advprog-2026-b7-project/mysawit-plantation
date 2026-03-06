package id.ac.ui.cs.advprog.mysawit.plantation.dto;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.Coordinate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePlantationRequest {

    private String plantationCode;

    @NotBlank
    private String plantationName;

    @NotNull
    private Double areaSize;

    @NotNull
    @NotEmpty
    @Size(min = 4, max = 4)
    private List<Coordinate> coordinates;
}
