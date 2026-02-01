package com.example.repository;

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
class RetrospectiveRepositoryTest {

    @Inject
    RetrospectiveRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void persistAndFindById() {
        Retrospective retro = new Retrospective("Sprint 1", LocalDate.of(2025, 1, 10));
        retro.description = "First sprint retrospective";
        repository.persist(retro);

        assertNotNull(retro.id);

        Retrospective found = repository.findById(retro.id);
        assertNotNull(found);
        assertEquals("Sprint 1", found.title);
        assertEquals(LocalDate.of(2025, 1, 10), found.date);
        assertEquals("First sprint retrospective", found.description);
    }

    @Test
    void findByIdReturnsNullForNonexistent() {
        assertNull(repository.findById(99999L));
    }

    @Test
    void listAll() {
        repository.persist(new Retrospective("Sprint 1", LocalDate.of(2025, 1, 1)));
        repository.persist(new Retrospective("Sprint 2", LocalDate.of(2025, 1, 15)));

        List<Retrospective> all = repository.listAll();
        assertEquals(2, all.size());
    }

    @Test
    void findByDate() {
        LocalDate targetDate = LocalDate.of(2025, 3, 1);
        repository.persist(new Retrospective("Sprint 1", targetDate));
        repository.persist(new Retrospective("Sprint 2", LocalDate.of(2025, 4, 1)));

        List<Retrospective> results = repository.findByDate(targetDate);
        assertEquals(1, results.size());
        assertEquals("Sprint 1", results.get(0).title);
    }

    @Test
    void findByDateReturnsEmptyWhenNoMatch() {
        repository.persist(new Retrospective("Sprint 1", LocalDate.of(2025, 1, 1)));

        List<Retrospective> results = repository.findByDate(LocalDate.of(2099, 12, 31));
        assertTrue(results.isEmpty());
    }

    @Test
    void findByTitleLikeCaseInsensitive() {
        repository.persist(new Retrospective("Sprint 1 Review", LocalDate.of(2025, 1, 1)));
        repository.persist(new Retrospective("sprint 2 review", LocalDate.of(2025, 2, 1)));
        repository.persist(new Retrospective("Release Retro", LocalDate.of(2025, 3, 1)));

        List<Retrospective> results = repository.findByTitleLike("sprint");
        assertEquals(2, results.size());
    }

    @Test
    void findByTitleLikePartialMatch() {
        repository.persist(new Retrospective("Sprint 1", LocalDate.of(2025, 1, 1)));
        repository.persist(new Retrospective("My Sprint Review", LocalDate.of(2025, 2, 1)));

        List<Retrospective> results = repository.findByTitleLike("Sprint");
        assertEquals(2, results.size());
    }

    @Test
    void findRecentReturnsOrderedByDateDesc() {
        repository.persist(new Retrospective("Old", LocalDate.of(2025, 1, 1)));
        repository.persist(new Retrospective("New", LocalDate.of(2025, 6, 1)));
        repository.persist(new Retrospective("Mid", LocalDate.of(2025, 3, 1)));

        List<Retrospective> results = repository.findRecent(10);
        assertEquals(3, results.size());
        assertEquals("New", results.get(0).title);
        assertEquals("Mid", results.get(1).title);
        assertEquals("Old", results.get(2).title);
    }

    @Test
    void findRecentRespectsLimit() {
        repository.persist(new Retrospective("A", LocalDate.of(2025, 1, 1)));
        repository.persist(new Retrospective("B", LocalDate.of(2025, 2, 1)));
        repository.persist(new Retrospective("C", LocalDate.of(2025, 3, 1)));

        List<Retrospective> results = repository.findRecent(2);
        assertEquals(2, results.size());
    }

    @Test
    void deleteById() {
        Retrospective retro = new Retrospective("To Delete", LocalDate.now());
        repository.persist(retro);

        assertTrue(repository.deleteById(retro.id));
        assertNull(repository.findById(retro.id));
    }
}
