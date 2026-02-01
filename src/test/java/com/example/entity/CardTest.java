package com.example.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void defaultConstructorInitializesVotesToZero() {
        Card card = new Card();
        assertEquals(0, card.votes);
    }

    @Test
    void parameterizedConstructorSetsTypeAndContent() {
        Card card = new Card(CardType.KEEP, "Good teamwork");

        assertEquals(CardType.KEEP, card.type);
        assertEquals("Good teamwork", card.content);
        assertEquals(0, card.votes);
        assertNull(card.retrospective);
    }

    @Test
    void incrementVoteIncreasesCount() {
        Card card = new Card(CardType.PROBLEM, "Slow reviews");

        card.incrementVote();
        assertEquals(1, card.votes);

        card.incrementVote();
        assertEquals(2, card.votes);
    }

    @Test
    void decrementVoteDecreasesCount() {
        Card card = new Card(CardType.TRY, "Pair programming");
        card.votes = 3;

        card.decrementVote();
        assertEquals(2, card.votes);
    }

    @Test
    void decrementVoteDoesNotGoBelowZero() {
        Card card = new Card(CardType.KEEP, "Something");
        assertEquals(0, card.votes);

        card.decrementVote();
        assertEquals(0, card.votes);
    }

    @Test
    void decrementVoteFromOneToZero() {
        Card card = new Card(CardType.KEEP, "Something");
        card.incrementVote();
        assertEquals(1, card.votes);

        card.decrementVote();
        assertEquals(0, card.votes);

        card.decrementVote();
        assertEquals(0, card.votes);
    }
}
