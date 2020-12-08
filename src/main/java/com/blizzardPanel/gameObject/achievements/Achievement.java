package com.blizzardPanel.gameObject.achievements;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.StaticInformation;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Achievement {

    // Achievement DB
    public static final String TABLE_NAME = "achievements";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;
    private JsonObject description;
    private JsonObject reward_description;
    private String faction_type;
    private int points;
    private long media_id;
    private int display_order;
    private boolean is_account_wide;
    private long category_id;

    // Update control
    private long last_modified;

    // Internal DATA
    private StaticInformation faction;
    private AchievementCategory achievementCategory;
    private Media media;

    public static class Builder extends DBLoadObject {

        private static Map<Long, Achievement> achievements = new HashMap<>();

        private long id;
        public Builder(long achievementId) {
            super(TABLE_NAME, Achievement.class);
            this.id = achievementId;
        }

        public Achievement build() {
            if (!achievements.containsKey(id)) {
                Achievement newAchievement = (Achievement) load(TABLE_KEY, id);
                if (newAchievement != null) {
                    newAchievement.achievementCategory = new AchievementCategory.Builder(newAchievement.category_id).build();
                    newAchievement.media = new Media.Builder(Media.type.ACHIEVEMENT, newAchievement.media_id).build();
                    if (newAchievement.faction_type != null) {
                        newAchievement.faction = new StaticInformation.Builder(newAchievement.faction_type).build();
                    }
                    achievements.put(id, newAchievement);
                }
            }
            return achievements.get(id);
        }
    }

    // Constructor
    private Achievement() {

    }

    //------------------------------------------------------------------------------------------------------------------
    //
    // GET / SET
    //
    //------------------------------------------------------------------------------------------------------------------

    public String getName(String locale) {
        return name.get(locale).getAsString();
    }

    public String getDescription(String locale) {
        return description.get(locale).getAsString();
    }

    public Media getMedia() {
        return media;
    }

    @Override
    public String toString() {
        return "{\"_class\":\"Achievement\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                //"\"description\":" + (description == null ? "null" : description) + ", " +
                "\"reward_description\":" + (reward_description == null ? "null" : reward_description) + ", " +
                "\"faction_type\":" + (faction_type == null ? "null" : "\"" + faction_type + "\"") + ", " +
                "\"points\":\"" + points + "\"" + ", " +
                "\"media_id\":\"" + media_id + "\"" + ", " +
                "\"display_order\":\"" + display_order + "\"" + ", " +
                "\"is_account_wide\":\"" + is_account_wide + "\"" + ", " +
                "\"category_id\":\"" + category_id + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"faction\":" + (faction == null ? "null" : faction) + ", " +
                "\"category\":" + (achievementCategory == null ? "null" : achievementCategory) + ", " +
                "\"media\":" + (media == null ? "null" : media) +
                "}";
    }
}
