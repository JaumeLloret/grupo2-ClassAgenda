package com.classagendag2.features.example.presentation.router;

import com.classagendag2.features.example.presentation.handlers.ExampleIntroHandler;
import com.sun.net.httpserver.HttpServer;

public final class ExampleRouter {
    private ExampleRouter() {}

    public static void registerRoutes(HttpServer httpServer) {
        httpServer.createContext("/example/intro", new ExampleIntroHandler());
    }
}
