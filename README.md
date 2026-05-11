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

## 🟦 API - CRUD de TAREAS (Sprint 5)
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
    MED,
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

# 📘 ClassAgenda – Módulo Events

## 📌 Descripción general

El módulo **Events** gestiona los eventos personales de cada usuario dentro de la aplicación ClassAgenda.  
Toda la funcionalidad está asociada a un usuario concreto mediante la cabecera `X-User-Id`, lo que garantiza que cada persona solo pueda acceder a sus propios eventos.

Este módulo implementa las operaciones básicas de un CRUD: creación, consulta individual, listado, actualización y eliminación.

---

## 📂 Contenido del módulo

El módulo se organiza en tres capas principales:

### 1. **Capa de dominio (`domain/`)**
Contiene la lógica central del módulo:
- **Event.java** → Modelo de dominio que representa un evento.
- **EventRepository.java** → Interfaz que define las operaciones necesarias (save, findById, findAllByOwner, delete).

### 2. **Capa de datos (`data/`)**
Gestiona la comunicación con la base de datos:
- **EventEntity.java** → Representación del evento en SQL Server.
- **EventDao.java** → Acceso directo a la base de datos mediante SQL.
- **EventMapper.java** → Conversión entre `EventEntity` y `Event`.
- **JdbcEventRepository.java** → Implementación real del repositorio usando el DAO.

### 3. **Capa de presentación (`presentation/`)**
Expone la API HTTP:
- **EventHandler.java** → Gestiona las peticiones GET, POST, PUT y DELETE.
- Valida datos de entrada.
- Construye respuestas JSON manualmente (sin librerías externas).
- Aplica reglas de negocio como la propiedad del evento.

---

## ⚙️ Funcionamiento del módulo

### ✔ Identificación del usuario
Cada petición debe incluir la cabecera:
***X-User-Id: <id_usuario>***

## Esto permite:
- Asociar nuevos eventos al usuario.
- Filtrar eventos en las consultas.
- Impedir el acceso a eventos ajenos.

### ✔ Creación de eventos
El servidor recibe los datos del evento, valida que las fechas sean correctas y crea un nuevo registro en la base de datos.

### ✔ Consulta de eventos
El módulo permite:
- Obtener todos los eventos del usuario.
- Obtener un evento concreto por su ID.
- Verificar que el evento pertenece al usuario antes de devolverlo.

### ✔ Validación de fechas
El modelo `Event` exige:
- `startAt` y `endAt` obligatorios.
- Formato `YYYY-MM-DDTHH:MM:SS`.
- Conversión a `LocalDateTime`.

Si los datos no cumplen estas reglas, se devuelve un error claro y consistente.

### ✔ Respuestas JSON uniformes
Todas las respuestas siguen el formato estándar definido en `ResponseContract`, incluyendo:
- `status`
- `service`
- `timestamp`
- `data` o `error`

Esto garantiza coherencia en toda la API.

---


## 📘 Colaboración: TASK_SHARES y EVENT_SHARES

## 📌 Descripción general

El sistema de colaboración permite que un usuario propietario (OWNER) comparta sus tareas y eventos con otros usuarios, asignándoles un nivel de permiso concreto.

Los niveles de permiso son:

- OWNER: acceso total (no se guarda en las tablas de compartición)
- EDIT: puede consultar y modificar
- READ: solo lectura

Esta colaboración se implementa mediante tablas intermedias (`TASK_SHARES` y `EVENT_SHARES`) y se integra con los handlers existentes para aplicar las reglas de acceso en los endpoints de la API.

---

## 📂 Contenido del módulo de colaboración

### 1. Capa de dominio (`domain/`)

Contiene la lógica central de permisos y compartición:

- PermissionLevel.java → Enum que define los niveles READ y EDIT.
- TaskShare.java → Modelo de dominio que representa la compartición de una tarea.
- EventShare.java → Modelo de dominio que representa la compartición de un evento.
- TaskShareRepository.java → Interfaz para operaciones de compartición de tareas.
- EventShareRepository.java → Interfaz para operaciones de compartición de eventos.

