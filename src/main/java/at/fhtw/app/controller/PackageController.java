package at.fhtw.app.controller;


import at.fhtw.app.model.Package;
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
                } else if ("/transaction/packages".equals(request.getPathname())) {
                    return aquirePackages(request);
                }
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid request\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Server error\"}");
        }
    }

    public void createPackage(Request request) {
        try {
            packageService.createPackage(pkg);
            System.out.println("Package created successfully.");
        } catch (Exception e) {
            System.err.println("Error creating package: " + e.getMessage());
        }
    }

    public void aquirePackages(Request request) {
        try {
            List<Package> packages = packageService.getAllPackages();
            packages.forEach(pkg -> System.out.println(pkg.getId()));
        } catch (Exception e) {
            System.err.println("Error listing packages: " + e.getMessage());
        }
    }
}

