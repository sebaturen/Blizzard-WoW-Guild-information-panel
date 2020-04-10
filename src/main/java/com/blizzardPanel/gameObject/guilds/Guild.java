/**
 * File : Guild.java
 * Desc : Guild Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guilds;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.*;
import com.blizzardPanel.gameObject.achievements.Achievement;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.*;

public class Guild {

    // Guild DB
    public static final String TABLE_NAME = "guild_info";
    public static final String TABLE_KEY = "id";
    // Achievement Guild DB
    public static final String ACHIEVEMENT_TABLE_NAME = "guild_achievements";
    public static final String ACHIEVEMENT_TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private String name;
    private long realm_id;
    private String faction_type;
    private long achievement_points;
    private long created_timestamp;
    private int member_count;

    // Update control
    private long last_modified;
    private long achievement_last_modified;
    private long roster_last_modified;
    private long activities_last_modified;
    private boolean full_sync;

    // Internal DATA
    private Realm realm;
    private StaticInformation faction;
    private List<Achievement> achievements;
    private List<GuildActivity> activities;
    private List<GuildRoster> guildRosters;
    private List<GuildRank> guildRanks;

    // RunTime update control time
    private Date lastRosterUpdate;
    private Date lastActivitiesUpdate;

    public static class Builder extends DBLoadObject {

        private long id;
        private boolean loadStatus = false;
        private int maxActivities = 10;

        public Builder(long guildId) {
            super(TABLE_NAME, Guild.class);
            this.id = guildId;
        }

        /**
         * Enable load Achievements / Activities / Rosters / Rank
         * @param loadStatus true load all
         * @return
         */
        public Builder fullLoad(boolean loadStatus) {
            this.loadStatus = loadStatus;
            return this;
        }

        /**
         * Max activities
         * @param max default 10
         * @return
         */
        public Builder maxActivities(int max) {
            this.maxActivities = max;
            return this;
        }

        public Guild build() {
            Guild newGuild = (Guild) load(TABLE_KEY, id);

            // Load internal data:
            newGuild.realm = new Realm.Builder(newGuild.realm_id).build();
            newGuild.faction = new StaticInformation.Builder(newGuild.faction_type).build();

            if (loadStatus) {
                newGuild.loadAchievements();
                newGuild.loadActivities(maxActivities);
                newGuild.loadRanks();
                newGuild.loadRosters();
            }
            return newGuild;
        }

    }

    // Constructor
    private Guild() {

    }

    /**
     * Load guild achievement
     */
    public void loadAchievements() {
        achievements = new ArrayList<>();
        try {
            JsonArray achievements_db = DBLoadObject.dbConnect.select(
                    ACHIEVEMENT_TABLE_NAME,
                    new String[]{"achievement_id"},
                    "guild_id=?",
                    new String[]{id+""}
            );

            if (achievements_db.size() > 0) {
                for(JsonElement achievement : achievements_db) {
                    JsonObject achievementDetail = achievement.getAsJsonObject();
                    achievements.add(new Achievement.Builder(achievementDetail.get("achievement_id").getAsLong()).build());
                }
            } else {
                Logs.infoLog(this.getClass(), "Guild not have an Achievement ["+ id +"]");
            }
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a guild Achievement ["+ id +"] - "+ e);
        }
    }

    /**
     * Load guild activities
     */
    public void loadActivities(int maxActivities) {
        lastActivitiesUpdate = new Date();
        activities = new ArrayList<>();
        try {
            JsonArray activities_db = DBLoadObject.dbConnect.select(
                    GuildActivity.TABLE_NAME,
                    new String[]{GuildActivity.TABLE_KEY},
                    "guild_id=? order by timestamp DESC limit "+ maxActivities,
                    new String[]{id+""}
            );

            if (activities_db.size() > 0) {
                for (JsonElement activity : activities_db) {
                    JsonObject actDetail = activity.getAsJsonObject();
                    activities.add(new GuildActivity.Builder(actDetail.get(GuildActivity.TABLE_KEY).getAsLong()).build());
                }
            } else {
                Logs.infoLog(this.getClass(), "Guild not have an Activities ["+ id +"]");
            }
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a guild Activity ["+ id +"] - "+ e);
        }
    }

    /**
     * Load guild rosters
     */
    public void loadRosters() {
        guildRosters = new ArrayList<>();
        lastRosterUpdate = new Date();
        try {

            //Only get members who logged in at least 1 month ago
            Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MONTH, -1);
            Date oneMotheAgo = cal.getTime();

            JsonArray rosters_db = DBLoadObject.dbConnect.selectQuery(
                    " SELECT " +
                    "    c.id " +
                    "FROM " +
                    "    guild_roster gr, " +
                    "    guild_rank grk, " +
                    "    `characters` c, " +
                    "    character_info ci " +
                    "WHERE " +
                    "    gr.character_id = c.id " +
                    "    AND c.id = ci.character_id " +
                    "    AND gr.rank_id = grk.id " +
                    "    AND gr.guild_id = "+ id +" " +
                    "    AND c.last_modified > "+ oneMotheAgo.getTime() +" " +
                    "ORDER BY " +
                    "    grk.rank_lvl ASC, " +
                    "    ci. `level` DESC, " +
                    "    c.`name` ASC;"
            );

            if (rosters_db.size() > 0) {
                for (JsonElement roster : rosters_db) {
                    JsonObject rosterDetail = roster.getAsJsonObject();
                    guildRosters.add(new GuildRoster.Builder(rosterDetail.get("id").getAsLong()).build());
                }
            } else {
                Logs.infoLog(this.getClass(), "Guild not have an Rosters ["+ id +"]");
            }
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a guild Rosters ["+ id +"] - "+ e);
        }
    }

    /**
     * Load guild ranks
     */
    public void loadRanks() {
        guildRanks = new ArrayList<>();
        try {
            JsonArray ranks_db = DBLoadObject.dbConnect.select(
                    GuildRank.TABLE_NAME,
                    new String[]{GuildRank.TABLE_KEY},
                    "guild_id=?",
                    new String[]{id+""}
            );

            if (ranks_db.size() > 0) {
                for (JsonElement rank : ranks_db) {
                    JsonObject rankDetail = rank.getAsJsonObject();
                    guildRanks.add(new GuildRank.Builder(rankDetail.get(GuildRank.TABLE_KEY).getAsLong()).build());
                }
            } else {
                Logs.infoLog(this.getClass(), "Guild not have a Ranks ["+ id +"]");
            }
        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get a guild Ranks ["+ id +"] - "+ e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public List<GuildActivity> getActivities() {
        if (activities == null) {
            loadActivities(10);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -10);
            Date tenMinuteAgo = cal.getTime();
            if (lastActivitiesUpdate.compareTo(tenMinuteAgo) < 0) {
                loadActivities(10);
            }
        }
        return activities;
    }

    public long getLast_modified() {
        return last_modified;
    }

    public List<GuildRoster> getGuildRosters() {
        if (guildRosters == null) {
            loadRosters();
        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -10);
            Date tenMinuteAgo = cal.getTime();
            if (lastRosterUpdate.compareTo(tenMinuteAgo) < 0) {
                loadRosters();
            }
        }
        return guildRosters;
    }

    public List<GuildRank> getGuildRanks() {
        if (guildRanks == null) {
            loadRanks();
        }
        return guildRanks;
    }

    public String getFaction_type() {
        return faction_type;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Guild\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : "\"" + name + "\"") + ", " +
                "\"realm_id\":\"" + realm_id + "\"" + ", " +
                "\"faction_type\":" + (faction_type == null ? "null" : "\"" + faction_type + "\"") + ", " +
                "\"achievement_points\":\"" + achievement_points + "\"" + ", " +
                "\"created_timestamp\":\"" + created_timestamp + "\"" + ", " +
                "\"member_count\":\"" + member_count + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"achievement_last_modified\":\"" + achievement_last_modified + "\"" + ", " +
                "\"roster_last_modified\":\"" + roster_last_modified + "\"" + ", " +
                "\"activities_last_modified\":\"" + activities_last_modified + "\"" + ", " +
                "\"full_sync\":\"" + full_sync + "\"" + ", " +
                "\"realm\":" + (realm == null ? "null" : realm) + ", " +
                "\"faction\":" + (faction == null ? "null" : faction) + ", " +
                "\"achievements\":" + (achievements == null ? "null" : Arrays.toString(achievements.toArray())) + ", " +
                "\"activities\":" + (activities == null ? "null" : Arrays.toString(activities.toArray())) + ", " +
                "\"rosters\":" + (guildRosters == null ? "null" : Arrays.toString(guildRosters.toArray())) + ", " +
                "\"ranks\":" + (guildRanks == null ? "null" : Arrays.toString(guildRanks.toArray())) +
                "}";
    }
}
