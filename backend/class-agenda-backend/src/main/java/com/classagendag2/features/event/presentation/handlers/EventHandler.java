package com.classagendag2.features.event.presentation.handlers;

import com.classagendag2.features.event.data.local.dao.EventShareDao;
import com.classagendag2.features.event.domain.model.Event;
import com.classagendag2.features.event.domain.model.PermissionLevel;
import com.classagendag2.features.event.domain.repository.EventRepository;
import com.classagendag2.shared.http.JsonResponses;
import com.classagendag2.shared.http.ResponseContract;
import com.classagendag2.shared.http.helpers.JsonEscaper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class EventHandler implements HttpHandler{

    private final EventShareDao eventShareDao;

    public EventHandler(EventRepository eventRepository, EventShareDao eventShareDao) {
        this.eventRepository = eventRepository;
        this.eventShareDao = eventShareDao;
    }

    private final EventRepository eventRepository;
    private Long eventId;


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        //lectura del ownerId para el POST
        String ownerHeader = httpExchange.getRequestHeaders().getFirst("X-User-Id");
        Long ownerId = Long.parseLong(ownerHeader);

        try {
            String httpMethod = httpExchange.getRequestMethod();
            switch (httpMethod) {
                case "GET" -> handleGet(httpExchange, ownerId); //Usamos el verbo GET (Dame información, solo quiero consultar).
                case "PUT" -> handlePut(httpExchange, ownerId);
                case "POST" -> handlePost(httpExchange, ownerId);
                case "PATCH" -> sendOk(httpExchange, "PATCH event"); //PATCH (Modifica solo una pequeña parte del dato).
                case "DELETE" -> sendOk(httpExchange, "DELETE event"); //Usamos el verbo DELETE (Elimina este dato de la base de datos).
                default -> sendMethodNotAllowed(httpExchange);
            }
        } catch (Exception exception) {
            sendServerError(httpExchange, exception.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, Long ownerId) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // GET /event → listar eventos
        if (path.equals("/event")) {
            var events = eventRepository.findAllByOwner(ownerId);
            JsonResponses.sendJson(exchange, 200, ResponseContract.okJson(eventsListToJson(events)));
            return;
        }

        // GET /event/{id}
        if (path.startsWith("/event/")) {
            String[] parts = path.split("/");
            if (parts.length != 3) {
                sendError(exchange, 400, "Formato de URL inválido");
                return;
            }

            long eventId;
            try {
                eventId = Long.parseLong(parts[2]);
            } catch (NumberFormatException e) {
                sendError(exchange, 400, "ID inválido");
                return;
            }

            var eventOpt = eventRepository.findById(eventId);

            if (eventOpt.isEmpty()) {
                sendError(exchange, 404, "Evento no encontrado");
                return;
            }

            Event event = eventOpt.get();

            if (event.getOwner_id() != ownerId) {
                sendError(exchange, 403, "No tienes permiso para ver este evento");
                return;
            }

            JsonResponses.sendJson(exchange, 200, ResponseContract.okJson(eventToJson(event)));
            return;
        }

        sendError(exchange, 404, "Ruta no encontrada");
    }

    private void handlePut(HttpExchange exchange, Long requestingUserId) throws Exception {

        // 1. Extraer ID del evento desde la URL
        String path = exchange.getRequestURI().getPath();
        Long eventId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));

        // 2. Buscar el evento en BD
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // 3. Validación de permisos
        boolean hasAccess = false;

        // 3.1 ¿Es el dueño?
        if (Objects.equals(event.getOwner_id(), requestingUserId)) {
            hasAccess = true;
        } else {
            // 3.2 ¿Tiene permiso EDIT?
            Optional<PermissionLevel> perm =
                    eventShareDao.getPermissionLevel(eventId, requestingUserId);

            if (perm.isPresent() && perm.get() == PermissionLevel.EDIT) {
                hasAccess = true;
            }
        }

        if (!hasAccess) {
            sendError(exchange, 403, "No tienes permisos suficientes para editar este evento.");
            return;
        }

        // 4. Leer body
        String json = readRequestBody(exchange);

        // 5. Extraer campos usando tu propio método extractField
        String title = extractField(json, "title");
        String description = extractField(json, "description");
        String location = extractField(json, "location");
        String rawStart = extractField(json, "startAt");
        String rawEnd = extractField(json, "endAt");

        LocalDateTime startAt = LocalDateTime.parse(rawStart);
        LocalDateTime endAt = LocalDateTime.parse(rawEnd);

        // 6. Construir evento actualizado usando tu constructor real
        Event updatedEvent = new Event(
                event.getId(),
                title,
                description,
                event.getStatus(),
                event.getPriority(),
                location,
                startAt,
                endAt,
                event.getOwner_id(),
                event.getCreated_at()
        );

        // 7. Guardar cambios
        eventRepository.update(updatedEvent);

        // 8. Responder OK
        JsonResponses.sendJson(exchange, 200, ResponseContract.okJson("{\"message\":\"Evento actualizado correctamente\"}"));
    }



    private String eventToJson(Event e) {
        return """
    {
      "id": %d,
      "title": "%s",
      "description": "%s",
      "location": "%s",
      "startAt": "%s",
      "endAt": "%s",
      "ownerId": %d,
      "createdAt": "%s"
    }
    """.formatted(
                e.getId(),
                JsonEscaper.escape(e.getTitle()),
                JsonEscaper.escape(e.getDescription()),
                JsonEscaper.escape(e.getLocation()),
                e.getStart_at(),
                e.getEnd_at(),
                e.getOwner_id(),
                e.getCreated_at()
        );
    }


    private String eventsListToJson(List<Event> list) {
        return list.stream()
                .map(this::eventToJson)
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }




    private String extractField(String json, String fieldName) {
        json = json.replace("\n", "").replace("\r", "").trim();

        String search = "\"" + fieldName + "\"";

        int index = json.indexOf(search);
        if (index == -1) return null;

        int colonIndex = json.indexOf(":", index);
        if (colonIndex == -1) return null;

        // Saltar espacios
        int i = colonIndex + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }

        // Debe empezar con comillas
        if (json.charAt(i) != '\"') return null;

        int firstQuote = i;
        int secondQuote = json.indexOf("\"", firstQuote + 1);

        if (secondQuote == -1) return null;

        return json.substring(firstQuote + 1, secondQuote);
    }



    private void sendError(HttpExchange exchange, int status, String message) throws IOException {
        String json = ResponseContract.errorJson(message, null);
        JsonResponses.sendJson(exchange, status, json);
    }


    private void handlePost(HttpExchange exchange, Long owner_id) throws IOException {
        try {
            String json = readRequestBody(exchange);
            String rawStart = extractField(json, "startAt");
            String rawEnd = extractField(json, "endAt");

            LocalDateTime start_at = LocalDateTime.parse(rawStart);
            LocalDateTime end_at = LocalDateTime.parse(rawEnd);

            Event newEvent = new Event(
                    extractField(json, "title"),
                    extractField(json, "description"),
                    extractField(json, "location"),
                    start_at,
                    end_at,
                    owner_id,
                    LocalDateTime.now()
            );

            eventRepository.save(newEvent);
            JsonResponses.sendJson(exchange, 201, ResponseContract.okJson(null));

        } catch (DateTimeParseException e) {
            sendError(exchange, 400, "Formato de fecha inválido. Use YYYY-MM-DDTHH:MM:SS");
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        }

    }


    private void sendOk(HttpExchange httpExchange, String message) throws IOException {
        String receivedBody = readRequestBody(httpExchange);
        String dataJson = "{"
                + "\"endpoint\":\"user\","
                + "\"method\":\"" + httpExchange.getRequestMethod() + "\","
                + "\"message\":\"" + JsonEscaper.escape(message) + "\","
                + "\"receivedBody\":" + toNullableJsonString(receivedBody)
                + "}";
        String responseJson = ResponseContract.okJson(dataJson);
        JsonResponses.sendJson(httpExchange, 200, responseJson);
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
}
