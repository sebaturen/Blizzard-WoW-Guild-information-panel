package com.blizzardPanel.gameObject.achievements;

import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class AchievementCategory {

    // AchievementCategory DB
    public static final String TABLE_NAME = "achievement_categories";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private JsonObject name;
    private boolean is_guild_category;
    private int display_order;
    private long parent_category_id;

    // Update Control
    private long last_modified;

    // Internal DATA
    private AchievementCategory parentAchievementCategory;

    public static class Builder extends DBLoadObject {

        private static Map<Long, AchievementCategory> achievementCategories = new HashMap<>();

        private long id;
        public Builder(long achievementCategoryId) {
            super(TABLE_NAME, AchievementCategory.class);
            this.id = achievementCategoryId;
        }

        public AchievementCategory build() {
            if (!achievementCategories.containsKey(id)) {
                AchievementCategory newCat = (AchievementCategory) load(TABLE_KEY, id);

                if (newCat.parent_category_id > 0) {
                    newCat.parentAchievementCategory = new AchievementCategory.Builder(newCat.parent_category_id).build();
                }
                achievementCategories.put(id, newCat);
            }

            return achievementCategories.get(id);
        }
    }

    // Constructor
    private AchievementCategory() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"AchievementCategory\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\":" + (name == null ? "null" : name) + ", " +
                "\"is_guild_category\":\"" + is_guild_category + "\"" + ", " +
                "\"display_order\":\"" + display_order + "\"" + ", " +
                "\"parent_category_id\":\"" + parent_category_id + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"parentCategory\":" + (parentAchievementCategory == null ? "null" : parentAchievementCategory) +
                "}";
    }
}
