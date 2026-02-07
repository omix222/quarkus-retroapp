package com.example.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ActionItemTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

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
        assertNull(item.jiraTicket);
        assertNull(item.retrospective);
    }

    @Test
    void threeArgConstructorSetsJiraTicket() {
        ActionItem item = new ActionItem("Improve CI", "Alice", "RETRO-123");

        assertEquals("Improve CI", item.description);
        assertEquals("Alice", item.assignee);
        assertEquals("RETRO-123", item.jiraTicket);
        assertEquals(ActionItemStatus.TODO, item.status);
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

    @Test
    void validationFailsWhenDescriptionIsBlank() {
        ActionItem item = new ActionItem("", "Alice", "RETRO-1");

        Set<ConstraintViolation<ActionItem>> violations = validator.validate(item);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void validationPassesWithValidJiraTicket() {
        ActionItem item = new ActionItem("Task", "Alice", "PROJECT-123");

        Set<ConstraintViolation<ActionItem>> violations = validator.validate(item);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"RETRO-1", "PROJECT-123", "ABC-999999", "A1-1"})
    void validationPassesWithVariousValidJiraTicketFormats(String jiraTicket) {
        ActionItem item = new ActionItem("Task", "Alice", jiraTicket);

        Set<ConstraintViolation<ActionItem>> violations = validator.validate(item);

        assertTrue(violations.isEmpty(), "Expected no violations for: " + jiraTicket);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "project-123", "123-ABC", "PROJECT123", "-123", "PROJECT-", "project-1"})
    void validationFailsWithInvalidJiraTicketFormats(String jiraTicket) {
        ActionItem item = new ActionItem("Task", "Alice", jiraTicket);

        Set<ConstraintViolation<ActionItem>> violations = validator.validate(item);

        assertFalse(violations.isEmpty(), "Expected violations for: " + jiraTicket);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("jiraTicket")));
    }

    @Test
    void validationPassesWhenJiraTicketIsNull() {
        ActionItem item = new ActionItem("Task", "Alice");

        Set<ConstraintViolation<ActionItem>> violations = validator.validate(item);

        assertTrue(violations.isEmpty());
    }

    @Test
    void validationPassesWhenJiraTicketIsEmpty() {
        ActionItem item = new ActionItem("Task", "Alice", "");

        Set<ConstraintViolation<ActionItem>> violations = validator.validate(item);

        assertTrue(violations.isEmpty());
    }
}
