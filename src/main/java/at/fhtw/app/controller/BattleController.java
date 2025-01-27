package at.fhtw.app.controller;

import at.fhtw.app.model.User;
import at.fhtw.app.persistence.repository.UserRepository;
import at.fhtw.app.service.BattleService;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;

import java.util.LinkedList;
import java.util.Queue;

public class BattleController implements RestController {
    private final Queue<User> waitingPlayers = new LinkedList<>();
    private final UserService userService;
    private final BattleService battleService;

    public BattleController(UserService userService, BattleService battleService) {
        this.userService = userService;
        this.battleService = battleService;
    }
    @Override
    public Response handleRequest(Request request) {
        String path = request.getPathname();
        HttpMethod method = request.getMethod();
        try {
            if (path.equals("/battles") && method == HttpMethod.POST) {
                return handleBattle(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }
    private Response handleBattle(Request request) {
        // Die Anfrage-Parameter parsen
        //String playerUsername = request.getQueryParam("player");
        String playerUsername = request.getBody();
        if (playerUsername == null) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"A player username must be specified\"}");
        }

        // Spieler-Repository abrufen
        User player = userService.findUserbyUsername(playerUsername);
        if (player == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"Player not found\"}");
        }

        // Überprüfen, ob der Spieler ein Deck konfiguriert hat
        if (player.getDeck().isEmpty()) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"Player must have a configured deck to battle\"}");
        }

        synchronized (waitingPlayers) {
            if (waitingPlayers.isEmpty()) {
                // Kein Spieler wartet, diesen Spieler in die Warteschlange setzen
                waitingPlayers.add(player);
                return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Waiting for another player to join the battle\"}");
            } else {
                // Ein Spieler wartet bereits, Battle starten
                User opponent = waitingPlayers.poll(); // Den wartenden Spieler aus der Warteschlange nehmen
                try {
                    String battleLog = battleService.startBattle(player, opponent);
                    return new Response(HttpStatus.OK, ContentType.JSON, "{\"battleLog\": \"" + battleLog.replace("\n", "\\n") + "\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An error occurred during the battle: " + e.getMessage() + "\"}");
                }
            }
        }
    }

}
