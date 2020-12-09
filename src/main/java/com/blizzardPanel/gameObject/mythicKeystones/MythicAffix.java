package com.blizzardPanel.gameObject.mythicKeystones;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.Media;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class MythicAffix {

    // Affix DB
    public static final String TABLE_NAME = "keystone_affixes";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;
    private JsonObject description;
    private long media_id;

    // Update Control
    private long last_modified;

    // Internal Data
    private Media media;

    public static class Builder extends DBLoadObject {

        private static Map<Long, MythicAffix> mythicAffixes = new HashMap<>();

        private long id;
        public Builder(long affixId) {
            super(TABLE_NAME, MythicAffix.class);
            this.id = affixId;
        }

        public MythicAffix build() {
            if (!mythicAffixes.containsKey(id)) {
                MythicAffix af = (MythicAffix) load(TABLE_KEY, id);
                if (af != null) {
                    mythicAffixes.put(id, af);
                }
            }
            return mythicAffixes.get(id);
        }
    }

    // Constructor
    private MythicAffix() {

    }

    private void loadMedia() {
        media = new Media.Builder(Media.type.KEYSTONE_AFFIX, media_id).build();
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getDescription(String locale) {
        return description.get(locale).getAsString();
    }

    public String getName(String locale) {
        return name.get(locale).getAsString();
    }

    public long getId() {
        return id;
    }

    public Media getMedia() {
        if (media == null) {
            loadMedia();
        }
        return media;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"MythicAffix\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"description\":" + (description == null ? "null" : description) + ", " +
                "\"media_id\":\"" + media_id + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"media\":" + (media == null ? "null" : media) +
                "}";
    }
}
