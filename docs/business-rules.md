# Business Rules

## Pomodoros

Pomodoro duration must be between 1 and 180 minutes.

Break durations must be between 1 and 60 minutes.

Sessions before long break must be between 1 and 12.

Optional linked tasks must belong to the authenticated user.

Repetition settings are optional.

If repetition is disabled, repeat values are informational and not used by session creation.

## Sessions

A session starts in `IN_PROGRESS` state.

Only `IN_PROGRESS` sessions can be completed or cancelled.

Completing a focus session awards XP to the authenticated user's progress profile.

Break sessions do not award XP.

Actual duration must be positive and cannot exceed 240 minutes.

## Progression

Every user has one progression profile.

The profile is created lazily the first time progression is requested or XP is awarded.

XP cannot be negative.

Level is derived from total XP using a simple threshold model: `level = totalXp / 100 + 1`.

## Achievements

Each user can unlock a specific achievement code only once.

Achievement code is immutable after creation.

Achievements may be unlocked manually by endpoint or automatically later from progression hooks.

## Waifus

Waifu catalog entries define available companions and their default skin.

Each user can unlock each skin for each waifu only once.

Unlocks store the skin code, display name, and unlock source.

## Validation

String fields use `@NotBlank` where required and `@Size` limits.

Numeric fields use `@Min` and `@Max`.

Enums must be valid enum values.

IDs required in requests use `@NotBlank`.
