package id.ac.ui.cs.advprog.mysawit.plantation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlantationResponse {
    private String id;
    private String plantationCode;
    private String plantationName;
    private String location;
    private Double areaSize;
}