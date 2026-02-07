# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Quarkus-based scrum retrospective (ふりかえり) web application using KPT (Keep/Problem/Try) format. Java 17, Quarkus 3.28.3, H2 in-memory database, Hibernate ORM with Panache, vanilla JavaScript frontend.

## Commands

```bash
# Development server with hot reload
./mvnw quarkus:dev

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=com.example.resource.RetrospectiveResourceTest

# Run integration tests
./mvnw verify

# Production build
./mvnw clean package

# Run production JAR
java -jar target/quarkus-app/quarkus-run.jar

# Native build
./mvnw package -Dnative
```

App runs at http://localhost:8080, Swagger UI at http://localhost:8080/swagger-ui/

## Architecture

Three-layer architecture: Entity → Repository → Resource (REST controller). No DTOs — entities are serialized directly to JSON.

**Entities** (`com.example.entity`):
- `Retrospective` — parent entity with `@OneToMany` cascade to `Card` and `ActionItem` (orphan removal enabled)
- `Card` — KPT card with `CardType` enum (KEEP, PROBLEM, TRY), vote count, belongs to Retrospective
- `ActionItem` — task with `ActionItemStatus` enum (TODO, IN_PROGRESS, DONE), assignee, belongs to Retrospective

**Repositories** (`com.example.repository`): Panache `PanacheRepository` implementations with custom query methods.

**Resources** (`com.example.resource`): REST endpoints under `/api/`. Write operations use `@Transactional`. Resources inject repositories directly.
- `RetrospectiveResource` → `/api/retrospectives`
- `CardResource` → `/api/cards` (nested operations use `/api/cards/retrospectives/{id}`)
- `ActionItemResource` → `/api/action-items` (nested operations use `/api/action-items/retrospectives/{id}`)

**Frontend** (`src/main/resources/META-INF/resources/`): Single-page app with `index.html` and `app.js`. Communicates with backend via Fetch API. Two views: list-view (all retrospectives) and detail-view (KPT columns + action items).

## Database

H2 file-based database stored in `./data/retrospective`. Schema generation is `update` (persists across restarts). `import.sql` exists but is not auto-loaded (`no-file`). Entities use public fields (Panache convention) rather than getters/setters.

## Testing

Uses `quarkus-junit5` and REST Assured. Test classes go in `src/test/java/com/example/`.

```bash
# Run all tests
./mvnw test

# Run mutation testing (PITest) - targets entity classes only
./mvnw test-compile org.pitest:pitest-maven:mutationCoverage

# View mutation report
open target/pit-reports/index.html
```

**Mutation Testing**: PITest is configured for entity classes (`com.example.entity.*`). Repository and Resource tests use `@QuarkusTest` which is incompatible with PITest's classloader, so they are excluded from mutation testing.
