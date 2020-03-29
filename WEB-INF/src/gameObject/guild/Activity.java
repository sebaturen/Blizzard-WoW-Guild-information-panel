/**
 * File : News.java
 * Desc : News Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild;

import com.blizzardPanel.gameObject.GameObject2;

public class Activity
{
    // Activity DB
    public static final String TABLE_NAME = "guild_activities";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private long guild_id;
    private String type;
    private long timestamp;
    private String detail;

    public static class Builder extends GameObject2 {

        private long id;
        public Builder(long activityId) {
            super(TABLE_NAME, Activity.class);
            this.id = activityId;
        }

        public Activity build() {
            return (Activity) load(TABLE_KEY+"=?", id);
        }
    }

    // Constructor
    private Activity() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"Activity\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"guild_id\":\"" + guild_id + "\"" + ", " +
                "\"type\":" + (type == null ? "null" : "\"" + type + "\"") + ", " +
                "\"timestamp\":\"" + timestamp + "\"" + ", " +
                "\"detail\": \"DETAIL\"" + //(detail == null ? "null" : "\"" + detail + "\"") +
                "}";
    }
}
