package com.classagendag2.features.task.presentation.handlers;

import com.classagendag2.features.task.domain.repository.TaskRepository;
import com.classagendag2.shared.http.JsonResponses;
import com.classagendag2.shared.http.ResponseContract;
import com.classagendag2.shared.http.helpers.JsonEscaper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public final class TaskHandler  implements HttpHandler{
    private final TaskRepository taskRepository;

    public TaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String httpMethod = httpExchange.getRequestMethod();
            switch (httpMethod) {
                //case "GET" -> sendOk(httpExchange, "GET okk");
                case "GET" -> sendOk(httpExchange, "GET okk");//Usamos el verbo GET (Dame información, solo quiero consultar).
                case "POST" -> sendOk(httpExchange, "POST okk"); //Usamos el verbo POST (Te envío datos nuevos para que los guardes)
                case "PUT" -> sendOk(httpExchange, "PUT user"); // Usamos PUT (Reemplaza este dato por completo)
                case "PATCH" -> sendOk(httpExchange, "PATCH user"); //PATCH (Modifica solo una pequeña parte del dato).
                case "DELETE" -> sendOk(httpExchange, "DELETE okk"); //Usamos el verbo DELETE (Elimina este dato de la base de datos).
                default -> sendMethodNotAllowed(httpExchange);
            }
        } catch (Exception exception) {
            sendServerError(httpExchange, exception.getMessage());
        }
    }

    // __________________________
    // Metodos auxiliares (Aules)
    // __________________________
    private void sendOk(HttpExchange httpExchange, String message) throws IOException {
        String receivedBody = readRequestBody(httpExchange);
        String dataJson = "{"
                + "\"endpoint\":\"task\","
                + "\"method\":\"" + httpExchange.getRequestMethod() + "\","
                + "\"message\":\"" + JsonEscaper.escape(message) + "\","
                + "\"receivedBody\":" + toNullableJsonString(receivedBody)
                + "}";
        String responseJson = ResponseContract.okJson(dataJson);
        JsonResponses.sendJson(httpExchange, 200, responseJson);
    }

    private String readRequestBody(HttpExchange httpExchange) throws IOException {
        InputStream requestBodyStream = httpExchange.getRequestBody();
        if (requestBodyStream == null) return null;
        byte[] bodyBytes = requestBodyStream.readAllBytes();
        if (bodyBytes.length == 0) return null;
        return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    private String toNullableJsonString(String rawValue) {
        if (rawValue == null) return "null";
        return "\"" + JsonEscaper.escape(rawValue) + "\"";
    }

    private void sendMethodNotAllowed(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().set("Allow", "GET, POST, PUT, PATCH, DELETE");
        String responseJson = ResponseContract.errorJson("Method not allowed", null);
        JsonResponses.sendJson(httpExchange, 405, responseJson);
    }

    private void sendServerError(HttpExchange httpExchange, String errorDetails) throws IOException {
        String responseJson = ResponseContract.errorJson("Internal server error", errorDetails);
        JsonResponses.sendJson(httpExchange, 500, responseJson);
    }

}
