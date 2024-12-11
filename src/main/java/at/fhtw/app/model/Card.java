package at.fhtw.app.model;

public abstract class Card {
    private final int damage;


    protected Card(int damage) {
        this.damage = damage;
    }
}
