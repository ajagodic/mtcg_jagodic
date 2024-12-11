package at.fhtw.app.model;

import java.util.ArrayList;

public class Package {
    private final int length = 5;
    private ArrayList<Card> cards;

    public Package(ArrayList<Card> cards) {
        this.cards = cards;
    }
}
