package com.blizzardPanel.gameObject.journal.instance;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.journal.encounter.Creature;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Area {

    // Area DB
    public static final String TABLE_NAME = "instance_area";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;

    public static class Builder extends DBLoadObject {

        private static Map<Long, Area> areas = new HashMap<>();

        private long id;
        public Builder(long locationId) {
            super(TABLE_NAME, Area.class);
            this.id = locationId;
        }

        public Area build() {
            if (!areas.containsKey(id)) {
                areas.put(id, (Area) load(TABLE_KEY, id));
            }
            return areas.get(id);
        }
    }

    // Constructor
    private Area() {

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
