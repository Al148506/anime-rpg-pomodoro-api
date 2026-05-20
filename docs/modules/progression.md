# Progression Module

## Responsibility

The progression module manages XP, level, completed focus session count, and streak metadata.

## Entity

`PlayerProgress` has a one-to-one relation with `User`.

## Endpoint

`GET /api/v1/progression/me`

## Business Rules

Profile is lazily created.

Focus sessions award XP.

Level is calculated from total XP.
