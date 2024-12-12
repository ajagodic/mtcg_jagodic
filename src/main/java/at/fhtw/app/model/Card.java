package at.fhtw.app.model;

public abstract class Card {
    private String id;
    private String name;
    private final double damage;


    public Card(String name, double damage) {
        this.id = id;
        this.name = name;
        this.damage = damage;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public double getDamage() {
        return damage;
    }

    public abstract String getCardType();

}
