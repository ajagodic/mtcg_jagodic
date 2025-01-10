package at.fhtw.app.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class User {
    @JsonAlias({"Username"})
    private String username;
    @JsonAlias({"Password"})
    private String password;
    @JsonAlias({"CardStack"})
    private ArrayList<Card> cardstack;
    @JsonAlias({"Coins"})
    private int coins = 20;
    @JsonAlias({"Deck"})
    private ArrayList<Card> deck;



    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public User(String username, String password, ArrayList<Card> cardstack) {
        this.username = username;
        this.password = password;
        this.cardstack = cardstack;
    }
    public User(String username, String password, ArrayList<Card> cardstack, ArrayList<Card> deck) {
        this.username = username;
        this.password = password;
        this.cardstack = cardstack;
        this.deck = deck;
    }
    public void addCard(Card card){
        cardstack.add(card);
    }

}

