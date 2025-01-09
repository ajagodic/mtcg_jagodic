package at.fhtw.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import at.fhtw.app.model.Package;
import at.fhtw.app.model.User;
import at.fhtw.app.service.AbstractService;
import at.fhtw.app.service.PackageService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;

import java.util.List;

public class PackageController extends AbstractService implements RestController {
    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            if (request.getMethod() == Method.POST) {
                if ("/packages".equals(request.getPathname())) {
                    return createPackage(request);
                } else if ("/transactions/packages".equals(request.getPathname())) {
                    return acquirePackage(request);
                }
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }

    private Response createPackage(Request request) {
        try {
            // Parse the request body into a Package object
            List<Package> packages = parseJsonArray(request.getBody(), Package.class);

            // Save the packages using the service
            packageService.createPackage(packages.getFirst());

            return new Response(HttpStatus.CREATED, ContentType.JSON,
                    "{\"message\": \"Packages created successfully\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"message\": \"Error creating packages\"}");
        }
    }

    private Response acquirePackage(Request request) {
        try {
            String token = request.getBody();   //needs to be checked later
            if (token == null || token.isEmpty()) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                        "{\"message\": \"Authorization token is missing\"}");
            }

            // Extract the username from the token
            String username = extractUsernameFromToken(token);
            if (username == null) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON,
                        "{\"message\": \"Invalid authorization token\"}");
            }

            // Acquire a package
            List<Package> acquiredPackages = packageService.acquirePackage(username);

            return new Response(HttpStatus.OK, ContentType.JSON,serializeJson(acquiredPackages));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON,
                    "{\"message\": \"Error acquiring package: " + e.getMessage() + "\"}");
        }

    }
    private String extractUsernameFromToken(String token) {
        // Token format: "<username>-mtcgToken"
        if (token != null && token.contains("-mtcgToken")) {
            return token.split("-mtcgToken")[0];
        }
        return null;
    }
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String serializeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object to JSON", e);
        }
    }
    public static <T> List<T> parseJsonArray(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON array", e);
        }
    }

}

