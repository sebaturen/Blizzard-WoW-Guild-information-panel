package com.blizzardPanel.gameObject;

public class Media {

    // Media DB
    public static final String TABLE_NAME = "media_assets";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private String key;
    private String value;

    // Update control
    private String last_modified;

    public static class Builder extends GameObject2 {

        private long id;
        public Builder(long mediaId) {
            super(TABLE_NAME, Media.class);
            this.id = mediaId;
        }

        public Media build() {
            return (Media) load(TABLE_KEY, id);
        }

    }

    // Constructor
    private Media() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"Media\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"key\":" + (key == null ? "null" : "\"" + key + "\"") + ", " +
                "\"value\":" + (value == null ? "null" : "\"" + value + "\"") + ", " +
                "\"last_modified\":" + (last_modified == null ? "null" : "\"" + last_modified + "\"") +
                "}";
    }
}
