package at.fhtw.app.service;

import at.fhtw.app.model.Card;
import at.fhtw.app.model.User;
import java.util.List;

public class DeckService {

    public List<Card> configureDeck(List<String> ids, User user) {
        for(int i = 0; user.getDeck().get(i).getId().equals(ids.get(i)); i++) {
           // user.addCard(card.);
        }
    }
}
