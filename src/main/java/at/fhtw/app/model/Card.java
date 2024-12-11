package at.fhtw.app.model;

public abstract class Card {
    private static int nextId = 1; //Counter damit immer eine neue ID zugewiesen werden kann
    private int id;
    private String name;
    private final double damage;


    public Card(String name, double damage) {
        this.id = nextId++; //zählt die ID immer um 1 Stelle hoch
        this.name = name;
        this.damage = damage;
    }
    public int getId() {
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
