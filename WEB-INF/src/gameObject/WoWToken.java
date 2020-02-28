package com.blizzardPanel.gameObject;

public class WoWToken
{
    // Atribute
    private int gold;
    private int silver;
    private int copper;
    private long lastUpdate;

    public WoWToken(int gold, int silver, int copper)
    {
        this.gold = gold;
        this.silver = silver;
        this.copper = copper;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getSilver() {
        return silver;
    }

    public void setSilver(int silver) {
        this.silver = silver;
    }

    public int getCopper() {
        return copper;
    }

    public void setCopper(int copper) {
        this.copper = copper;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
