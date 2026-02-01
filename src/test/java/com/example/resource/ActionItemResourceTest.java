package com.example.resource;

import com.example.entity.ActionItem;
import com.example.entity.ActionItemStatus;
import com.example.entity.Retrospective;
import com.example.repository.ActionItemRepository;
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
class ActionItemResourceTest {

    @Inject
    ActionItemRepository actionItemRepository;

    @Inject
    RetrospectiveRepository retrospectiveRepository;

    private Long retroId;

    @BeforeEach
    @Transactional
    void setUp() {
        actionItemRepository.deleteAll();
        retrospectiveRepository.deleteAll();

        Retrospective retro = new Retrospective("Sprint 1", LocalDate.of(2025, 1, 15));
        retrospectiveRepository.persist(retro);
        retroId = retro.id;
    }

    @Transactional
    ActionItem createItem(String description, String assignee, ActionItemStatus status) {
        ActionItem item = new ActionItem(description, assignee);
        item.status = status;
        item.retrospective = retrospectiveRepository.findById(retroId);
        actionItemRepository.persist(item);
        return item;
    }

    @Test
    void getActionItemsByRetrospective() {
        createItem("Task 1", "Alice", ActionItemStatus.TODO);
        createItem("Task 2", "Bob", ActionItemStatus.IN_PROGRESS);

        given()
            .when().get("/api/action-items/retrospectives/" + retroId)
            .then()
            .statusCode(200)
            .body("$.size()", is(2));
    }

    @Test
    void getActionItemsByRetrospectiveReturnsEmptyForNonexistent() {
        given()
            .when().get("/api/action-items/retrospectives/99999")
            .then()
            .statusCode(200)
            .body("$.size()", is(0));
    }

    @Test
    void getActionItemsByStatus() {
        createItem("Task 1", "Alice", ActionItemStatus.TODO);
        createItem("Task 2", "Bob", ActionItemStatus.TODO);
        createItem("Task 3", "Charlie", ActionItemStatus.DONE);

        given()
            .when().get("/api/action-items/retrospectives/" + retroId + "/status/TODO")
            .then()
            .statusCode(200)
            .body("$.size()", is(2));
    }

    @Test
    void getActionItemsByAssignee() {
        createItem("Task 1", "Alice", ActionItemStatus.TODO);
        createItem("Task 2", "Alice", ActionItemStatus.DONE);
        createItem("Task 3", "Bob", ActionItemStatus.TODO);

        given()
            .when().get("/api/action-items/assignee/Alice")
            .then()
            .statusCode(200)
            .body("$.size()", is(2));
    }

    @Test
    void createActionItemReturns201() {
        given()
            .contentType("application/json")
            .body("{\"description\":\"New task\",\"assignee\":\"Alice\"}")
            .when().post("/api/action-items/retrospectives/" + retroId)
            .then()
            .statusCode(201)
            .body("description", equalTo("New task"))
            .body("assignee", equalTo("Alice"))
            .body("status", equalTo("TODO"))
            .body("id", notNullValue());
    }

    @Test
    void createActionItemReturns404WhenRetrospectiveNotFound() {
        given()
            .contentType("application/json")
            .body("{\"description\":\"New task\",\"assignee\":\"Alice\"}")
            .when().post("/api/action-items/retrospectives/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void updateActionItem() {
        ActionItem item = createItem("Original", "Alice", ActionItemStatus.TODO);

        given()
            .contentType("application/json")
            .body("{\"description\":\"Updated\",\"assignee\":\"Bob\",\"status\":\"IN_PROGRESS\"}")
            .when().put("/api/action-items/" + item.id)
            .then()
            .statusCode(200)
            .body("description", equalTo("Updated"))
            .body("assignee", equalTo("Bob"))
            .body("status", equalTo("IN_PROGRESS"));
    }

    @Test
    void updateActionItemReturns404WhenNotFound() {
        given()
            .contentType("application/json")
            .body("{\"description\":\"Updated\",\"assignee\":\"Bob\",\"status\":\"TODO\"}")
            .when().put("/api/action-items/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void patchStatus() {
        ActionItem item = createItem("Task", "Alice", ActionItemStatus.TODO);

        given()
            .contentType("application/json")
            .queryParam("status", "DONE")
            .when().patch("/api/action-items/" + item.id + "/status")
            .then()
            .statusCode(200)
            .body("status", equalTo("DONE"));
    }

    @Test
    void patchStatusReturns404WhenNotFound() {
        given()
            .contentType("application/json")
            .queryParam("status", "DONE")
            .when().patch("/api/action-items/99999/status")
            .then()
            .statusCode(404);
    }

    @Test
    void deleteActionItem() {
        ActionItem item = createItem("To delete", "Alice", ActionItemStatus.TODO);

        given()
            .when().delete("/api/action-items/" + item.id)
            .then()
            .statusCode(204);
    }

    @Test
    void deleteActionItemReturns404WhenNotFound() {
        given()
            .when().delete("/api/action-items/99999")
            .then()
            .statusCode(404);
    }
}
