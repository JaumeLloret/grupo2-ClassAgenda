package com.classagendag2.features.event.presentation.handlers;

import com.classagendag2.features.event.domain.model.Event;
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
import java.util.stream.Collectors;

public final class EventHandler implements HttpHandler{

    private final EventRepository eventRepository;


    public EventHandler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        //lectura del ownerId para el POST
        String ownerHeader = httpExchange.getRequestHeaders().getFirst("X-User-Id");
        Long ownerId = Long.parseLong(ownerHeader);

        try {
            String httpMethod = httpExchange.getRequestMethod();
            switch (httpMethod) {
                case "GET" -> handleGet(httpExchange, ownerId); //Usamos el verbo GET (Dame información, solo quiero consultar).
                case "PUT" -> sendOk(httpExchange, "PUT event"); // Usamos PUT (Reemplaza este dato por completo)
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

            Long eventId;
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
