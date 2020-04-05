/**
 * File : News.java
 * Desc : News Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guilds;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.guilds.activity.GuildActivityCharacterAchievement;
import com.blizzardPanel.gameObject.guilds.activity.GuildActivityEncounter;
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
    private GuildActivityCharacterAchievement characterAchievement;
    private GuildActivityEncounter guildEncounter;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long activityId) {
            super(TABLE_NAME, GuildActivity.class);
            this.id = activityId;
        }

        public GuildActivity build() {
            GuildActivity newActivity = (GuildActivity) load(TABLE_KEY, id);

            // Load internal data:
            switch ( newActivity.type) {
                case "CHARACTER_ACHIEVEMENT":
                    newActivity.characterAchievement = new GuildActivityCharacterAchievement(newActivity.detail);
                    break;
                case "ENCOUNTER":
                    newActivity.guildEncounter = new GuildActivityEncounter(newActivity.detail);
                    break;
            }

            return newActivity;
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

    public GuildActivityCharacterAchievement getCharacterAchievement() {
        return characterAchievement;
    }

    public GuildActivityEncounter getGuildEncounter() {
        return guildEncounter;
    }

    public long getTimestamp() {
        return timestamp;
    }

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
