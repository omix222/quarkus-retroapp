package com.example.resource;

import com.example.entity.Retrospective;
import com.example.repository.RetrospectiveRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class RetrospectiveResourceTest {

    @Inject
    RetrospectiveRepository repository;

    @BeforeEach
    @Transactional
    void setUp() {
        repository.deleteAll();
    }

    @Transactional
    Retrospective createRetro(String title, LocalDate date) {
        Retrospective retro = new Retrospective(title, date);
        repository.persist(retro);
        return retro;
    }

    @Test
    void getAllReturnsEmptyListWhenNoData() {
        given()
            .when().get("/api/retrospectives")
            .then()
            .statusCode(200)
            .body("$.size()", is(0));
    }

    @Test
    void getAllReturnsAllRetrospectives() {
        createRetro("Sprint 1", LocalDate.of(2025, 1, 1));
        createRetro("Sprint 2", LocalDate.of(2025, 2, 1));

        given()
            .when().get("/api/retrospectives")
            .then()
            .statusCode(200)
            .body("$.size()", is(2));
    }

    @Test
    void getByIdReturnsRetrospective() {
        Retrospective retro = createRetro("Sprint 1", LocalDate.of(2025, 1, 15));

        given()
            .when().get("/api/retrospectives/" + retro.id)
            .then()
            .statusCode(200)
            .body("title", equalTo("Sprint 1"))
            .body("date", equalTo("2025-01-15"));
    }

    @Test
    void getByIdReturns404WhenNotFound() {
        given()
            .when().get("/api/retrospectives/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void getRecentReturnsOrderedByDateDesc() {
        createRetro("Old", LocalDate.of(2025, 1, 1));
        createRetro("New", LocalDate.of(2025, 6, 1));

        given()
            .queryParam("limit", 10)
            .when().get("/api/retrospectives/recent")
            .then()
            .statusCode(200)
            .body("$.size()", is(2))
            .body("[0].title", equalTo("New"))
            .body("[1].title", equalTo("Old"));
    }

    @Test
    void createReturns201() {
        given()
            .contentType("application/json")
            .body("{\"title\":\"New Sprint\",\"date\":\"2025-05-01\"}")
            .when().post("/api/retrospectives")
            .then()
            .statusCode(201)
            .body("title", equalTo("New Sprint"))
            .body("id", notNullValue());
    }

    @Test
    void updateReturns200() {
        Retrospective retro = createRetro("Original", LocalDate.of(2025, 1, 1));

        given()
            .contentType("application/json")
            .body("{\"title\":\"Updated\",\"date\":\"2025-06-01\",\"description\":\"Updated desc\"}")
            .when().put("/api/retrospectives/" + retro.id)
            .then()
            .statusCode(200)
            .body("title", equalTo("Updated"))
            .body("date", equalTo("2025-06-01"))
            .body("description", equalTo("Updated desc"));
    }

    @Test
    void updateReturns404WhenNotFound() {
        given()
            .contentType("application/json")
            .body("{\"title\":\"Updated\",\"date\":\"2025-06-01\"}")
            .when().put("/api/retrospectives/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void deleteReturns204() {
        Retrospective retro = createRetro("To Delete", LocalDate.of(2025, 1, 1));

        given()
            .when().delete("/api/retrospectives/" + retro.id)
            .then()
            .statusCode(204);

        given()
            .when().get("/api/retrospectives/" + retro.id)
            .then()
            .statusCode(404);
    }

    @Test
    void deleteReturns404WhenNotFound() {
        given()
            .when().delete("/api/retrospectives/99999")
            .then()
            .statusCode(404);
    }
}
