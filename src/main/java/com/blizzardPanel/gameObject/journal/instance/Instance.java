package com.blizzardPanel.gameObject.journal.instance;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.StaticInformation;
import com.blizzardPanel.gameObject.journal.instance.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

public class Instance {

    // Instance DB
    public static final String TABLE_NAME = "instances";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;
    private long map_id;
    private long area_id;
    private JsonObject description;
    private long expansion_id;
    private long location_id;
    private JsonArray modes;
    private long media_id;
    private int minimum_level;
    private String category_type;

    // Update control
    private long last_modified;

    // Internal DATa
    private Map map;
    private Location location;
    private Area area;
    private Expansion expansion;
    private Media media;
    private List<StaticInformation> modesList;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long instanceId) {
            super(TABLE_NAME, Instance.class);
            this.id = instanceId;
        }

        public Instance build() {
            Instance newInstance = (Instance) load(TABLE_KEY, id);

            // Load internal data
            newInstance.media = new Media.Builder(Media.type.INSTANCE, newInstance.media_id).build();

            return newInstance;
        }
    }

    // Constructor
    private Instance() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public Media getMedia() {
        return media;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Instance\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"map_id\":\"" + map_id + "\"" + ", " +
                "\"area_id\":\"" + area_id + "\"" + ", " +
                "\"description\":" + (description == null ? "null" : description) + ", " +
                "\"expansion_id\":\"" + expansion_id + "\"" + ", " +
                "\"location_id\":\"" + location_id + "\"" + ", " +
                "\"modes\":" + (modes == null ? "null" : modes) + ", " +
                "\"media_id\":\"" + media_id + "\"" + ", " +
                "\"minimum_level\":\"" + minimum_level + "\"" + ", " +
                "\"category_type\":" + (category_type == null ? "null" : "\"" + category_type + "\"") + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"map\":" + (map == null ? "null" : map) + ", " +
                "\"location\":" + (location == null ? "null" : location) + ", " +
                "\"area\":" + (area == null ? "null" : area) + ", " +
                "\"expansion\":" + (expansion == null ? "null" : expansion) + ", " +
                "\"media\":" + (media == null ? "null" : media) + ", " +
                "\"modesList\":" + (modesList == null ? "null" : Arrays.toString(modesList.toArray())) +
                "}";
    }
}
