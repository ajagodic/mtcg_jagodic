package at.fhtw.app.service;

import at.fhtw.app.model.Card;
import at.fhtw.app.model.Package;
import at.fhtw.app.persistence.UnitOfWork;
import at.fhtw.app.persistence.repository.PackageRepository;
import at.fhtw.app.persistence.repository.PackageRepositoryImpl;
import at.fhtw.app.persistence.repository.UserRepositoryImpl;

import java.util.List;

public class PackageService {

    private final PackageRepository packageRepository;

    public PackageService() {
        this.packageRepository = new PackageRepositoryImpl(new UnitOfWork());
    }

    public void addPackage(Package pkg) throws Exception {
        if (pkg == null) {
            throw new IllegalStateException("No packages");
        }
        packageRepository.createPackage(pkg);
    }
    public List<Card> getUserCards(String username) throws Exception {
        return packageRepository.fetchUserCards(username);
    }


    public void buyPackage(String username) throws Exception {
        Package pkg = packageRepository.fetchPackage(username);
        if (pkg == null) {
            throw new IllegalStateException("No packages available to buy.");
        }
        packageRepository.removePackage(pkg.getId());
    }
}
