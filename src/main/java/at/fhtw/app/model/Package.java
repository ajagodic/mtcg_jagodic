package at.fhtw.app.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Package {
    // Getters and Setters
    private String id;
    private String name;
    private List<Card> cards; // Assume Card is another model in your application

    public Package(String id, String name, List<Card> cards) {
        this.id = id;
        this.name = name;
        this.cards = cards;
    }

}
