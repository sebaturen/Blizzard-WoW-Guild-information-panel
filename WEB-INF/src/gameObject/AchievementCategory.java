package com.blizzardPanel.gameObject;

public class AchievementCategory {

    // AchievementCategory DB
    public static final String TABLE_NAME = "achievement_categories";
    public static final String TABLE_KEY = "id";

    // DB Attribute
    private long id;
    private String name;
    private boolean is_guild_category;
    private int display_order;
    private long parent_category_id;

    // Update Control
    private long last_modified;

    // Internal DATA
    private AchievementCategory parentCategory;

    public static class Builder extends GameObject2 {

        private long id;
        public Builder(long achievementCategoryId) {
            super(TABLE_NAME, AchievementCategory.class);
            this.id = achievementCategoryId;
        }

        public AchievementCategory build() {
            AchievementCategory newCat = (AchievementCategory) load(TABLE_KEY+"=?", id);

            if (newCat.parent_category_id > 0) {
                newCat.parentCategory = new AchievementCategory.Builder(newCat.parent_category_id).build();
            }

            return newCat;
        }
    }

    // Constructor
    private AchievementCategory() {

    }

    @Override
    public String toString() {
        return "{\"_class\":\"AchievementCategory\", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"name\": \"NAME\", " + //(name == null ? "null" : "\"" + name + "\"") + ", " +
                "\"is_guild_category\":\"" + is_guild_category + "\"" + ", " +
                "\"display_order\":\"" + display_order + "\"" + ", " +
                "\"parent_category_id\":\"" + parent_category_id + "\"" + ", " +
                "\"last_modified\":\"" + last_modified + "\"" + ", " +
                "\"parentCategory\":" + (parentCategory == null ? "null" : parentCategory) +
                "}";
    }
}
