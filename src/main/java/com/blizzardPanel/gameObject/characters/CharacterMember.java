/**
 * File : Member.java
 * Desc : Character Object
 *
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.*;

import com.blizzardPanel.gameObject.mythicKeystones.MythicDungeonRun;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.*;

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
    private long media_last_modified;
    private long mythic_plus_last_modified;
    private JsonObject mythic_plus_seasons_last_modified;

    // Internal DATA
    private Realm realm;
    private CharacterInfo info;
    private CharacterStats stats;
    private CharacterMedia media;
    private Map<String, CharacterItem> items;
    private int hoaLvl;
    private List<CharacterSpec> specs;
    private MythicDungeonRun bestMythicRun;

    public static class Builder extends DBLoadObject {

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
            CharacterMember charMember = (CharacterMember) load(TABLE_KEY, id);

            // Load internal data:
            if (charMember != null) {
                charMember.realm = new Realm.Builder(charMember.realm_slug).build();
                if (loadStatus && charMember.is_valid) {
                    // load info, item, spec y mythicPlus
                    charMember.loadInfo();
                    charMember.loadItems();
                    charMember.loadSpec();
                    charMember.loadStats();
                    charMember.loadBestMythicRun();
                    charMember.loadMedia();
                }
            }

            return charMember;
        }
    }

    // Constructor
    private CharacterMember() {

    }

    /**
     * Load a character information (character_info)
     */
    private void loadInfo() {
        info = new CharacterInfo.Builder(id).build();
    }

    /**
     * Load a character items (character_items)
     */
    private void loadItems() {
        items = new HashMap<>();
        try {
            JsonArray items_db = DBLoadObject.dbConnect.select(
                    CharacterItem.TABLE_NAME,
                    new String[]{CharacterItem.TABLE_KEY, "slot_type"},
                    "character_id=?",
                    new String[]{id+""}
            );

            if (items_db.size() > 0) {
                for (JsonElement itemDb : items_db) {
                    JsonObject itemDetail = itemDb.getAsJsonObject();
                    CharacterItem newItem = new CharacterItem.Builder(itemDetail.get(CharacterItem.TABLE_KEY).getAsLong()).build();
                    if (newItem.getSlot_type().equals("NECK")) {
                        hoaLvl = newItem.getAzerite_level();
                    }
                    items.put(
                            newItem.getSlot_type(),
                            newItem
                    );
                }
            } else {
                Logs.infoLog(this.getClass(), "Character not have an Items ["+ id +"]");
            }
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a Character items ["+ id +"]");
        }
    }

    /**
     * Load a character spec (character_spec)
     */
    private void loadSpec() {
        specs = new ArrayList<>();
        try {
            JsonArray specs_db = DBLoadObject.dbConnect.selectQuery(
                    "SELECT " +
                    "   id " +
                    "FROM " +
                    "   character_specs " +
                    "WHERE " +
                    "   character_id = "+ id +" " +
                    "ORDER BY " +
                    "   `enable` DESC"
            );

            if (specs_db.size() > 0) {
                for (JsonElement specDb : specs_db) {
                    JsonObject specDetail = specDb.getAsJsonObject();
                    specs.add(new CharacterSpec.Builder(specDetail.get(CharacterSpec.TABLE_KEY).getAsLong()).build());
                }
            } else {
                Logs.infoLog(this.getClass(), "Character not have a spec");
            }
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a Character spec ["+ id +"]");
        }
    }

    /**
     * Load a character statistics (character_stats)
     */
    private void loadStats() {
        stats = new CharacterStats.Builder(id).build();
    }

    private void loadMedia() {
        media = new CharacterMedia.Builder(id).build();
    }

    private void loadBestMythicRun() {
        try {
            JsonArray myRunDB = DBLoadObject.dbConnect.selectQuery(
                    "SELECT " +
                    "    kr.id " +
                    "FROM " +
                    "    keystone_dungeon_run kr, " +
                    "    keystone_dungeon_run_members km " +
                    "WHERE " +
                    "    kr.id = km.keystone_dungeon_run_id " +
                    "    AND kr.completed_timestamp > "+ ServerTime.getLastResetTime() +" " +
                    "    AND km.character_id = '"+ id +"' " +
                    "ORDER BY " +
                    "    kr.keystone_level DESC " +
                    "LIMIT 1;"
            );

            for(JsonElement myRun : myRunDB) {
                bestMythicRun = new MythicDungeonRun.Builder(myRun.getAsJsonObject().get(MythicDungeonRun.TABLE_KEY).getAsLong()).build();
            }
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a best mythic plis! "+ e);
        }
    }

    /**
     * Set a active spec from specific ID
     * @param specId
     */
    public void setActiveSpec(long specId) {
        if (specs == null) {
            loadSpec();
        }
        specs.forEach(spec -> {
            spec.setEnable((spec.getSpecialization_id() == specId));
        });
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CharacterInfo getInfo() {
        if (info == null) {
            loadInfo();
        }
        return info;
    }

    public List<CharacterSpec> getSpecs() {
        return specs;
    }

    public CharacterSpec getActiveSpec() {
        if (specs == null) {
            loadSpec();
        }
        for (CharacterSpec spec : specs) {
            if (spec.isEnable()) {
                return spec;
            }
        }
        return null;
    }

    public CharacterMedia getMedia() {
        if (media == null) {
            loadMedia();
        }
        return media;
    }

    public Realm getRealm() {
        return realm;
    }

    public boolean isIs_valid() {
        return is_valid;
    }

    public MythicDungeonRun getBestMythicRun() {
        if (bestMythicRun == null) {
            loadBestMythicRun();
        }
        return bestMythicRun;
    }

    public Map<String, CharacterItem> getItems() {
        if (items == null) {
            loadItems();
        }
        return items;
    }

    public int getHoaLvl() {
        if (items == null) {
            loadItems();
        }
        return hoaLvl;
    }

    public CharacterStats getStats() {
        if (stats == null) {
            loadStats();
        }
        return stats;
    }

    public CharacterItem getItem(String slot) {
        if (items.containsKey(slot)) {
            return items.get(slot);
        }
        return new CharacterItem.Builder(slot).build();
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
                "\"media_last_modified\":\"" + media_last_modified + "\"" + ", " +
                "\"mythic_plus_last_modified\":\"" + mythic_plus_last_modified + "\"" + ", " +
                "\"mythic_plus_seasons_last_modified\":" + (mythic_plus_seasons_last_modified == null ? "null" : mythic_plus_seasons_last_modified) + ", " +
                "\"realm\":" + (realm == null ? "null" : realm) + ", " +
                "\"info\":" + (info == null ? "null" : info) + ", " +
                "\"stats\":" + (stats == null ? "null" : stats) + ", " +
                "\"media\":" + (media == null ? "null" : media) + ", " +
                "\"items\":" + (items == null ? "null" : "\"" + items + "\"") + ", " +
                "\"hoaLvl\":\"" + hoaLvl + "\"" + ", " +
                "\"specs\":" + (specs == null ? "null" : Arrays.toString(specs.toArray())) + ", " +
                "\"bestMythicRun\":" + (bestMythicRun == null ? "null" : bestMythicRun) +
                "}";
    }
}
