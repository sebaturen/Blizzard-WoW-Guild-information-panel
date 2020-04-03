package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.dbConnect.DBLoadObject;

public class CharacterMedia {

    // Character Media DB
    public static final String TABLE_NAME = "character_media";
    public static final String TABLE_KEY = "character_id";

    // DB Attribute
    private long character_id;
    private String avatar_url;
    private String bust_url;
    private String render_url;

    public static class Builder extends DBLoadObject {

        private long id;
        public Builder(long characterId) {
            super(TABLE_NAME, CharacterMedia.class);
            this.id = characterId;
        }

        public CharacterMedia build() {
            return (CharacterMedia) load(TABLE_KEY, id);
        }
    }

    // Constructor
    private CharacterMedia() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getBust_url() {
        return bust_url;
    }

    public String getRender_url() {
        return render_url;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"CharacterMedia\", " +
                "\"character_id\":\"" + character_id + "\"" + ", " +
                "\"avatar_url\":" + (avatar_url == null ? "null" : "\"" + avatar_url + "\"") + ", " +
                "\"bust_url\":" + (bust_url == null ? "null" : "\"" + bust_url + "\"") + ", " +
                "\"render_url\":" + (render_url == null ? "null" : "\"" + render_url + "\"") +
                "}";
    }
}
