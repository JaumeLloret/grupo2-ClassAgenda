CREATE DATABASE ClassAgenda;
GO

USE ClassAgenda;
GO

-- ======================================
-- TABLE: USERS
-- ======================================
CREATE TABLE USERS (
	id INT IDENTITY(1,1) PRIMARY KEY,
	[name] VARCHAR(80) NOT NULL,
	email VARCHAR(255) NOT NULL UNIQUE,
	created_at DATETIME NOT NULL DEFAULT GETDATE()
);

-- ======================================
-- TABLE: EVENTS
-- ======================================
CREATE TABLE [EVENTS] (
	id INT IDENTITY(1,1) PRIMARY KEY,
	owner_user_id INT NOT NULL,
	title VARCHAR(120) NOT NULL,
	[description] VARCHAR(1000),
	start_at DATETIME,
	end_at DATETIME,
	event_type VARCHAR(12) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT GETDATE(),

	CONSTRAINT fk_events_user
		FOREIGN KEY (owner_user_id) REFERENCES USERS(id)
);

-- ========================================
-- TABLE: TASKS
-- ========================================
CREATE TABLE TASKS (
	id INT IDENTITY(1,1) PRIMARY KEY,
	owner_user_id INT NOT NULL,
	title VARCHAR(120) NOT NULL,
	[description] VARCHAR(1000),
	due_date DATE,
	[status] VARCHAR(10) NOT NULL,
	[priority] VARCHAR(6) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT GETDATE(),

	CONSTRAINT fk_tasks_user
		FOREIGN KEY (owner_user_id) REFERENCES USERS(id)
);

-- =========================================
-- TABLE: EVENT_SHARES
-- =========================================
CREATE TABLE EVENT_SHARES (
	event_id INT NOT NULL,
	shared_with_user_id INT NOT NULL,
	permission VARCHAR(6) NOT NULL,
	shared_at DATETIME NOT NULL DEFAULT GETDATE(),

	PRIMARY KEY (event_id, shared_with_user_id),

	CONSTRAINT fk_eventshares_event
		FOREIGN KEY (event_id) REFERENCES [EVENTS](id),

	CONSTRAINT fk_eventshares_user
		FOREIGN KEY (shared_with_user_id) REFERENCES USERS(id)
);

-- ===========================================
-- TABLE: TASK_SHARES
-- ===========================================
CREATE TABLE TASK_SHARES (
	task_id INT NOT NULL,
	shared_with_user_id INT NOT NULL,
	permission VARCHAR(6) NOT NULL,
	shared_at DATETIME NOT NULL DEFAULT GETDATE(),

	PRIMARY KEY (task_id, shared_with_user_id),

	CONSTRAINT fk_taskshares_task
		FOREIGN KEY (task_id) REFERENCES TASKS(id),

	CONSTRAINT fk_taskshares_user
		FOREIGN KEY (shared_with_user_id) REFERENCES USERS(id)
);
