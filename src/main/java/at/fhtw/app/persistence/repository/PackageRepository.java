package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.Package;
import at.fhtw.app.model.User;

import java.util.List;

public interface PackageRepository {
    void createPackage(Package pkg) throws Exception;
    List<Package> getAllPackages() throws Exception;
    void removePackage(String packageId) throws Exception;
    int getCoinsForUser(User user) throws Exception;
}
