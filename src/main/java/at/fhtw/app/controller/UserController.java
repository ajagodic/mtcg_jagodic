package at.fhtw.app.controller;

import at.fhtw.app.model.User;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserController implements RestController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response handleRequest(Request request) {
        String path = request.getPathname();
        HttpMethod method = request.getMethod();
        try {
            if (path.equals("/users") && method == HttpMethod.POST) {
                return handleRegistration(request);
            } else if (path.startsWith("/users") && method == HttpMethod.GET) {
                return handleGetUserData(request);
            } else if (path.startsWith("/users") && method == HttpMethod.PUT) {
                return handleUserUpdate(request);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"User not found\"}");
    }

    private Response handleRegistration(Request request) throws JsonProcessingException {

        User user = new ObjectMapper().readValue(request.getBody(), User.class);
        boolean success = userService.registerUser(user);
        if (success) {
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"User registered successfully\"}");
        } else {
            return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"message\": \"User already exists\"}");
        }
    }
    private Response handleUserUpdate(Request request) throws JsonProcessingException {
        User user = new ObjectMapper().readValue(request.getBody(), User.class);
        boolean success = userService.editUser(user);
        if (success) {
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"User registered successfully\"}");
        } else {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"User already exists\"}");
        }
    }

    private Response handleGetUserData(Request request) throws JsonProcessingException {
        String[] pathSegments = request.getPathname().split("/");
        String s = userService.getUSerData(pathSegments[2]);
        if (s!=null) {
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"Hier sind die Userdaten\":" + s);
        } else {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"No userdata\"}");
        }
    }
    private Response handleUserEditData(Request request) throws JsonProcessingException {
        User user = new ObjectMapper().readValue(request.getBody(), User.class);
        boolean success = userService.updateUserData(user);
        if (success) {
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"User registered successfully\"}");
        } else {
            return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"message\": \"User already exists\"}");
        }
    }
}

