package com.classagendag2.features.task.presentation.handlers;

import com.classagendag2.features.task.domain.model.Task;
import com.classagendag2.features.task.domain.model.TaskPriority;
import com.classagendag2.features.task.domain.model.TaskStatus;
import com.classagendag2.features.task.domain.repository.TaskRepository;
import com.classagendag2.shared.http.JsonResponses;
import com.classagendag2.shared.http.ResponseContract;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public final class TaskHandler implements HttpHandler {

    private final TaskRepository taskRepository;

    // Inyectamos el Repositorio desde el exterior
    public TaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // 1. Extraemos quién está haciendo la petición desde la cabecera de seguridad
            String userIdHeader = exchange.getRequestHeaders().getFirst("X-User-Id");
            if (userIdHeader == null || userIdHeader.isBlank()) {
                JsonResponses.sendJson(exchange, 401, ResponseContract.errorJson("No autorizado", "Falta la cabecera X-User-Id"));
                return;
            }
            Long requestingUserId = Long.parseLong(userIdHeader);

            // 2. Enrutamos según el verbo HTTP
            String method = exchange.getRequestMethod();
            switch (method) {
                case "POST" -> handlePost(exchange, requestingUserId);
                case "GET" -> handleGet(exchange, requestingUserId);
                case "PUT" -> handlePut(exchange, requestingUserId);
                case "DELETE" -> handleDelete(exchange, requestingUserId);
                default ->
                        JsonResponses.sendJson(exchange, 405, ResponseContract.errorJson("Método no permitido", null));
            }
        } catch (SecurityException e) {
            JsonResponses.sendJson(exchange, 403, ResponseContract.errorJson("Prohibido", e.getMessage()));
        } catch (Exception e) {
            JsonResponses.sendJson(exchange, 500, ResponseContract.errorJson("Error interno", e.getMessage()));
        }
    }

    private void handlePost(HttpExchange exchange, Long requestingUserId) throws Exception {
        // 1. Leemos el cuerpo de la petición (JSON crudo) a texto
        InputStream is = exchange.getRequestBody();
        String jsonBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        // (En un entorno profesional usaríamos Jackson ObjectMapper aquí.
        // Para mantenernos libres de dependencias, simularemos la extracción manual o usaremos un parser propio)
        String title = extractJsonField(jsonBody, "title");
        String description = extractJsonField(jsonBody, "description");
        String priorityStr = extractJsonField(jsonBody, "priority");

        // 2. Convertimos a nuestro modelo de Dominio puro. ¡Fijaos que le pasamos el ID del usuario directamente!
        Task newTask = new Task(
                title,
                description,
                TaskPriority.valueOf(priorityStr.toUpperCase()),
                requestingUserId
        );

        // 3. Guardamos a través del repositorio y devolvemos la respuesta al cliente
        Task savedTask = taskRepository.save(newTask);

        // Creamos un JSON de respuesta manual para este ejemplo
        String responseData = String.format(
                "{\"id\":%d, \"title\":\"%s\", \"status\":\"%s\"}",
                savedTask.getId(), savedTask.getTitle(), savedTask.getStatus().name()
        );

        JsonResponses.sendJson(exchange, 201, ResponseContract.okJson(responseData));
    }

    // Método auxiliar ultra-simplificado para extraer valores de un JSON nativamente.
    // OJO: En la empresa real se usa librerías como Jackson.
    private String extractJsonField(String json, String field) {
        String key = "\"" + field + "\":";
        int start = json.indexOf(key);
        if (start == -1) return "";
        start += key.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return json.substring(start, end).replace("\"", "").trim();
    }

    private void handleGet(HttpExchange exchange, Long requestingUserId) throws Exception {
        // Obtenemos todo lo que hay en la URL después de la interrogación (?)
        String queryParams = exchange.getRequestURI().getQuery();

        List<Task> tasks;

        // Comprobamos si el usuario ha enviado un filtro de estado
        if (queryParams != null && queryParams.contains("status=")) {
            // Parseamos el valor (ej. status=PENDING -> extraemos PENDING)
            String statusParam = queryParams.split("status=")[1].split("&")[0];
            TaskStatus statusToFilter = TaskStatus.valueOf(statusParam.toUpperCase());

            // Llamamos a nuestro Repositorio usando el filtro múltiple (Dueño + Estado)
            tasks = taskRepository.findByOwnerIdAndStatus(requestingUserId, statusToFilter);
        } else {
            // Si no hay filtros, le devolvemos todas SUS tareas
            tasks = taskRepository.findByOwnerId(requestingUserId);
        }

        // Transformamos la lista Java a formato JSON
        StringBuilder jsonArray = new StringBuilder("[");
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            jsonArray.append(String.format(
                    "{\"id\":%d, \"title\":\"%s\", \"status\":\"%s\", \"priority\":\"%s\"}",
                    t.getId(), t.getTitle(), t.getStatus().name(), t.getPriority().name()
            ));
            if (i < tasks.size() - 1) jsonArray.append(",");
        }
        jsonArray.append("]");

        JsonResponses.sendJson(exchange, 200, ResponseContract.okJson(jsonArray.toString()));
    }

    private void handleDelete(HttpExchange exchange, Long requestingUserId) throws Exception {
        String queryParams = exchange.getRequestURI().getQuery();
        if (queryParams == null || !queryParams.contains("id=")) {
            throw new IllegalArgumentException("Debe proporcionar el ID de la tarea a borrar (?id=X)");
        }

        Long taskId = Long.parseLong(queryParams.split("id=")[1].split("&")[0]);

        // 1. Buscamos la tarea en la Base de Datos
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            JsonResponses.sendJson(exchange, 404, ResponseContract.errorJson("No encontrado", "La tarea no existe"));
            return;
        }

        // 2. LA BARRERA DE SEGURIDAD
        // Si el usuario 2 intenta borrar la tarea del usuario 5, este método lanzará una excepción y abortará el proceso.
        Task taskToModify = optionalTask.get();
        taskToModify.validateIsOwnedBy(requestingUserId);

        // 3. Ejecución autorizada
        taskRepository.deleteById(taskId);
        JsonResponses.sendJson(exchange, 200, ResponseContract.okJson("{\"message\":\"Tarea borrada exitosamente\"}"));
    }

    private void handlePut(HttpExchange exchange, Long requestingUserId) throws Exception {
        // Implementación similar: Buscar, Validar Propiedad (validateIsOwnedBy), Leer JSON y Guardar (taskRepository.save).
        // (La lógica es un híbrido entre el POST y el DELETE. Se deja como ejercicio final ensamblarla).
        JsonResponses.sendJson(exchange, 200, ResponseContract.okJson("{\"message\":\"Endpoint PUT listo para implementar\"}"));
    }
}

