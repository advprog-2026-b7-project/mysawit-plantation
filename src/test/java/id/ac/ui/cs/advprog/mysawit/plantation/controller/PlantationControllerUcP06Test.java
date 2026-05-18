package id.ac.ui.cs.advprog.mysawit.plantation.controller;

import id.ac.ui.cs.advprog.mysawit.plantation.entity.Plantation;
import id.ac.ui.cs.advprog.mysawit.plantation.entity.PlantationDriverAssignment;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationDriverAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.security.JwtTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlantationControllerUcP06Test {

    private static final UUID MANDOR_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID DRIVER_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID OTHER_DRIVER_ID =
            UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Autowired private MockMvc mockMvc;
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
        p.setMandorId(MANDOR_ID);
        p.setMandorName("Budi Santoso");
        p.setMandorEmail("budi@mysawit.id");
        p.setMandorCertificationNumber("CERT-2024-001");
        saved = plantationRepository.save(p);

        driverAssignmentRepository.save(makeDriver(DRIVER_ID, "Agus Triyanto", "agus@sawit.id"));
        driverAssignmentRepository.save(makeDriver(OTHER_DRIVER_ID, "Rina Supir", "rina@sawit.id"));
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
                .andExpect(jsonPath("$.data.mandor.name").value("Budi Santoso"))
                .andExpect(jsonPath("$.data.mandor.email").value("budi@mysawit.id"))
                .andExpect(jsonPath("$.data.mandor.certificationNumber")
                        .value("CERT-2024-001"))
                .andExpect(jsonPath("$.data.drivers.content[0].name")
                        .value("Agus Triyanto"))
                .andExpect(jsonPath("$.data.drivers.content[0].email")
                        .value("agus@sawit.id"))
                .andExpect(jsonPath("$.data.drivers.page").value(0))
                .andExpect(jsonPath("$.data.drivers.size").value(20))
                .andExpect(jsonPath("$.data.drivers.totalElements").value(2))
                .andExpect(jsonPath("$.data.drivers.totalPages").value(1));
    }

    @Test
    void getPlantationDetailFiltersDriverName() throws Exception {
        mockMvc.perform(get("/api/v1/plantations/" + saved.getId())
                        .param("driverName", "rina")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.drivers.content.length()").value(1))
                .andExpect(jsonPath("$.data.drivers.content[0].name").value("Rina Supir"));
    }

    @Test
    void getPlantationDetailPaginatesDrivers() throws Exception {
        mockMvc.perform(get("/api/v1/plantations/" + saved.getId())
                        .param("page", "1")
                        .param("size", "1")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.drivers.content.length()").value(1))
                .andExpect(jsonPath("$.data.drivers.page").value(1))
                .andExpect(jsonPath("$.data.drivers.size").value(1))
                .andExpect(jsonPath("$.data.drivers.totalElements").value(2))
                .andExpect(jsonPath("$.data.drivers.totalPages").value(2));
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

    private String adminToken() {
        return JwtTestHelper.adminBearer();
    }

    private String userToken() {
        return JwtTestHelper.userBearer("BURUH");
    }

    private PlantationDriverAssignment makeDriver(UUID driverId, String name, String email) {
        PlantationDriverAssignment driver = new PlantationDriverAssignment();
        driver.setPlantationId(saved.getId());
        driver.setDriverId(driverId);
        driver.setDriverName(name);
        driver.setDriverEmail(email);
        driver.setAssignedAt(Instant.now());
        return driver;
    }
}
