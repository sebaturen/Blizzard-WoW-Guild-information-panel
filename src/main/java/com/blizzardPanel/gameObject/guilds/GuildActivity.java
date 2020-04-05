/**
 * File : News.java
 * Desc : News Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guilds;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

public class GuildActivity {

    // Activity DB
    public static final String TABLE_NAME = "guild_activities";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private long guild_id;
    private String type;
    private long timestamp;
    private JsonObject detail;

    // Internal DATA
    //private Achievement CharacterAchievement???

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long activityId) {
            super(TABLE_NAME, GuildActivity.class);
            this.id = activityId;
        }

        public GuildActivity build() {
            return (GuildActivity) load(TABLE_KEY, id);
        }
    }

    // Constructor
    private GuildActivity() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Activity\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"guild_id\":\"" + guild_id + "\"" + ", " +
                "\"type\":" + (type == null ? "null" : "\"" + type + "\"") + ", " +
                "\"timestamp\":\"" + timestamp + "\"" + ", " +
                "\"detail\":" + (detail == null ? "null" : detail) +
                "}";
    }
}