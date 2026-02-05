# Grupo2 - ClassAgenda

Proyecto Intermodular de **1º DAM**  
Aplicación web de agenda colaborativa desarrollada sin frameworks.

---

## 👥 Equipo

- Alumno/a 1:  Beatriz
- Alumno/a 2:  José Manuel Ruiz Sojo (Chema)
- Alumno/a 3:  Alfonso Daniel Perucho Domínguez
- Alumno/a 4: Bruno Regueira Ayuso
- Alumno/a 5:

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

---

## 🧱 Arquitectura del proyecto

> Describir aquí la estructura de carpetas y la arquitectura utilizada.

Ejemplo:
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

---

## 🗄️ Base de datos

- Motor: SQL Server
- Tablas principales:
  - USERS
  - TASKS
  - EVENTS
  - TASK_SHARES
  - EVENT_SHARES

📌 **Pendiente**:  
- Esquema relacional  
- Diagrama E-R  
- Scripts SQL  

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

