package com.blizzardPanel.gameObject;

import com.blizzardPanel.dbConnect.DBLoadObject;

public class WoWToken {

    // WoW Token DB
    public static final String TABLE_NAME = "wow_token";
    public static final String TABLE_KEY = "last_updated_timestamp";

    // DB Attribute
    private long last_updated_timestamp;
    private long price;

    // Internal DATA
    private Price obPrice;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long lastUpdate) {
            super(TABLE_NAME, WoWToken.class);
            this.id = lastUpdate;
        }
        public Builder() {
            super(TABLE_NAME, WoWToken.class);
        }

        public WoWToken build() {
            WoWToken newToken;
            if (id > 0) {
                newToken = (WoWToken) load(TABLE_KEY, id);
            } else {
                newToken = (WoWToken) load("1=1 ORDER BY last_updated_timestamp DESC limit 1", new String[]{});
            }

            // Load internal data
            newToken.obPrice = new Price(newToken.price);

            return newToken;
        }
    }

    // Constructor
    private WoWToken() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public Price getPrice() {
        return obPrice;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"WoWToken\", " +
                "\"last_updated_timestamp\":\"" + last_updated_timestamp + "\"" + ", " +
                "\"price\":\"" + price + "\"" + ", " +
                "\"obPrice\":" + (obPrice == null ? "null" : obPrice) +
                "}";
    }
}
