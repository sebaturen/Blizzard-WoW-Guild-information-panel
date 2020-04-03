package com.blizzardPanel.gameObject;

public class Price {

    // Attribute
    private int gold;
    private int silver;
    private int copper;

    public Price() {

    }

    public Price(long itemPrice) {
        Price p = loadFromLong(itemPrice);
        this.gold = p.gold;
        this.silver = p.silver;
        this.copper = p.copper;
    }

    public Price(int gold, int silver, int copper) {
        this.gold = gold;
        this.silver = silver;
        this.copper = copper;
    }

    private Price loadFromLong(long itemPrice) {
        Price p = new Price();
        String price = ((Long) itemPrice).toString();
        int[] out = {0,0,0}; //[0-gold][1-silver][2-copper]
        if(price.length() > 4)
            out[0] = Integer.parseInt(price.substring(0,price.length()-4));
        if(price.length() >= 4)
            out[1] = Integer.parseInt(price.substring(price.length()-4,price.length()-2));
        if(price.length() >= 2)
            out[2] = Integer.parseInt(price.substring(price.length()-2,price.length()));
        else
            out[2] = Integer.parseInt(price);
        p.gold = out[0];
        p.silver = out[1];
        p.copper = out[2];
        return p;
    }

    public int getGold() {
        return gold;
    }

    public int getSilver() {
        return silver;
    }

    public int getCopper() {
        return copper;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Price\", " +
                "\"gold\":\"" + gold + "\"" + ", " +
                "\"silver\":\"" + silver + "\"" + ", " +
                "\"copper\":\"" + copper + "\"" +
                "}";
    }
}
