package at.fhtw.app.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card {


    private String id;
    private String name;
    private final double damage;
    private final Type type;
    private final Element element;

    public enum Type {
        MONSTER,
        SPELL
    }
    public enum Element{
        FIRE,
        WATER,
        NORMAL
    }

    public Card(String name, double damage, Type type, Element element) {
        this.type = type;
        this.element = element;
        this.id = id;
        this.name = name;
        this.damage = damage;
    }

    public String getCardType(){
        return type.name();
    }

    @Override
    public String toString() {
        return "Card{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", damage=" + damage +
                ", type=" + type +
                ", element=" + element +
                '}';
    }

}
