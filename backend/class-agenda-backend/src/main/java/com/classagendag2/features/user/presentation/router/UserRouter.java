package com.classagendag2.features.user.presentation.router;

import com.classagendag2.features.user.presentation.handlers.UserHandler;
import com.sun.net.httpserver.HttpServer;

public final class UserRouter {
    private UserRouter() {}

    public static void registerRoutes(HttpServer httpServer) {
        httpServer.createContext("/user", new UserHandler());
    }
}
