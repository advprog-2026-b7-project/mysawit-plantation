package id.ac.ui.cs.advprog.mysawit.plantation.dto;

import lombok.Data;

@Data
public class CreatePlantationRequest {
    private String plantationCode;
    private String plantationName;
    private String location;
    private Double areaSize;
}