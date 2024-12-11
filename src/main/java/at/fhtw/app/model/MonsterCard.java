package at.fhtw.app.model;

public class MonsterCard extends Card {
    protected MonsterCard(String name,int damage) {
        super(name,damage);
    }

    @Override
    public String getCardType() {
        return "Monster";
    }
}
