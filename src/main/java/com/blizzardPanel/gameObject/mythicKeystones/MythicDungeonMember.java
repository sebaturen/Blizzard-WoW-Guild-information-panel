package com.blizzardPanel.gameObject.mythicKeystones;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.characters.CharacterMember;

public class MythicDungeonMember {

    // Members Dungeon Run DB
    public static final String TABLE_NAME = "keystone_dungeon_run_members";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private int id;
    private int keystone_dungeon_run_id;
    private long character_id;
    private long character_spec_id;
    private int character_item_level;

    // Internal DATA
    private CharacterMember character;

    public static class Builder extends DBLoadObject {

        private int id;
        public Builder(int dunMemberId) {
            super(TABLE_NAME, MythicDungeonMember.class);
            this.id = dunMemberId;
        }

        public MythicDungeonMember build() {
            MythicDungeonMember newMyMember = (MythicDungeonMember) load(TABLE_KEY, id);

            // Load internal data
            newMyMember.character = new CharacterMember.Builder(newMyMember.character_id).build();
            newMyMember.character.setActiveSpec(newMyMember.character_spec_id);

            return newMyMember;
        }
    }

    // Constructor
    private MythicDungeonMember() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public CharacterMember getCharacter() {
        return character;
    }

    public int getCharacter_item_level() {
        return character_item_level;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"MythicDungeonMember\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"keystone_dungeon_run_id\":\"" + keystone_dungeon_run_id + "\"" + ", " +
                "\"character_id\":\"" + character_id + "\"" + ", " +
                "\"character_spec_id\":\"" + character_spec_id + "\"" + ", " +
                "\"character_item_level\":\"" + character_item_level + "\"" + ", " +
                "\"character\":" + (character == null ? "null" : character) +
                "}";
    }
}
