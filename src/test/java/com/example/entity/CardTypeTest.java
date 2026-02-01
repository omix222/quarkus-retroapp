package com.example.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTypeTest {

    @Test
    void hasThreeValues() {
        assertEquals(3, CardType.values().length);
    }

    @Test
    void keepDisplayName() {
        assertEquals("良かったこと", CardType.KEEP.getDisplayName());
    }

    @Test
    void problemDisplayName() {
        assertEquals("問題・課題", CardType.PROBLEM.getDisplayName());
    }

    @Test
    void tryDisplayName() {
        assertEquals("試したいこと", CardType.TRY.getDisplayName());
    }

    @Test
    void valueOfReturnsCorrectEnum() {
        assertEquals(CardType.KEEP, CardType.valueOf("KEEP"));
        assertEquals(CardType.PROBLEM, CardType.valueOf("PROBLEM"));
        assertEquals(CardType.TRY, CardType.valueOf("TRY"));
    }
}
