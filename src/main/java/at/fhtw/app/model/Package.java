package at.fhtw.app.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Package {

    private String id;
    private String name;
    private List<Card> cards;

    public Package(String id, String name, List<Card> cards) {
        this.id = id;
        this.name = name;
        this.cards = cards;
    }

}
