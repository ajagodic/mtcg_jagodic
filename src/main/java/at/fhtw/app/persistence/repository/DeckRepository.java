package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.Card;

import java.util.List;

public interface DeckRepository {
    List<Card> getDeckByUsername(String username, boolean isConfigured);
    void setDeckForUser(String username, List<String> cardIds);
}
