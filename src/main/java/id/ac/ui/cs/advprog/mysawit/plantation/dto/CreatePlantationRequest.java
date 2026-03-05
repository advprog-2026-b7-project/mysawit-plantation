package id.ac.ui.cs.advprog.mysawit.plantation.dto;

import lombok.Data;
import java.util.List;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Coordinate;
import jakarta.validation.constraints.Size;

@Data
public class CreatePlantationRequest {
    private String plantationCode;
    private String plantationName;
    private String location;
    private Double areaSize;
    private List<Coordinate> coordinates;
}