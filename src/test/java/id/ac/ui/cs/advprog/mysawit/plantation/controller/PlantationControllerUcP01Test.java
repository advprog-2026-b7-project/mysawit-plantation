package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlantationControllerUcP01Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

        @Autowired
        private PlantationRepository plantationRepository;

        @BeforeEach
        void setUp() {
                plantationRepository.deleteAll();
        }

    @Test
    void createPlantationSuccess() throws Exception {
        String body = """
                {
                  "name": "Kebun Blok A",
                  "code": "KBN-001",
                  "area": 200.5,
                  "coordinates": [[0,0],[200,0],[200,200],[0,200]]
                }
                """;

        mockMvc.perform(post("/api/v1/plantations")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Kebun Blok A"))
                .andExpect(jsonPath("$.data.code").value("KBN-001"))
                .andExpect(jsonPath("$.data.mandor").value(nullValue()))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty());
    }

    @Test
    void createPlantationDuplicateCode() throws Exception {
        String body = """
                {
                  "name": "Kebun Blok A",
                  "code": "KBN-002",
                  "area": 200.5,
                  "coordinates": [[0,0],[200,0],[200,200],[0,200]]
                }
                """;

        mockMvc.perform(post("/api/v1/plantations")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/plantations")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("CODE_ALREADY_EXISTS"));
    }

    @Test
    void createPlantationOverlap() throws Exception {
        String body1 = """
                {
                  "name": "Kebun Blok A",
                  "code": "KBN-003",
                  "area": 200.5,
                  "coordinates": [[0,0],[200,0],[200,200],[0,200]]
                }
                """;
        String body2 = """
                {
                  "name": "Kebun Blok B",
                  "code": "KBN-004",
                  "area": 210.0,
                  "coordinates": [[100,100],[250,100],[250,250],[100,250]]
                }
                """;

        mockMvc.perform(post("/api/v1/plantations")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/plantations")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("PLANTATION_OVERLAP"));
    }

    @Test
    void createPlantationInvalidCoordinates() throws Exception {
        String body = """
                {
                  "name": "Kebun Blok A",
                  "code": "KBN-005",
                  "area": 200.5,
                  "coordinates": [[0,0],[200,0],[200,200]]
                }
                """;

        mockMvc.perform(post("/api/v1/plantations")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("VALIDATION_FAILED"));
    }

    @Test
    void createPlantationForbiddenNonAdmin() throws Exception {
        String body = """
                {
                  "name": "Kebun Blok A",
                  "code": "KBN-006",
                  "area": 200.5,
                  "coordinates": [[0,0],[200,0],[200,200],[0,200]]
                }
                """;

        mockMvc.perform(post("/api/v1/plantations")
                        .header("Authorization", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("FORBIDDEN"));
    }

    private String adminToken() throws Exception {
        Map<String, Object> payload = Map.of(
                "role", "ADMIN",
                "sub", "11111111-1111-1111-1111-111111111111"
        );
        return bearerWithPayload(payload);
    }

    private String userToken() throws Exception {
        Map<String, Object> payload = Map.of(
                "role", "MANDOR",
                "sub", "22222222-2222-2222-2222-222222222222"
        );
        return bearerWithPayload(payload);
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
