package at.fhtw.app.service;

import at.fhtw.app.model.Card;
import at.fhtw.app.persistence.UnitOfWork;
import at.fhtw.app.persistence.repository.DeckRepository;
import at.fhtw.app.persistence.repository.DeckRepositoryImpl;

import java.util.List;

public class DeckService {
    private final DeckRepository deckRepository;

    public DeckService() {
        this.deckRepository = new DeckRepositoryImpl(new UnitOfWork());
    }

    public List<Card> getDeckByUsername(String username) {
        return deckRepository.getDeckByUsername(username);
    }

    public boolean setDeckForUser(String username, List<String> cardIds) {
        if (cardIds == null || cardIds.size() != 4) {
            throw new IllegalArgumentException("A deck must contain exactly 4 cards.");
        }
        deckRepository.setDeckForUser(username, cardIds);
        return true;
    }
}
