/**
 * File : PlayableClass.java
 * Desc : Playable class object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters.playable;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.Media;
import com.google.gson.JsonObject;

public class PlayableClass {

    // Playable Class DB
    public static final String TABLE_NAME = "playable_class";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;
    private JsonObject gender_name_male;
    private JsonObject gender_name_female;
    private long media_id;

    // Update control
    private long last_modified;

    // Internal DATA
    private Media media;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long classId) {
            super(TABLE_NAME, PlayableClass.class);
            this.id = classId;
        }

        public PlayableClass build() {
            PlayableClass newClass = (PlayableClass) load(TABLE_KEY, id);
            newClass.media = new Media.Builder(newClass.media_id).build();
            return newClass;
        }
    }

    // Constructor
    private PlayableClass() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public long getId() {
        return id;
    }

    public Media getMedia() {
        return media;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Class\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"gender_name_male\":" + (gender_name_male == null ? "null" : gender_name_male) + ", " +
                "\"gender_name_female\":" + (gender_name_female == null ? "null" : gender_name_female) + ", " +
                "\"last_modified\":\"" + last_modified + "\"" +
                "}";
    }
}