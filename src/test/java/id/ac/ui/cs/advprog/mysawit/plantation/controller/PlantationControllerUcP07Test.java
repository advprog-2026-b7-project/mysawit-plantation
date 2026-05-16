package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
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
import java.util.Base64;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlantationControllerUcP07Test {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PlantationRepository plantationRepository;
    @Autowired private PlantationDriverAssignmentRepository driverAssignmentRepository;

    @BeforeEach
    void setUp() {
        driverAssignmentRepository.deleteAll();
        plantationRepository.deleteAll();

        plantationRepository.save(makeP("Kebun Alpha", "KBN-ALPHA", 0, 0, 100, 100));
        plantationRepository.save(makeP("Kebun Beta", "KBN-BETA", 200, 200, 300, 300));
        plantationRepository.save(makeP("Sawit Gamma", "SAW-GAMMA", 400, 400, 500, 500));
    }

    private Plantation makeP(String name, String code, int x1, int y1, int x2, int y2) {
        Plantation p = new Plantation();
        p.setName(name); p.setCode(code); p.setArea(50.0);
        p.setMinX(x1); p.setMinY(y1); p.setMaxX(x2); p.setMaxY(y2);
        p.setCoordinatesJson("[[" + x1 + "," + y1 + "],[" + x2 + "," + y1
                + "],[" + x2 + "," + y2 + "],[" + x1 + "," + y2 + "]]");
        return p;
    }

    @Test
    void getAllPlantationsNoFilter() throws Exception {
        mockMvc.perform(get("/api/v1/plantations")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    void filterByName() throws Exception {
        mockMvc.perform(get("/api/v1/plantations?name=Kebun")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    void filterByCode() throws Exception {
        mockMvc.perform(get("/api/v1/plantations?code=KBN")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    void filterByNameNoMatch() throws Exception {
        mockMvc.perform(get("/api/v1/plantations?name=Nonexistent")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void filterByNameCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/v1/plantations?name=alpha")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("Kebun Alpha"));
    }

    @Test
    void getAllPlantationsForbiddenNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/plantations")
                        .header("Authorization", userToken()))
                .andExpect(status().isForbidden());
    }

    private String adminToken() throws Exception {
        return bearerWithPayload(
                Map.of("role", "ADMIN", "sub", "11111111-1111-1111-1111-111111111111"));
    }

    private String userToken() throws Exception {
        return bearerWithPayload(
                Map.of("role", "BURUH", "sub", "22222222-2222-2222-2222-222222222222"));
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
