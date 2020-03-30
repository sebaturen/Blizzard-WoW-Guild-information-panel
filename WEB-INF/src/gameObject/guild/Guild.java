/**
 * File : Guild.java
 * Desc : Guild Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.*;
import com.blizzardPanel.gameObject.characters.CharacterMember;
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
    // Roster Guild DB
    public static final String ROSTER_TABLE_NAME = "guild_roster";
    public static final String ROSTER_TABLE_KEY = "character_id";

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
    private List<Achievement> achievements = new ArrayList<>();
    private List<Activity> activities = new ArrayList<>();
    private List<CharacterMember> rosters = new ArrayList<>();
    private List<Rank> ranks = new ArrayList<>();

    public static class Builder extends GameObject2 {

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
                //newGuild.loadActivities(maxActivities);
                //newGuild.loadRanks();
                //newGuild.loadRosters();
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
    private void loadAchievements() {
        try {
            JsonArray achievements_db = GameObject2.dbConnect.select(
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
    private void loadActivities(int maxActivities) {
        try {
            JsonArray activities_db = GameObject2.dbConnect.select(
                    Activity.TABLE_NAME,
                    new String[]{Activity.TABLE_KEY},
                    "guild_id=? order by timestamp DESC limit "+ maxActivities,
                    new String[]{id+""}
            );

            if (activities_db.size() > 0) {
                for (JsonElement activity : activities_db) {
                    JsonObject actDetail = activity.getAsJsonObject();
                    activities.add(new Activity.Builder(actDetail.get(Activity.TABLE_KEY).getAsLong()).build());
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
    private void loadRosters() {
        try {

            //Only get members who logged in at least 1 month ago
            Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MONTH, -1);
            Date oneMotheAgo = cal.getTime();

            JsonArray rosters_db = GameObject2.dbConnect.selectQuery(
                    "SELECT " +
                            "    c.id " +
                            "FROM " +
                            "    guild_roster gr, " +
                            "    `characters` c " +
                            "WHERE " +
                            "    gr.character_id = c.id " +
                            "    AND    gr.guild_id = "+ id +
                            "    AND c.last_modified > "+ oneMotheAgo.getTime() +";"
            );

            if (rosters_db.size() > 0) {
                for (JsonElement roster : rosters_db) {
                    JsonObject rosterDetail = roster.getAsJsonObject();
                    rosters.add(new CharacterMember.Builder(rosterDetail.get("id").getAsLong()).fullLoad(true).build());
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
    private void loadRanks() {
        try {
            JsonArray ranks_db = GameObject2.dbConnect.select(
                    Rank.TABLE_NAME,
                    new String[]{Rank.TABLE_KEY},
                    "guild_id=?",
                    new String[]{id+""}
            );

            if (ranks_db.size() > 0) {
                for (JsonElement rank : ranks_db) {
                    JsonObject rankDetail = rank.getAsJsonObject();
                    ranks.add(new Rank.Builder(rankDetail.get(Rank.TABLE_KEY).getAsLong()).build());
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
                "\"rosters\":" + (rosters == null ? "null" : Arrays.toString(rosters.toArray())) + ", " +
                "\"ranks\":" + (ranks == null ? "null" : Arrays.toString(ranks.toArray())) +
                "}";
    }
}
