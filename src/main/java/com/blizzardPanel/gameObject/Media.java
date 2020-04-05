package com.blizzardPanel.gameObject;

import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBLoadObject;

public class Media {

    // Media DB
    public static final String ACHIEVEMENT_TABLE_NAME = "achievement_media";
    public static final String ACHIEVEMENT_TABLE_KEY = "id";
    public static final String ITEM_TABLE_NAME = "item_media";
    public static final String ITEM_TABLE_KEY = "id";
    public static final String P_SPEC_TABLE_NAME = "playable_spec_media";
    public static final String P_SPEC_TABLE_KEY = "id";
    public static final String P_CLASS_TABLE_NAME = "playable_class_media";
    public static final String P_CLASS_TABLE_KEY = "id";
    public static final String SPELL_TABLE_NAME = "spell_media";
    public static final String SPELL_TABLE_KEY = "id";
    public static final String KEYSTONE_AFFIX_TABLE_NAME = "keystone_affix_media";
    public static final String KEYSTONE_AFFIX_TABLE_KEY = "id";
    public static final String INSTANCE_TABLE_NAME = "instance_media";
    public static final String INSTANCE_TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private String key;
    private String value;

    // Update control
    private String last_modified;

    // Type structure
    public enum type {
        ACHIEVEMENT(ACHIEVEMENT_TABLE_NAME),
        ITEM(ITEM_TABLE_NAME),
        P_SPEC(P_SPEC_TABLE_NAME),
        P_CLASS(P_CLASS_TABLE_NAME),
        SPELL(SPELL_TABLE_NAME),
        KEYSTONE_AFFIX(KEYSTONE_AFFIX_TABLE_NAME),
        INSTANCE(INSTANCE_TABLE_NAME);

        private final String val;
        private type(String val) {
            this.val = val;
        }

        public String toString() {
            return this.val;
        }
    }


    public static class Builder extends DBLoadObject {

        private long id;
        private type mediaType;
        public Builder(type mediaType, long mediaId) {
            super(mediaType.toString(), Media.class);
            this.id = mediaId;
            this.mediaType = mediaType;
        }

        public Media build() {
            switch (mediaType) {
                case ITEM:
                    return (Media) load(ITEM_TABLE_KEY, id);
                case P_SPEC:
                    return (Media) load(P_SPEC_TABLE_KEY, id);
                case P_CLASS:
                    return (Media) load(P_CLASS_TABLE_KEY, id);
                case ACHIEVEMENT:
                    return (Media) load(ACHIEVEMENT_TABLE_KEY, id);
                case KEYSTONE_AFFIX:
                    return (Media) load(KEYSTONE_AFFIX_TABLE_KEY, id);
                case SPELL:
                    return (Media) load(SPELL_TABLE_KEY, id);
                case INSTANCE:
                    return (Media) load(INSTANCE_TABLE_KEY, id);
                default:
                    Logs.fatalLog(this.getClass(), "FAILED media type process NOT EXIST! "+ mediaType);
            }
            return null;
        }

    }

    // Constructor
    private Media() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getValue() {
        return value;
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
