package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
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
class PlantationControllerUcP04Test {

    private static final UUID MANDOR_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID DRIVER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

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
    void assignMandorSuccess() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-401");

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/mandor", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mandorId\":\"" + MANDOR_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.plantationId").value(plantationId))
                .andExpect(jsonPath("$.data.mandor.id").value(MANDOR_ID.toString()))
                .andExpect(jsonPath("$.data.mandor.name").value("Budi Santoso"))
                .andExpect(jsonPath("$.data.mandor.certificationNumber")
                        .value("CERT-2024-001"))
                .andExpect(jsonPath("$.data.assignedAt").isNotEmpty());
    }

    @Test
    void assignMandorAlreadyAssignedToPlantation() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-402");

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/mandor", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mandorId\":\"" + MANDOR_ID + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/mandor", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mandorId\":\"" + MANDOR_ID + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("MANDOR_ALREADY_ASSIGNED"));
    }

    @Test
    void assignMandorInOtherPlantation() throws Exception {
        String targetPlantationId = createPlantationAndGetId("Kebun Blok A", "KBN-403");
        Plantation otherPlantation = createPlantationEntity("Kebun Blok B", "KBN-404");
        otherPlantation.setMandorId(MANDOR_ID);
        otherPlantation.setMandorName("Budi Santoso");
        otherPlantation.setMandorCertificationNumber("CERT-2024-001");
        plantationRepository.save(otherPlantation);

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/mandor", targetPlantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mandorId\":\"" + MANDOR_ID + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("MANDOR_IN_OTHER_PLANTATION"));
    }

    @Test
    void assignMandorUserNotFound() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-405");
        UUID unknownUserId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/mandor", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mandorId\":\"" + unknownUserId + "\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("USER_NOT_FOUND"));
    }

    @Test
    void assignMandorRejectedWhenUserNotMandor() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-406");

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/mandor", plantationId)
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mandorId\":\"" + DRIVER_ID + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("USER_NOT_MANDOR"));
    }

    @Test
    void assignMandorForbiddenNonAdmin() throws Exception {
        String plantationId = createPlantationAndGetId("Kebun Blok A", "KBN-407");

        mockMvc.perform(post("/api/v1/plantations/{plantationId}/mandor", plantationId)
                        .header("Authorization", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mandorId\":\"" + MANDOR_ID + "\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors[0].code").value("FORBIDDEN"));
    }

    @Test
    void unassignMandorReassignsToTargetPlantation() throws Exception {
        Plantation source = createPlantationEntity("Kebun Source", "KBN-408");
        source.setMandorId(MANDOR_ID);
        source.setMandorName("Budi Santoso");
        source.setMandorEmail("budi@mysawit.id");
        source.setMandorCertificationNumber("CERT-2024-001");
        source = plantationRepository.save(source);
        Plantation target = plantationRepository.save(
                createPlantationEntity("Kebun Target", "KBN-409")
        );

        mockMvc.perform(delete("/api/v1/plantations/{plantationId}/mandor", source.getId())
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reassignBody(target.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.plantationId").value(target.getId().toString()))
                .andExpect(jsonPath("$.data.mandor.id").value(MANDOR_ID.toString()));

        Plantation updatedSource = plantationRepository.findById(source.getId()).orElseThrow();
        Plantation updatedTarget = plantationRepository.findById(target.getId()).orElseThrow();
        assertNull(updatedSource.getMandorId());
        assertEquals(MANDOR_ID, updatedTarget.getMandorId());
    }

    @Test
    void unassignMandorRequiresReassignTarget() throws Exception {
        Plantation source = createPlantationEntity("Kebun Source", "KBN-410");
        source.setMandorId(MANDOR_ID);
        source.setMandorName("Budi Santoso");
        source = plantationRepository.save(source);

        mockMvc.perform(delete("/api/v1/plantations/{plantationId}/mandor", source.getId())
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code")
                        .value("REASSIGN_PLANTATION_REQUIRED"));
    }

    @Test
    void unassignMandorTargetNotFound() throws Exception {
        Plantation source = createPlantationEntity("Kebun Source", "KBN-411");
        source.setMandorId(MANDOR_ID);
        source.setMandorName("Budi Santoso");
        source = plantationRepository.save(source);

        mockMvc.perform(delete("/api/v1/plantations/{plantationId}/mandor", source.getId())
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reassignBody(UUID.randomUUID())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].code").value("PLANTATION_NOT_FOUND"));
    }

    @Test
    void unassignMandorTargetAlreadyHasMandor() throws Exception {
        Plantation source = createPlantationEntity("Kebun Source", "KBN-412");
        source.setMandorId(MANDOR_ID);
        source.setMandorName("Budi Santoso");
        source = plantationRepository.save(source);

        Plantation target = createPlantationEntity("Kebun Target", "KBN-413");
        target.setMandorId(UUID.randomUUID());
        target.setMandorName("Other Mandor");
        target = plantationRepository.save(target);

        mockMvc.perform(delete("/api/v1/plantations/{plantationId}/mandor", source.getId())
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reassignBody(target.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].code").value("MANDOR_ALREADY_ASSIGNED"));
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

    private String reassignBody(UUID targetId) {
        return "{\"reassignToPlantationId\":\"" + targetId + "\"}";
    }
}
