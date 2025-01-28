package at.fhtw.app.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

public class Card {

    @JsonProperty("Id")
    private String id;
    @JsonProperty("Name")
    private CardName name;
    @JsonProperty("Damage")
    private double damage;
    @JsonProperty
    private Type type;
    @JsonProperty
    private Element element;
    @JsonFormat(shape = JsonFormat.Shape.STRING)

    public enum CardName {
        WaterGoblin, FireGoblin, RegularGoblin,
        WaterTroll, FireTroll, RegularTroll,
        WaterElf, FireElf, RegularElf,
        WaterSpell, FireSpell, RegularSpell,
        Knight, Dragon, Ork, Kraken, Wizzard;

        @com.fasterxml.jackson.annotation.JsonCreator
        public static CardName fromString(String value) {
            for (CardName cardName : CardName.values()) {
                if (cardName.name().equalsIgnoreCase(value)) {
                    return cardName;
                }
            }
            throw new IllegalArgumentException("Unknown card name: " + value);
        }
    }


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public enum Type {
        MONSTER,
        SPELL
    }
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public enum Element{
        FIRE,
        WATER,
        NORMAL
    }
    public Card(){

    }


    public Card(CardName name, double damage, Type type, Element element) {
        this.type = type;
        this.element = element;
        this.name = name;
        this.damage = damage;
    }

    public Card(String id, CardName name, double damage, Type type, Element element) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.type = type;
        this.element = element;
    }

    public Card(String id, CardName name, double damage){
        this.id = id;
        this.name = name;
        this.damage = damage;
        type = null;
        element = null;
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
