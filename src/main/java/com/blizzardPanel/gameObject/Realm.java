/**
 * File : Realm.java
 * Desc : Realm object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

public class Realm {

    // Realm DB
    public static final String TABLE_NAME = "realms";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private int id;
    private String slug;
    private JsonObject name;
    private String locale;
    private String timezone;
    private String type_type;

    // Update control
    private long last_modified;

    // Internal DATA
    private StaticInformation type;

    public static class Builder extends DBLoadObject {

        private long id;
        private String realmSlug;

        public Builder(long realmId) {
            super(TABLE_NAME, Realm.class);
            this.id = realmId;
        }

        public Builder(String realmSlug) {
            super(TABLE_NAME, Realm.class);
            this.realmSlug = realmSlug;
        }

        public Realm build() {
            if (realmSlug == null) {
                return (Realm) load(TABLE_KEY, id);
            } else {
                return (Realm) load("slug", realmSlug);
            }
        }
    }

    // Constructor
    private Realm() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getName(String locale) {
        return name.get(locale).getAsString();
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Realm\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"slug\":" + (slug == null ? "null" : "\"" + slug + "\"") + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"locale\":" + (locale == null ? "null" : "\"" + locale + "\"") + ", " +
                "\"timezone\":" + (timezone == null ? "null" : "\"" + timezone + "\"") + ", " +
                "\"type_type\":" + (type_type == null ? "null" : "\"" + type_type + "\"") + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"type\":" + (type == null ? "null" : type) +
                "}";
    }
}