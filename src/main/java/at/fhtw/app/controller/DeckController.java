package at.fhtw.app.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;

public class DeckController implements RestController {
    @Override
    public Response handleRequest(Request request) {
        String path = request.getPathname();
        HttpMethod method = request.getMethod();
        try {
            if (path.equals("/deck") && method.equals(HttpMethod.GET)) {
                return handleShowingDeck(request);
            } else if (path.equals("/sessions") && method.equals(HttpMethod.PUT)) {
                return handleConfigureDeck(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }
    private Response handleShowingDeck(Request request) {
        return null;
    }
    private Response handleConfigureDeck(Request request) {
        return null;
    }
}
