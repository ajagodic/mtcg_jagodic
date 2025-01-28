package at.fhtw.app.controller;

import at.fhtw.app.model.Card;
import at.fhtw.app.model.Package;
import at.fhtw.app.model.User;
import at.fhtw.app.service.PackageService;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class PackageController implements RestController {

    private final PackageService packageService;
    private final ObjectMapper objectMapper;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Response handleRequest(Request request) {
        String path = request.getPathname();
        HttpMethod method = request.getMethod();

        try {
            if ("/transactions/packages".equals(path) && method == HttpMethod.POST) {
                return handleBuyPackage(request);
            } else if ("/packages".equals(path) && method == HttpMethod.POST) {
                return handleAddPackage(request);
            } else if ("/cards".equals(path) && method == HttpMethod.GET) {
                return handleGetCards(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }

    private Response handleAddPackage(Request request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Authorization header is missing or invalid");
        }

        String token = header.substring("Bearer ".length());
        String username = token.split("-")[0];

        if (!UserService.checkAuth(username, token) || !username.equals("admin")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");
        }

        try {
            // JSON-Body als Kartenliste verarbeiten
            System.out.println("Received request body: " + request.getBody());
            List<Card> cards = objectMapper.readValue(request.getBody(), new com.fasterxml.jackson.core.type.TypeReference<List<Card>>() {});

            // Karten verarbeiten (Type und Element setzen)
            for (Card card : cards) {
                try {
                    card.setType(detectCardType(card.getName()));
                    card.setElement(detectElement(card.getName())); // Optional: Wenn du Elemente dynamisch bestimmen willst
                } catch (IllegalArgumentException e) {
                    return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Unknown card name: " + card.getName() + "\"}");
                }
            }

            // Karten validieren
            if (cards.isEmpty()) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Package must contain at least one card\"}");
            }
            int a = 1;
            // Package-Objekt erstellen
            Package pkg = new Package(cards);

            // Debugging
            System.out.println("Package parsed: " + pkg.getId());
            System.out.println("Cards in package: " + pkg.getCards().size());

            // Package in die Datenbank einfügen
            packageService.addPackage(pkg);
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"Package successfully added\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid package data\"}");
        }
    }

    private Response handleGetCards(Request request) {
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
            List<Card> userCards = packageService.getUserCards(username);
            StringBuilder cardsString = new StringBuilder("[");

            for (int i = 0; i < userCards.size(); i++) {
                Card card = userCards.get(i);
                cardsString.append(card.toString()); // Ruft die toString()-Methode von Card auf
                if (i < userCards.size() - 1) { // Komma hinzufügen, außer bei der letzten Karte
                    cardsString.append(", ");
                }
            }

            cardsString.append("]");
            String jsonResponse = cardsString.toString(); // Das Ergebnis als JSON-ähnlicher String
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Error fetching cards\"}");
        }
    }



    private Card.Type detectCardType(Card.CardName name) {


        if (name == Card.CardName.WaterGoblin || name == Card.CardName.FireGoblin || name == Card.CardName.RegularGoblin ||
        name == Card.CardName.WaterTroll || name == Card.CardName.FireTroll ||  name == Card.CardName.RegularTroll ||
        name == Card.CardName.WaterElf ||  name == Card.CardName.FireElf ||  name == Card.CardName.RegularElf ||
        name == Card.CardName.Knight || name == Card.CardName.Dragon || name == Card.CardName.Ork ||
        name == Card.CardName.Kraken) {
            return Card.Type.MONSTER;
        }

        if(name == Card.CardName.Wizzard || name == Card.CardName.WaterSpell ||
        name == Card.CardName.FireSpell || name == Card.CardName.RegularSpell) {
            return Card.Type.SPELL;
        }


        throw new IllegalArgumentException("Unknown card type for name: " + name);
    }
    private Card.Element detectElement(Card.CardName name) {
        if (name.toString().startsWith("Water")) {
            return Card.Element.WATER;
        } else if (name.toString().startsWith("Fire")) {
            return Card.Element.FIRE;
        } else {
            return Card.Element.NORMAL;
        }
    }


    private Response handleBuyPackage(Request request){
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
            packageService.buyPackage(username);
            return new Response(HttpStatus.OK, ContentType.JSON, "Successfully bought the package");
        } catch (IllegalStateException e) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Error processing request\"}");
        }
    }
}
