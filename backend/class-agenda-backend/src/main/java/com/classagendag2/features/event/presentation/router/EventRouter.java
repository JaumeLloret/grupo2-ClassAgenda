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

        DbConnectionFactory connectionFactory = new DbConnectionFactory();

        EventDao eventDao;
        try {
            eventDao = new EventDao(connectionFactory.open());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        EventRepository eventRepository = new JdbcEventRepository(eventDao);

        httpServer.createContext("/event", new EventHandler(eventRepository));
        httpServer.createContext("/event/", new EventHandler(eventRepository));
    }
}