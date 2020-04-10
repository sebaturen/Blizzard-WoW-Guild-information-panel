package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class CharacterTitle {

    // Title DB
    public static final String TABLE_NAME = "character_titles";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;
    private JsonObject gender_name_male;
    private JsonObject gender_name_female;

    // Update Control
    private long last_modified;

    public static class Builder extends DBLoadObject {

        private static Map<Long, CharacterTitle> characterTitles = new HashMap<>();

        private long id;
        public Builder(long titleId) {
            super(TABLE_NAME, CharacterTitle.class);
            this.id = titleId;
        }

        public CharacterTitle build() {
            if (!characterTitles.containsKey(id)) {
                CharacterTitle newTitle = (CharacterTitle) load(TABLE_KEY, id);
                characterTitles.put(id, newTitle);
            }

            return characterTitles.get(id);
        }
    }

    // Constructor
    private CharacterTitle() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getName(String locale) {
        return name.get(locale).getAsString();
    }

    public String getGender_name_male(String locale) {
        return gender_name_male.get(locale).getAsString();
    }

    public String getGender_name_female(String locale) {
        return gender_name_female.get(locale).getAsString();
    }

    @Override
    public String toString() {
        return "{\"_class\":\"CharacterTitle\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"gender_name_male\":" + (gender_name_male == null ? "null" : gender_name_male) + ", " +
                "\"gender_name_female\":" + (gender_name_female == null ? "null" : gender_name_female) + ", " +
                "\"last_modified\":\"" + last_modified + "\"" +
                "}";
    }
}
