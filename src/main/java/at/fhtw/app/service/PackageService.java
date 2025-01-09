package at.fhtw.app.service;

import at.fhtw.app.model.Package;
import at.fhtw.app.persistence.repository.PackageRepository;
import at.fhtw.app.persistence.repository.UserRepository;

import java.util.List;

public class PackageService {
    private final PackageRepository packageRepository;
    private final UserRepository userRepository;

    public PackageService(PackageRepository packageRepository, UserRepository userRepository) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
    }

    public void createPackage(Package pkg) throws Exception {
        if (pkg.getCards().size() != 5) {
            throw new Exception("A package must contain exactly 5 cards.");
        }
        packageRepository.createPackage(pkg);
    }

    public List<Package> acquirePackage(String username) throws Exception {
        // Überprüfen, ob Pakete verfügbar sind
        List<Package> availablePackages = packageRepository.getAllPackages();

        if (availablePackages.isEmpty()) {
            throw new Exception("No packages available.");
        }

        // Benutzer-Coins abfragen
        int userCoins = userRepository.getCoins(username);

        if (userCoins < 5) {
            throw new Exception("Not enough coins to purchase a package.");
        }

        // Paket erwerben
        //Package acquiredPackage = availablePackages[0];
        userRepository.updateCoins(username, userCoins - 5); // Coins abziehen
        userRepository.addPackageToUser(username, availablePackages.getFirst()); // Paket dem Benutzer hinzufügen
        packageRepository.removePackage(availablePackages.get(0).getId()); // Paket aus Repository entfernen

        return List.of(availablePackages.getFirst()); // Rückgabe des erworbenen Pakets
    }
}

