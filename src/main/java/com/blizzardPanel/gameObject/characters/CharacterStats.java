/**
 * File : StatusMember.java
 * Desc : Status from members
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.StaticInformation;
import com.google.gson.JsonObject;

public class CharacterStats {

    // Status Character DB
    public static final String TABLE_NAME  = "character_stats";
    public static final String TABLE_KEY   = "character_id";

    // DB Attribute
    private long character_id;
    private int health;
    private int power;
    private String power_type;
    private JsonObject speed;
    private JsonObject strength;
    private JsonObject agility;
    private JsonObject intellect;
    private JsonObject stamina;
    private JsonObject melee;
    private JsonObject mastery;
    private int bonus_armor;
    private JsonObject lifesteal;
    private JsonObject versatility;
    private JsonObject avoidance;
    private int attack_power;
    private JsonObject hand;
    private JsonObject spell;
    private JsonObject mana;
    private JsonObject armor;
    private JsonObject dodge;
    private JsonObject parry;
    private JsonObject block;
    private JsonObject ranged;
    private JsonObject corruption;

    // Internal DATA
    private StaticInformation powerType;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long characterId) {
            super(TABLE_NAME, CharacterStats.class);
            this.id = characterId;
        }

        public CharacterStats build() {
            CharacterStats newStats = (CharacterStats) load(TABLE_KEY, id);

            // Load internal data:
            newStats.powerType = new StaticInformation.Builder(newStats.power_type).build();

            return newStats;
        }
    }

    // Constructor
    private CharacterStats() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------


    @Override
    public String toString() {
        return "{\"_class\":\"CharacterStats\", " +
                "\"character_id\":\"" + character_id + "\"" + ", " +
                "\"health\":\"" + health + "\"" + ", " +
                "\"power\":\"" + power + "\"" + ", " +
                "\"power_type\":" + (power_type == null ? "null" : "\"" + power_type + "\"") + ", " +
                "\"speed\":" + (speed == null ? "null" : speed) + ", " +
                "\"strength\":" + (strength == null ? "null" : strength) + ", " +
                "\"agility\":" + (agility == null ? "null" : agility) + ", " +
                "\"intellect\":" + (intellect == null ? "null" : intellect) + ", " +
                "\"stamina\":" + (stamina == null ? "null" : stamina) + ", " +
                "\"melee\":" + (melee == null ? "null" : melee) + ", " +
                "\"mastery\":" + (mastery == null ? "null" : mastery) + ", " +
                "\"bonus_armor\":\"" + bonus_armor + "\"" + ", " +
                "\"lifesteal\":" + (lifesteal == null ? "null" : lifesteal) + ", " +
                "\"versatility\":" + (versatility == null ? "null" : versatility) + ", " +
                "\"avoidance\":" + (avoidance == null ? "null" : avoidance) + ", " +
                "\"attack_power\":\"" + attack_power + "\"" + ", " +
                "\"hand\":" + (hand == null ? "null" : hand) + ", " +
                "\"spell\":" + (spell == null ? "null" : spell) + ", " +
                "\"mana\":" + (mana == null ? "null" : mana) + ", " +
                "\"armor\":" + (armor == null ? "null" : armor) + ", " +
                "\"dodge\":" + (dodge == null ? "null" : dodge) + ", " +
                "\"parry\":" + (parry == null ? "null" : parry) + ", " +
                "\"block\":" + (block == null ? "null" : block) + ", " +
                "\"ranged\":" + (ranged == null ? "null" : ranged) + ", " +
                "\"corruption\":" + (corruption == null ? "null" : corruption) + ", " +
                "\"powerType\":" + (powerType == null ? "null" : powerType) +
                "}";
    }
}
