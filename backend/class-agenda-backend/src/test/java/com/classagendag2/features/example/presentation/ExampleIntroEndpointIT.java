package com.classagendag2.features.example.presentation;

import com.classagendag2.shared.http.HttpServerBootstrap;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExampleIntroEndpointIT {

    //El aviso amarillo en IntelliJ nos indica que HttpClient debería cerrarse, pero al hacerlo
    //'private static final' lo reutilizamos para toda la clase, lo cual es más eficiente y limpio aquí
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @Test
    void getExampleIntroReturnsJsonAnd200() throws Exception {
        System.setProperty("CLASSAGENDA_PORT", "0"); //El puerto 0 le dice a Java: Usa el primero que este libre
        HttpServerBootstrap bootstrap = new HttpServerBootstrap();

        //Modifica tu HttpServerBootstarp para que start() devuelve el objeto HttpServer, así podremos probarlo aquí
        HttpServer httpServer = bootstrap.startAndReturnServer();
        int realPort = httpServer.getAddress().getPort();

        try {
            URI endpointUri = URI.create("http://localhost:" + realPort + "/example/intro");
            HttpRequest httpRequest = HttpRequest.newBuilder(endpointUri).GET().build();
            HttpResponse<String> httpResponse = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, httpResponse.statusCode());
            assertTrue(httpResponse.body().contains("\"status\":\"ok\""));
        } finally {
            httpServer.stop(0); //El bloque 'finally asegura que el servidor SIEMPRE se apague, incluso si el test falla
        }
    }
}
