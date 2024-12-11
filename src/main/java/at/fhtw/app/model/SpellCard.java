package at.fhtw.app.model;

public class SpellCard extends Card{
    protected SpellCard(String name,int damage) {
        super(name,damage);
    }

    @Override
    public String getCardType() {
        return "Spell";
    }
}
