/**
 * File : Spell.java
 * Desc : Spell object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

public class Spell {

    // Spells DB
    public static final String TABLE_NAME = "spells";
    public static final String TABLE_KEY = "id";

    // Attribute
    private long id;
    private JsonObject name;
    private JsonObject description;
    private long media_id;
    private boolean is_valid;

    // Update Control
    private long last_modified;

    // Internal DATA:
    private Media media;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long spellId) {
            super(TABLE_NAME, Spell.class);
            this.id = spellId;
        }

        public Spell build() {
            Spell newSpell = (Spell) load(TABLE_KEY, id);

            // Load internal data:
            newSpell.media = new Media.Builder(Media.type.SPELL, newSpell.media_id).build();

            return newSpell;
        }
    }

    // Constructor
    private Spell() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"Spell\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\": \"NAME\", " + //(name == null ? "null" : "\"" + name + "\"") + ", " +
                "\"description\": \"DESC\", " + //(description == null ? "null" : "\"" + description + "\"") + ", " +
                "\"media_id\":\"" + media_id + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"media\":" + (media == null ? "null" : media) +
                "}";
    }
}
