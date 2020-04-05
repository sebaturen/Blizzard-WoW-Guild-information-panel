/**
 * File : Rank.java
 * Desc : Guild Ranks (Officer, Member, GuildLeader, etc)
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guilds;

import com.blizzardPanel.dbConnect.DBLoadObject;

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

        private long id;
        public Builder(long rankId) {
            super(TABLE_NAME, GuildRank.class);
            this.id = rankId;
        }

        public GuildRank build() {
            return (GuildRank) load(TABLE_KEY, id);
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
