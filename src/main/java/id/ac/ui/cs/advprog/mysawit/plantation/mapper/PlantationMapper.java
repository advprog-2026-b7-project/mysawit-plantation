package id.ac.ui.cs.advprog.mysawit.plantation.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationUpdateResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.ValidationFailedException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PlantationMapper {

    private final ObjectMapper objectMapper;

    public PlantationMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public PlantationResponse toResponse(Plantation plantation) {
        PlantationResponse response = new PlantationResponse();
        response.setId(plantation.getId().toString());
        response.setName(plantation.getName());
        response.setCode(plantation.getCode());
        response.setArea(plantation.getArea());
        response.setCoordinates(readCoordinatesJson(plantation.getCoordinatesJson()));
        response.setMandor(null);
        response.setCreatedAt(plantation.getCreatedAt());
        return response;
    }

    public PlantationUpdateResponse toUpdateResponse(Plantation plantation) {
        PlantationUpdateResponse response = new PlantationUpdateResponse();
        response.setId(plantation.getId().toString());
        response.setName(plantation.getName());
        response.setCode(plantation.getCode());
        response.setArea(plantation.getArea());
        response.setCoordinates(readCoordinatesJson(plantation.getCoordinatesJson()));
        response.setUpdatedAt(plantation.getUpdatedAt());
        return response;
    }

    public String toCoordinatesJson(List<List<Integer>> points) {
        try {
            return objectMapper.writeValueAsString(points);
        } catch (Exception ex) {
            throw new ValidationFailedException("invalid coordinates payload");
        }
    }

    public List<List<Integer>> readCoordinatesJson(String coordinatesJson) {
        try {
            return objectMapper.readValue(
                    coordinatesJson,
                    new TypeReference<List<List<Integer>>>() {
                    }
            );
        } catch (Exception ex) {
            throw new ValidationFailedException("failed to read stored coordinates");
        }
    }
}
