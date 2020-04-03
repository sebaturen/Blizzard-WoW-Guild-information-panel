package com.blizzardPanel.gameObject.mythicKeystones;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

public class MythicDungeon {

    // Dungeon DB
    public static final String TABLE_NAME = "keystone_dungeon";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;
    private String slug;
    private long keystone_upgrades_1;
    private long keystone_upgrades_2;
    private long keystone_upgrades_3;

    // Update control
    private long last_modified;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long dungeonId) {
            super(TABLE_NAME, MythicDungeon.class);
            this.id = dungeonId;
        }

        public MythicDungeon build() {
            return (MythicDungeon) load(TABLE_KEY, id);
        }
    }

    // Constructor
    private MythicDungeon() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public long getId() {
        return id;
    }

    public String getName(String locale) {
        return name.get(locale).getAsString();
    }

    public void setName(JsonObject name) {
        this.name = name;
    }

    public long getKeystone_upgrades_1() {
        return keystone_upgrades_1;
    }

    public long getKeystone_upgrades_2() {
        return keystone_upgrades_2;
    }

    public long getKeystone_upgrades_3() {
        return keystone_upgrades_3;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"MythicDungeon\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"slug\":" + (slug == null ? "null" : "\"" + slug + "\"") + ", " +
                "\"keystone_upgrades_1\":\"" + keystone_upgrades_1 + "\"" + ", " +
                "\"keystone_upgrades_2\":\"" + keystone_upgrades_2 + "\"" + ", " +
                "\"keystone_upgrades_3\":\"" + keystone_upgrades_3 + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" +
                "}";
    }
}
