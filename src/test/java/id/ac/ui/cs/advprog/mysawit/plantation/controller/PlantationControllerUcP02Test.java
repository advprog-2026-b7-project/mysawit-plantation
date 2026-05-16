package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlantationControllerUcP02Test {

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
    void editPlantationSuccess() throws Exception {
        String plantationId = createPlantationAndGetId(
                "Kebun Blok A",
                "KBN-201",
                "[[0,0],[200,0],[200,200],[0,200]]"
        );

        String body = """
                {
                  "name": "Kebun Blok A Updated",
                  "area": 250.0,
                  "coordinates": [[0,0],[250,0],[250,250],[0,250]]
                }
                """;

        mockMvc.perform(put("/api/v1/plantations/{plantationId}", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(plantationId))
                .andExpect(jsonPath("$.data.name").value("Kebun Blok A Updated"))
                .andExpect(jsonPath("$.data.code").value("KBN-201"))
                .andExpect(jsonPath("$.data.area").value(250.0))
                .andExpect(jsonPath("$.data.updatedAt").isNotEmpty());
    }

    @Test
    void editPlantationWithCodeKeyPresent() throws Exception {
        String plantationId = createPlantationAndGetId(
                "Kebun Blok A",
                "KBN-202",
                "[[0,0],[200,0],[200,200],[0,200]]"
        );

        mockMvc.perform(put("/api/v1/plantations/{plantationId}", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("CODE_UPDATE_FORBIDDEN"));
    }

    @Test
    void editPlantationOverlap() throws Exception {
        createPlantationAndGetId(
                "Kebun Blok A",
                "KBN-203",
                "[[0,0],[200,0],[200,200],[0,200]]"
        );
        String secondId = createPlantationAndGetId(
                "Kebun Blok B",
                "KBN-204",
                "[[300,300],[450,300],[450,450],[300,450]]"
        );

        String body = """
                {
                  "coordinates": [[100,100],[250,100],[250,250],[100,250]]
                }
                """;

        mockMvc.perform(put("/api/v1/plantations/{plantationId}", secondId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("PLANTATION_OVERLAP"));
    }

    @Test
    void editPlantationNotFound() throws Exception {
        String body = """
                {
                  "name": "Kebun Tidak Ada"
                }
                """;

        mockMvc.perform(put("/api/v1/plantations/{plantationId}", UUID.randomUUID())
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("PLANTATION_NOT_FOUND"));
    }

    @Test
    void editPlantationForbiddenNonAdmin() throws Exception {
        String plantationId = createPlantationAndGetId(
                "Kebun Blok A",
                "KBN-205",
                "[[0,0],[200,0],[200,200],[0,200]]"
        );

        mockMvc.perform(put("/api/v1/plantations/{plantationId}", plantationId)
                        .header("Authorization", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Nope\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("FORBIDDEN"));
    }

    private String createPlantationAndGetId(
            String name,
            String code,
            String coordinates
    ) throws Exception {
        String body = "{" +
                "\"name\":\"" + name + "\"," +
                "\"code\":\"" + code + "\"," +
                "\"area\":200.5," +
                "\"coordinates\":" + coordinates +
                "}";

        MvcResult result = mockMvc.perform(post("/api/v1/plantations")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("data").get("id").asText();
    }

    private String adminToken() {
        return JwtTestHelper.adminBearer();
    }

    private String userToken() {
        return JwtTestHelper.userBearer("MANDOR");
    }
}
