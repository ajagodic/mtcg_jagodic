package at.fhtw.app.model;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }
    public String getID(int index) {
        return cards.get(index).getId();
    }
}
