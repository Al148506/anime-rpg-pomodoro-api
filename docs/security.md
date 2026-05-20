# Security Design

## JWT Flow

The authentication module issues JWT tokens after successful login or registration.

The token subject is the user email. The `JwtAuthenticationFilter` extracts the email and stores it as the Spring Security principal.

Protected services resolve the user from `SecurityContextHolder.getContext().getAuthentication().getName()`.

## Public Endpoints

`/api/v1/auth/**` is public.

`/swagger-ui/**` is public.

`/v3/api-docs/**` is public.

## Protected Endpoints

Every other endpoint requires authentication.

## Authorization

Authorization is resource ownership-based.

Services must query resources by both id and user identity where the resource is user-owned.

For non-owned resources such as waifu catalog or global categories, write access may later be restricted to admins. The current implementation keeps JWT protection and focuses on user ownership for unlocks and sessions.

## Ownership Rules

Tasks belong to users.

Pomodoros belong to users.

Pomodoro sessions belong to users.

Player progress belongs to exactly one user.

Achievements belong to users as unlock records.

Waifu skin unlocks belong to users.

Waifu definitions are catalog data.

## Failure Behavior

Missing or non-owned resources return `404 Not Found`.

Duplicate unlocks return `409 Conflict`.

Invalid lifecycle transitions return `400 Bad Request`.
