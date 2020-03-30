package com.blizzardPanel.gameObject;

import com.google.gson.JsonObject;

public class StaticInformation {

    // Static information DB
    public static final String TABLE_NAME = "static_info";
    public static final String TABLE_KEY = "type";

    // DB Attribute
    private String type;
    private JsonObject name;

    public static class Builder extends GameObject2 {

        private String type;
        public Builder(String type) {
            super(TABLE_NAME, StaticInformation.class);
            this.type = type;
        }

        public StaticInformation build() {
            return (StaticInformation) load(TABLE_KEY, type);
        }
    }

    // Constructor
    private StaticInformation() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"StaticInformation\", " +
                "\"type\":" + (type == null ? "null" : "\"" + type + "\"") + ", " +
                "\"name\":" + (name == null ? "null" : name) +
                "}";
    }
}
