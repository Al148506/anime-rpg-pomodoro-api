# Pomodoro Module

## Responsibility

The pomodoro module manages reusable timer presets owned by users.

## Entity

`Pomodoro` contains name, type, work duration, break durations, long break cadence, optional task, user, and timestamps.

## DTOs

`PomodoroRequest` validates create and update payloads.

`PomodoroResponse` returns safe API data without exposing JPA relations.

`RepetitionSettingsRequest` and `RepetitionSettingsResponse` represent recurrence configuration.

## Endpoints

`POST /api/v1/pomodoros`

`GET /api/v1/pomodoros`

`GET /api/v1/pomodoros/{id}`

`PUT /api/v1/pomodoros/{id}`

`DELETE /api/v1/pomodoros/{id}`

## Business Rules

Linked task must belong to the authenticated user.

Only owner can view, update, or delete a pomodoro.

Delete removes owned repetition settings through orphan removal.
