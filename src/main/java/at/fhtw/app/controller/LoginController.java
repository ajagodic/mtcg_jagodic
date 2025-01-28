package at.fhtw.app.controller;

import at.fhtw.app.model.User;
import at.fhtw.app.service.AbstractService;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;

public class LoginController extends AbstractService implements RestController {
    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response handleRequest(Request request) {
        String path = request.getPathname();
        HttpMethod method = request.getMethod();
        if (path.equals("/sessions") && method.equals(HttpMethod.POST)) {
            try {
                User user = this.getObjectMapper().readValue(request.getBody(), User.class);
                String token = userService.loginUser(user.getUsername(),user.getPassword());
                return new Response(HttpStatus.ACCEPTED, ContentType.JSON, "{\"Token\": "+token+"}");
            } catch (JsonProcessingException e) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid JSON format\"}");
            }
        }
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
    }
}

