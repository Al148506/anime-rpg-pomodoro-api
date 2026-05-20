# Entity Relationship Model

## Main Tables

`users` stores application accounts and owns gameplay state.

`tasks` stores productivity tasks created by users.

`categories` stores task categories.

`pomodoros` stores reusable pomodoro presets owned by users.

`repetition_settings` stores optional recurrence configuration for pomodoro presets.

`pomodoro_sessions` stores execution history for work, short break, and long break sessions.

`player_progress` stores one progression profile per user.

`achievements` stores achievement definitions or unlocked user achievements.

`waifus` stores collectible companion definitions.

`waifu_skin_unlocks` stores user-owned waifu skin unlocks.

## Cardinalities

```text
User 1 -> N Task
Category 1 -> N Task
User 1 -> N Pomodoro
Pomodoro 1 -> 0..1 RepetitionSettings
User 1 -> N PomodoroSession
Task 1 -> N PomodoroSession
Pomodoro 1 -> N PomodoroSession
User 1 -> 1 PlayerProgress
User 1 -> N Achievement
User 1 -> N WaifuSkinUnlock
Waifu 1 -> N WaifuSkinUnlock
```

## JPA Relationships

`Pomodoro.user`: `@ManyToOne(fetch = LAZY)`.

`Pomodoro.task`: optional `@ManyToOne(fetch = LAZY)`.

`Pomodoro.repetitionSettings`: `@OneToOne(mappedBy = "pomodoro", cascade = ALL, orphanRemoval = true)`.

`PomodoroSession.user`: `@ManyToOne(fetch = LAZY)`.

`PomodoroSession.task`: optional `@ManyToOne(fetch = LAZY)`.

`PomodoroSession.pomodoro`: optional `@ManyToOne(fetch = LAZY)`.

`PlayerProgress.user`: `@OneToOne(fetch = LAZY)` with a unique `user_id`.

`Achievement.user`: `@ManyToOne(fetch = LAZY)`.

`WaifuSkinUnlock.user`: `@ManyToOne(fetch = LAZY)`.

`WaifuSkinUnlock.waifu`: `@ManyToOne(fetch = LAZY)`.

## Ownership

User ownership is enforced by service methods using repository queries scoped by authenticated user email or user id.

Category is currently global. Tasks reference categories but task ownership remains user-scoped.

Waifu definitions are global catalog data. Skin unlocks are user-owned.

Achievement rows are user-owned unlock records in this implementation.
