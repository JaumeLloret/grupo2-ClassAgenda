package com.classagendag2.features.event.presentation.handlers;

import com.classagendag2.features.event.data.local.dao.EventShareDao;
import com.classagendag2.features.event.domain.model.Event;
import com.classagendag2.features.event.domain.model.PermissionLevel;
import com.classagendag2.features.event.domain.repository.EventRepository;
import com.classagendag2.shared.http.JsonResponses;
import com.classagendag2.shared.http.ResponseContract;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Optional;

public final class EventShareHandler implements HttpHandler {

    private final EventRepository eventRepository;
    private final EventShareDao eventShareDao;

    // Inyectamos ambas dependencias
    public EventShareHandler(EventRepository eventRepository, EventShareDao eventShareDao) {
        this.eventRepository = eventRepository;
        this.eventShareDao = eventShareDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // 1. Extraemos el usuario que hace la petición (X-User-Id)
            String userIdHeader = exchange.getRequestHeaders().getFirst("X-User-Id");
            if (userIdHeader == null) throw new SecurityException("No autorizado");
            Long requestingUserId = Long.parseLong(userIdHeader);

            // 2. Extraemos el event_id de la URL (ej: /events/share?eventId=5)
            String query = exchange.getRequestURI().getQuery();
            Long eventId = Long.parseLong(query.split("eventId=")[1].split("&")[0]);

            // 3. LA BARRERA DE SEGURIDAD ABSOLUTA
            // Buscamos el evento y comprobamos que el que hace la petición es el OWNER.
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            if (eventOpt.isEmpty()) {
                JsonResponses.sendJson(exchange, 404, ResponseContract.errorJson("Evento no encontrado", null));
                return;
            }
            Event event = eventOpt.get();
            // Esto explotará y lanzará excepción si no es el dueño original
            event.validateIsOwnedBy(requestingUserId);

            // 4. Enrutamos
            if ("POST".equals(exchange.getRequestMethod())) {
                shareEvent(exchange, eventId);
            } else if ("DELETE".equals(exchange.getRequestMethod())) {
                revokeShare(exchange, eventId);
            } else {
                JsonResponses.sendJson(exchange, 405, ResponseContract.errorJson("Método no permitido", null));
            }
        } catch (SecurityException e) {
            JsonResponses.sendJson(exchange, 403, ResponseContract.errorJson("Prohibido", e.getMessage()));
        } catch (Exception e) {
            JsonResponses.sendJson(exchange, 400, ResponseContract.errorJson("Bad Request", e.getMessage()));
        }
    }

    private void shareEvent(HttpExchange exchange, Long eventId) throws IOException {
        // En una API real usaríamos Jackson. Aquí simulamos la lectura del DTO.
        // Supongamos que leemos el JSON {"targetUserId": 2, "permission": "READ"}
        String body = new String(exchange.getRequestBody().readAllBytes());
        Long targetUser = Long.parseLong(body.split("\"targetUserId\":")[1].split(",")[0].trim());
        String permStr = body.split("\"permission\":\"")[1].split("\"")[0];

        PermissionLevel level = PermissionLevel.valueOf(permStr.toUpperCase());

        eventShareDao.upsertShare(eventId, targetUser, level);
        JsonResponses.sendJson(exchange, 200, ResponseContract.okJson("{\"message\":\"Evento compartido\"}"));
    }

    private void revokeShare(HttpExchange exchange, Long eventId) throws IOException {
        // Para el DELETE, esperamos el ID del usuario a revocar en la URL: ?eventId=5&targetUserId=2
        String query = exchange.getRequestURI().getQuery();
        Long targetUser = Long.parseLong(query.split("targetUserId=")[1].split("&")[0]);

        eventShareDao.deleteShare(eventId, targetUser);
        JsonResponses.sendJson(exchange, 200, ResponseContract.okJson("{\"message\":\"Permisos revocados\"}"));
    }
}