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

public class LoginController extends AbstractService implements RestController {
    private UserService userService;
    boolean success;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST && "/sessions".equals(request.getPathname())) {
            try {
                User user = this.getObjectMapper().readValue(request.getBody(), User.class);
                String token = userService.loginUser(user.getUsername(),user.getPassword());
                return new Response(HttpStatus.ACCEPTED, ContentType.JSON, "{\"Token\": \"%s\"}".formatted(token));
                    //return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"message\": \"Login error\"}");
            } catch (JsonProcessingException e) {
                // Fehler bei der Verarbeitung von JSON
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid JSON format\"}");
            }
        }
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
    }
}

