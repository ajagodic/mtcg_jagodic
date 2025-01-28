package at.fhtw.app.controller;

import at.fhtw.app.model.Card;
import at.fhtw.app.service.DeckService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class DeckController implements RestController {

    private final DeckService deckService;
    private final ObjectMapper objectMapper;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Response handleRequest(Request request) {
        String path = request.getPathname();
        HttpMethod method = request.getMethod();

        try {
            if (path.equals("/deck") && method == HttpMethod.GET) {
                return handleGetDeck(request);
            } else if (path.equals("/deck") && method == HttpMethod.PUT) {
                return handleSetDeck(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }

    private Response handleGetDeck(Request request) {


        String header = request.getHeader("Authorization");
        String token = header.substring("Bearer ".length());
        String username = token.split("-")[0];
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Missing Authorization\"}");
        }

        List<Card> deck = deckService.getDeckByUsername(username);

        try {
            String jsonResponse = objectMapper.writeValueAsString(deck);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (JsonProcessingException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Error processing deck data\"}");
        }
    }

    private Response handleSetDeck(Request request) {
        String username = request.getHeader("Authorization"); // Username aus Header oder Token extrahieren
        if (username == null) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Missing Authorization\"}");
        }

        try {
            List<String> cardIds = objectMapper.readValue(request.getBody(), List.class);
            if(deckService.setDeckForUser(username, cardIds)) {
                return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Deck successfully updated\"}");
            }
        } catch (JsonProcessingException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid JSON format\"}");
        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"" + e.getMessage() + "\"}");
        }
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
    }
}
