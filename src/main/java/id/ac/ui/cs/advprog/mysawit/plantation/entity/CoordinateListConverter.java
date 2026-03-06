package id.ac.ui.cs.advprog.mysawit.plantation.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter
public class CoordinateListConverter implements AttributeConverter<List<Coordinate>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<Coordinate>> COORDINATE_LIST_TYPE =
            new TypeReference<>() { };

    @Override
    public String convertToDatabaseColumn(List<Coordinate> coordinates) {
        if (coordinates == null) {
            return "[]";
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(coordinates);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize coordinates", ex);
        }
    }

    @Override
    public List<Coordinate> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return OBJECT_MAPPER.readValue(dbData, COORDINATE_LIST_TYPE);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize coordinates", ex);
        }
    }
}
