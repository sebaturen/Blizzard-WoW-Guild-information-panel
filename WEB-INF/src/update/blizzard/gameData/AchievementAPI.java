package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.AchievementCategory;
import com.blizzardPanel.update.blizzard.AccessToken;
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
import java.util.Date;
import java.util.List;

public class AchievementAPI {

    private WoWAPIService apiCalls;
    private AccessToken ac;

    public AchievementAPI(WoWAPIService apiCalls) {
        this.apiCalls = apiCalls;
    }

    public void update(AccessToken ac) {
        this.ac = ac;
        // Prepare Achievements categories:
        categories();
        // ...
    }

    // Get all categories index
    private void categories() {
        Call<JsonObject> call = apiCalls.achievementCategories(
                "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                ac.getAuthorization()
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
        String urlHref = category.getAsJsonObject("key").get("href").getAsString();
        String catId = category.get("id").getAsString();

        try {

            // Check is category previously exist:
            JsonArray cat_db = BlizzardUpdate.dbConnect.select(
                    AchievementCategory.ACHIEVEMENT_CATEGORY_TABLE_NAME,
                    new String[] {"last_modified"},
                    "id = ?",
                    new String[] {catId}
            );
            boolean isInDb = (cat_db.size() > 0);
            Long lastModified = 0L;
            if (cat_db.size() > 0) {
                lastModified = cat_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    ac.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );


            // Run call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject blizz_cat = resp.body();

                // Prepare values:
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name_language");
                values.add(blizz_cat.get("name").getAsJsonObject().get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")).getAsString());
                columns.add("name_second_language");
                values.add(blizz_cat.get("name").getAsJsonObject().get(GeneralConfig.getStringConfig("ALTERNATIVE_LANGUAGE_API_LOCALE")).getAsString());
                columns.add("is_guild_category");
                values.add((blizz_cat.get("is_guild_category").getAsBoolean())? "1":"0");
                columns.add("display_order");
                values.add(blizz_cat.get("display_order").getAsString());
                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");
                if (blizz_cat.has("parent_category")) {
                    columns.add("parent_category_id");
                    values.add(blizz_cat.get("parent_category").getAsJsonObject().get("id").getAsString());
                }

                if (isInDb) { // UPDATE
                    BlizzardUpdate.dbConnect.update(
                            AchievementCategory.ACHIEVEMENT_CATEGORY_TABLE_NAME,
                            columns,
                            values,
                            "id = ?",
                            new String[]{catId}
                    );
                } else { // INSERT
                    columns.add("id");
                    values.add(catId);
                    BlizzardUpdate.dbConnect.insert(
                            AchievementCategory.ACHIEVEMENT_CATEGORY_TABLE_NAME,
                            AchievementCategory.ACHIEVEMENT_CATEGORY_TABLE_KEY,
                            columns,
                            values
                    );
                }
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


}
