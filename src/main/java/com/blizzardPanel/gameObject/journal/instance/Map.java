package com.blizzardPanel.gameObject.journal.instance;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class Map {

    // Map DB
    public static final String TABLE_NAME = "instance_maps";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;

    public static class Builder extends DBLoadObject {

        private static java.util.Map<Long, Map> maps = new HashMap<>();

        private long id;
        public Builder(long locationId) {
            super(TABLE_NAME, Map.class);
            this.id = locationId;
        }

        public Map build() {
            if (!maps.containsKey(id)) {
                maps.put(id, (Map) load(TABLE_KEY, id));
            }
            return maps.get(id);
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
