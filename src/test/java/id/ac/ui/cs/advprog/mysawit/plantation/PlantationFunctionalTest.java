package id.ac.ui.cs.advprog.mysawit.plantation;

import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationDriverAssignmentRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.repository.PlantationRepository;
import id.ac.ui.cs.advprog.mysawit.plantation.security.JwtTestHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PlantationFunctionalTest {

    private static final UUID MANDOR_ID =
        UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SUPIR_ID =
        UUID.fromString("22222222-2222-2222-2222-222222222222");

    @LocalServerPort
    private int port;

    @Autowired
    private PlantationRepository plantationRepository;

    @Autowired
    private PlantationDriverAssignmentRepository driverAssignmentRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        driverAssignmentRepository.deleteAll();
        plantationRepository.deleteAll();
    }

    private String adminHeader() {
        return JwtTestHelper.adminBearer();
    }

    private Map<String, Object> createBody(String name, String code) {
        return Map.of(
            "name", name,
            "code", code,
            "area", 150.0,
            "coordinates", List.of(
                List.of(0, 0),
                List.of(100, 0),
                List.of(100, 100),
                List.of(0, 100)
            )
        );
    }

    private String createPlantation() {
        return given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminHeader())
            .body(createBody("Kebun Functional Test", "KBN-FT-001"))
        .when()
            .post("/api/v1/plantations")
        .then()
            .statusCode(201)
            .extract().path("data.id");
    }

    @Test
    void create_withAdminToken_returns201() {
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminHeader())
            .body(createBody("Kebun Functional Test", "KBN-FT-001"))
        .when()
            .post("/api/v1/plantations")
        .then()
            .statusCode(201)
            .body("data.id", notNullValue())
            .body("data.name", equalTo("Kebun Functional Test"));
    }

    @Test
    void create_withoutToken_returns401() {
        given()
            .contentType(ContentType.JSON)
            .body(createBody("Kebun Test", "KBN-NO-TOKEN"))
        .when()
            .post("/api/v1/plantations")
        .then()
            .statusCode(401);
    }

    @Test
    void create_withNonAdminToken_returns403() {
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", JwtTestHelper.userBearer("MANDOR"))
            .body(createBody("Kebun Test", "KBN-MANDOR"))
        .when()
            .post("/api/v1/plantations")
        .then()
            .statusCode(403);
    }

    @Test
    void create_duplicateCode_returns409() {
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminHeader())
            .body(createBody("Kebun Pertama", "KBN-DUP"))
        .when()
            .post("/api/v1/plantations")
        .then()
            .statusCode(201);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminHeader())
            .body(createBody("Kebun Kedua", "KBN-DUP"))
        .when()
            .post("/api/v1/plantations")
        .then()
            .statusCode(409);
    }

    @Test
    void list_returnsExistingPlantations() {
        createPlantation();
        given()
            .header("Authorization", adminHeader())
        .when()
            .get("/api/v1/plantations")
        .then()
            .statusCode(200)
            .body("data", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    void list_withNameFilter_returnsFiltered() {
        createPlantation();
        given()
            .header("Authorization", adminHeader())
            .queryParam("name", "Kebun Functional")
        .when()
            .get("/api/v1/plantations")
        .then()
            .statusCode(200)
            .body("data", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    void getDetail_existingPlantation_returns200() {
        String id = createPlantation();
        given()
            .header("Authorization", adminHeader())
        .when()
            .get("/api/v1/plantations/" + id)
        .then()
            .statusCode(200)
            .body("data.drivers", notNullValue());
    }

    @Test
    void getDetail_notFound_returns404() {
        given()
            .header("Authorization", adminHeader())
        .when()
            .get("/api/v1/plantations/" + UUID.randomUUID())
        .then()
            .statusCode(404);
    }

    @Test
    void update_validRequest_returns200() {
        String id = createPlantation();
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminHeader())
            .body(Map.of("name", "Updated Name"))
        .when()
            .put("/api/v1/plantations/" + id)
        .then()
            .statusCode(200)
            .body("data.name", equalTo("Updated Name"));
    }

    @Test
    void assignMandor_returns200() {
        String id = createPlantation();
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminHeader())
            .body(Map.of("mandorId", MANDOR_ID.toString()))
        .when()
            .post("/api/v1/plantations/" + id + "/mandor")
        .then()
            .statusCode(200)
            .body("data.mandor.id", notNullValue());
    }

    @Test
    void unassignMandor_returns200() {
        String id = createPlantation();
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminHeader())
            .body(Map.of("mandorId", MANDOR_ID.toString()))
        .when()
            .post("/api/v1/plantations/" + id + "/mandor")
        .then()
            .statusCode(200);

        given()
            .header("Authorization", adminHeader())
        .when()
            .delete("/api/v1/plantations/" + id + "/mandor")
        .then()
            .statusCode(200);
    }

    @Test
    void assignDriver_returns200() {
        String id = createPlantation();
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminHeader())
            .body(Map.of("driverId", SUPIR_ID.toString()))
        .when()
            .post("/api/v1/plantations/" + id + "/drivers")
        .then()
            .statusCode(200)
            .body("data.driver.id", notNullValue());
    }

    @Test
    void unassignDriver_returns200() {
        String id = createPlantation();
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", adminHeader())
            .body(Map.of("driverId", SUPIR_ID.toString()))
        .when()
            .post("/api/v1/plantations/" + id + "/drivers")
        .then()
            .statusCode(200);

        given()
            .header("Authorization", adminHeader())
        .when()
            .delete("/api/v1/plantations/" + id + "/drivers/" + SUPIR_ID)
        .then()
            .statusCode(200);
    }
}
