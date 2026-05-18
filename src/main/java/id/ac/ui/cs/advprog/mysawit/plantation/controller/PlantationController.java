package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignDriverRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.AssignMandorRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.CreatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.ReassignPlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.request.UpdatePlantationRequest;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.ApiSuccessResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.ApiSuccessMessageResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.DriverAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.MandorAssignmentResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PageResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationDetailResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationListItemResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationUpdateResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.CodeUpdateForbiddenException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.InvalidQueryParamException;
import id.ac.ui.cs.advprog.mysawit.plantation.service.PlantationService;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/plantations")
public class PlantationController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> PLANTATION_SORT_FIELDS = Set.of(
            "name",
            "code",
            "area",
            "createdAt"
    );

    private final PlantationService plantationService;
    private final ObjectMapper objectMapper;

    public PlantationController(PlantationService plantationService, ObjectMapper objectMapper) {
        this.plantationService = plantationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<PageResponse<PlantationListItemResponse>>>
            getAllPlantations(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort
    ) {
        Pageable pageable = buildPlantationPageable(page, size, sort);
        PageResponse<PlantationListItemResponse> data = plantationService.getAll(
                authorizationHeader,
                name,
                code,
                pageable
        );
        return ResponseEntity.ok(new ApiSuccessResponse<>(data));
    }

    @GetMapping("/{plantationId}")
    public ResponseEntity<ApiSuccessResponse<PlantationDetailResponse>> getPlantationById(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable UUID plantationId,
            @RequestParam(required = false) String driverName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable driverPageable = buildDriverPageable(page, size);
        PlantationDetailResponse data = plantationService.getById(
                authorizationHeader,
                plantationId,
                driverName,
                driverPageable
        );
        return ResponseEntity.ok(new ApiSuccessResponse<>(data));
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

    @DeleteMapping("/{plantationId}/mandor")
    public ResponseEntity<ApiSuccessResponse<MandorAssignmentResponse>> unassignMandor(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable UUID plantationId,
            @RequestBody(required = false) ReassignPlantationRequest request
    ) {
        MandorAssignmentResponse data =
                plantationService.unassignMandor(authorizationHeader, plantationId, request);
        return ResponseEntity.ok(new ApiSuccessResponse<>(data));
    }

    @DeleteMapping("/{plantationId}/drivers/{driverId}")
    public ResponseEntity<ApiSuccessResponse<DriverAssignmentResponse>> unassignDriver(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable UUID plantationId,
            @PathVariable UUID driverId,
            @RequestBody(required = false) ReassignPlantationRequest request
    ) {
        DriverAssignmentResponse data =
                plantationService.unassignDriver(
                        authorizationHeader,
                        plantationId,
                        driverId,
                        request
                );
        return ResponseEntity.ok(new ApiSuccessResponse<>(data));
    }

    private Pageable buildPlantationPageable(int page, int size, String sort) {
        validatePageRequest(page, size);
        return PageRequest.of(page, size, parseSort(sort));
    }

    private Pageable buildDriverPageable(int page, int size) {
        validatePageRequest(page, size);
        return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "driverName"));
    }

    private void validatePageRequest(int page, int size) {
        if (page < DEFAULT_PAGE || size <= 0 || size > MAX_PAGE_SIZE) {
            throw new InvalidQueryParamException(
                    "page must be >= 0 and size must be between 1 and 100."
            );
        }
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.split(",");
        if (parts.length > 2) {
            throw new InvalidQueryParamException("sort must use field,direction format.");
        }
        String field = parts[0].trim();
        if (!PLANTATION_SORT_FIELDS.contains(field)) {
            throw new InvalidQueryParamException("Unsupported sort field: " + field);
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length == 2 && !parts[1].isBlank()) {
            try {
                direction = Sort.Direction.fromString(parts[1].trim());
            } catch (IllegalArgumentException ex) {
                throw new InvalidQueryParamException("Unsupported sort direction.");
            }
        }
        return Sort.by(direction, field);
    }
}
