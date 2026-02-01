package com.example.repository;

import com.example.entity.Card;
import com.example.entity.CardType;
import com.example.entity.Retrospective;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
class CardRepositoryTest {

    @Inject
    CardRepository cardRepository;

    @Inject
    RetrospectiveRepository retrospectiveRepository;

    private Retrospective retro;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        retrospectiveRepository.deleteAll();

        retro = new Retrospective("Sprint 1", LocalDate.of(2025, 1, 15));
        retrospectiveRepository.persist(retro);
    }

    private Card createCard(CardType type, String content, int votes, Retrospective parent) {
        Card card = new Card(type, content);
        card.votes = votes;
        card.retrospective = parent;
        cardRepository.persist(card);
        return card;
    }

    @Test
    void findByRetrospectiveId() {
        createCard(CardType.KEEP, "Good teamwork", 0, retro);
        createCard(CardType.PROBLEM, "Slow reviews", 0, retro);

        Retrospective other = new Retrospective("Sprint 2", LocalDate.of(2025, 2, 1));
        retrospectiveRepository.persist(other);
        createCard(CardType.TRY, "Pair programming", 0, other);

        List<Card> cards = cardRepository.findByRetrospectiveId(retro.id);
        assertEquals(2, cards.size());
    }

    @Test
    void findByRetrospectiveIdReturnsEmptyForNonexistent() {
        List<Card> cards = cardRepository.findByRetrospectiveId(99999L);
        assertTrue(cards.isEmpty());
    }

    @Test
    void findByType() {
        createCard(CardType.KEEP, "Keep 1", 0, retro);
        createCard(CardType.KEEP, "Keep 2", 0, retro);
        createCard(CardType.PROBLEM, "Problem 1", 0, retro);

        List<Card> keeps = cardRepository.findByType(CardType.KEEP);
        assertEquals(2, keeps.size());

        List<Card> problems = cardRepository.findByType(CardType.PROBLEM);
        assertEquals(1, problems.size());
    }

    @Test
    void findByRetrospectiveIdAndType() {
        createCard(CardType.KEEP, "Keep 1", 0, retro);
        createCard(CardType.PROBLEM, "Problem 1", 0, retro);

        Retrospective other = new Retrospective("Sprint 2", LocalDate.of(2025, 2, 1));
        retrospectiveRepository.persist(other);
        createCard(CardType.KEEP, "Keep other", 0, other);

        List<Card> result = cardRepository.findByRetrospectiveIdAndType(retro.id, CardType.KEEP);
        assertEquals(1, result.size());
        assertEquals("Keep 1", result.get(0).content);
    }

    @Test
    void findTopVotedReturnsOrderedByVotesDesc() {
        createCard(CardType.KEEP, "Low", 1, retro);
        createCard(CardType.KEEP, "High", 10, retro);
        createCard(CardType.PROBLEM, "Mid", 5, retro);

        List<Card> topVoted = cardRepository.findTopVoted(retro.id, 10);
        assertEquals(3, topVoted.size());
        assertEquals("High", topVoted.get(0).content);
        assertEquals("Mid", topVoted.get(1).content);
        assertEquals("Low", topVoted.get(2).content);
    }

    @Test
    void findTopVotedRespectsLimit() {
        createCard(CardType.KEEP, "A", 3, retro);
        createCard(CardType.KEEP, "B", 2, retro);
        createCard(CardType.KEEP, "C", 1, retro);

        List<Card> topVoted = cardRepository.findTopVoted(retro.id, 2);
        assertEquals(2, topVoted.size());
    }

    @Test
    void deleteById() {
        Card card = createCard(CardType.KEEP, "To delete", 0, retro);

        assertTrue(cardRepository.deleteById(card.id));
        assertNull(cardRepository.findById(card.id));
    }
}
