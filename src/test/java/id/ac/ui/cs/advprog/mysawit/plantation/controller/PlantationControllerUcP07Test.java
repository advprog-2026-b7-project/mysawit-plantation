package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.PlantationDriverAssignment;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationDriverAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.security.JwtTestHelper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlantationControllerUcP07Test {

    @Autowired private MockMvc mockMvc;
    @Autowired private PlantationRepository plantationRepository;
    @Autowired private PlantationDriverAssignmentRepository driverAssignmentRepository;

    @BeforeEach
    void setUp() {
        driverAssignmentRepository.deleteAll();
        plantationRepository.deleteAll();

        Plantation alpha =
                plantationRepository.save(makeP("Kebun Alpha", "KBN-ALPHA", 0, 0, 100, 100));
        alpha.setMandorId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        alpha.setMandorName("Budi Santoso");
        alpha.setMandorEmail("budi@mysawit.id");
        alpha.setMandorCertificationNumber("CERT-2024-001");
        plantationRepository.save(alpha);

        plantationRepository.save(makeP("Kebun Beta", "KBN-BETA", 200, 200, 300, 300));
        plantationRepository.save(makeP("Sawit Gamma", "SAW-GAMMA", 400, 400, 500, 500));

        driverAssignmentRepository.save(makeDriver(alpha.getId(), "Agus Triyanto"));
        driverAssignmentRepository.save(makeDriver(alpha.getId(), "Rina Supir"));
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
                .andExpect(jsonPath("$.data.content", hasSize(3)))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void filterByName() throws Exception {
        mockMvc.perform(get("/api/v1/plantations?name=Kebun")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    void filterByCode() throws Exception {
        mockMvc.perform(get("/api/v1/plantations?code=KBN")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    void filterByNameNoMatch() throws Exception {
        mockMvc.perform(get("/api/v1/plantations?name=Nonexistent")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    void filterByNameCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/v1/plantations?name=alpha")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].name").value("Kebun Alpha"))
                .andExpect(jsonPath("$.data.content[0].mandorName").value("Budi Santoso"))
                .andExpect(jsonPath("$.data.content[0].driverCount").value(2));
    }

    @Test
    void sortAndPaginationWork() throws Exception {
        mockMvc.perform(get("/api/v1/plantations")
                        .param("page", "1")
                        .param("size", "1")
                        .param("sort", "name,asc")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].name").value("Kebun Beta"))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(3));
    }

    @Test
    void pageLessThanZeroRejected() throws Exception {
        mockMvc.perform(get("/api/v1/plantations")
                        .param("page", "-1")
                        .header("Authorization", adminToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code").value("INVALID_QUERY_PARAM"));
    }

    @Test
    void sizeGreaterThanHundredRejected() throws Exception {
        mockMvc.perform(get("/api/v1/plantations")
                        .param("size", "101")
                        .header("Authorization", adminToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code").value("INVALID_QUERY_PARAM"));
    }

    @Test
    void getAllPlantationsForbiddenNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/plantations")
                        .header("Authorization", userToken()))
                .andExpect(status().isForbidden());
    }

    private String adminToken() {
        return JwtTestHelper.adminBearer();
    }

    private String userToken() {
        return JwtTestHelper.userBearer("BURUH");
    }

    private PlantationDriverAssignment makeDriver(UUID plantationId, String name) {
        PlantationDriverAssignment assignment = new PlantationDriverAssignment();
        assignment.setPlantationId(plantationId);
        assignment.setDriverId(UUID.randomUUID());
        assignment.setDriverName(name);
        assignment.setDriverEmail(name.toLowerCase().replace(" ", ".") + "@sawit.id");
        assignment.setAssignedAt(Instant.now());
        return assignment;
    }
}
