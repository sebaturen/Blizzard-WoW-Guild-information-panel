package com.blizzardPanel.gameObject;

public class WoWToken
{
    // Atribute
    private Price price;
    private long lastUpdate;

    public WoWToken(long price)
    {
        this.price = new Price(price);
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
