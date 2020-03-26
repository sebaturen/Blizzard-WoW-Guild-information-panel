package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Achievement;
import com.blizzardPanel.gameObject.AchievementCategory;
import com.blizzardPanel.gameObject.AchievementMedia;
import com.blizzardPanel.update.blizzard.AccessToken;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AchievementAPI extends BlizzardAPI {

    public AchievementAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    public void update() {
        // Prepare Achievements categories:
        categories();
    }

    // Get all categories index
    private void categories() {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        Call<JsonObject> call = apiCalls.achievementCategories(
                "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                BlizzardUpdate.shared.accessToken.getAuthorization()
        );

        try {
            JsonObject resp = call.execute().body();

            // Get all [categories]
            JsonArray categories = resp.getAsJsonArray("categories");
            Logs.infoLog(AchievementAPI.class, "Update - Get categories");
            for(JsonElement cat : categories) {
                categoryDetail(cat.getAsJsonObject());
            }
            // Get all [root_categories]
            JsonArray root_categories = resp.getAsJsonArray("root_categories");
            Logs.infoLog(AchievementAPI.class, "Update - Get root_categories");
            for(JsonElement cat : root_categories) {
                categoryDetail(cat.getAsJsonObject());
            }
            // Get all [guild_categories]
            JsonArray guild_categories = resp.getAsJsonArray("guild_categories");
            Logs.infoLog(AchievementAPI.class, "Update - Get guild_categories");
            for(JsonElement cat : guild_categories) {
                categoryDetail(cat.getAsJsonObject());
            }

        } catch (IOException e) {
            Logs.infoLog(AchievementAPI.class, "FAIL - to get all categories "+ e);
        }
    }

    // Get detail for one category
    private void categoryDetail(JsonObject category) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = category.getAsJsonObject("key").get("href").getAsString();
        String catId = category.get("id").getAsString();

        try {

            // Check is category previously exist:
            JsonArray cat_db = BlizzardUpdate.dbConnect.select(
                    AchievementCategory.TABLE_NAME,
                    new String[] {"last_modified"},
                    "id = ?",
                    new String[] {catId}
            );
            boolean isInDb = (cat_db.size() > 0);
            long lastModified = 0L;
            if (cat_db.size() > 0) {
                lastModified = cat_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );


            // Run call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject blizz_cat = resp.body();

                // Prepare values:
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name");
                values.add(blizz_cat.get("name").getAsJsonObject());
                columns.add("is_guild_category");
                values.add((blizz_cat.get("is_guild_category").getAsBoolean())? "1":"0");
                columns.add("display_order");
                values.add(blizz_cat.get("display_order").getAsString());
                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");
                if (blizz_cat.has("parent_category")) {
                    // Load info from father
                    categoryDetail(blizz_cat.get("parent_category").getAsJsonObject());
                    // Add father information
                    columns.add("parent_category_id");
                    values.add(blizz_cat.get("parent_category").getAsJsonObject().get("id").getAsString());
                }

                if (isInDb) { // UPDATE
                    BlizzardUpdate.dbConnect.update(
                            AchievementCategory.TABLE_NAME,
                            columns,
                            values,
                            "id = ?",
                            new String[]{catId}
                    );
                } else { // INSERT
                    columns.add("id");
                    values.add(catId);
                    BlizzardUpdate.dbConnect.insert(
                            AchievementCategory.TABLE_NAME,
                            AchievementCategory.TABLE_KEY,
                            columns,
                            values
                    );
                }

                // Get Achievements:
                if (blizz_cat.has("achievements")) {
                    for (JsonElement achievement : blizz_cat.get("achievements").getAsJsonArray()) {
                        achievementDetail(achievement.getAsJsonObject());
                    }
                }
                Logs.infoLog(AchievementAPI.class, "Achievement Category OK "+ catId);
            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(AchievementAPI.class, "NOT Modified Achievement Category "+ catId);
                } else {
                    Logs.infoLog(AchievementAPI.class, "ERROR - achievement Category "+ catId +" - "+ resp.code());
                }
            }

        } catch (IOException | DataException | SQLException e) {
            Logs.infoLog(AchievementAPI.class, "FAIL - to get achievement category detail "+ e);
        }
    }

    public void achievementDetail(JsonObject achievement) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = achievement.getAsJsonObject("key").get("href").getAsString();
        String achievId = achievement.get("id").getAsString();

        try {

            // Check is category previously exist:
            JsonArray achiev_db = BlizzardUpdate.dbConnect.select(
                    Achievement.TABLE_NAME,
                    new String[]{"last_modified"},
                    "id = ?",
                    new String[]{achievId}
            );
            boolean isInDb = (achiev_db.size() > 0);
            long lastModified = 0L;
            if (achiev_db.size() > 0) {
                lastModified = achiev_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );


            // Run call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject blizz_achiev = resp.body();

                // Prepare values:
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name");
                values.add(blizz_achiev.get("name").getAsJsonObject().toString());
                columns.add("description");
                values.add(blizz_achiev.get("description").getAsJsonObject().toString());
                columns.add("points");
                values.add(blizz_achiev.get("points").getAsString());
                columns.add("media_id");
                achievementMedia(blizz_achiev.get("media").getAsJsonObject());
                values.add(blizz_achiev.get("media").getAsJsonObject().get("id").getAsString());
                columns.add("display_order");
                values.add(blizz_achiev.get("display_order").getAsString());
                columns.add("is_account_wide");
                values.add((blizz_achiev.get("is_account_wide").getAsBoolean())? "1":"0");
                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");
                if (blizz_achiev.has("category_id")) {
                    columns.add("category_id");
                    values.add(blizz_achiev.get("category_id").getAsJsonObject().get("id").getAsString());
                }
                if (blizz_achiev.has("reward_description")) {
                    columns.add("reward_description");
                    values.add(blizz_achiev.get("reward_description").getAsJsonObject().toString());
                }
                if (blizz_achiev.has("faction_type")) {
                    BlizzardUpdate.shared.staticInformationAPI.faction(blizz_achiev.get("faction").getAsJsonObject());
                    columns.add("faction_type");
                    values.add(blizz_achiev.get("faction").getAsJsonObject().get("type").getAsString());
                }
                if (isInDb) { // UPDATE
                    BlizzardUpdate.dbConnect.update(
                            Achievement.TABLE_NAME,
                            columns,
                            values,
                            "id = ?",
                            new String[]{achievId}
                    );
                } else { // INSERT
                    columns.add("id");
                    values.add(achievId);
                    BlizzardUpdate.dbConnect.insert(
                            Achievement.TABLE_NAME,
                            Achievement.TABLE_KEY,
                            columns,
                            values
                    );
                }

                Logs.infoLog(AchievementAPI.class, "Achievement info OK "+ achievId);

            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(AchievementAPI.class, "NOT Modified Achievement detail "+ achievId);
                } else {
                    Logs.infoLog(AchievementAPI.class, "ERROR - achievement detail "+ achievId +" - "+ resp.code());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.infoLog(AchievementAPI.class, "FAIL - to get achievement detail "+ e);
        }
    }

    private void achievementMedia(JsonObject achievMedia) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = achievMedia.getAsJsonObject("key").get("href").getAsString();
        String achievMediaId = achievMedia.get("id").getAsString();

        try {

            // Check is category previously exist:
            JsonArray achievMedia_db = BlizzardUpdate.dbConnect.select(
                    AchievementMedia.TABLE_NAME,
                    new String[]{"last_modified"},
                    "id = ?",
                    new String[]{achievMediaId}
            );
            boolean isInDb = (achievMedia_db.size() > 0);
            Long lastModified = 0L;
            if (achievMedia_db.size() > 0) {
                lastModified = achievMedia_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );


            // Run call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject blizz_achievMedia = resp.body();
                JsonObject blizz_achievMedia_assets = blizz_achievMedia.getAsJsonArray("assets").get(0).getAsJsonObject();

                // Prepare values:
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("key");
                values.add(blizz_achievMedia_assets.get("key").getAsString());
                columns.add("value");
                values.add(blizz_achievMedia_assets.get("value").getAsString());
                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");

                if (isInDb) { // UPDATE
                    BlizzardUpdate.dbConnect.update(
                            AchievementMedia.TABLE_NAME,
                            columns,
                            values,
                            "id = ?",
                            new String[]{achievMediaId}
                    );
                } else { // INSERT
                    columns.add("id");
                    values.add(achievMediaId);
                    BlizzardUpdate.dbConnect.insert(
                            AchievementMedia.TABLE_NAME,
                            AchievementMedia.TABLE_KEY,
                            columns,
                            values
                    );
                }

                Logs.infoLog(AchievementAPI.class, "Achievement media OK "+ achievMediaId);

            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(AchievementAPI.class, "NOT Modified Achievement media "+ achievMediaId);
                } else {
                    Logs.infoLog(AchievementAPI.class, "ERROR - achievement media "+ achievMediaId +" - "+ resp.code());
                }
            }

        } catch (IOException | DataException | SQLException e) {
            Logs.infoLog(AchievementAPI.class, "FAIL - to get achievement media "+ e);
        }
    }


}
