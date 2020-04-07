package com.blizzardPanel.gameObject;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class StaticInformation {

    // Static information DB
    public static final String TABLE_NAME = "static_info";
    public static final String TABLE_KEY = "type";

    // DB Attribute
    private String type;
    private JsonObject name;

    public static class Builder extends DBLoadObject {

        private static Map<String, StaticInformation> staticInformation = new HashMap<>();

        private String type;
        public Builder(String type) {
            super(TABLE_NAME, StaticInformation.class);
            this.type = type;
        }

        public StaticInformation build() {
            if (!staticInformation.containsKey(type)) {
                staticInformation.put(type, (StaticInformation) load(TABLE_KEY, type));
            }
            return staticInformation.get(type);
        }
    }

    // Constructor
    private StaticInformation() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getType() {
        return type;
    }

    public String getName(String locale) {
        return name.get(locale).getAsString();
    }

    @Override
    public String toString() {
        return "{\"_class\":\"StaticInformation\", " +
                "\"type\":" + (type == null ? "null" : "\"" + type + "\"") + ", " +
                "\"name\":" + (name == null ? "null" : name) +
                "}";
    }
}
