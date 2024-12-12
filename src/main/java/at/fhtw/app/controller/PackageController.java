package at.fhtw.app.controller;


import at.fhtw.app.model.Package;
import at.fhtw.app.service.PackageService;

import java.util.List;

public class PackageController {
    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    public void createPackage(Package pkg) {
        try {
            packageService.createPackage(pkg);
            System.out.println("Package created successfully.");
        } catch (Exception e) {
            System.err.println("Error creating package: " + e.getMessage());
        }
    }

    public void listPackages() {
        try {
            List<Package> packages = packageService.getAllPackages();
            packages.forEach(pkg -> System.out.println(pkg.getId()));
        } catch (Exception e) {
            System.err.println("Error listing packages: " + e.getMessage());
        }
    }
}

