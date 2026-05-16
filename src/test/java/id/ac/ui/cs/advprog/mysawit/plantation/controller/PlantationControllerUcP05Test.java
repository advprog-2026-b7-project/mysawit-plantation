package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationDriverAssignmentRepository;
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
class PlantationControllerUcP05Test {

    private static final UUID MANDOR_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID DRIVER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlantationRepository plantationRepository;

    @Autowired
    private PlantationDriverAssignmentRepository plantationDriverAssignmentRepository;

    @BeforeEach
    void setUp() {
        plantationDriverAssignmentRepository.deleteAll();
        plantationRepository.deleteAll();
    }

    @Test
    void assignDriverSuccess() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-501");

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/drivers", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverId\":\"" + DRIVER_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.plantationId").value(plantationId))
                .andExpect(jsonPath("$.data.driver.id").value(DRIVER_ID.toString()))
                .andExpect(jsonPath("$.data.driver.name").value("Agus Triyanto"))
                .andExpect(jsonPath("$.data.assignedAt").isNotEmpty());
    }

    @Test
    void assignDriverAlreadyAssignedToPlantation() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-502");

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/drivers", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverId\":\"" + DRIVER_ID + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/drivers", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverId\":\"" + DRIVER_ID + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("DRIVER_ALREADY_IN_PLANTATION"));
    }

    @Test
    void assignDriverUserNotFound() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-503");
        UUID unknownUserId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/drivers", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverId\":\"" + unknownUserId + "\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("USER_NOT_FOUND"));
    }

    @Test
    void assignDriverRejectedWhenUserNotDriver() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-504");

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/drivers", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverId\":\"" + MANDOR_ID + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("USER_NOT_DRIVER"));
    }

    @Test
    void assignDriverPlantationNotFound() throws Exception {
        UUID unknownPlantationId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/drivers", unknownPlantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverId\":\"" + DRIVER_ID + "\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("PLANTATION_NOT_FOUND"));
    }

    @Test
    void assignDriverForbiddenNonAdmin() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-505");

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/drivers", plantationId)
                        .header("Authorization", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"driverId\":\"" + DRIVER_ID + "\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("FORBIDDEN"));
    }

    private Plantation createPlantationEntity(String name, String code) {
        Plantation plantation = new Plantation();
        plantation.setName(name);
        plantation.setCode(code);
        plantation.setArea(200.5);
        plantation.setMinX(0);
        plantation.setMinY(0);
        plantation.setMaxX(200);
        plantation.setMaxY(200);
        plantation.setCoordinatesJson("[[0,0],[200,0],[200,200],[0,200]]");
        return plantation;
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

        JsonNode json = objectMapper.readTree(response);
        return json.get("data").get("id").asText();
    }

    private String adminToken() {
        return JwtTestHelper.adminBearer();
    }

    private String userToken() {
        return JwtTestHelper.userBearer("MANDOR");
    }
}
