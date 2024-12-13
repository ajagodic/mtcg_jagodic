package at.fhtw.app.controller;

import at.fhtw.app.model.User;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
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
        try {
            if (request.getMethod() == Method.POST) {
                if ("/users".equals(request.getPathname())) {
                    return handleRegistration(request);
                } else if ("/sessions".equals(request.getPathname())) {
                    return handleLogin(request);
                }
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
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

    private Response handleLogin(Request request) throws JsonProcessingException {

        User user = new ObjectMapper().readValue(request.getBody(), User.class);
        String token = userService.loginUser(user.getUsername(), user.getPassword());
        if (token != null) {
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"token\": \"" + token + "\"}");
        } else {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Invalid username or password\"}");
        }
    }

    /*private Response handleNameEdit(Request request) throws JsonProcessingException {
        User user = new ObjectMapper().readValue(request.getBody(), User.class);
        boolean success = userService.editName(user.getUsername(), u)
        if (success) {
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"User registered successfully\"}");
        } else {
            return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"message\": \"User already exists\"}");
        }
    }*/
    /*private Response handleNameEdit(Request request) throws JsonProcessingException {
        User user = new ObjectMapper().readValue(request.getBody(), User.class);
        boolean success = userService.editName(user.getUsername(), u)
        if (success) {
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"User registered successfully\"}");
        } else {
            return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"message\": \"User already exists\"}");
        }
    }*/

}

