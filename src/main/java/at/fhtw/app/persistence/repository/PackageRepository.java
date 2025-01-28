package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.Card;
import at.fhtw.app.model.Package;
import at.fhtw.app.model.User;

import java.util.List;

public interface PackageRepository {
    void createPackage(Package pkg) throws Exception;
    Package fetchPackage(String usename) throws Exception;
    void removePackage(int packageId) throws Exception;
    List<Card> fetchUserCards(String username) throws Exception;
}
