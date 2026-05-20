# Implementation Roadmap

## Phase 1: Documentation

- Define modular architecture.
- Define ER model and JPA ownership.
- Define REST API design.
- Define JWT and authorization rules.
- Define module-level business rules.

## Phase 2: Shared Foundation

- Add common business exceptions.
- Keep global validation error responses consistent.
- Keep endpoints secured by the existing JWT filter.
- Use `ResponseEntity` on new controllers.
- Add Flyway migration for existing core tables and new RPG modules.

## Phase 3: Pomodoro Core

- Implement `PomodoroType`.
- Implement `Pomodoro`.
- Implement `RepetitionSettings`.
- Implement DTOs, mapper, repository, service, controller.

## Phase 4: Session Execution

- Implement `PomodoroSession` and session status.
- Add lifecycle endpoints: start, complete, cancel.
- Award XP on completed focus sessions.
- Optionally complete linked task progress in future iterations.

## Phase 5: Progression

- Implement `PlayerProgress`.
- Provide profile retrieval and XP mutation through services.
- Calculate level from XP thresholds.

## Phase 6: Achievements

- Implement user-owned achievement unlock records.
- Support listing and manual unlock for current portfolio scope.
- Keep automatic unlock hooks in progression/session services extensible.

## Phase 7: Waifu System

- Implement `Waifu` catalog.
- Implement `WaifuSkinUnlock` ownership records.
- Support catalog listing, waifu creation, unlock listing, and skin unlock.

## Technical Checklist

- No entities exposed directly.
- Use `@Valid` request validation.
- Use UUID primary keys.
- Use `createdAt` and `updatedAt` timestamps.
- Use `@Transactional` on critical writes.
- Enforce ownership from services.
- Compile with Maven.
- Keep docs synchronized with implementation.
