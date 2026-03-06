package id.ac.ui.cs.advprog.mysawit.plantation.dto;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.Coordinate;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlantationResponse {
    private String id;
    private String plantationCode;
    private String plantationName;
    private Double areaSize;
    private List<Coordinate> coordinates;
}