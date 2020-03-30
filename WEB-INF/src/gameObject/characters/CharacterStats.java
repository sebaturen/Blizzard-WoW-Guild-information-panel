/**
 * File : StatusMember.java
 * Desc : Status from members
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.gameObject.GameObject2;
import com.blizzardPanel.gameObject.StaticInformation;

public class CharacterStats {

    // Status Character DB
    public static final String TABLE_NAME  = "character_stats";
    public static final String TABLE_KEY   = "character_id";

    // DB Attribute
    private long character_id;
    private int health;
    private int power;
    private String power_type;
    private String speed;
    private String strength;
    private String agility;
    private String intellect;
    private String stamina;
    private String melee;
    private String mastery;
    private String bonus_armor;
    private String lifesteal;
    private String versatility;
    private String avoidance;
    private int attack_power;
    private String hand;
    private String spell;
    private String mana;
    private String armor;
    private String dodge;
    private String parry;
    private String block;
    private String ranged;
    private String corruption;

    // Internal DATA
    private StaticInformation powerType;

    public static class Builder extends GameObject2 {

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

    @Override
    public String toString() {
        return "{\"_class\":\"CharacterStats\", " +
                "\"character_id\":\"" + character_id + "\"" + ", " +
                "\"health\":\"" + health + "\"" + ", " +
                "\"power\":\"" + power + "\"" + ", " +
                "\"power_type\":" + (power_type == null ? "null" : "\"" + power_type + "\"") + ", " +
                "\"speed\":" + (speed == null ? "null" : "\"" + speed + "\"") + ", " +
                "\"strength\":" + (strength == null ? "null" : "\"" + strength + "\"") + ", " +
                "\"agility\":" + (agility == null ? "null" : "\"" + agility + "\"") + ", " +
                "\"intellect\":" + (intellect == null ? "null" : "\"" + intellect + "\"") + ", " +
                "\"stamina\":" + (stamina == null ? "null" : "\"" + stamina + "\"") + ", " +
                "\"melee\":" + (melee == null ? "null" : "\"" + melee + "\"") + ", " +
                "\"mastery\":" + (mastery == null ? "null" : "\"" + mastery + "\"") + ", " +
                "\"bonus_armor\":" + (bonus_armor == null ? "null" : "\"" + bonus_armor + "\"") + ", " +
                "\"lifesteal\":" + (lifesteal == null ? "null" : "\"" + lifesteal + "\"") + ", " +
                "\"versatility\":" + (versatility == null ? "null" : "\"" + versatility + "\"") + ", " +
                "\"avoidance\":" + (avoidance == null ? "null" : "\"" + avoidance + "\"") + ", " +
                "\"attack_power\":\"" + attack_power + "\"" + ", " +
                "\"hand\":" + (hand == null ? "null" : "\"" + hand + "\"") + ", " +
                "\"spell\":" + (spell == null ? "null" : "\"" + spell + "\"") + ", " +
                "\"mana\":" + (mana == null ? "null" : "\"" + mana + "\"") + ", " +
                "\"armor\":" + (armor == null ? "null" : "\"" + armor + "\"") + ", " +
                "\"dodge\":" + (dodge == null ? "null" : "\"" + dodge + "\"") + ", " +
                "\"parry\":" + (parry == null ? "null" : "\"" + parry + "\"") + ", " +
                "\"block\":" + (block == null ? "null" : "\"" + block + "\"") + ", " +
                "\"ranged\":" + (ranged == null ? "null" : "\"" + ranged + "\"") + ", " +
                "\"corruption\":" + (corruption == null ? "null" : "\"" + corruption + "\"") + ", " +
                "\"powerType\":" + (powerType == null ? "null" : powerType) +
                "}";
    }
}
