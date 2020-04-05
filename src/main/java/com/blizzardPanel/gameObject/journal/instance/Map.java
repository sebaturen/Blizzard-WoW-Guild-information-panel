package com.blizzardPanel.gameObject.journal.instance;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

public class Map {

    // Map DB
    public static final String TABLE_NAME = "instance_maps";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long locationId) {
            super(TABLE_NAME, Map.class);
            this.id = locationId;
        }

        public Map build() {
            return (Map) load(TABLE_KEY, id);
        }
    }

    // Constructor
    private Map() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "{\"_class\":\"Location\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) +
                "}";
    }
}
