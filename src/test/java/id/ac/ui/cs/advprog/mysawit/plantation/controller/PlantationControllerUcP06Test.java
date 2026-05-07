package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.PlantationDriverAssignment;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationDriverAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlantationControllerUcP06Test {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PlantationRepository plantationRepository;
    @Autowired private PlantationDriverAssignmentRepository driverAssignmentRepository;

    private Plantation saved;

    @BeforeEach
    void setUp() {
        driverAssignmentRepository.deleteAll();
        plantationRepository.deleteAll();

        Plantation p = new Plantation();
        p.setName("Kebun Detail Test");
        p.setCode("KBN-P06");
        p.setArea(100.0);
        p.setMinX(0); p.setMinY(0); p.setMaxX(100); p.setMaxY(100);
        p.setCoordinatesJson("[[0,0],[100,0],[100,100],[0,100]]");
        p.setMandorId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        p.setMandorName("Budi Mandor");
        p.setMandorCertificationNumber("CERT-001");
        saved = plantationRepository.save(p);

        PlantationDriverAssignment d = new PlantationDriverAssignment();
        d.setPlantationId(saved.getId());
        d.setDriverId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
        d.setDriverName("Siti Supir");
        d.setAssignedAt(Instant.now());
        driverAssignmentRepository.save(d);
    }

    @Test
    void getPlantationDetailSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/plantations/" + saved.getId())
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.data.name").value("Kebun Detail Test"))
                .andExpect(jsonPath("$.data.code").value("KBN-P06"))
                .andExpect(jsonPath("$.data.mandor.name").value("Budi Mandor"))
                .andExpect(jsonPath("$.data.mandor.certificationNumber").value("CERT-001"))
                .andExpect(jsonPath("$.data.drivers[0].name").value("Siti Supir"));
    }

    @Test
    void getPlantationDetailNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/plantations/" + UUID.randomUUID())
                        .header("Authorization", adminToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].code").value("PLANTATION_NOT_FOUND"));
    }

    @Test
    void getPlantationDetailForbiddenNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/plantations/" + saved.getId())
                        .header("Authorization", userToken()))
                .andExpect(status().isForbidden());
    }

    private String adminToken() throws Exception {
        return bearerWithPayload(Map.of("role", "ADMIN", "sub", "11111111-1111-1111-1111-111111111111"));
    }

    private String userToken() throws Exception {
        return bearerWithPayload(Map.of("role", "BURUH", "sub", "22222222-2222-2222-2222-222222222222"));
    }

    private String bearerWithPayload(Map<String, Object> payload) throws Exception {
        String headerJson = objectMapper.writeValueAsString(Map.of("alg", "none", "typ", "JWT"));
        String payloadJson = objectMapper.writeValueAsString(payload);
        String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        return "Bearer " + encodedHeader + "." + encodedPayload + ".signature";
    }
}
