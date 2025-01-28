package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.Card;
import at.fhtw.app.model.Package;
import at.fhtw.app.persistence.DataAccessException;
import at.fhtw.app.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageRepositoryImpl implements PackageRepository {

    private final UnitOfWork unitOfWork;

    public PackageRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void createPackage(Package pkg) {
        String packageSql = "INSERT INTO packages (id, name) VALUES (?, ?)";
        String cardSql = "INSERT INTO cards (id, name, damage, element, type, package_id) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Package speichern
            try (PreparedStatement packageStmt = unitOfWork.prepareStatement(packageSql)) {
                packageStmt.setString(1, pkg.getId());
                packageStmt.setString(2, pkg.getName());
                packageStmt.executeUpdate();
            }

            // Karten im Package speichern
            try (PreparedStatement cardStmt = unitOfWork.prepareStatement(cardSql)) {
                for (Card card : pkg.getCards()) {
                    cardStmt.setString(1, card.getId());
                    cardStmt.setString(2, card.getName());
                    cardStmt.setDouble(3, card.getDamage());
                    cardStmt.setString(4, card.getType().name());
                    cardStmt.setString(5, card.getElement().name());
                    cardStmt.setString(6, pkg.getId());
                    cardStmt.executeUpdate();
                }
            }
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error saving package", e);
        }
    }

    @Override
    public Package fetchPackage() {
        String packageSql = "SELECT id, name FROM packages LIMIT 1";
        String cardSql = "SELECT id, name, damage, element, type FROM cards WHERE package_id = ?";

        try {
            // Package abrufen
            try (PreparedStatement packageStmt = unitOfWork.prepareStatement(packageSql)) {
                ResultSet packageRs = packageStmt.executeQuery();
                if (packageRs.next()) {
                    String packageId = packageRs.getString("id");
                    String packageName = packageRs.getString("name");

                    // Karten im Package abrufen
                    try (PreparedStatement cardStmt = unitOfWork.prepareStatement(cardSql)) {
                        cardStmt.setString(1, packageId);
                        ResultSet cardRs = cardStmt.executeQuery();
                        List<Card> cards = new ArrayList<>();

                        while (cardRs.next()) {
                            cards.add(new Card(
                                    cardRs.getString("id"),
                                    cardRs.getString("name"),
                                    cardRs.getDouble("damage"),
                                    Card.Type.valueOf(cardRs.getString("type")),
                                    Card.Element.valueOf(cardRs.getString("element"))
                            ));
                        }

                        return new Package(packageId, packageName, cards);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching package", e);
        }

        return null; // Kein Package gefunden
    }

    @Override
    public void removePackage(String packageId) {
        String sql = "DELETE FROM packages WHERE id = ?";

        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, packageId);
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error deleting package", e);
        }
    }
}
