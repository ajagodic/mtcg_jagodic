package at.fhtw.app.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Package {

    //private static int idCounter = 1;
    private int id;
    private List<Card> cards;

    public Package(List<Card> cards) {
        this.cards = cards;
    }



}
