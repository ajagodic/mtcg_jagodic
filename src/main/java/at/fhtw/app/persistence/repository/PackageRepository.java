package at.fhtw.app.persistence.repository;

import java.util.List;

public interface PackageRepository {
    void createPackage(Package pack);
    List<Package> getAllPackages();
    String serializePackage(Package pack);
}
