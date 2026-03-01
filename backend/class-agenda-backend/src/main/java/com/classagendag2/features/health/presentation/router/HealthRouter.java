package com.classagendag2.features.health.presentation.router;

import com.classagendag2.features.health.presentation.handlers.HealthHandler;
import com.sun.net.httpserver.HttpServer;

public final class HealthRouter {
    private HealthRouter() {}

    public static void registerRoutes(HttpServer httpServer) {
        httpServer.createContext("/health", new HealthHandler());
    }
}
