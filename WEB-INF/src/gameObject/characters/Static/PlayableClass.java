/**
 * File : PlayableClass.java
 * Desc : Playable class object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters.Static;

import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.GameObject2;
import com.google.gson.JsonObject;

public class PlayableClass
{	
    // Playable Class DB
    public static final String TABLE_NAME = "playable_class";
    public static final String TABLE_KEY = "id";
    
    // DB Attribute
    private long id;
    private String name;
    private String gender_name_male;
    private String gender_name_female;

    // Update control
    private long last_modified;

    public static class Builder extends GameObject2 {

        private long id;
        public Builder(long classId) {
            super(TABLE_NAME, PlayableClass.class);
            this.id = classId;
        }

        public PlayableClass build() {
            return (PlayableClass) load(TABLE_KEY+"=?", id);
        }
    }

    private PlayableClass() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"PlayableClass\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\": \"NAME\", " + //(name == null ? "null" : "\"" + name + "\"") + ", " +
                "\"gender_name_male\": \"DESC_MALE\", " + //(gender_name_male == null ? "null" : "\"" + gender_name_male + "\"") + ", " +
                "\"gender_name_female\": \"DESC_FEMALE\", " + //(gender_name_female == null ? "null" : "\"" + gender_name_female + "\"") + ", " +
                "\"last_modified\":\"" + last_modified + "\"" +
                "}";
    }
}