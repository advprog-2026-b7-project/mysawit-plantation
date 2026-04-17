package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignDriverRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.UpdatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.ApiSuccessMessageResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.DriverAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationUpdateResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.CodeUpdateForbiddenException;
import id.ac.ui.cs.advprog.mysawit.plantation.service.PlantationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/plantations")
public class PlantationController {

    private final PlantationService plantationService;
    private final ObjectMapper objectMapper;

    public PlantationController(PlantationService plantationService, ObjectMapper objectMapper) {
        this.plantationService = plantationService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<PlantationResponse>> createPlantation(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody CreatePlantationRequest request
    ) {
        PlantationResponse data = plantationService.create(authorizationHeader, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse<>(data));
    }

    @PutMapping("/{plantationId}")
    public ResponseEntity<ApiSuccessResponse<PlantationUpdateResponse>> updatePlantation(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable UUID plantationId,
            @RequestBody JsonNode rawRequestBody
    ) {
        if (rawRequestBody.has("code")) {
            throw new CodeUpdateForbiddenException();
        }

        UpdatePlantationRequest request = objectMapper.convertValue(
                rawRequestBody,
                UpdatePlantationRequest.class
        );

        PlantationUpdateResponse data = plantationService.update(
                authorizationHeader,
                plantationId,
                request
        );
        return ResponseEntity.ok(new ApiSuccessResponse<>(data));
    }

    @DeleteMapping("/{plantationId}")
    public ResponseEntity<ApiSuccessMessageResponse> deletePlantation(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable UUID plantationId
    ) {
        String plantationCode = plantationService.delete(authorizationHeader, plantationId);
        return ResponseEntity.ok(
                new ApiSuccessMessageResponse(
                        "Plantation " + plantationCode + " successfully deleted."
                )
        );
    }

    @PostMapping("/{plantationId}/mandor")
    public ResponseEntity<ApiSuccessResponse<MandorAssignmentResponse>> assignMandor(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable UUID plantationId,
            @Valid @RequestBody AssignMandorRequest request
    ) {
        MandorAssignmentResponse data = plantationService.assignMandor(
                authorizationHeader,
                plantationId,
                request
        );
        return ResponseEntity.ok(new ApiSuccessResponse<>(data));
    }

    @PostMapping("/{plantationId}/drivers")
    public ResponseEntity<ApiSuccessResponse<DriverAssignmentResponse>> assignDriver(
            @RequestHeader(value = "Authorization", required = false)
            String authorizationHeader,
            @PathVariable UUID plantationId,
            @Valid @RequestBody AssignDriverRequest request
    ) {
        DriverAssignmentResponse data = plantationService.assignDriver(
                authorizationHeader,
                plantationId,
                request
        );
        return ResponseEntity.ok(new ApiSuccessResponse<>(data));
    }
}
