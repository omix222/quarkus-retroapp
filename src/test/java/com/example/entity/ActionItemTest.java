package com.example.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionItemTest {

    @Test
    void defaultConstructorSetsStatusToTodo() {
        ActionItem item = new ActionItem();
        assertEquals(ActionItemStatus.TODO, item.status);
    }

    @Test
    void parameterizedConstructorSetsFieldsCorrectly() {
        ActionItem item = new ActionItem("Improve CI", "Alice");

        assertEquals("Improve CI", item.description);
        assertEquals("Alice", item.assignee);
        assertEquals(ActionItemStatus.TODO, item.status);
        assertNull(item.retrospective);
    }

    @Test
    void updateStatusChangesStatus() {
        ActionItem item = new ActionItem("Task", "Bob");

        item.updateStatus(ActionItemStatus.IN_PROGRESS);
        assertEquals(ActionItemStatus.IN_PROGRESS, item.status);

        item.updateStatus(ActionItemStatus.DONE);
        assertEquals(ActionItemStatus.DONE, item.status);
    }

    @Test
    void updateStatusBackToTodo() {
        ActionItem item = new ActionItem("Task", "Bob");
        item.updateStatus(ActionItemStatus.DONE);

        item.updateStatus(ActionItemStatus.TODO);
        assertEquals(ActionItemStatus.TODO, item.status);
    }
}
