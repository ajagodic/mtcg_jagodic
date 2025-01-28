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
        String packageSql = "INSERT INTO packages (id) VALUES (DEFAULT) RETURNING id";
        String cardSql = "INSERT INTO cards (id, name, damage, type, element, package_id) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Package speichern
            try (PreparedStatement packageStmt = unitOfWork.prepareStatement(packageSql)) {
                ResultSet rs = packageStmt.executeQuery();
                if (rs.next()) {
                    int packageId = rs.getInt(1);
                }

                // Karten im Package speichern
                try (PreparedStatement cardStmt = unitOfWork.prepareStatement(cardSql)) {
                    for (Card card : pkg.getCards()) {
                        cardStmt.setString(1, card.getId());
                        cardStmt.setString(2, card.getName().name());
                        cardStmt.setDouble(3, card.getDamage());
                        cardStmt.setString(4, card.getType().name());
                        cardStmt.setString(5, card.getElement().name());
                        cardStmt.setInt(6, rs.getInt(1));
                        cardStmt.executeUpdate();
                    }
                }
                unitOfWork.commitTransaction();
            } catch (SQLException e) {
                unitOfWork.rollbackTransaction();
                throw new DataAccessException("Error saving package", e);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Package fetchPackage(String username) throws DataAccessException {
        String packageSql = "SELECT id FROM packages";
        String cardSql = "SELECT id, name, damage, type, element FROM cards WHERE package_id = ?";
        String userCoinsSql = "SELECT coins FROM users WHERE username = ?";
        String updateCoinsSql = "UPDATE users SET coins = coins - 5 WHERE username = ?";
        String assignCardSql = "UPDATE cards SET username = ? WHERE id = ?";

        try {
            // Überprüfen, ob der Benutzer genug Coins hat
            try (PreparedStatement userCoinsStmt = unitOfWork.prepareStatement(userCoinsSql)) {
                userCoinsStmt.setString(1, username);
                ResultSet coinsRs = userCoinsStmt.executeQuery();
                if (coinsRs.next()) {
                    int coins = coinsRs.getInt("coins");
                    if (coins < 5) {
                        throw new IllegalStateException("Not enough coins to buy a package.");
                    }
                } else {
                    throw new IllegalStateException("User not found.");
                }
            }

            // Package abrufen
            try (PreparedStatement packageStmt = unitOfWork.prepareStatement(packageSql)) {
                ResultSet packageRs = packageStmt.executeQuery();
                if (packageRs.next()) {
                    int packageId = packageRs.getInt("id");

                    // Karten im Package abrufen
                    try (PreparedStatement cardStmt = unitOfWork.prepareStatement(cardSql)) {
                        cardStmt.setInt(1, packageId);
                        ResultSet cardRs = cardStmt.executeQuery();
                        List<Card> cards = new ArrayList<>();

                        while (cardRs.next()) {
                            String cardId = cardRs.getString("id");
                            Card card = new Card(
                                    cardId,
                                    Card.CardName.valueOf(cardRs.getString("name")),
                                    cardRs.getDouble("damage"),
                                    Card.Type.valueOf(cardRs.getString("type")),
                                    Card.Element.valueOf(cardRs.getString("element"))
                            );
                            cards.add(card);

                            // Karte dem Benutzer zuweisen
                            try (PreparedStatement assignCardStmt = unitOfWork.prepareStatement(assignCardSql)) {
                                assignCardStmt.setString(1, username);
                                assignCardStmt.setString(2, cardId);
                                assignCardStmt.executeUpdate();
                            }
                        }

                        // Prüfen, ob das Package Karten enthält
                        if (cards.isEmpty()) {
                            throw new IllegalStateException("No cards found for package ID: " + packageId);
                        }

                        // Benutzercoins aktualisieren
                        try (PreparedStatement updateCoinsStmt = unitOfWork.prepareStatement(updateCoinsSql)) {
                            updateCoinsStmt.setString(1, username);
                            updateCoinsStmt.executeUpdate();
                        }

                        // Package löschen
                        removePackage(packageId);

                        // Package zurückgeben
                        return new Package(cards);
                    }
                } else {
                    throw new IllegalStateException("No packages available.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching package", e);
        }
    }

    @Override
    public void removePackage(int packageId) {
        String sql = "DELETE FROM packages WHERE id = ?";

        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setInt(1, packageId);
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error deleting package", e);
        }
    }

    @Override
    public List<Card> fetchUserCards(String username) {
        String sql = "SELECT id, name, damage, type, element FROM cards WHERE username = ?";
        List<Card> userCards = new ArrayList<>();

        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                userCards.add(new Card(
                        rs.getString("id"),
                        Card.CardName.valueOf(rs.getString("name")),
                        rs.getDouble("damage"),
                        Card.Type.valueOf(rs.getString("type")),
                        Card.Element.valueOf(rs.getString("element"))
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching user cards", e);
        }

        return userCards;
    }

}
