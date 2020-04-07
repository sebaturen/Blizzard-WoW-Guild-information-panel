/**
 * File : Rank.java
 * Desc : Guild Ranks (Officer, Member, GuildLeader, etc)
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guilds;

import com.blizzardPanel.dbConnect.DBLoadObject;

import java.util.HashMap;
import java.util.Map;

public class GuildRank {

    // Rank DB
    public static final String TABLE_NAME = "guild_rank";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private long guild_id;
    private int rank_lvl;
    private String title;

    public static class Builder extends DBLoadObject {

        private static Map<Long, GuildRank> guildRanks = new HashMap<>();

        private long id;
        public Builder(long rankId) {
            super(TABLE_NAME, GuildRank.class);
            this.id = rankId;
        }

        public GuildRank build() {
            if (!guildRanks.containsKey(id)) {
                guildRanks.put(id, (GuildRank) load(TABLE_KEY, id));
            }
            return guildRanks.get(id);
        }
    }

    // Constructor
    private GuildRank() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------
    public int getRank_lvl() {
        return rank_lvl;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Rank\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"guild_id\":\"" + guild_id + "\"" + ", " +
                "\"rank_lvl\":\"" + rank_lvl + "\"" + ", " +
                "\"title\":" + (title == null ? "null" : "\"" + title + "\"") +
                "}";
    }
}
