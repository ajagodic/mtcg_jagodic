package at.fhtw.app.controller;

import at.fhtw.app.model.Package;
import at.fhtw.app.service.PackageService;
import at.fhtw.app.service.UserService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            if (path.equals("/packages") && method == HttpMethod.POST) {
                return handleAddPackage(request);
            } else if (path.equals("/packages") && method == HttpMethod.GET) {
                return handleBuyPackage(request);
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }

    private Response handleAddPackage(Request request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length());
            String username = token.split("-")[0];
            if (!UserService.checkAuth(username, token) && username.equals("admin")) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");
            }
            try {
                Package pkg = objectMapper.readValue(request.getBody(), Package.class);
                packageService.addPackage(pkg);
                return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"Package successfully added\"}");
            } catch (Exception e) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid package data\"}");
            }
        }
        return null;
    }

    private Response handleBuyPackage(Request request) {
        try {
            Package pkg = packageService.buyPackage();
            String jsonResponse = objectMapper.writeValueAsString(pkg);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (IllegalStateException e) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Error processing request\"}");
        }
    }
}
