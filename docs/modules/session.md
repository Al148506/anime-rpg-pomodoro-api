# Session Module

## Responsibility

The session module records timer executions and owns lifecycle transitions.

## Entity

`PomodoroSession` contains user, optional pomodoro preset, optional task, type, status, planned duration, actual duration, started/completed timestamps, notes, and audit timestamps.

## Endpoints

`POST /api/v1/sessions/start`

`PATCH /api/v1/sessions/{id}/complete`

`PATCH /api/v1/sessions/{id}/cancel`

`GET /api/v1/sessions`

`GET /api/v1/sessions/{id}`

## Business Rules

Only in-progress sessions can be completed or cancelled.

Completing focus sessions awards XP through the progression service.

All lookups are scoped to the authenticated user.
