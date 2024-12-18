package at.fhtw.app.service;


import at.fhtw.app.model.Package;
import at.fhtw.app.persistence.repository.PackageRepositoryImpl;

import java.util.List;

public class PackageService {
    private final PackageRepositoryImpl packageRepository;

    public PackageService(PackageRepositoryImpl packageRepository) {
        this.packageRepository = packageRepository;
    }

    public void createPackage(Package pkg) throws Exception {
        if (pkg.getCards().size() != 5) {
            throw new Exception("A package must contain exactly 5 cards.");
        }
        packageRepository.createPackage(pkg);
    }
    public List<Package> getAllPackages() throws Exception {
        try {
            // Abrufen aller Pakete aus dem Repository
            return packageRepository.getAllPackages();
        } catch (Exception e) {
            // Fehlerbehandlung für Datenbank- oder andere Fehler
            throw new Exception("Failed to retrieve packages: " + e.getMessage(), e);
        }
    }
    public void aquirePackage(String userId) throws Exception {

        //
    }
}

