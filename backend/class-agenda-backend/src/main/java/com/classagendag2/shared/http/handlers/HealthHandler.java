package com.classagendag2.shared.http.handlers;

import com.classagendag2.shared.http.JsonResponses;
import com.classagendag2.shared.http.ResponseContract;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public final class HealthHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String jsonBody = ResponseContract.healthOkJson();
    JsonResponses.sendJson(exchange, 200, jsonBody);

  }
}
