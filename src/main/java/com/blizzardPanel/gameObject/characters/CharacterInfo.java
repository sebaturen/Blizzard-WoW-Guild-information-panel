package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.StaticInformation;
import com.blizzardPanel.gameObject.characters.playable.PlayableClass;
import com.blizzardPanel.gameObject.characters.playable.PlayableRace;
import com.google.gson.JsonObject;

public class CharacterInfo {

    // Info Character DB
    public static final String TABLE_NAME = "character_info";
    public static final String TABLE_KEY = "character_id";

    // DB Attribute
    private long character_id;
    private long character_class_id;
    private long race_id;
    private String gender_type;
    private int level;
    private long achievement_points;
    private String faction_type;
    private JsonObject bestMythicPlusScore;
    private JsonObject mythicPlusScores;
    private long guild_id;
    private long last_login;
    private int average_item_level;
    private int equipped_item_level;

    // Internal DATA
    private PlayableRace playableRace;
    private PlayableClass playableClass;
    private StaticInformation gender;
    private StaticInformation faction;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long characterMemberID) {
            super(TABLE_NAME, CharacterInfo.class);
            this.id = characterMemberID;
        }

        public CharacterInfo build() {
            CharacterInfo newInfo = (CharacterInfo) load(TABLE_KEY, id);
            if (newInfo != null) {
                newInfo.playableRace = new PlayableRace.Builder(newInfo.race_id).build();
                newInfo.playableClass = new PlayableClass.Builder(newInfo.character_class_id).build();
                newInfo.gender = new StaticInformation.Builder(newInfo.gender_type).build();
                newInfo.faction = new StaticInformation.Builder(newInfo.faction_type).build();
            }
            return newInfo;
        }
    }

    // Constructor
    private CharacterInfo() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public PlayableRace getPlayableRace() {
        return playableRace;
    }

    public int getAverage_item_level() {
        return average_item_level;
    }

    public int getEquipped_item_level() {
        return equipped_item_level;
    }

    public int getLevel() {
        return level;
    }

    public PlayableClass getPlayableClass() {
        return playableClass;
    }

    public StaticInformation getFaction() {
        return faction;
    }

    public long getGuild_id() {
        return guild_id;
    }

    public JsonObject getBestMythicPlusScore() {
        return bestMythicPlusScore;
    }

    public JsonObject getMythicPlusScores() {
        return mythicPlusScores;
    }

    public long getCharacter_class_id() {
        return character_class_id;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"CharacterInfo\", " +
                "\"character_id\":\"" + character_id + "\"" + ", " +
                "\"character_class_id\":\"" + character_class_id + "\"" + ", " +
                "\"race_id\":\"" + race_id + "\"" + ", " +
                "\"gender_type\":" + (gender_type == null ? "null" : "\"" + gender_type + "\"") + ", " +
                "\"level\":\"" + level + "\"" + ", " +
                "\"achievement_points\":\"" + achievement_points + "\"" + ", " +
                "\"faction_type\":" + (faction_type == null ? "null" : "\"" + faction_type + "\"") + ", " +
                "\"bestMythicPlusScore\":" + (bestMythicPlusScore == null ? "null" : bestMythicPlusScore) + ", " +
                "\"mythicPlusScores\":" + (mythicPlusScores == null ? "null" : mythicPlusScores) + ", " +
                "\"guild_id\":\"" + guild_id + "\"" + ", " +
                "\"last_login\":\"" + last_login + "\"" + ", " +
                "\"average_item_level\":\"" + average_item_level + "\"" + ", " +
                "\"equipped_item_level\":\"" + equipped_item_level + "\"" + ", " +
                "\"playableRace\":" + (playableRace == null ? "null" : playableRace) + ", " +
                "\"playableClass\":" + (playableClass == null ? "null" : playableClass) + ", " +
                "\"gender\":" + (gender == null ? "null" : gender) + ", " +
                "\"faction\":" + (faction == null ? "null" : faction) +
                "}";
    }
}
