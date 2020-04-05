package com.blizzardPanel.gameObject.guilds;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.characters.CharacterMember;

public class GuildRoster {

    // Roster DB
    public static final String TABLE_NAME = "guild_roster";
    public static final String TABLE_KEY = "character_id";

    // DB Attribute
    private long character_id;
    private long guild_id;
    private int rank_id;
    private long add_regist;
    private boolean current_status;

    // Internal DATA
    private CharacterMember member;
    private GuildRank guildRank;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long characterId) {
            super(TABLE_NAME, GuildRoster.class);
            this.id = characterId;
        }

        public GuildRoster build() {
            GuildRoster newGuildRoster = (GuildRoster) load(TABLE_KEY, id);

            // Load internal data:
            newGuildRoster.member = new CharacterMember.Builder(newGuildRoster.character_id).build();
            newGuildRoster.guildRank = new GuildRank.Builder(newGuildRoster.rank_id).build();

            return newGuildRoster;
        }
    }

    // Constructor
    private GuildRoster() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public GuildRank getGuildRank() {
        return guildRank;
    }

    public CharacterMember getMember() {
        return member;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Roster\", " +
                "\"character_id\":\"" + character_id + "\"" + ", " +
                "\"guild_id\":\"" + guild_id + "\"" + ", " +
                "\"rank_id\":\"" + rank_id + "\"" + ", " +
                "\"add_regist\":\"" + add_regist + "\"" + ", " +
                "\"current_status\":\"" + current_status + "\"" + ", " +
                "\"member\":" + (member == null ? "null" : member) + ", " +
                "\"rank\":" + (guildRank == null ? "null" : guildRank) +
                "}";
    }
}
