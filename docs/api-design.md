# REST API Design

## Security

All endpoints below require `Authorization: Bearer <jwt>` unless explicitly public.

## Pomodoros

### Create Pomodoro

`POST /api/v1/pomodoros`

```json
{
  "name": "Deep Work",
  "type": "FOCUS",
  "durationMinutes": 25,
  "shortBreakMinutes": 5,
  "longBreakMinutes": 15,
  "sessionsBeforeLongBreak": 4,
  "taskId": "uuid",
  "repetitionSettings": {
    "enabled": true,
    "repeatCount": 4,
    "repeatDaily": false
  }
}
```

Returns `201 Created` with `PomodoroResponse`.

### List Pomodoros

`GET /api/v1/pomodoros`

Returns `200 OK`.

### Get Pomodoro

`GET /api/v1/pomodoros/{id}`

Returns `200 OK` or `404 Not Found`.

### Update Pomodoro

`PUT /api/v1/pomodoros/{id}`

Returns `200 OK`.

### Delete Pomodoro

`DELETE /api/v1/pomodoros/{id}`

Returns `204 No Content`.

## Pomodoro Sessions

### Start Session

`POST /api/v1/sessions/start`

```json
{
  "pomodoroId": "uuid",
  "taskId": "uuid",
  "type": "FOCUS",
  "plannedDurationMinutes": 25
}
```

Returns `201 Created`.

### Complete Session

`PATCH /api/v1/sessions/{id}/complete`

```json
{
  "actualDurationMinutes": 25,
  "notes": "Finished chapter review"
}
```

Returns `200 OK`.

### Cancel Session

`PATCH /api/v1/sessions/{id}/cancel`

Returns `200 OK`.

### List Sessions

`GET /api/v1/sessions`

Returns `200 OK`.

## Player Progress

`GET /api/v1/progression/me`

Returns the authenticated user's progression profile.

## Achievements

`GET /api/v1/achievements`

Lists current user's unlocked achievements.

`POST /api/v1/achievements/unlock`

Unlocks an achievement for the authenticated user.

## Waifus

`GET /api/v1/waifus`

Lists global waifu catalog.

`POST /api/v1/waifus`

Creates a waifu catalog item.

`GET /api/v1/waifus/skins/unlocks`

Lists authenticated user's skin unlocks.

`POST /api/v1/waifus/{waifuId}/skins/unlock`

Unlocks a skin for the authenticated user.

## Status Codes

`200 OK`: read/update success.

`201 Created`: resource created.

`204 No Content`: resource deleted.

`400 Bad Request`: validation error or invalid state.

`401 Unauthorized`: missing or invalid JWT.

`404 Not Found`: missing or non-owned resource.

`409 Conflict`: duplicate or already unlocked resource.
