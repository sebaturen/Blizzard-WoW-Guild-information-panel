package com.blizzardPanel.gameObject.mythicKeystones;

import com.blizzardPanel.dbConnect.DBLoadObject;

public class MythicSeason {

    // Season DB
    public static final String TABLE_NAME = "keystone_seasons";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private long start_timestamp;
    private long end_timestamp;

    // Update control
    private long last_modified;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long seasonId) {
            super(TABLE_NAME, MythicSeason.class);
            this.id = seasonId;
        }

        public MythicSeason build() {
            return (MythicSeason) load(TABLE_KEY, id);
        }
    }

    // Constructor
    private MythicSeason() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public long getStart_timestamp() {
        return start_timestamp;
    }

    public long getEnd_timestamp() {
        return end_timestamp;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"MythicSeason\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"start_timestamp\":\"" + start_timestamp + "\"" + ", " +
                "\"end_timestamp\":\"" + end_timestamp + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" +
                "}";
    }
}
