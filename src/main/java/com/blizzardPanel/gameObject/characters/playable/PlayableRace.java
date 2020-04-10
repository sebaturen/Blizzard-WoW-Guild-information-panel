/**
 * File : Race.java
 * Desc : Race object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters.playable;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.StaticInformation;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class PlayableRace {

    // Playable Race DB
    public static final String TABLE_NAME = "playable_races";
    public static final String TABLE_KEY = "id";

    // Attribute
    private long id;
    private JsonObject name;
    private JsonObject gender_name_male;
    private JsonObject gender_name_female;
    private String faction_type;
    private boolean is_selectable;
    private boolean is_allied_race;

    // Update Control
    private long last_modified;

    // Internal DATA
    private StaticInformation faction;

    public static class Builder extends DBLoadObject {

        private static Map<Long, PlayableRace> playableRaces = new HashMap<>();

        private long id;
        public Builder(long raceId) {
            super(TABLE_NAME, PlayableRace.class);
            this.id = raceId;
        }

        public PlayableRace build() {
            if (!playableRaces.containsKey(id)) {
                PlayableRace newPlayableRace = (PlayableRace) load(TABLE_KEY, id);
                newPlayableRace.faction = new StaticInformation.Builder(newPlayableRace.faction_type).build();
                playableRaces.put(id, newPlayableRace);
            }
            return playableRaces.get(id);
        }
    }

    // Constructor
    private PlayableRace() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getFaction_type() {
        return faction_type;
    }

    public boolean isIs_selectable() {
        return is_selectable;
    }

    public String getName(String locale) {
        return name.get(locale).getAsString();
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Race\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"gender_name_male\":" + (gender_name_male == null ? "null" : gender_name_male) + ", " +
                "\"gender_name_female\":" + (gender_name_female == null ? "null" : gender_name_female) + ", " +
                "\"faction_type\":" + (faction_type == null ? "null" : "\"" + faction_type + "\"") + ", " +
                "\"is_selectable\":\"" + is_selectable + "\"" + ", " +
                "\"is_allied_race\":\"" + is_allied_race + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"faction\":" + (faction == null ? "null" : faction) +
                "}";
    }
}