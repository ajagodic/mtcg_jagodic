package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.Package;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import at.fhtw.app.model.Card;
import at.fhtw.app.persistence.DataAccessException;

public class PackageRepositoryImpl implements PackageRepository {
    private final Connection connection;

    public PackageRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createPackage(Package pkg){
        String query = "INSERT INTO packages (id, cards, price) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, pkg.getId());
            statement.setString(2, serializeCards(pkg.getCards())); // Convert list to JSON or String
            statement.setInt(3, pkg.getPrice());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating package", e);
        }
    }

    @Override
    public List<java.lang.Package> getAllPackages(){
        String query = "SELECT * FROM packages";
        List<Package> packages = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                packages.add(new Package(
                        rs.getString("id"),
                        deserializeCards(rs.getString("cards")) // Convert back to List<Card>
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating package", e);
        }
        return packages;
    }

    @Override
    public String serializePackage(java.lang.Package pack) {
        return "";
    }

    private String serializeCards(List<Card> cards) {
        // Serialize cards to JSON (use a library like Jackson)
        return "";
    }

    private List<Card> deserializeCards(String cardsJson) {
        // Deserialize JSON to List<Card>
        return new ArrayList<>();
    }
}
