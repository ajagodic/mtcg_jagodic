package at.fhtw.app.persistence.repository;
import at.fhtw.app.model.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DeckRepositoryImpl implements DeckRepository {
    private final Connection connection;

    public DeckRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Card> getDeckByUsername(String username) {
        String query = "SELECT c.id, c.name, c.damage FROM cards c " + "INNER JOIN decks d ON c.id = d.card_id " + "WHERE d.username = ?";
        List<Card> deck = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            /*while (rs.next()) {
                deck.add(new Card( rs.getString("name"), rs.getDouble("damage")));
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deck;
    }

    @Override
    public void saveDeck(String username, List<String> cardIds) {
        String deldql = "DELETE FROM decks WHERE username = ?";
        String insql = "INSERT INTO decks (username, card_id) VALUES (?, ?)";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deldql)) {
            deleteStmt.setString(1, username);
            deleteStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (PreparedStatement insertStmt = connection.prepareStatement(insql)) {
            for (String cardId : cardIds) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, cardId);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDeck(String username) {
        String query = "DELETE FROM decks WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

