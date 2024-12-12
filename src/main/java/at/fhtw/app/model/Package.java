package at.fhtw.app.model;

import lombok.Setter;

import java.util.List;

public class Package {
    @Setter
    private String id;
    @Setter
    private List<Card> cards; // Card is a model for individual cards
    private int price = 5; // Default price for a package

    public Package(String id, List<Card> cards) {
        this.id = id;
        this.cards = cards;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getPrice() {
        return price;
    }
}
