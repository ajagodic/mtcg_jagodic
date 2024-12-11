package at.fhtw.app.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
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


    public User(){

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