### 2. Capa de datos (`data/`)

Gestiona la comunicación con las tablas intermedias de base de datos.

Tablas intermedias:

- TASK_SHARES
- EVENT_SHARES

Estructura general:

\`\`\`sql
resource_id      -- task_id o event_id según el caso
user_id          -- usuario invitado
permission_level -- READ o EDIT
created_at       -- fecha de creación de la invitación
\`\`\`

---

## ⚙️ Funcionamiento del sistema de permisos

### Identificación del usuario

Todas las peticiones que acceden a tareas o eventos deben incluir la cabecera:

\`\`\`
X-User-Id: <id_usuario>
\`\`\`

Con esta cabecera se determina si el usuario es:

- Propietario (OWNER) del recurso.
- Invitado con permiso READ.
- Invitado con permiso EDIT.
- Un usuario sin acceso.

---

### Reglas de autorización

Para cada operación sobre una tarea o evento:

- GET
  - Permitido para OWNER.
  - Permitido para usuarios con permiso READ o EDIT.

- PUT
  - Permitido para OWNER.
  - Permitido para usuarios con permiso EDIT.

- DELETE
  - Reservado al OWNER.

Si el usuario no cumple las condiciones de permiso, el sistema devuelve un error (403 Forbidden).

---

### Resumen de comportamiento esperado

- Un usuario OWNER puede:
  - Compartir una tarea o evento con otros usuarios.
  - Cambiar el nivel de permiso (READ ↔ EDIT).
  - Revocar el acceso.
  - Realizar GET, PUT y DELETE sobre sus recursos.

- Un usuario con permiso EDIT puede:
  - Realizar GET y PUT sobre la tarea o evento compartido.
  - No puede realizar DELETE.
  - No puede volver a compartir el recurso.

- Un usuario con permiso READ puede:
  - Realizar GET sobre la tarea o evento compartido.
  - No puede realizar PUT ni DELETE.
  - No puede volver a compartir el recurso.

- Un usuario sin permisos:
  - No puede acceder al recurso (GET, PUT, DELETE devuelven error de autorización).

---

## 🗂️ EventShare — Compartición de eventos

El módulo EventShare permite que el propietario de un evento pueda compartirlo con otros usuarios, asignando permisos READ o EDIT, así como revocar dichos permisos.  
La validación de propietario se realiza en el dominio mediante validateIsOwnedBy().

---

## 📌 Endpoints principales

### 1. Compartir un evento

POST /event/share?eventId={id}

Headers:
\`\`\`
X-User-Id: {ownerId}
Content-Type: application/json
\`\`\`

Body:
\`\`\`json
{
"targetUserId": 2,
"permission": "READ"
}
\`\`\`

Respuesta (200 OK):
\`\`\`json
{
"status": "ok",
"data": { "message": "Evento compartido" }
}
\`\`\`

---

### 2. Intento de compartir por usuario NO propietario

POST /event/share?eventId={id}

Headers:
\`\`\`
X-User-Id: {noOwnerId}
Content-Type: application/json
\`\`\`

Body:
\`\`\`json
{
"targetUserId": 3,
"permission": "READ"
}
\`\`\`

Respuesta (403 Forbidden):
\`\`\`json
{
"status": "error",
"error": {
"message": "Prohibido",
"details": "No eres el dueño del evento"
}
}
\`\`\`

---

### 3. Revocar permisos

DELETE /event/share?eventId={id}&targetUserId={userId}

Headers:
\`\`\`
X-User-Id: {ownerId}
\`\`\`

Respuesta (200 OK):
\`\`\`json
{
"status": "ok",
"data": { "message": "Permisos revocados" }
}
\`\`\`

---

## 🧪 Pruebas realizadas sobre EventShare

- Compartición correcta por parte del propietario.
- Intento de compartición por usuario no autorizado (403).
- Revocación de permisos correctamente aplicada.

Estas pruebas demuestran el correcto funcionamiento del módulo EventShare, incluyendo seguridad, persistencia y gestión completa del ciclo de permisos.


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

