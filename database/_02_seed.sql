USE ClassAgenda;
GO

SET LANGUAGE English;

-- ============================
-- INSERT: USERS
-- ============================
INSERT INTO USERS ([name], email) VALUES
('Beatriz López', 'beatriz@loquesea.com'),
('Carlos Martínez', 'carlos@loquesea.com'),
('Ana Torres', 'ana@loquesea.com'),
('Javier Ruiz', 'javier@loquesea.com');
GO

-- =============================
-- INSERT: EVENTS
-- =============================
INSERT INTO [EVENTS] (owner_user_id, title, [description], start_at, end_at, event_type) VALUES
(1, 'Reunión de proyecto', 'Revició del avance del proyecto', '2025-02-20 10:00', '2025-02-20 11:00', 'meeting'),
(2, 'Clase de SQL', 'Sesión práctica de SQL Server', '2025-02-21 9:00', '2025-02-21 10:30', 'class'),
(1, 'Turoría', 'Tutoria con el profesor', '2025-02-22 12:00', '2025-02-22 12:30', 'reunión'),
(3, 'Examen de recuperación', 'Examen de recuperación de programación', '2025-03-01 18:00', '2025-03-01 19:30', 'examen');

-- ==============================
-- INSERT: TASKS
-- ==============================
INSERT INTO TASKS (owner_user_id, title, [description], due_date, [status], [priority]) VALUES
(1, 'Terminar Hito 2', 'Implementar base de datos en SQL Server', '2025-02-25', 'pending', 'high'),
(2, 'Preparar presentación', 'Diapositivas para el examen oral de inglés', '2026-02-25', 'in_prog', 'medium'),
(3, 'Estudiar SQL', 'Repasar claves primarias y foráneas', '2026-03-01', 'pending', 'high'),
(4, 'Subir práctica', 'Entrega de la práctica de programación', '2026-03-02', 'completed', 'low');

-- ===============================
--  INSERT: EVENT_SHARES
-- ===============================
INSERT INTO EVENT_SHARES (event_id, shared_with_user_id, permission) VALUES
(1, 2, 'read'),
(2, 3, 'edit'),
(3, 1, 'read'),
(4, 4, 'read');

-- ================================
-- INSERT:TASK_SHARES
-- ================================
INSERT INTO TASK_SHARES (task_id, shared_with_user_id, permission) VALUES
(1, 2, 'edit'),
(2, 3, 'read'),
(3, 1, 'read'),
(4, 4, 'edit');
