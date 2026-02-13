package dnd.manager.app.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Stats {
    private int str;
    private int dex;
    private int con;
    private int inte;
    private int wis;
    private int cha;

    public Stats(){}

    public Stats(int strength, int dexterity, int constitution, int inte, int wisdom, int charisma) {
        this.str = strength;
        this.dex = dexterity;
        this.con = constitution;
        this.inte = inte;
        this.wis = wisdom;
        this.cha = charisma;
    }

    @Override
    public String toString() {
        return "Stats -> [str=" + str + ", dex=" + dex + ", con=" + con + ", inte=" + inte + ", wis=" + wis
                + ", cha=" + cha + "]";
    }
}
