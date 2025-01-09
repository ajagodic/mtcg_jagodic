package at.fhtw.app.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Trade {
    private String id;
    private String cardToTrade;
    private String owner;
    private int status;

    public Trade(String id, String cardToTrade, String owner, int status) {
        this.id = id;
        this.cardToTrade = cardToTrade;
        this.owner = owner;
        this.status = status;
    }
}
