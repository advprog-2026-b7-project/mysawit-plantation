package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.security.JwtTestHelper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlantationControllerUcP03Test {

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
    void deletePlantationSuccess() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-301");

        mockMvc.perform(delete("/api/v1/plantations/{plantationId}", plantationId)
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message")
                        .value("Plantation KBN-301 successfully deleted."));
    }

    @Test
    void deletePlantationBlockedWhenMandorAssigned() throws Exception {
        Plantation plantation = new Plantation();
        plantation.setName("Kebun Blok A");
        plantation.setCode("KBN-302");
        plantation.setArea(200.5);
        plantation.setMinX(0);
        plantation.setMinY(0);
        plantation.setMaxX(200);
        plantation.setMaxY(200);
        plantation.setCoordinatesJson("[[0,0],[200,0],[200,200],[0,200]]");
        plantation.setMandorId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        plantation.setMandorName("Budi Santoso");
        plantationRepository.save(plantation);

        mockMvc.perform(delete("/api/v1/plantations/{plantationId}", plantation.getId())
                        .header("Authorization", adminToken()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("PLANTATION_HAS_MANDOR"))
                .andExpect(jsonPath("$.errors[0].detail", containsString("Budi Santoso")));
    }

    @Test
    void deletePlantationNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/plantations/{plantationId}", UUID.randomUUID())
                        .header("Authorization", adminToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("PLANTATION_NOT_FOUND"));
    }

    @Test
    void deletePlantationForbiddenNonAdmin() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-303");

        mockMvc.perform(delete("/api/v1/plantations/{plantationId}", plantationId)
                        .header("Authorization", userToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("FORBIDDEN"));
    }

    private String createPlantationAndGetId(String name, String code) throws Exception {
        String body = "{" +
                "\"name\":\"" + name + "\"," +
                "\"code\":\"" + code + "\"," +
                "\"area\":200.5," +
                "\"coordinates\":[[0,0],[200,0],[200,200],[0,200]]" +
                "}";

        String response = mockMvc.perform(post("/api/v1/plantations")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("data").get("id").asText();
    }

    private String adminToken() {
        return JwtTestHelper.adminBearer();
    }

    private String userToken() {
        return JwtTestHelper.userBearer("MANDOR");
    }
}
