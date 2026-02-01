package com.example.resource;

import com.example.entity.Card;
import com.example.entity.CardType;
import com.example.entity.Retrospective;
import com.example.repository.CardRepository;
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
class CardResourceTest {

    @Inject
    CardRepository cardRepository;

    @Inject
    RetrospectiveRepository retrospectiveRepository;

    private Long retroId;

    @BeforeEach
    @Transactional
    void setUp() {
        cardRepository.deleteAll();
        retrospectiveRepository.deleteAll();

        Retrospective retro = new Retrospective("Sprint 1", LocalDate.of(2025, 1, 15));
        retrospectiveRepository.persist(retro);
        retroId = retro.id;
    }

    @Transactional
    Card createCard(CardType type, String content, int votes) {
        Card card = new Card(type, content);
        card.votes = votes;
        card.retrospective = retrospectiveRepository.findById(retroId);
        cardRepository.persist(card);
        return card;
    }

    @Test
    void getCardsByRetrospective() {
        createCard(CardType.KEEP, "Good teamwork", 0);
        createCard(CardType.PROBLEM, "Slow reviews", 0);

        given()
            .when().get("/api/cards/retrospectives/" + retroId)
            .then()
            .statusCode(200)
            .body("$.size()", is(2));
    }

    @Test
    void getCardsByRetrospectiveReturnsEmptyForNonexistent() {
        given()
            .when().get("/api/cards/retrospectives/99999")
            .then()
            .statusCode(200)
            .body("$.size()", is(0));
    }

    @Test
    void getCardsByType() {
        createCard(CardType.KEEP, "Keep 1", 0);
        createCard(CardType.KEEP, "Keep 2", 0);
        createCard(CardType.PROBLEM, "Problem 1", 0);

        given()
            .when().get("/api/cards/retrospectives/" + retroId + "/type/KEEP")
            .then()
            .statusCode(200)
            .body("$.size()", is(2));
    }

    @Test
    void getTopVotedCards() {
        createCard(CardType.KEEP, "Low", 1);
        createCard(CardType.KEEP, "High", 10);
        createCard(CardType.PROBLEM, "Mid", 5);

        given()
            .queryParam("limit", 2)
            .when().get("/api/cards/retrospectives/" + retroId + "/top-voted")
            .then()
            .statusCode(200)
            .body("$.size()", is(2))
            .body("[0].content", equalTo("High"))
            .body("[1].content", equalTo("Mid"));
    }

    @Test
    void createCardReturns201() {
        given()
            .contentType("application/json")
            .body("{\"type\":\"KEEP\",\"content\":\"New card\"}")
            .when().post("/api/cards/retrospectives/" + retroId)
            .then()
            .statusCode(201)
            .body("content", equalTo("New card"))
            .body("type", equalTo("KEEP"))
            .body("votes", equalTo(0))
            .body("id", notNullValue());
    }

    @Test
    void createCardReturns404WhenRetrospectiveNotFound() {
        given()
            .contentType("application/json")
            .body("{\"type\":\"KEEP\",\"content\":\"New card\"}")
            .when().post("/api/cards/retrospectives/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void updateCard() {
        Card card = createCard(CardType.KEEP, "Original", 0);

        given()
            .contentType("application/json")
            .body("{\"type\":\"PROBLEM\",\"content\":\"Updated\"}")
            .when().put("/api/cards/" + card.id)
            .then()
            .statusCode(200)
            .body("type", equalTo("PROBLEM"))
            .body("content", equalTo("Updated"));
    }

    @Test
    void updateCardReturns404WhenNotFound() {
        given()
            .contentType("application/json")
            .body("{\"type\":\"KEEP\",\"content\":\"Updated\"}")
            .when().put("/api/cards/99999")
            .then()
            .statusCode(404);
    }

    @Test
    void voteCard() {
        Card card = createCard(CardType.KEEP, "Votable", 0);

        given()
            .contentType("application/json")
            .when().post("/api/cards/" + card.id + "/vote")
            .then()
            .statusCode(200)
            .body("votes", equalTo(1));
    }

    @Test
    void voteCardReturns404WhenNotFound() {
        given()
            .contentType("application/json")
            .when().post("/api/cards/99999/vote")
            .then()
            .statusCode(404);
    }

    @Test
    void unvoteCard() {
        Card card = createCard(CardType.KEEP, "Voted", 3);

        given()
            .contentType("application/json")
            .when().post("/api/cards/" + card.id + "/unvote")
            .then()
            .statusCode(200)
            .body("votes", equalTo(2));
    }

    @Test
    void unvoteCardDoesNotGoBelowZero() {
        Card card = createCard(CardType.KEEP, "No votes", 0);

        given()
            .contentType("application/json")
            .when().post("/api/cards/" + card.id + "/unvote")
            .then()
            .statusCode(200)
            .body("votes", equalTo(0));
    }

    @Test
    void deleteCard() {
        Card card = createCard(CardType.KEEP, "To delete", 0);

        given()
            .when().delete("/api/cards/" + card.id)
            .then()
            .statusCode(204);
    }

    @Test
    void deleteCardReturns404WhenNotFound() {
        given()
            .when().delete("/api/cards/99999")
            .then()
            .statusCode(404);
    }
}
