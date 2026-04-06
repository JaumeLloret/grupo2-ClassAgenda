<p align="center">
  <img src="docs/Header.png" alt="ClassAgenda Header">
</p>

---

## 👥 Equipo

- Alumno/a 1:  Beatriz
- Alumno/a 2:  José Manuel Ruiz Sojo (Chema)
- Alumno/a 3:  Alfonso Daniel Perucho Domínguez
- Alumno/a 4: Bruno Regueira Ayuso

---

## 📑 Índice

1. [Descripción del proyecto](#-descripción-del-proyecto)
2. [Tecnologías utilizadas](#-tecnologías-utilizadas)
3. [Restricciones](#-restricciones)
4. [Arquitectura del proyecto](#-arquitectura-del-proyecto)
5. [Base de datos](#-base-de-datos)
6. [Modelo de datos](#-modelo-de-datos)
7. [API REST](#-api-rest)
8. [Cliente web](#-cliente-web)
9. [Máquina virtual (Servidor)](#-máquina-virtual-servidor)
10. [Pruebas](#-pruebas)
11. [Metodología de trabajo](#-metodología-de-trabajo)
12. [Estado del proyecto](#-estado-del-proyecto)
13. [Notas finales](#-notas-finales)

---

## 🎯 Descripción del proyecto

ClassAgenda es una aplicación web que permite a los usuarios gestionar **tareas** y **eventos**, asociarlos a un usuario propietario y **compartirlos con otros usuarios** con distintos permisos (READ / EDIT).

El proyecto integra contenidos de Programación, Bases de Datos, Sistemas Informáticos, Entornos de Desarrollo y Lenguajes de Marcas.

---

## ⚙️ Tecnologías utilizadas

### Backend
- Java puro
- HttpServer
- JDBC
- Arquitectura limpia
- Principios SOLID

### Base de datos

- SQL Server

### Cliente
- HTML5
- CSS3
- JavaScript (fetch + JSON)

### Infraestructura
- Máquina virtual Windows
- Git y GitHub

---

## 🚫 Restricciones

- No se utilizan frameworks
- No hay sistema de autenticación
- El usuario activo se indica mediante la cabecera HTTP:

```
X-User-Id: <id_del_usuario>
```
## 🧱 Arquitectura del proyecto

```
src/main/java/com/classagendag2/
│
├── di/
│   └── Configuración de dependencias
│
├── features/
│   └── example/
│       ├── data/
│       │   └── local/
│       │       ├── connection/
│       │       │   ├── DbConnectionFactory.java
│       │       │   └── DbSmokeTest.java
│       │       ├── dao/
│       │       └── entity/
│       │
│       ├── mapper/
│       └── repository/
│
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
│
├── presentation/
│   ├── dto/
│   ├── handlers/
│   │   └── ExampleIntroHandler.java
│   └── router/
│       └── ExampleRouter.java
│
├── shared/
│   ├── config/
│   │   ├── DbConfig.java
│   │   └── ServerConfig.java
│   │
│   ├── http/
│   │   ├── handlers/
│   │   ├── helpers/
│   │   │   ├── HttpServerBootstrap.java
│   │   │   ├── JsonResponses.java
│   │   │   └── ResponseContract.java
│   │
│   └── utilities/
│       └── EnvLoader.java
│
└── App.java

```

## 🗄️ Base de datos

- Motor: SQL Server
- Tablas principales:
  - USERS
  - TASKS
  - EVENTS
  - TASK_SHARES
  - EVENT_SHARES

## Esquema relacional

| Tabla            | Columnas                                                                                                   | Descripción |
|------------------|-------------------------------------------------------------------------------------------------------------|-------------|
| **USERS**        | id (PK, IDENTITY)<br>name VARCHAR(80) NOT NULL<br>email VARCHAR(255) UNIQUE NOT NULL<br>created_at DATETIME DEFAULT GETDATE() | Información de los usuarios registrados. |
| **EVENTS**       | id (PK, IDENTITY)<br>owner_user_id (FK → USERS.id)<br>title VARCHAR(120) NOT NULL<br>description VARCHAR(1000)<br>start_at DATETIME NOT NULL<br>end_at DATETIME NOT NULL<br>event_type VARCHAR(12) NOT NULL<br>created_at DATETIME DEFAULT GETDATE() | Eventos creados por los usuarios. |
| **TASKS**        | id (PK, IDENTITY)<br>owner_user_id (FK → USERS.id)<br>title VARCHAR(120) NOT NULL<br>description VARCHAR(1000)<br>due_date DATETIME<br>status VARCHAR(10)<br>priority VARCHAR(6)<br>created_at DATETIME DEFAULT GETDATE() | Tareas creadas por los usuarios. |
| **EVENT_SHARES** | event_id (FK → EVENTS.id)<br>shared_with_user_id (FK → USERS.id)<br>permission VARCHAR(10)<br>shared_at DATETIME DEFAULT GETDATE()<br>PRIMARY KEY (event_id, shared_with_user_id) | Compartición de eventos entre usuarios. |
| **TASK_SHARES**  | task_id (FK → TASKS.id)<br>shared_with_user_id (FK → USERS.id)<br>permission VARCHAR(10)<br>shared_at DATETIME DEFAULT GETDATE()<br>PRIMARY KEY (task_id, shared_with_user_id) | Compartición de tareas entre usuarios. |

## Diagrama E-R
  ![Diagrama E-R](docs/Diagrama-E-R.png)
  
## Scripts SQL  
  * [01_schema_sql](database/01_schema.sql)
  * [02_seed_sql](database/02_seed.sql)
---

## 📘 Modelo de Datos

Descripción general:

El modelo de datos de ClassAgenda permite gestionar eventos y tareas personales o compartidas entre usuarios. Cada usuario puede crear sus propios recursos y compartirlos con otros mediante un sistema de permisos.

Las tablas EVENT_SHARES y TASK_SHARES controlan qué usuario tiene acceso a qué recurso y con qué nivel de permiso.

Tablas del modelo:

- USERS:	Almacena los usuarios registrados (nombre, email, fecha de creación).

- EVENTS:	Eventos creados por los usuarios (título, descripción, fechas, tipo).

- TASKS:	Tareas personales (título, descripción, fecha límite, estado, prioridad).

- EVENT_SHARES:	Compartición de eventos entre usuarios con permisos.

- TASK_SHARES:	Compartición de tareas entre usuarios con permisos.
  
Relaciones principales:

- Un usuario puede crear múltiples eventos y tareas (1:N).

- Un evento o tarea puede compartirse con varios usuarios mediante las tablas de shares (1:N).

- Un usuario puede recibir múltiples recursos compartidos(1:N).

---

## 🟦 API REST — CRUD de Usuarios (Sprint 4)
Este sprint implementa la primera entidad completa del proyecto: User, siguiendo Clean Architecture, SOLID y acceso a datos mediante JDBC.
La API expone un CRUD funcional para gestionar usuarios en la base de datos SQL Server.

### 🧱 Arquitectura aplicada en este sprint
El módulo de usuarios se ha construido siguiendo las capas de Clean Architecture:
- Dominio
- User (modelo inmutable con validaciones estrictas)
- UserRepository (contrato de acceso a datos)
- Data
- UserEntity (reflejo exacto de la tabla USERS)
- UserDao (sentencias SQL con PreparedStatement)
- UserMapper (traducción Entity ↔ Dominio)
- JdbcUserRepository (implementación del contrato del Dominio)
- Presentación

### 🧩 Modelo de Dominio: User
El objeto User representa un usuario válido según las reglas de negocio:
- Atributos inmutables (final)

Validaciones estrictas:
- Nombre no vacío y ≤ 80 caracteres
- Email no vacío, ≤ 255 caracteres y con '@'
- Fecha de creación no nula

Dos constructores:
- Con ID (para datos que vienen de la BD)
- Sin ID (para nuevos usuarios)

### 🗄️ Acceso a Datos (DAO + Repository)
UserEntity
Refleja la tabla SQL USERS y permite ser rellenada por JDBC.

UserDao
Responsable de todas las operaciones SQL:
- insert
- findById
- findByEmail
- findAll
- update
- deleteById

Todas las consultas usan PreparedStatement para evitar inyección SQL.

UserMapper
Traduce entre:
- Dominio → Entity
- Entity → Dominio

Garantizando que los datos siempre pasan por las validaciones del Dominio.

JdbcUserRepository
Implementa el contrato UserRepository:
- Decide si guardar implica insert o update
- Usa Optional en las búsquedas
- Usa Streams para transformar listas

## 🟦 Endpoints disponibles en este sprint
### ⚠️ Importante:
En este sprint solo se ha implementado el CRUD interno de usuarios.

## 🧪 Pruebas manuales realizadas (CRUD de Usuarios)
Las pruebas verifican que el CRUD funciona correctamente contra SQL Server.

### ✔️ Crear usuario
Acción: Guardar un nuevo User("Nombre", "email@ejemplo.com")

Resultado esperado:
- ID autogenerado
- Fecha creada correctamente
- Datos válidos tras pasar las validaciones del Dominio

### ✔️ Buscar por email
Acción: findByEmail("email@ejemplo.com")

Resultado esperado:
- Optional con usuario presente
- Datos coinciden con lo guardado

### ✔️ Buscar por ID
Acción: findById(id)

Resultado esperado:
- Optional presente si existe
- Optional vacío si no existe

### ✔️ Listar todos
Acción: findAll()
Resultado esperado:
- Lista de usuarios
- Mapeo correcto Entity → Dominio

### ✔️ Actualizar usuario
Acción: modificar nombre/email y llamar a save()

Resultado esperado:
- UPDATE ejecutado correctamente
- Datos actualizados en BD

### ✔️ Borrar usuario
Acción: deleteById(id)
- Resultado esperado:
- Fila eliminada sin errores

## 🧪 Test de Integración (IT)
Se ha creado un test de integración real:

JdbcUserRepositoryIT.java

Incluye:
- Lectura protegida del .env mediante Assumptions
- Inserción real en SQL Server
- Búsqueda por email
- Comparación de fechas truncadas a segundos
- Verificación de ID autogenerado

**Si la BD no está disponible (como en GitHub Actions), el test se aborta sin fallar.**
---

## 🟦 GET `/example/intro`

Este endpoint devuelve un JSON estándar indicando que el servidor funciona correctamente y mostrando información básica de la petición.

### 📤 Petición
```http
GET http://localhost:8080/example/intro
```

### 📥 Respuesta
```json
{
  "status": "ok",
  "service": "ClassAgenda",
  "timestamp": "2026-02-25T12:18:45.918940916Z",
  "data": {
    "endpoint": "example/intro",
    "method": "GET",
    "message": "GET ok",
    "receivedBody": null
  }
}
```
---

## 🛠 Endpoints en desarrollo
El proyecto está preparado para añadir nuevos endpoints en:

```
shared/http/handlers
```

A medida que se implementen nuevos handlers y routers, se irán documentando en esta sección.

## 🟦 CRUD de TAREAS (Sprint 5)
### Arquitectura aplicada en este sprint

**Dominio:**
- Task (modelo inmutable con validaciones)
- TaskStatus (enum)
- TaskPriority (enum)

**Data:**
- TaskEntity (reflejo de la tabla TASKS)
- TaskDao (operaciones SQL con JDBC)
- TaskMapper (Entity ↔ Dominio)
- JdbcTaskRepository (implementación del repositorio)

**Presentacion:**
- TaskHandler (endpoints REST)
- TaskRouter (rutas HTTP)

### Enumeraciones (Enums)

Para garantizar consistencia y evitar errores de escritura, el estado y la prioridad se representan mediante enums:

**TaskStatus.java**
```java
public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}
```
**TaskPriority.java**
```java
public enum TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}
```
### Modelo de Dominio: Task
La clase Task es inmutable y representa una tarea válida según las reglas de negocio.

Incluye:
- Validación de título
- Validación de propietario
- Validación de estado y prioridad
- Fecha de creación truncada a segundos
- Regla de negocio: solo el propietario puede acceder a la tarea

### Regla OWNER
```java
public void validateIsOwnedBy(Long requestingUserId) {
    if (!Objects.equals(this.ownerId, requestingUserId)) {
        throw new SecurityException("No tienes acceso a esta tarea");
    }
}
```

### Acceso a Datos (DAO + Repository)
**TaskEntity**

Refleja la tabla SQL TASKS.

**TaskDao**

Incluye:
- insert
- findById
- findAll (con filtros)
- update
- deleteById

Todas las consultas usan PreparedStatement.

**TaskMapper**

Traduce:
- Entity → Dominio
- Dominio → Entity

### Endpoints de la API de Tareas

**Crear tarea**

POST /tasks

**Listar tareas (con filtros)**

GET /tasks?scope=OWN&status=PENDING&priority=HIGH

**Obtener tarea por ID**

GET /tasks/{id}

**Actualizar tarea**

PUT /tasks/{id}

**Eliminar tarea**

DELETE /tasks/{id}

### Control de Propietario (OWNER)
Toda operación requiere el header:

X-User-Id: <id>

Si el usuario intenta acceder a una tarea que no es suya, la API devuelve:
```json
{
  "error": {
    "code": "NOT_FOUND",
    "message": "No tienes acceso a esta tarea"
  }
}
```
### Filtros implementados
Por SCOPE:

GET /tasks?scope=OWN

Por ESTADO:

GET /tasks?status=PENDING

Por PRIORIDAD:

GET /tasks?priority=HIGH

### Prueba de control OWNER (403/NOT_FOUND)

Los parámetros usados para la prueba determinan el acceso a una tarea asignada a un ID desde otro ID diferente:

Petición:

GET /tasks/1

X-User-Id: 2

Respuesta:
```json
{
  "error": {
    "code": "NOT_FOUND",
    "message": "No tienes acceso a esta tarea"
  }
}
```

## 🖥️ Cliente web

📌 **Pendiente**:  
- Descripción de las vistas
- Flujo de navegación
- Capturas de pantalla

---

## 🖥️ Máquina virtual (Servidor)

📌 **Pendiente**:  
- Configuración de la VM
- Instalación de SQL Server
- Puesta en marcha de la API
- Evidencias (capturas)

---

## 🧪 Pruebas

📌 **Pendiente**:  
- Casos de prueba manuales
- Evidencias de funcionamiento

---

## 📈 Metodología de trabajo

El proyecto se desarrolla siguiendo **Extreme Programming (XP)**:

- Trabajo en iteraciones
- Pair programming
- Commits pequeños y frecuentes
- Uso de Issues, Projects y Pull Requests en GitHub
- Refactorización continua

---

## 🚀 Estado del proyecto

- [ ] Diseño inicial
- [ ] Base de datos
- [ ] API REST
- [ ] Cliente web
- [ ] Integración
- [ ] Despliegue en VM
- [ ] Documentación final

---

## 📌 Notas finales

Este README debe actualizarse durante todo el desarrollo del proyecto.  
La calidad de la documentación forma parte de la evaluación.

