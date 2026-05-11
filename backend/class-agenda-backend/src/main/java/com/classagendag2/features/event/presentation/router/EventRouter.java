package com.classagendag2.features.event.presentation.router;

import com.classagendag2.features.event.data.local.dao.EventShareDao;
import com.classagendag2.features.event.presentation.handlers.EventShareHandler;
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

        EventShareDao eventShareDao;
        try {
            eventShareDao = new EventShareDao(connectionFactory.open());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        httpServer.createContext("/event", new EventHandler(eventRepository, eventShareDao));
        httpServer.createContext("/event/", new EventHandler(eventRepository, eventShareDao));
        httpServer.createContext("/event/share", new EventShareHandler(eventRepository, eventShareDao));

    }
}