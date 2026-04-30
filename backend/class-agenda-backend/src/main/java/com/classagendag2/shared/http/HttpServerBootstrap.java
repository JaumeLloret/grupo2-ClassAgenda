package com.classagendag2.shared.http;

import com.classagendag2.features.task.presentation.router.TaskRouter;
import com.classagendag2.features.user.presentation.router.UserRouter;
import com.classagendag2.features.event.presentation.router.EventRouter;
import com.classagendag2.shared.config.ServerConfig;
import com.classagendag2.shared.http.handlers.HealthHandler;
import com.classagendag2.features.example.presentation.router.ExampleRouter;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public final class HttpServerBootstrap {
    public void start() throws Exception {
        // 1.Preguntamos porqué puerto debemos abrir el servidor
        int serverPort = ServerConfig.port();

        // 2.Creamos la dirección física (IP de la máquina + Puerto
        InetSocketAddress serverAddress = new InetSocketAddress(serverPort);
        HttpServer httpServer = HttpServer.create(serverAddress, 0);

        // 3.REGISTRAMOS LA RUTA "Si alguien pide /health, pásale la llamada al HealthHandler"
        httpServer.createContext("/health", new HealthHandler());
        EventRouter.registerRoutes(httpServer);
        TaskRouter.registerRoutes(httpServer);
        UserRouter.registerRoutes(httpServer);
        ExampleRouter.registerRoutes(httpServer);

        // 4.Encendemos el servidor para que empiece a escuchar infinitamente
        httpServer.start();
        System.out.println("ClassAgenda API running on http://localhost:" + serverPort);
    }

    public HttpServer startAndReturnServer() throws Exception {
        int configuredPort = ServerConfig.port();
        InetSocketAddress serverAddress = new InetSocketAddress(configuredPort);
        HttpServer httpServer = HttpServer.create(serverAddress, 0);

        httpServer.createContext("/health", new HealthHandler());
        EventRouter.registerRoutes(httpServer);
        TaskRouter.registerRoutes(httpServer);
        UserRouter.registerRoutes(httpServer);
        ExampleRouter.registerRoutes(httpServer);

        httpServer.start();
        return httpServer;
    }
}