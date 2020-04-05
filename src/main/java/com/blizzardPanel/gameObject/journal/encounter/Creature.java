package com.blizzardPanel.gameObject.journal.encounter;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.Media;
import com.google.gson.JsonObject;

public class Creature {

    // Creature encounter DB
    public static final String TABLE_NAME = "creatures";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;
    private long media_id;

    // Internal DATA
    private Media media;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long creatureId) {
            super(TABLE_NAME, Creature.class);
            this.id = creatureId;
        }

        public Creature build() {
            Creature newCreature = (Creature) load(TABLE_KEY, id);

            // Load internal data
            newCreature.media = new Media.Builder(Media.type.CREATURE, newCreature.media_id).build();

            return newCreature;
        }
    }

    // Constructor
    private Creature() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "{\"_class\":\"Creature\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"media_id\":\"" + media_id + "\"" + ", " +
                "\"media\":" + (media == null ? "null" : media) +
                "}";
    }
}
