package id.ac.ui.cs.advprog.mysawit.plantation.dto.response;

import java.time.Instant;
import java.util.List;

public class PlantationResponse {

    private String id;
    private String name;
    private String code;
    private Double area;
    private List<List<Integer>> coordinates;
    private Object mandor;
    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public List<List<Integer>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Integer>> coordinates) {
        this.coordinates = coordinates;
    }

    public Object getMandor() {
        return mandor;
    }

    public void setMandor(Object mandor) {
        this.mandor = mandor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
