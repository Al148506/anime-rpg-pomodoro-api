# Anime RPG Pomodoro API

Backend REST API desarrollada con **Java 21 + Spring Boot 3** para una aplicación de productividad gamificada inspirada en anime y RPGs.

El proyecto combina la técnica **Pomodoro**, gestión de tareas y mecánicas de progresión estilo videojuego para ayudar al usuario a mantenerse productivo de una forma más interactiva y motivante.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-316192)
![JWT](https://img.shields.io/badge/Auth-JWT-black)
![Flyway](https://img.shields.io/badge/Migrations-Flyway-red)
![License](https://img.shields.io/badge/License-MIT-blue)

## Características

- Autenticación y autorización con JWT.
- Registro e inicio de sesión de usuarios.
- Gestión de tareas.
- Gestión de categorías.
- Sistema Pomodoro.
- Persistencia con PostgreSQL.
- Migraciones con Flyway.
- Validación de datos.
- Manejo global de excepciones.
- Testing de controladores y servicios.
- Seguridad con Spring Security.
- API preparada para integrarse con un frontend en React.

## Stack tecnológico

### Backend
- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- Hibernate
- JWT Authentication
- Maven

### Base de datos
- PostgreSQL
- Flyway

### Testing
- JUnit 5
- Mockito
- MockMvc

## Estructura del proyecto

```bash
src/
├── main/
│   ├── java/com/alexdev/animerpgpomodoro/
│   │   ├── config/         # Configuración de seguridad y JWT
│   │   ├── controller/     # Endpoints REST
│   │   ├── dto/            # Objetos de transferencia de datos
│   │   ├── entity/         # Entidades JPA
│   │   ├── exception/      # Manejo global de errores
│   │   ├── repository/     # Acceso a datos
│   │   ├── security/       # Filtros JWT y utilidades
│   │   ├── service/        # Lógica de negocio
│   │   └── AnimeRpgPomodoroApiApplication.java
│   └── resources/
└── test/
    ├── controller/         # Tests de controladores
    └── service/            # Tests de servicios
```

## Autenticación JWT

La API usa autenticación basada en tokens JWT.

### Flujo de autenticación
1. El usuario se registra o inicia sesión.
2. La API genera un token JWT.
3. El frontend almacena el token.
4. El token se envía en cada request a endpoints protegidos.

### Header de autorización
```http
Authorization: Bearer YOUR_TOKEN
```

## Configuración local

### 1. Clonar el repositorio
```bash
git clone https://github.com/your-username/anime-rpg-pomodoro-api.git
cd anime-rpg-pomodoro-api
```

### 2. Crear la base de datos en PostgreSQL
```sql
CREATE DATABASE anime_rpg_pomodoro;
```

### 3. Configurar `application.properties`
```properties
spring.application.name=anime-rpg-pomodoro-api

spring.datasource.url=jdbc:postgresql://localhost:5432/anime_rpg_pomodoro
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

## Ejecución

### Ejecutar con Maven
```bash
./mvnw spring-boot:run
```

### En Windows
```bash
mvnw.cmd spring-boot:run
```

Por defecto, la API se ejecuta en:

```bash
http://localhost:8080
```

## Endpoints principales

### Auth
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Registrar usuario |
| POST | `/api/v1/auth/login` | Iniciar sesión |

### Tasks
| Método | Endpoint |
|--------|----------|
| GET | `/api/v1/tasks` |
| GET | `/api/v1/tasks/{id}` |
| POST | `/api/v1/tasks` |
| PUT | `/api/v1/tasks/{id}` |
| DELETE | `/api/v1/tasks/{id}` |

### Categories
| Método | Endpoint |
|--------|----------|
| GET | `/api/v1/categories` |
| POST | `/api/v1/categories` |
| PUT | `/api/v1/categories/{id}` |
| DELETE | `/api/v1/categories/{id}` |

### Pomodoro Sessions
| Método | Endpoint |
|--------|----------|
| GET | `/api/v1/pomodoros` |
| POST | `/api/v1/pomodoros` |
| PUT | `/api/v1/pomodoros/{id}` |
| DELETE | `/api/v1/pomodoros/{id}` |

## Ejemplo de uso

### Login
```http
POST /api/v1/auth/login
Content-Type: application/json
```

```json
{
  "email": "user@example.com",
  "password": "12345678"
}
```

### Respuesta esperada
```json
{
  "token": "YOUR_JWT_TOKEN"
}
```

### Request autenticado
```http
GET /api/v1/tasks
Authorization: Bearer YOUR_JWT_TOKEN
```

## Testing

El proyecto incluye pruebas para:

- Controllers
- Services
- Seguridad
- Endpoints protegidos

### Ejecutar tests
```bash
mvn test
```

## Arquitectura

El proyecto sigue una arquitectura por capas:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

## Seguridad

La API implementa:

- Spring Security
- JWT Authentication
- Stateless Sessions
- Password Encoding con BCrypt
- Protección de endpoints privados

## Roadmap

Funcionalidades planeadas para próximas versiones:

- Sistema de experiencia y niveles
- Achievements
- Sistema de waifus desbloqueables
- Skins y cosméticos
- Estadísticas de productividad
- Notificaciones
- Dockerización
- Deploy en AWS o Railway
- Integración completa con frontend React

## Frontend previsto

El backend está diseñado para integrarse con un frontend construido con:

- React
- TypeScript
- TailwindCSS

## Concepto del proyecto

**Anime RPG Pomodoro** busca transformar la productividad en una experiencia divertida mediante:

- Mecánicas RPG
- Recompensas
- Progresión
- Gamificación
- Estética anime

La idea es motivar al usuario a completar tareas y sesiones Pomodoro mientras avanza dentro del sistema.

## Autor

Desarrollado por **Alejandro Castañeda**.

- Java Backend Developer
- ASP.NET Developer
- Fullstack Developer en formación

## Licencia

Este proyecto está bajo la licencia **MIT**.
