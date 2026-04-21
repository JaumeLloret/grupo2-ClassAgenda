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
import java.util.List;
import java.util.Optional;

public final class EventHandler implements HttpHandler{

    private final EventRepository userRepository;

    public EventHandler(EventRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String httpMethod = httpExchange.getRequestMethod();
            switch (httpMethod) {
                case "GET" -> sendOk(httpExchange, "GET event");//Usamos el verbo GET (Dame información, solo quiero consultar).
                case "POST" -> sendOk(httpExchange, "POST event"); //Usamos el verbo POST (Te envío datos nuevos para que los guardes)
                case "PUT" -> sendOk(httpExchange, "PUT event"); // Usamos PUT (Reemplaza este dato por completo)
                case "PATCH" -> sendOk(httpExchange, "PATCH event"); //PATCH (Modifica solo una pequeña parte del dato).
                case "DELETE" -> sendOk(httpExchange, "DELETE event"); //Usamos el verbo DELETE (Elimina este dato de la base de datos).
                default -> sendMethodNotAllowed(httpExchange);
            }
        } catch (Exception exception) {
            sendServerError(httpExchange, exception.getMessage());
        }
    }

    // _______________________________
    // POST / user --> Crear usuario
    // _______________________________
    /*private  void handleCreateEvent(HttpExchange exchange) throws IOException {
        String body =readRequestBody(exchange);
        CreateUserDto dto = parseCreateUserDto(body);
        Event event = new Event(dto.name(), dto.email());
        Event saved = userRepository.save(event);
        String json =
                "{"
                + "\"id\":" + saved.getId() + ","
                + "\"name\":\"" + JsonEscaper.escape(saved.getName()) + "\","
                + "\"email\":\"" + JsonEscaper.escape(saved.getEmail()) + "\","
                + "\"createdAt\":\"" + saved.getCreatedAt() + "\""
                + "}";
        JsonResponses.sendJson(exchange, 201, json);
    }*/

    // _______________________________
    // GET / user --> Mostrar Usuarios
    // _______________________________
    /*private void handleUserInfo(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        //Muestro todos los usuarios
        if(path.equals("/user/")){
            handleListsUsers(exchange);
        } //Busqueda por ID
        else if (path.matches("/user/\\d+")) {
            handleUserById(exchange);
        }  //Busqueda por email
        else if (path.startsWith("/user/email/")){
            handleUserByEmail(exchange);
        }

    }*/

    // _______________________________
    // DELETE / user --> Eliminar usuario
    // _______________________________
    /*private  void handleDeleteUser(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String userId = path.substring("/user/".length());
        Long id = Long.parseLong(userId);

        userRepository.deleteById(id);


        //sendOk(exchange, "DELETE user: " + id);
        String json =
                "{"
                        + "\"id\":" + id + ","
                        + "\"Method\": DELETE user,"
                        + "}";
        JsonResponses.sendJson(exchange, 200, json);
    }*/

    /*private  void handleListsUsers(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        List<Event> users = userRepository.findAll();

        String json =
                    "{"
                            + "\"items\": ";

            for(int i = 0; i< users.size();i++){
                json += "["
                        + "\"id\":" + users.get(i).getId() + ","
                        + "\"name\":\"" + JsonEscaper.escape(users.get(i).getName()) + "\","
                        + "\"email\":\"" + JsonEscaper.escape(users.get(i).getEmail()) + "\","
                        + "\"createdAt\":\"" + users.get(i).getCreatedAt() + "\""
                        + "]";
            }
            json += "}";

        JsonResponses.sendJson(exchange, 201, json);
    }*/

    /*private  void handleUserById(HttpExchange exchange) throws IOException {
        //sendOk(exchange, "GET ok ID");
        String path = exchange.getRequestURI().getPath();
        String userId = path.substring("/user/".length());
        Long id = Long.parseLong(userId);

        Optional<Event> user = userRepository.findById(id);

        String json =
                "{"
                        + "\"id\":" + user.get().getId() + ","
                        + "\"name\":\"" + JsonEscaper.escape(user.get().getName()) + "\","
                        + "\"email\":\"" + JsonEscaper.escape(user.get().getEmail()) + "\","
                        + "\"createdAt\":\"" + user.get().getCreatedAt() + "\""
                        + "}";

        JsonResponses.sendJson(exchange, 201, json);

    }*/

    /*private  void handleUserByEmail(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String email = path.substring("/user/email/".length());
        Optional<Event> user = userRepository.findByEmail(email);

        String json =
                "{"
                        + "\"id\":" + user.get().getId() + ","
                        + "\"name\":\"" + JsonEscaper.escape(user.get().getName()) + "\","
                        + "\"email\":\"" + JsonEscaper.escape(user.get().getEmail()) + "\","
                        + "\"createdAt\":\"" + user.get().getCreatedAt() + "\""
                        + "}";

        JsonResponses.sendJson(exchange, 201, json);
    }*/
    // ______________________________
    // Parseo manual de JSON
    // ______________________________
    /*private CreateEventDto parseCreateEventDto(String json) {
        json = json.trim();

        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        String[] parts = json.split(",");

        String name = null;
        String email = null;

        for (String part : parts) {
            String[] keyValue = part.split(":");

            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");

            if (key.equals("name")) name = value;
            if (key.equals("email")) email = value;
        }

        return new CreateUserDto(name, email);
    }*/

    //private record CreateUserDto(String name, String email) {}

    // __________________________
    // Metodos auxiliares (Aules)
    // __________________________
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
