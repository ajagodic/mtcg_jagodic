package at.fhtw.app.controller;

import at.fhtw.app.service.AbstractService;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;

public class StatsController extends AbstractService implements RestController {
    private UserService userService;

    public StatsController(UserService userService) {this.userService = userService;}


    @Override
    public Response handleRequest(Request request) {
        String path = request.getPathname();
        HttpMethod method = request.getMethod();
        try {
            if (path.equals("/stats") && method == HttpMethod.GET) {
                return handlesStats(request);
            } else if (path.equals("/scoreboard") && method == HttpMethod.GET) {
                return handleScoreboard(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }

    private Response handlesStats(Request request) throws JsonProcessingException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length());
            String username = token.split("-")[0];
            if (!UserService.checkAuth(username, token)) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,"Access token is missing or invalid");
            }
            String s = userService.displayStats(username);
            return new Response(HttpStatus.OK, ContentType.JSON, s);
        } else {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,"Acess token is missing or invalid");
        }


    }
    private Response handleScoreboard(Request request) throws Exception {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length());
            String username = token.split("-")[0];
            if (!UserService.checkAuth(username, token)) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,"Access token is missing or invalid");
            }
            String s = userService.displayStats(username);
            return new Response(HttpStatus.OK, ContentType.JSON, s);
        } else {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,"Acess token is missing or invalid");
        }
    }
}
