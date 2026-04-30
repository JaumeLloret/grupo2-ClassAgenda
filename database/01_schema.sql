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
CREATE TABLE EVENTS (
    id INT IDENTITY(1,1) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NULL,
    status VARCHAR(20) NULL,
    priority VARCHAR(20) NULL,
    location VARCHAR(255) NULL,
    end_at DATETIME NOT NULL,
    owner_id INT NOT NULL,
    created_at DATETIME NOT NULL,

    CONSTRAINT FK_Task_User FOREIGN KEY (owner_id) REFERENCES USERS(id)
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
