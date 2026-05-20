# Achievement Module

## Responsibility

The achievement module stores achievements unlocked by users.

## Entity

`Achievement` stores user, code, title, description, type, XP reward, unlockedAt, and audit timestamps.

## Endpoints

`GET /api/v1/achievements`

`POST /api/v1/achievements/unlock`

## Business Rules

Achievement code must be unique per user.

Unlocking an already unlocked achievement returns conflict.
