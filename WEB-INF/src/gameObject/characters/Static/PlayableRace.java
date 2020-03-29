/**
 * File : Race.java
 * Desc : Race object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters.Static;

import com.blizzardPanel.gameObject.GameObject2;
import com.blizzardPanel.gameObject.StaticInformation;

public class PlayableRace {

    // Playable Race DB
    public static final String TABLE_NAME = "playable_races";
    public static final String TABLE_KEY = "id";
    
    // Attribute
    private long id;
    private String name;
    private String gender_name_male;
    private String gender_name_female;
    private String faction_type;
    private Boolean is_selectable;
    private Boolean is_allied_race;

    // Update Control
    private long last_modified;

    // Internal DATA
    private StaticInformation faction;

    public static class Builder extends GameObject2 {

        private long id;
        public Builder(long raceId) {
            super(TABLE_NAME, PlayableRace.class);
            this.id = raceId;
        }

        public PlayableRace build() {
            PlayableRace newRace = (PlayableRace) load(TABLE_KEY+"=?", id);
            newRace.faction = new StaticInformation.Builder(newRace.faction_type).build();
            return newRace;
        }
    }

    private PlayableRace() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"PlayableRace\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":\"NAME\", " + //name + "\"" + ", " +
                "\"gender_name_male\":\"GENDER_MALE\", " + //(gender_name_male == null ? "null" : "\"" + gender_name_male + "\"") + ", " +
                "\"gender_name_female\":\"GENDER_FEMALE\", " + //(gender_name_female == null ? "null" : "\"" + gender_name_female + "\"") + ", " +
                "\"faction_type\":" + (faction_type == null ? "null" : "\"" + faction_type + "\"") + ", " +
                "\"is_selectable\":" + (is_selectable == null ? "null" : "\"" + is_selectable + "\"") + ", " +
                "\"is_allied_race\":" + (is_allied_race == null ? "null" : "\"" + is_allied_race + "\"") + ", " +
                "\"last_modified\":\"" + last_modified + "\"" +
                "}";
    }
}