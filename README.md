Anime RPG Pomodoro API

Backend REST API desarrollada con Java + Spring Boot para una aplicación de productividad gamificada inspirada en anime y RPGs.
El proyecto combina la técnica Pomodoro, gestión de tareas y mecánicas de progresión estilo videojuego para mejorar la productividad del usuario de forma interactiva.

Características Principales

✅ Autenticación con JWT
✅ Registro e inicio de sesión de usuarios
✅ Gestión de tareas
✅ Gestión de categorías
✅ Sistema Pomodoro
✅ Persistencia con PostgreSQL
✅ Migraciones con Flyway
✅ Arquitectura RESTful
✅ Validación de datos
✅ Manejo global de excepciones
✅ Testing de controladores y servicios
✅ Seguridad con Spring Security
✅ API preparada para integración con frontend React

🛠️ Tecnologías Utilizadas

Backend
Java 21
Spring Boot 3
Spring Web
Spring Data JPA
Spring Security
JWT Authentication
Hibernate
Flyway
Maven
Base de Datos
PostgreSQL
Testing
JUnit 5
Mockito
MockMvc

📂 Estructura del Proyecto
src/
 ├── main/
 │   ├── java/com/alexdev/animerpgpomodoro/
 │   │
 │   ├── config/              # Configuraciones de seguridad y JWT
 │   ├── controller/          # Endpoints REST
 │   ├── dto/                 # Objetos de transferencia de datos
 │   ├── entity/              # Entidades JPA
 │   ├── exception/           # Manejo global de errores
 │   ├── repository/          # Acceso a datos
 │   ├── security/            # JWT Filters y helpers
 │   ├── service/             # Lógica de negocio
 │   └── AnimeRpgPomodoroApiApplication.java
 │
 └── test/
     ├── controller/          # Tests de controllers
     └── service/             # Tests de services
🔐 Autenticación JWT

La API utiliza autenticación basada en tokens JWT.

Flujo
El usuario se registra o inicia sesión
La API genera un token JWT
El frontend almacena el token
El token se envía en cada request protegida

Ejemplo:

Authorization: Bearer YOUR_TOKEN
⚙️ Configuración del Proyecto
1. Clonar el repositorio
git clone https://github.com/your-username/anime-rpg-pomodoro-api.git
2. Configurar PostgreSQL

Crear una base de datos:

CREATE DATABASE anime_rpg_pomodoro;
3. Configurar variables en application.properties
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
▶️ Ejecutar el Proyecto
Ejecutar con Maven
./mvnw spring-boot:run

o en Windows:

mvnw.cmd spring-boot:run

📌 Endpoints Principales

🔑 Auth
Método	Endpoint	Descripción
POST	/api/v1/auth/register	Registrar usuario
POST	/api/v1/auth/login	Iniciar sesión

✅ Tasks
Método	Endpoint
GET	/api/v1/tasks
GET	/api/v1/tasks/{id}
POST	/api/v1/tasks
PUT	/api/v1/tasks/{id}
DELETE	/api/v1/tasks/{id}

📁 Categories
Método	Endpoint
GET	/api/v1/categories
POST	/api/v1/categories
PUT	/api/v1/categories/{id}
DELETE	/api/v1/categories/{id}

🍅 Pomodoro Sessions
Método	Endpoint
GET	/api/v1/pomodoros
POST	/api/v1/pomodoros
PUT	/api/v1/pomodoros/{id}
DELETE	/api/v1/pomodoros/{id}

🧪 Testing

El proyecto incluye pruebas unitarias y de integración para:

Controllers
Services
Seguridad
Endpoints protegidos
Ejecutar tests
mvn test

🧱 Arquitectura

El proyecto sigue una arquitectura por capas:

Controller
   ↓
Service
   ↓
Repository
   ↓
Database
🔒 Seguridad

La API implementa:

Spring Security
JWT Authentication
Stateless Sessions
Password Encoding con BCrypt
Protección de endpoints privados

📈 Roadmap
Funcionalidades futuras

🎮 Sistema de experiencia y niveles
🏆 Achievements
👤 Sistema de waifus desbloqueables
🎨 Skins y cosméticos
📊 Estadísticas de productividad
🔔 Notificaciones
☁️ Deploy en AWS o Railway
🐳 Dockerización
📱 Integración completa con frontend React
🖼️ Futuro Frontend

El backend está diseñado para conectarse con un frontend en:

React
TypeScript
TailwindCSS

📚 Concepto del Proyecto

Anime RPG Pomodoro busca transformar la productividad en una experiencia divertida mediante:

Mecánicas RPG
Recompensas
Progresión
Gamificación
Estética anime

La idea es motivar al usuario a completar tareas y sesiones Pomodoro mientras avanza dentro del sistema.

👨‍💻 Autor

Desarrollado por Alejandro Castañeda.

Java Backend Developer
ASP.NET Developer
Fullstack Developer en formación
📄 Licencia

Este proyecto está bajo la licencia MIT.
