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

    public List<Card> getDeckByUsername(String username, boolean isConfigured) {
        return deckRepository.getDeckByUsername(username, isConfigured);
    }

    public void setDeckForUser(String username, List<String> cardIds) {
        deckRepository.setDeckForUser(username, cardIds);
    }

}
