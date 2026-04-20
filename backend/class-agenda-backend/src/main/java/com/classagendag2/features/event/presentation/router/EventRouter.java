package com.classagendag2.features.event.presentation.router;

import com.classagendag2.features.example.data.local.connection.DbConnectionFactory;
import com.classagendag2.features.event.data.local.dao.EventDao;
import com.classagendag2.features.event.data.repository.JdbcEventRepository;
import com.classagendag2.features.event.domain.repository.EventRepository;
import com.classagendag2.features.event.presentation.handlers.EventHandler;
import com.sun.net.httpserver.HttpServer;

import java.sql.SQLException;

public final class EventRouter {
    private EventRouter() {}

    public static void registerRoutes(HttpServer httpServer) {

        // 1. Crear la fábrica de conexiones
        DbConnectionFactory connectionFactory = new DbConnectionFactory();

        // 2.  Crear el Dao con la fábrica
        EventDao eventDao = null;
        try {
            eventDao = new EventDao(connectionFactory.open());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 3. Crear el repositorio con el DAO
        EventRepository eventRepository = new JdbcEventRepository(eventDao);

        // 4.  Registar la ruta en el DAO
        httpServer.createContext("/event/",new EventHandler(eventRepository));
    }
}
