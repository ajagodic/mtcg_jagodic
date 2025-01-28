package at.fhtw.app.controller;

import at.fhtw.app.model.User;
import at.fhtw.app.service.BattleService;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BattleController implements RestController {
    private final Queue<User> waitingPlayers = new ConcurrentLinkedQueue<>();
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
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

    private Response handleBattle(Request request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Authorization token required\"}");
        }

        String token = header.substring("Bearer ".length());
        String username = token.split("-")[0];
        User player = userService.findUserbyUsername(username);
        if (player == null) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"Player not found\"}");
        }

        /*if (player.getDeck() == null || player.getDeck().isEmpty()) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Player must have a configured deck to battle\"}");
        }*/

        synchronized (waitingPlayers) {
            if (waitingPlayers.isEmpty()) {
                // Kein Gegner verfügbar → Spieler in Warteschlange setzen
                waitingPlayers.add(player);
                return new Response(HttpStatus.ACCEPTED, ContentType.JSON, "{\"message\": \"Waiting for another player to join the battle\"}");
            } else {
                // Gegner vorhanden → Battle starten
                User opponent = waitingPlayers.poll();

                // Sicherheitsprüfung: Falls der Gegner null ist, Fehler vermeiden
                if (opponent == null) {
                    return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Unexpected error: No opponent available\"}");
                }

                try {
                    String battleLog = battleService.startBattle(player, opponent);
                    return new Response(HttpStatus.OK, ContentType.JSON, "{\"battleLog\": \"" + battleLog.replace("\n", "\\n") + "\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Error during battle: " + e.getMessage() + "\"}");
                }
            }
        }
    }
}
