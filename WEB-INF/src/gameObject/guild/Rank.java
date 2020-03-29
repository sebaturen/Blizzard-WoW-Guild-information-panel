/**
 * File : Rank.java
 * Desc : Guild Ranks (Officer, Member, GuildLeader, etc)
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild;

import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.GameObject2;
import com.google.gson.JsonObject;

public class Rank {

    // Rank DB
    public static final String TABLE_NAME = "guild_rank";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private long guild_id;
    private int rank;
    private String title;

    public static class Builder extends GameObject2 {

        private long id;
        public Builder(long rankId) {
            super(TABLE_NAME, Rank.class);
            this.id = rankId;
        }

        public Rank build() {
            return (Rank) load(TABLE_KEY +"=?", id);
        }
    }

    private Rank() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"Rank\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"guild_id\":\"" + guild_id + "\"" + ", " +
                "\"rank\":\"" + rank + "\"" + ", " +
                "\"title\":" + (title == null ? "null" : "\"" + title + "\"") +
                "}";
    }
}
