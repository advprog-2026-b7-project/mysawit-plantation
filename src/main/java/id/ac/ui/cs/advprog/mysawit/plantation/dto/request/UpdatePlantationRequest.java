package id.ac.ui.cs.advprog.mysawit.plantation.dto.request;

import java.util.List;

public class UpdatePlantationRequest {

    private String name;
    private Double area;
    private List<List<Integer>> coordinates;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
