package com.example.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionItemStatusTest {

    @Test
    void hasThreeValues() {
        assertEquals(3, ActionItemStatus.values().length);
    }

    @Test
    void todoDisplayName() {
        assertEquals("未着手", ActionItemStatus.TODO.getDisplayName());
    }

    @Test
    void inProgressDisplayName() {
        assertEquals("進行中", ActionItemStatus.IN_PROGRESS.getDisplayName());
    }

    @Test
    void doneDisplayName() {
        assertEquals("完了", ActionItemStatus.DONE.getDisplayName());
    }

    @Test
    void valueOfReturnsCorrectEnum() {
        assertEquals(ActionItemStatus.TODO, ActionItemStatus.valueOf("TODO"));
        assertEquals(ActionItemStatus.IN_PROGRESS, ActionItemStatus.valueOf("IN_PROGRESS"));
        assertEquals(ActionItemStatus.DONE, ActionItemStatus.valueOf("DONE"));
    }
}
