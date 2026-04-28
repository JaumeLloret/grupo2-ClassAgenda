package com.classagendag2.features.task.data.repository;

import com.classagendag2.features.example.data.local.connection.DbConnectionFactory;
import com.classagendag2.features.task.data.local.dao._TaskDao;
import com.classagendag2.features.task.domain.model._Task;
import com.classagendag2.features.task.domain.model.TaskPriority;
import com.classagendag2.features.task.domain.model.TaskStatus;
import com.classagendag2.features.user.data.local.dao.UserDao;
import com.classagendag2.features.user.data.repository.JdbcUserRepository;
import com.classagendag2.features.user.domain.model.User;
import com.classagendag2.shared.config.DbConfig;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcTaskRepositoryIT {

    @Test
    void executeFullTaskLifecycleAndVerifyAuthorization() throws Exception {
        // ESCUDO DE PROTECCIÓN PARA CI/CD (GitHub Actions)
        // Intentamos leer las variables de entorno. Si la base de datos no está
        // configurada en el servidor, usamos Assumptions.abort para cancelar el test
        // amistosamente sin provocar un fallo rojo que bloquee la subida de código.
        try { DbConfig.url(); } catch (Exception e) {
            Assumptions.abort("Interrupción controlada: Base de datos no configurada en el entorno.");
        }

        // === RAÍZ DE COMPOSICIÓN (Dependency Injection) ===
        DbConnectionFactory factory = new DbConnectionFactory();

        // 1. El Cliente (este Test) abre y gestiona LA ÚNICA conexión a la base de datos.
        try (Connection sharedConnection = factory.open()) {

            // 2. INYECCIÓN: Suministramos exactamente la MISMA conexión a ambos DAOs.
            // Esto es lo que permite que en el futuro puedan realizar Transacciones conjuntas (todo o nada).
            UserDao userDao = new UserDao(sharedConnection);
            _TaskDao taskDao = new _TaskDao(sharedConnection);

            // 3. Ensamblamos los Repositorios de alto nivel inyectando los DAOs
            JdbcUserRepository userRepository = new JdbcUserRepository(userDao);
            _JdbcTaskRepository taskRepository = new _JdbcTaskRepository(taskDao);
            // === FASE 1: INTEGRIDAD REFERENCIAL (Trampa de la Clave Foránea) ===
            // Para poder crear una tarea en SQL Server, necesitamos primero un dueño real insertado.
            // Usamos System.currentTimeMillis() para generar un email distinto en cada ejecución
            // y evitar chocar repetidamente con la regla UNIQUE de nuestra tabla de usuarios.
            String uniqueEmail = "gestor_" + System.currentTimeMillis() + "@empresa.com";
            User owner = new User("Gestor Principal", uniqueEmail);
            User savedOwner = userRepository.save(owner);
            Long currentOwnerId = savedOwner.getId();

            // === FASE 2: OPERACIÓN CREATE ===
            _Task newTask = new _Task("Revisión de Arquitectura", "Analizar patrón DI", TaskPriority.HIGH, currentOwnerId, "2026-01-01");
            _Task savedTask = taskRepository.save(newTask);

            assertNotNull(savedTask.getId(), "El motor SQL debe proveer un identificador primario numérico.");
            assertEquals(TaskStatus.PENDING, savedTask.getStatus(), "Toda instancia de tarea nueva inicia en estado PENDING.");

            // === FASE 3: VALIDACIÓN DE FILTRADO MÚLTIPLE (AND) ===
            List<_Task> pendingTasks = taskRepository.findByOwnerIdAndStatus(currentOwnerId, TaskStatus.PENDING);
            assertEquals(1, pendingTasks.size(), "El filtro debe recuperar exactamente la tarea recién insertada.");
            assertEquals(savedTask.getId(), pendingTasks.get(0).getId());

            // === FASE 4: REGLA DE NEGOCIO Y OPERACIÓN UPDATE ===
            // Verificamos que nuestro código de Dominio aprueba los permisos antes de hacer un cambio
            assertDoesNotThrow(() -> savedTask.validateIsOwnedBy(currentOwnerId));

            _Task taskToUpdate = new _Task(
                    savedTask.getId(), // Al proveer el mismo ID, el repositorio sabrá que debe hacer un UPDATE
                    "Revisión Finalizada",
                    savedTask.getDescription(),
                    TaskStatus.COMPLETED, // Mutamos el estado de la tarea a completada
                    savedTask.getPriority(),
                    savedTask.getOwnerId(),
                    savedTask.getDueDate(),
                    savedTask.getCreatedAt()
            );
            taskRepository.save(taskToUpdate);

            // Vamos a la base de datos a comprobar que el UPDATE ha sido efectivo
            Optional<_Task> verifiedTask = taskRepository.findById(savedTask.getId());
            assertTrue(verifiedTask.isPresent());
            assertEquals(TaskStatus.COMPLETED, verifiedTask.get().getStatus());

            // === FASE 5: OPERACIÓN DELETE Y LIMPIEZA ===
            taskRepository.deleteById(savedTask.getId());
            assertTrue(taskRepository.findById(savedTask.getId()).isEmpty(), "La tarea debe ser eliminada físicamente del disco.");
        }
        // Al cerrarse la llave de este bloque 'try', la conexión compartida a la BD se clausura de forma segura.
    }
}