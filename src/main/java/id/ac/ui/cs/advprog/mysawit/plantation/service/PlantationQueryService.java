package id.ac.ui.cs.advprog.mysawit.plantation.service;

import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PageResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationDetailResponse;
import id.ac.ui.cs.advprog.mysawit.plantation.dto.response.PlantationListItemResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface PlantationQueryService {
    PageResponse<PlantationListItemResponse> getAll(
            String name,
            String code,
            Pageable pageable
    );

    PlantationDetailResponse getById(
            UUID plantationId,
            String driverName,
            Pageable driverPageable
    );
}
