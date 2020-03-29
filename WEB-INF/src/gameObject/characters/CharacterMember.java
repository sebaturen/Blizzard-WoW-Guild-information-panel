/**
 * File : Member.java
 * Desc : Character Object
 *
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class CharacterMember {

    // CharacterMember DB
    public static final String TABLE_NAME = "characters";
    public static final String TABLE_KEY = "id";


    // DB Attribute
    private long id;
    private String name;
    private String realm_slug;
    private boolean is_valid;
    private long blizzard_id;

    // Update control
    private long last_modified;
    private long specializations_last_modified;
    private long equipment_last_modified;
    private long statistics_last_modified;
    private long mythic_plus_last_modified;
    private String mythic_plus_seasons_last_modified;

    // Internal DATA
    private Realm realm;
    private CharacterInfo info;
    private CharacterItem item;
    private CharacterStats stats;
    private List<CharacterSpec> specs;

    public static class Builder extends GameObject2 {

        private long id;
        private boolean loadStatus = false;

        public Builder(long characterId) {
            super(TABLE_NAME, CharacterMember.class);
            this.id = characterId;
        }

        public Builder fullLoad(boolean loadStatus) {
            this.loadStatus = loadStatus;
            return this;
        }

        public CharacterMember build() {
            CharacterMember charMember = (CharacterMember) load(TABLE_KEY +"=?", id);

            // Load internal data:
            charMember.realm = new Realm.Builder(charMember.realm_slug).build();
            if (loadStatus && charMember.is_valid) {
                // load info, item, spec y mythicPlus
                charMember.loadInfo();
                charMember.loadItems();
                charMember.loadSpec();
            }

            return charMember;
        }
    }

    private CharacterMember() {

    }

    /**
     * Load a character information (character_info)
     */
    private void loadInfo() {
        try {
            JsonArray info_db = GameObject2.dbConnect.select(
                    CharacterInfo.TABLE_NAME,
                    new String[]{CharacterInfo.TABLE_KEY},
                    CharacterInfo.TABLE_KEY+"=?",
                    new String[]{id+""}
            );

            if (info_db.size() > 0) {
                JsonObject infoDetail = info_db.get(0).getAsJsonObject();
                info = new CharacterInfo.Builder(infoDetail.get(CharacterInfo.TABLE_KEY).getAsLong()).build();
            } else {
                Logs.infoLog(this.getClass(), "Character not have an Info ["+ id +"]");
            }
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a Character info ["+ id +"] - "+ e);
        }
    }

    /**
     * Load a character items (character_items)
     */
    private void loadItems() {

    }

    /**
     * Load a character spec (character_spec)
     */
    private void loadSpec() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"CharacterMember\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : "\"" + name + "\"") + ", " +
                "\"realm_slug\":" + (realm_slug == null ? "null" : "\"" + realm_slug + "\"") + ", " +
                "\"is_valid\":\"" + is_valid + "\"" + ", " +
                "\"blizzard_id\":\"" + blizzard_id + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"specializations_last_modified\":\"" + specializations_last_modified + "\"" + ", " +
                "\"equipment_last_modified\":\"" + equipment_last_modified + "\"" + ", " +
                "\"statistics_last_modified\":\"" + statistics_last_modified + "\"" + ", " +
                "\"mythic_plus_last_modified\":\"" + mythic_plus_last_modified + "\"" + ", " +
                "\"mythic_plus_seasons_last_modified\":\"SEASON_MODI\", " + //(mythic_plus_seasons_last_modified == null ? "null" : "\"" + mythic_plus_seasons_last_modified + "\"") + ", " +
                "\"realm\":" + (realm == null ? "null" : realm) + ", " +
                "\"info\":" + (info == null ? "null" : info) + ", " +
                "\"item\":" + (item == null ? "null" : item) + ", " +
                "\"stats\":" + (stats == null ? "null" : stats) + ", " +
                "\"specs\":" + (specs == null ? "null" : Arrays.toString(specs.toArray())) +
                "}";
    }
}
