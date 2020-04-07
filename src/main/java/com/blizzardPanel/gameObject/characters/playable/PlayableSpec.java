/**
 * File : PlayableSpec.java
 * Desc : Playable spec info
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters.playable;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.StaticInformation;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class PlayableSpec {

    // Playable Specs DB
    public static final String TABLE_NAME = "playable_spec";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private long playable_class_id;
    private JsonObject name;
    private String role_type;
    private JsonObject desc_male;
    private JsonObject desc_female;
    private long media_id;

    // Update Control
    private long last_modified;

    // Internal DATA
    private PlayableClass playableClass;
    private StaticInformation role;
    private Media media;

    public static class Builder extends DBLoadObject {

        private static Map<Long, PlayableSpec> playableSpecs = new HashMap<>();

        private long id;
        public Builder(long specId) {
            super(TABLE_NAME, PlayableSpec.class);
            this.id = specId;
        }

        public PlayableSpec build() {
            if (!playableSpecs.containsKey(id)) {
                PlayableSpec newPlayableSpec = (PlayableSpec) load(TABLE_KEY, id);

                // Load internal data:
                newPlayableSpec.playableClass = new PlayableClass.Builder(newPlayableSpec.playable_class_id).build();
                newPlayableSpec.role = new StaticInformation.Builder(newPlayableSpec.role_type).build();
                newPlayableSpec.media = new Media.Builder(Media.type.P_SPEC, newPlayableSpec.media_id).build();

                playableSpecs.put(id, newPlayableSpec);
            }
            return playableSpecs.get(id);
        }
    }

    // Constructor
    private PlayableSpec() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public Media getMedia() {
        return media;
    }

    public StaticInformation getRole() {
        return role;
    }

    public long getId() {
        return id;
    }

    public PlayableClass getPlayableClass() {
        return playableClass;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Spec\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"playable_class_id\":\"" + playable_class_id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"role_type\":" + (role_type == null ? "null" : "\"" + role_type + "\"") + ", " +
                "\"desc_male\":" + (desc_male == null ? "null" : desc_male) + ", " +
                "\"desc_female\":" + (desc_female == null ? "null" : desc_female) + ", " +
                "\"media_id\":\"" + media_id + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"playableClass\":" + (playableClass == null ? "null" : playableClass) + ", " +
                "\"role\":" + (role == null ? "null" : role) + ", " +
                "\"media\":" + (media == null ? "null" : media) +
                "}";
    }
}
