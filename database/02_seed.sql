USE ClassAgenda;
GO

SET LANGUAGE English;

-- ======================================
-- USERS (5 registros)
-- ======================================
INSERT INTO USERS (name, email) VALUES
('Ana López', 'ana.lopez@email.com'),
('Carlos Pérez', 'carlos.perez@email.com'),
('María García', 'maria.garcia@email.com'),
('Juan Torres', 'juan.torres@email.com'),
('Lucía Fernández', 'lucia.fernandez@email.com');

-- ======================================
-- EVENTS (6 registros)
-- ======================================
INSERT INTO EVENTS (title, description, status, priority, owner_id, created_at) VALUES
('Reunión de proyecto', 'Revisión semanal del proyecto', 'OPEN', 'HIGH', 1, GETDATE()),
('Clase de bases de datos', 'Clase sobre SQL Server', 'OPEN', 'MEDIUM', 2, GETDATE()),
('Entrega de informe', 'Entrega final del informe del proyecto', 'OPEN', 'HIGH', 3, GETDATE()),
('Reunión con cliente', 'Presentación de avances', 'DONE', 'HIGH', 1, GETDATE()),
('Planificación semanal', 'Organizar tareas de la semana', 'OPEN', 'LOW', 4, GETDATE()),
('Taller de programación', 'Taller práctico de backend', 'OPEN', 'MEDIUM', 5, GETDATE());

-- ======================================
-- TASKS (7 registros)
-- ======================================
INSERT INTO TASKS (owner_user_id, title, description, due_date, status, priority) VALUES
(1, 'Preparar presentación', 'Preparar diapositivas del proyecto', '2026-03-15', 'OPEN', 'HIGH'),
(2, 'Estudiar SQL', 'Repasar joins y subqueries', '2026-03-12', 'OPEN', 'MED'),
(3, 'Corregir informe', 'Revisar errores de formato', '2026-03-14', 'OPEN', 'HIGH'),
(4, 'Enviar correos', 'Contactar con el equipo', '2026-03-10', 'DONE', 'LOW'),
(5, 'Actualizar repositorio', 'Subir últimos cambios', '2026-03-11', 'OPEN', 'MED'),
(1, 'Revisar tareas', 'Revisar progreso del equipo', '2026-03-13', 'OPEN', 'MED'),
(2, 'Preparar ejercicios', 'Ejercicios para la clase', '2026-03-16', 'OPEN', 'HIGH');

-- ======================================
-- EVENT_SHARES (5 registros)
-- ======================================
INSERT INTO EVENT_SHARES (event_id, shared_with_user_id, permission) VALUES
(1, 2, 'EDIT'),
(1, 3, 'VIEW'),
(2, 4, 'VIEW'),
(3, 1, 'EDIT'),
(5, 2, 'VIEW');

-- ======================================
-- TASK_SHARES (5 registros)
-- ======================================
INSERT INTO TASK_SHARES (task_id, shared_with_user_id, permission) VALUES
(1, 2, 'EDIT'),
(2, 3, 'VIEW'),
(3, 4, 'VIEW'),
(4, 1, 'EDIT'),
(5, 3, 'VIEW');
