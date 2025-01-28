package at.fhtw.app.controller;

import at.fhtw.app.model.Card;
import at.fhtw.app.service.DeckService;
import at.fhtw.app.service.UserService;
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
        if (header == null || !header.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Authorization header is missing or invalid");
        }

        String token = header.substring("Bearer ".length());
        String username = token.split("-")[0];

        if (!UserService.checkAuth(username, token)) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");
        }

        try {
            // Prüfen, ob es sich um ein konfiguriertes Deck handelt
            List<Card> deck = deckService.getDeckByUsername(username, true); // `true` für konfigurierte Decks
            if (deck.isEmpty()) {
                return new Response(HttpStatus.OK, ContentType.JSON, "[]"); // Keine Karten im Deck
            }

            // JSON-Ausgabe erstellen
            StringBuilder jsonResponse = new StringBuilder("[");
            for (int i = 0; i < deck.size(); i++) {
                Card card = deck.get(i);
                jsonResponse.append(card.toString());
                if (i < deck.size() - 1) {
                    jsonResponse.append(", ");
                }
            }
            jsonResponse.append("]");
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Error fetching deck\"}");
        }
    }


    private Response handleSetDeck(Request request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Authorization header is missing or invalid");
        }

        String token = header.substring("Bearer ".length());
        String username = token.split("-")[0];

        if (!UserService.checkAuth(username, token)) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");
        }

        try {
            // Karten-IDs aus dem Request-Body lesen
            List<String> cardIds = objectMapper.readValue(request.getBody(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});

            // Validieren, dass genau 4 Karten übergeben wurden
            if (cardIds.size() != 4) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Deck must contain exactly 4 cards\"}");
            }

            // Deck konfigurieren
            deckService.setDeckForUser(username, cardIds);
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Deck successfully configured\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Error setting deck\"}");
        }
    }

}
