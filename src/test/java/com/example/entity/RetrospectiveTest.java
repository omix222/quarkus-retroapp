package com.example.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RetrospectiveTest {

    @Test
    void defaultConstructorInitializesEmptyCollections() {
        Retrospective retro = new Retrospective();
        assertNotNull(retro.cards);
        assertNotNull(retro.actionItems);
        assertTrue(retro.cards.isEmpty());
        assertTrue(retro.actionItems.isEmpty());
    }

    @Test
    void parameterizedConstructorSetsTitleAndDate() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        Retrospective retro = new Retrospective("Sprint 1", date);

        assertEquals("Sprint 1", retro.title);
        assertEquals(date, retro.date);
        assertNull(retro.description);
    }

    @Test
    void addCardEstablishesBidirectionalRelationship() {
        Retrospective retro = new Retrospective("Sprint 1", LocalDate.now());
        Card card = new Card(CardType.KEEP, "Good teamwork");

        retro.addCard(card);

        assertEquals(1, retro.cards.size());
        assertSame(card, retro.cards.get(0));
        assertSame(retro, card.retrospective);
    }

    @Test
    void addMultipleCards() {
        Retrospective retro = new Retrospective("Sprint 1", LocalDate.now());
        Card card1 = new Card(CardType.KEEP, "Good teamwork");
        Card card2 = new Card(CardType.PROBLEM, "Slow reviews");

        retro.addCard(card1);
        retro.addCard(card2);

        assertEquals(2, retro.cards.size());
    }

    @Test
    void addActionItemEstablishesBidirectionalRelationship() {
        Retrospective retro = new Retrospective("Sprint 1", LocalDate.now());
        ActionItem item = new ActionItem("Improve CI", "Alice");

        retro.addActionItem(item);

        assertEquals(1, retro.actionItems.size());
        assertSame(item, retro.actionItems.get(0));
        assertSame(retro, item.retrospective);
    }

    @Test
    void addMultipleActionItems() {
        Retrospective retro = new Retrospective("Sprint 1", LocalDate.now());
        retro.addActionItem(new ActionItem("Task 1", "Alice"));
        retro.addActionItem(new ActionItem("Task 2", "Bob"));

        assertEquals(2, retro.actionItems.size());
    }
}
