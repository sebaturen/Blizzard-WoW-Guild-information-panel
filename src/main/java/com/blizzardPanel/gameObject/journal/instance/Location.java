package com.blizzardPanel.gameObject.journal.instance;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Location {

    // Location DB
    public static final String TABLE_NAME = "instance_location";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;

    public static class Builder extends DBLoadObject {

        private static Map<Long, Location> locations = new HashMap<>();

        private long id;
        public Builder(long locationId) {
            super(TABLE_NAME, Location.class);
            this.id = locationId;
        }

        public Location build() {
            if (!locations.containsKey(id)) {
                locations.put(id, (Location) load(TABLE_KEY, id));
            }
            return locations.get(id);
        }
    }

    // Constructor
    private Location() {

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
