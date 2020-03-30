/**
 * File : PlayableSpec.java
 * Desc : Playable spec info
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters.Static;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.GameObject2;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.StaticInformation;
import com.google.gson.JsonObject;

public class PlayableSpec {

    // Playable Specs DB
    public static final String TABLE_NAME = "playable_spec";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private long playable_class_id;
    private String name;
    private String role_type;
    private String desc_male;
    private String desc_female;
    private long media_id;

    // Update Control
    private long last_modified;

    // Internal DATA
    private PlayableClass playableClass;
    private StaticInformation role;
    private Media media;

    public static class Builder extends GameObject2 {

        private long id;
        public Builder(long specId) {
            super(TABLE_NAME, PlayableSpec.class);
            this.id = specId;
        }

        public PlayableSpec build() {
            PlayableSpec newSpec = (PlayableSpec) load(TABLE_KEY, id);

            // Load internal data:
            newSpec.playableClass = new PlayableClass.Builder(newSpec.playable_class_id).build();
            newSpec.role = new StaticInformation.Builder(newSpec.role_type).build();
            newSpec.media = new Media.Builder(newSpec.media_id).build();

            return newSpec;
        }
    }

    // Constructor
    private PlayableSpec() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"PlayableSpec\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"playable_class_id\":\"" + playable_class_id + "\"" + ", " +
                "\"name\": \"NAME\", " + //(name == null ? "null" : "\"" + name + "\"") + ", " +
                "\"role_type\":" + (role_type == null ? "null" : "\"" + role_type + "\"") + ", " +
                "\"desc_male\": \"DESCMALE\", " + //(desc_male == null ? "null" : "\"" + desc_male + "\"") + ", " +
                "\"desc_female\": \"DESCFEMALE\", " + //(desc_female == null ? "null" : "\"" + desc_female + "\"") + ", " +
                "\"media_id\":\"" + media_id + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"playableClass\":" + (playableClass == null ? "null" : playableClass) + ", " +
                "\"role\":" + (role == null ? "null" : role) + ", " +
                "\"media\":" + (media == null ? "null" : media) +
                "}";
    }
}
