package com.classagendag2.features.task.presentation.handlers;

import com.classagendag2.features.task.domain.model.Task;
import com.classagendag2.features.task.domain.model.TaskStatus;
import com.classagendag2.features.task.domain.repository.TaskRepository;
import com.classagendag2.features.task.presentation.json.TaskJson;
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
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();

            Long userId = getUserId(httpExchange);
            if (userId == null) return;

            Long taskId = extractId(path);

            switch (method) {
                case "GET" -> handleGet(httpExchange, userId, taskId, query);
                case "POST" -> handlePost(httpExchange, userId);
                case "PUT" -> handlePut(httpExchange, userId, taskId);
                case "DELETE" -> handleDelete(httpExchange, userId, taskId);
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
    private void handleGet(HttpExchange ex, Long userId, Long taskId, String query) throws IOException {
        if (taskId != null) {
            var opt = taskRepository.findById(taskId);
            if (opt.isEmpty()) {
                sendError(ex, 404, "NOT_FOUND", "La tarea no existe");
                return;
            }

            var task = opt.get();
            if (!task.getOwnerId().equals(userId)) {
                sendError(ex, 404, "NOT_FOUND", "No tienes acceso a esta tarea");
                return;
            }

            sendJson(ex, 200, TaskJson.toJson(task));
            return;
        }

        // Filtro simple: ?status=PENDING
        if (query != null && query.contains("status=")) {
            String status = query.split("=")[1];
            var list = taskRepository.findByOwnerIdAndStatus(userId, TaskStatus.valueOf(status));
            sendJson(ex, 200, TaskJson.toJsonList(list));
            return;
        }

        var list = taskRepository.findByOwnerId(userId);
        sendJson(ex, 200, TaskJson.toJsonList(list));
    }
    private void handlePost(HttpExchange ex, Long userId) throws IOException {
        String body = readRequestBody(ex);
        TaskJson.ParsedTask data = TaskJson.fromJson(body);

        Task task = new Task(
                data.title(),
                data.description(),
                data.priority(),
                userId,
                data.dueDate()
        );


        Task saved = taskRepository.save(task);
        sendJson(ex, 201, TaskJson.toJson(saved));
    }
    private void handlePut(HttpExchange ex, Long userId, Long taskId) throws IOException {
        if (taskId == null) {
            sendError(ex, 400, "BAD_REQUEST", "Falta ID en la URL");
            return;
        }

        var opt = taskRepository.findById(taskId);
        if (opt.isEmpty()) {
            sendError(ex, 404, "NOT_FOUND", "La tarea no existe");
            return;
        }

        var existing = opt.get();
        if (!existing.getOwnerId().equals(userId)) {
            sendError(ex, 403, "FORBIDDEN", "No puedes modificar esta tarea");
            return;
        }

        String body = readRequestBody(ex);
        TaskJson.ParsedTask data = TaskJson.fromJson(body);

        //solo cambio de datos enviados.
        Task updated = new Task(
                existing.getId(),
                data.title() != null ? data.title() : existing.getTitle(),
                data.description() != null ? data.description() : existing.getDescription(),
                data.status() != null ? data.status() : existing.getStatus(),
                data.priority() != null ? data.priority() : existing.getPriority(),
                existing.getOwnerId(),
                data.dueDate() != null ? data.dueDate() : existing.getDueDate(),
                existing.getCreatedAt()
        );



        taskRepository.save(updated);
        sendJson(ex, 200, TaskJson.toJson(updated));
    }
    private void handleDelete(HttpExchange ex, Long userId, Long taskId) throws IOException {
        if (taskId == null) {
            sendError(ex, 400, "BAD_REQUEST", "Falta ID en la URL");
            return;
        }

        var opt = taskRepository.findById(taskId);
        if (opt.isEmpty()) {
            sendError(ex, 404, "NOT_FOUND", "La tarea no existe");
            return;
        }

        if (!opt.get().getOwnerId().equals(userId)) {
            sendError(ex, 403, "FORBIDDEN", "Solo el owner puede borrar");
            return;
        }

        taskRepository.deleteById(taskId);
        JsonResponses.sendJson(ex, 204, ""); // sin cuerpo
    }

// ---------------------------
// Helpers nuevos
// ---------------------------

    // Validar X-User-Id
    private Long getUserId(HttpExchange ex) throws IOException {
        String header = ex.getRequestHeaders().getFirst("X-User-Id");
        if (header == null) {
            sendError(ex, 400, "BAD_REQUEST", "Falta cabecera X-User-Id");
            return null;
        }
        try {
            return Long.parseLong(header);
        } catch (NumberFormatException e) {
            sendError(ex, 400, "BAD_REQUEST", "X-User-Id debe ser un entero");
            return null;
        }
    }

    // Extraer ID de /tasks/{id}
    private Long extractId(String path) {
        try {
            String[] parts = path.split("/");
            if (parts.length == 3) return Long.parseLong(parts[2]);
        } catch (Exception ignored) {}
        return null;
    }

    // Enviar error según contrato
    private void sendError(HttpExchange ex, int status, String code, String message) throws IOException {
        String json = """
    {
      "error": {
        "code": "%s",
        "message": "%s"
      }
    }
    """.formatted(code, message);
        JsonResponses.sendJson(ex, status, json);
    }

    // Enviar JSON normal
    private void sendJson(HttpExchange ex, int status, String json) throws IOException {
        JsonResponses.sendJson(ex, status, json);
    }

}
