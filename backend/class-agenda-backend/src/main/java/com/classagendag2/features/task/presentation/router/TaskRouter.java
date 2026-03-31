package com.classagendag2.features.task.presentation.router;

import com.classagendag2.features.example.data.local.connection.DbConnectionFactory;
import com.classagendag2.features.task.data.local.dao.TaskDao;
import com.classagendag2.features.task.data.repository.JdbcTaskRepository;
import com.classagendag2.features.task.domain.model.Task;
import com.classagendag2.features.task.domain.repository.TaskRepository;
import com.classagendag2.features.task.presentation.handlers.TaskHandler;
import com.sun.net.httpserver.HttpServer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class TaskRouter {
    private TaskRouter() {}

    public static void registerRoutes(HttpServer httpServer) {

        // 1. Crear la fábrica de conexiones
        DbConnectionFactory connectionFactory = new DbConnectionFactory();

        // 2.  Crear el Dao con la fábrica
        TaskDao taskDao = null;
        try {
            taskDao = new TaskDao(connectionFactory.open());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 3. Crear el repositorio con el DAO
        TaskRepository taskRepository = new JdbcTaskRepository(taskDao);

        // 4.  Registar la ruta en el DAO
        httpServer.createContext("/task/",new TaskHandler(taskRepository));
    }

}