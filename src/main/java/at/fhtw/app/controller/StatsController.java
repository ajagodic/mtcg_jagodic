package at.fhtw.app.controller;

import at.fhtw.app.model.User;
import at.fhtw.app.service.AbstractService;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StatsController extends AbstractService implements RestController {
    private UserService userService;

    public StatsController(UserService userService) {this.userService = userService;}


    @Override
    public Response handleRequest(Request request) {
        try {
            if (request.getMethod() == Method.POST) {
                if ("/users".equals(request.getPathname())) {
                    return null;
                } else if ("/stats".equals(request.getPathname())) {
                    return handleStatsboard(request);
                }
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }

    private Response handleStatsboard(Request request) throws JsonProcessingException {

        User user = new ObjectMapper().readValue(request.getBody(), User.class);
        String token = userService.displayStats(user);
        if (token != null) {
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"token\": \"" + token + "\"}");
        } else {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Statsboard couldn't be displayed\"}");
        }
    }
}
