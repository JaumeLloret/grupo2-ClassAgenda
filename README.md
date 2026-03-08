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
## 🌐 API REST

## 📝 API de Tareas

La API de tareas implementa un CRUD completo siguiendo Arquitectura Limpia, JDBC y control de permisos por usuario mediante la cabecera `X-User-Id`.

---

## 📌 Endpoints principales

### ➕ Crear tarea
````
POST /tasks
Headers:
X-User-Id: <id_usuario>
Body:
{
"title": "Hacer ejercicio",
"description": "30 minutos de cardio",
"priority": "high",
"scope": "personal"
}
````

### 📄 Obtener todas las tareas
````
GET /tasks
Headers:
X-User-Id: <id_usuario>
````

### 🔍 Obtener una tarea por ID
````
GET /tasks/{id}
Headers:
X-User-Id: <id_usuario>
````
### ✏️ Actualizar tarea
````
PUT /tasks/{id}
Headers:
X-User-Id: <id_usuario>
````

### 🗑️ Borrar tarea
````
DELETE /tasks/{id}
Headers:
X-User-Id: <id_usuario>
````
---

## 🔎 Filtros disponibles

Los filtros se aplican sobre `/tasks`:
````
GET /tasks?status=done
GET /tasks?priority=high
GET /tasks?scope=school
````
Pueden combinarse:
````
GET /tasks?status=pending&priority=low
````

---

## 🔐 Control de permisos OWNER

Cada petición debe incluir:
````
X-User-Id: <id_usuario>
````

La API garantiza:

- Un usuario **solo puede ver, editar o borrar sus propias tareas**.
- Si intenta acceder a una tarea ajena → **403 Forbidden**.

Ejemplo:
````
GET /tasks/12
X-User-Id: 3
````

Si la tarea pertenece al usuario 5:

```json
{ "error": "Forbidden" }
```
---
## 🧪 Pruebas manuales documentadas

### ✔️ Crear tarea
````
POST /tasks
````
Código esperado: 201

Devuelve la tarea con ID generado.

### ✔️ Listar tareas
````
GET /tasks
````
Devuelve solo las tareas del usuario del header.

### ✔️ Filtrar tareas
````
GET /tasks?priority=high
````
Devuelve solo tareas del usuario con prioridad alta.

### ✔️ Obtener tarea por ID
````
GET /tasks/{id}
````
Si pertenece al usuario → 200

Si NO pertenece → 403 Forbidden

### ✔️ Actualizar tarea
````
PUT /tasks/{id}
````
Código esperado: 200

### ✔️ Borrar tarea
````
DELETE /tasks/{id}
````
Código esperado: 204

### ✔️ Intento de acceso no autorizado
````
GET /tasks/{id} con usuario incorrecto
````
Código esperado: 403 Forbidden

## 🗄️ Tabla TASKS (SQL)
```
sql
CREATE TABLE TASKS (
    id BIGINT IDENTITY PRIMARY KEY,
    owner_user_id BIGINT NOT NULL,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(1000),
    due_date DATETIME,
    status VARCHAR(10),
    priority VARCHAR(6),
    created_at DATETIME DEFAULT GETDATE()
);
```
## 🧩 Notas técnicas
- Validaciones estrictas en el dominio (Task.java).

- DAO protegido contra inyección SQL mediante PreparedStatement.

- Repositorio implementado con JdbcTaskRepository.

- Filtros aplicados en SQL de forma segura.

- Control de permisos en el controlador antes de acceder al repositorio.


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

