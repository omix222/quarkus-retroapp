package com.example.repository;

import com.example.entity.ActionItem;
import com.example.entity.ActionItemStatus;
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
class ActionItemRepositoryTest {

    @Inject
    ActionItemRepository actionItemRepository;

    @Inject
    RetrospectiveRepository retrospectiveRepository;

    private Retrospective retro;

    @BeforeEach
    void setUp() {
        actionItemRepository.deleteAll();
        retrospectiveRepository.deleteAll();

        retro = new Retrospective("Sprint 1", LocalDate.of(2025, 1, 15));
        retrospectiveRepository.persist(retro);
    }

    private ActionItem createItem(String description, String assignee, ActionItemStatus status, Retrospective parent) {
        ActionItem item = new ActionItem(description, assignee);
        item.status = status;
        item.retrospective = parent;
        actionItemRepository.persist(item);
        return item;
    }

    @Test
    void findByRetrospectiveId() {
        createItem("Task 1", "Alice", ActionItemStatus.TODO, retro);
        createItem("Task 2", "Bob", ActionItemStatus.IN_PROGRESS, retro);

        Retrospective other = new Retrospective("Sprint 2", LocalDate.of(2025, 2, 1));
        retrospectiveRepository.persist(other);
        createItem("Task 3", "Charlie", ActionItemStatus.DONE, other);

        List<ActionItem> items = actionItemRepository.findByRetrospectiveId(retro.id);
        assertEquals(2, items.size());
    }

    @Test
    void findByRetrospectiveIdReturnsEmptyForNonexistent() {
        List<ActionItem> items = actionItemRepository.findByRetrospectiveId(99999L);
        assertTrue(items.isEmpty());
    }

    @Test
    void findByStatus() {
        createItem("Task 1", "Alice", ActionItemStatus.TODO, retro);
        createItem("Task 2", "Bob", ActionItemStatus.TODO, retro);
        createItem("Task 3", "Charlie", ActionItemStatus.DONE, retro);

        List<ActionItem> todoItems = actionItemRepository.findByStatus(ActionItemStatus.TODO);
        assertEquals(2, todoItems.size());

        List<ActionItem> doneItems = actionItemRepository.findByStatus(ActionItemStatus.DONE);
        assertEquals(1, doneItems.size());
    }

    @Test
    void findByAssignee() {
        createItem("Task 1", "Alice", ActionItemStatus.TODO, retro);
        createItem("Task 2", "Alice", ActionItemStatus.DONE, retro);
        createItem("Task 3", "Bob", ActionItemStatus.TODO, retro);

        List<ActionItem> aliceItems = actionItemRepository.findByAssignee("Alice");
        assertEquals(2, aliceItems.size());

        List<ActionItem> bobItems = actionItemRepository.findByAssignee("Bob");
        assertEquals(1, bobItems.size());
    }

    @Test
    void findByAssigneeReturnsEmptyForUnknown() {
        createItem("Task 1", "Alice", ActionItemStatus.TODO, retro);

        List<ActionItem> items = actionItemRepository.findByAssignee("Unknown");
        assertTrue(items.isEmpty());
    }

    @Test
    void findByRetrospectiveIdAndStatus() {
        createItem("Task 1", "Alice", ActionItemStatus.TODO, retro);
        createItem("Task 2", "Bob", ActionItemStatus.IN_PROGRESS, retro);
        createItem("Task 3", "Charlie", ActionItemStatus.TODO, retro);

        Retrospective other = new Retrospective("Sprint 2", LocalDate.of(2025, 2, 1));
        retrospectiveRepository.persist(other);
        createItem("Task 4", "Dave", ActionItemStatus.TODO, other);

        List<ActionItem> result = actionItemRepository.findByRetrospectiveIdAndStatus(retro.id, ActionItemStatus.TODO);
        assertEquals(2, result.size());
    }

    @Test
    void deleteById() {
        ActionItem item = createItem("To delete", "Alice", ActionItemStatus.TODO, retro);

        assertTrue(actionItemRepository.deleteById(item.id));
        assertNull(actionItemRepository.findById(item.id));
    }
}
