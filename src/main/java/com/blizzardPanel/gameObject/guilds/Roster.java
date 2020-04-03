package com.blizzardPanel.gameObject.guilds;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.characters.CharacterMember;

public class Roster {

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
    private Rank rank;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long characterId) {
            super(TABLE_NAME, Roster.class);
            this.id = characterId;
        }

        public Roster build() {
            Roster newRoster = (Roster) load(TABLE_KEY, id);

            // Load internal data:
            newRoster.member = new CharacterMember.Builder(newRoster.character_id).build();
            newRoster.rank = new Rank.Builder(newRoster.rank_id).build();

            return newRoster;
        }
    }

    // Constructor
    private Roster() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public Rank getRank() {
        return rank;
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
                "\"rank\":" + (rank == null ? "null" : rank) +
                "}";
    }
}
