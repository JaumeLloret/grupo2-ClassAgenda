package com.classagendag2.features.user.presentation.router;

import com.classagendag2.features.example.data.local.connection.DbConnectionFactory;
import com.classagendag2.features.user.data.local.dao.UserDao;
import com.classagendag2.features.user.data.repository.JdbcUserRepository;
import com.classagendag2.features.user.domain.repository.UserRepository;
import com.classagendag2.features.user.presentation.handlers.UserHandler;
import com.sun.net.httpserver.HttpServer;

import java.sql.SQLException;

public final class UserRouter {
    private UserRouter() {}

    public static void registerRoutes(HttpServer httpServer) {

        // 1. Crear la fábrica de conexiones
        DbConnectionFactory connectionFactory = new DbConnectionFactory();

        // 2.  Crear el Dao con la fábrica
        UserDao userDao = null;
        try {
            userDao = new UserDao(connectionFactory.open());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 3. Crear el repositorio con el DAO
        UserRepository userRepository = new JdbcUserRepository(userDao);

        // 4.  Registar la ruta en el DAO
        httpServer.createContext("/user/",new UserHandler(userRepository));
    }
}
