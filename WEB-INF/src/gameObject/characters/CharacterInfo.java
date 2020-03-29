package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.gameObject.GameObject2;
import com.blizzardPanel.gameObject.StaticInformation;
import com.blizzardPanel.gameObject.characters.Static.PlayableClass;
import com.blizzardPanel.gameObject.characters.Static.PlayableRace;

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
    private String bestMythicPlusScore;
    private String mythicPlusScores;
    private long guild_id;
    private long last_login;
    private int average_item_level;
    private int equipped_item_level;

    // Internal DATA
    private PlayableRace race;
    private PlayableClass charClass;
    private StaticInformation gender;
    private StaticInformation faction;

    public static class Builder extends GameObject2 {

        private long id;
        public Builder(long characterMemberID) {
            super(TABLE_NAME, CharacterInfo.class);
            this.id = characterMemberID;
        }

        public CharacterInfo build() {
            CharacterInfo newInfo = (CharacterInfo) load(TABLE_KEY+"=?", id);
            newInfo.race = new PlayableRace.Builder(newInfo.race_id).build();
            newInfo.charClass = new PlayableClass.Builder(newInfo.character_class_id).build();
            newInfo.gender = new StaticInformation.Builder(newInfo.gender_type).build();
            newInfo.faction = new StaticInformation.Builder(newInfo.faction_type).build();
            return newInfo;
        }
    }

    private CharacterInfo() {

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
                "\"bestMythicPlusScore\": \"BEST_MPlus\", " + //(bestMythicPlusScore == null ? "null" : "\"" + bestMythicPlusScore + "\"") + ", " +
                "\"mythicPlusScores\": \"MSCORE\", " + //(mythicPlusScores == null ? "null" : "\"" + mythicPlusScores + "\"") + ", " +
                "\"guild_id\":\"" + guild_id + "\"" + ", " +
                "\"last_login\":\"" + last_login + "\"" + ", " +
                "\"average_item_level\":\"" + average_item_level + "\"" + ", " +
                "\"equipped_item_level\":\"" + equipped_item_level + "\"" + ", " +
                "\"race\":" + (race == null ? "null" : race) + ", " +
                "\"gender\":" + (gender == null ? "null" : gender) + ", " +
                "\"faction\":" + (faction == null ? "null" : faction) +
                "}";
    }
}
