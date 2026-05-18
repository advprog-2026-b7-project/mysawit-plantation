package id.ac.ui.cs.advprog.mysawit.plantation.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.DriverAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.DriverSummaryResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PageResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationDetailResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationListItemResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorSummaryResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationUpdateResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.PlantationDriverAssignment;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.ValidationFailedException;
import id.ac.ui.cs.advprog.mysawit.plantation.gateway.UserProfile;
import java.util.List;
import java.time.Instant;
import org.springframework.data.domain.Page;
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

    public PlantationListItemResponse toListItemResponse(
            Plantation plantation,
            long driverCount
    ) {
        PlantationListItemResponse response = new PlantationListItemResponse();
        response.setId(plantation.getId().toString());
        response.setName(plantation.getName());
        response.setCode(plantation.getCode());
        response.setArea(plantation.getArea());
        response.setMandorName(plantation.getMandorName());
        response.setDriverCount(driverCount);
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

    public MandorAssignmentResponse toMandorAssignmentResponse(
            Plantation plantation,
            UserProfile userProfile,
            Instant assignedAt
    ) {
        MandorSummaryResponse mandor = new MandorSummaryResponse();
        mandor.setId(userProfile.id().toString());
        mandor.setName(userProfile.name());
        mandor.setEmail(userProfile.email());
        mandor.setCertificationNumber(userProfile.certificationNumber());

        MandorAssignmentResponse response = new MandorAssignmentResponse();
        response.setPlantationId(plantation.getId().toString());
        response.setMandor(mandor);
        response.setAssignedAt(assignedAt);
        return response;
    }

    public DriverAssignmentResponse toDriverAssignmentResponse(
            PlantationDriverAssignment assignment,
            UserProfile userProfile
    ) {
        DriverSummaryResponse driver = new DriverSummaryResponse();
        driver.setId(userProfile.id().toString());
        driver.setName(userProfile.name());
        driver.setEmail(userProfile.email());

        DriverAssignmentResponse response = new DriverAssignmentResponse();
        response.setPlantationId(assignment.getPlantationId().toString());
        response.setDriver(driver);
        response.setAssignedAt(assignment.getAssignedAt());
        return response;
    }

    public PlantationDetailResponse toDetailResponse(
            Plantation plantation,
            Page<PlantationDriverAssignment> driverAssignments
    ) {
        PlantationDetailResponse response = new PlantationDetailResponse();
        response.setId(plantation.getId().toString());
        response.setName(plantation.getName());
        response.setCode(plantation.getCode());
        response.setArea(plantation.getArea());
        response.setCoordinates(readCoordinatesJson(plantation.getCoordinatesJson()));
        response.setCreatedAt(plantation.getCreatedAt());
        response.setUpdatedAt(plantation.getUpdatedAt());

        if (plantation.getMandorId() != null) {
            MandorSummaryResponse mandor = new MandorSummaryResponse();
            mandor.setId(plantation.getMandorId().toString());
            mandor.setName(plantation.getMandorName());
            mandor.setEmail(plantation.getMandorEmail());
            mandor.setCertificationNumber(plantation.getMandorCertificationNumber());
            response.setMandor(mandor);
        }

        Page<DriverSummaryResponse> drivers = driverAssignments.map(a -> {
            DriverSummaryResponse d = new DriverSummaryResponse();
            d.setId(a.getDriverId().toString());
            d.setName(a.getDriverName());
            d.setEmail(a.getDriverEmail());
            return d;
        });
        response.setDrivers(PageResponse.from(drivers));

        return response;
    }

    public PageResponse<PlantationListItemResponse> toListPageResponse(
            Page<Plantation> plantations,
            java.util.function.Function<Plantation, Long> driverCounter
    ) {
        Page<PlantationListItemResponse> mapped = plantations.map(
                plantation -> toListItemResponse(plantation, driverCounter.apply(plantation))
        );
        return PageResponse.from(mapped);
    }

    public List<DriverSummaryResponse> toDriverSummaries(
            List<PlantationDriverAssignment> driverAssignments
    ) {
        return driverAssignments.stream()
                .map(a -> {
                    DriverSummaryResponse d = new DriverSummaryResponse();
                    d.setId(a.getDriverId().toString());
                    d.setName(a.getDriverName());
                    d.setEmail(a.getDriverEmail());
                    return d;
                })
                .toList();
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
