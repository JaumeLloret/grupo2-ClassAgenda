<p align="center">
  <img src="docs/Header.png" alt="ClassAgenda Header">
</p>

---

## 👥 Equipo

- Alumno/a 1:  Beatriz
- Alumno/a 2:  José Manuel Ruiz Sojo (Chema)
- Alumno/a 3:  Alfonso Daniel Perucho Domínguez
- Alumno/a 4: Bruno Regueira Ayuso
- Alumno/a 5:

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


| Capa               | Descripción |
|--------------------|-------------|
| **/api**           | Controladores REST. Reciben las peticiones HTTP, validan datos y llaman a los casos de uso de la capa *application*. |
| **/presentation**  | Cliente web: HTML, CSS y JavaScript. Interfaz de usuario que consume la API. |
| **/application**   | Casos de uso. Contienen la lógica de orquestación entre dominio e infraestructura. Aplican reglas de aplicación y coordinan operaciones. |
| **/domain**        | Entidades, modelos y lógica de negocio pura. No depende de otras capas. Representa el corazón del sistema (Task, Event, User, Permission…). |
| **/infrastructure**| Implementaciones técnicas: repositorios JDBC, conexión a SQL Server, mapeadores y adaptadores. Todo lo dependiente de tecnología concreta. |
| **/client**        | Código del cliente web si se separa de *presentation*. Puede contener componentes, servicios o scripts organizados por módulos. |
| **/database**      | Scripts SQL, diagramas E‑R, esquema relacional y datos de prueba. Incluye la definición de tablas y la estructura de la base de datos. |
| **/docs**          | Documentación del proyecto: decisiones técnicas, endpoints, pruebas, evidencias, configuración de la máquina virtual y material adicional. |

```
/api
  /presentation
  /application
  /domain
  /infrastructure
/client
/database
/docs
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
  * [01_schema_sql](https://github.com/JaumeLloret/grupo2-ClassAgenda/blob/feature/database/database/01_schema.sql)
  * [02_seed_sql](https://github.com/JaumeLloret/grupo2-ClassAgenda/blob/feature/database/database/02_seed.sql)
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

📌 **Pendiente**:  
- Listado de endpoints
- Ejemplos de peticiones y respuestas
- Contratos JSON

---

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

