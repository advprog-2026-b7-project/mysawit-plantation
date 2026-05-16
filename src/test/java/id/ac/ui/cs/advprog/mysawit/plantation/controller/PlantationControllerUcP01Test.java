package id.ac.ui.cs.advprog.mysawit.plantation.controller;

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

import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.security.JwtTestHelper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlantationControllerUcP01Test {

    @Autowired
    private MockMvc mockMvc;

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

    private String adminToken() {
        return JwtTestHelper.adminBearer();
    }

    private String userToken() {
        return JwtTestHelper.userBearer("MANDOR");
    }
}
