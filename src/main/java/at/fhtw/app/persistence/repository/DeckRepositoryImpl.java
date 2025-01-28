package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.Card;
import at.fhtw.app.persistence.DataAccessException;
import at.fhtw.app.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeckRepositoryImpl implements DeckRepository {

    private final UnitOfWork unitOfWork;

    public DeckRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public List<Card> getDeckByUsername(String username) {
        List<Card> deck = new ArrayList<>();
        String sql = "SELECT id, name, damage, type, element FROM cards WHERE username = ? AND in_deck = true";

        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Card card = new Card(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("damage"));
                        //Card.Type.valueOf(resultSet.getString("card")),
                        //Card.Element.valueOf(resultSet.getString("element")));
                deck.add(card);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching deck for user: " + username, e);
        }
        return deck;
    }


    @Override
    public void setDeckForUser(String username, List<String> cardIds) {
        String sql = "UPDATE cards SET in_deck = true WHERE id = ? AND username = ?";
        String resetSql = "UPDATE cards SET in_deck = false WHERE username = ?";

        try {
            // Alle Karten des Benutzers zurücksetzen
            try (PreparedStatement resetStatement = unitOfWork.prepareStatement(resetSql)) {
                resetStatement.setString(1, username);
                resetStatement.executeUpdate();
            }

            // Neue Karten ins Deck setzen
            try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
                for (String cardId : cardIds) {
                    statement.setString(1, cardId);
                    statement.setString(2, username);
                    int rows = statement.executeUpdate();
                    if(rows == 0){
                        throw new DataAccessException("Card with id " + cardId + " was not found");
                    }
                }
                unitOfWork.commitTransaction();
            }
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error setting deck for user: " + username, e);
        }
    }

}
