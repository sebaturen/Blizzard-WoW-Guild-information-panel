package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.gameObject.mythicKeystones.MythicAffix;
import com.blizzardPanel.gameObject.mythicKeystones.MythicDungeon;
import com.blizzardPanel.gameObject.mythicKeystones.MythicSeason;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MythicKeystoneDungeonAPI extends BlizzardAPI {

    public MythicKeystoneDungeonAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    /**
     * Load keystonde dungeon detail
     * @param reference {"key": {"href": URL}, "id": ID }
     */
    public void dungDetail(JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.getAsJsonObject("key").get("href").getAsString();
        String dungId = reference.get("id").getAsString();

        boolean isInDb = false;
        long lastModified = 0L;
        try {
            // Check if dungeon previously exist:
            JsonArray dun_db = BlizzardUpdate.dbConnect.select(
                    MythicDungeon.TABLE_NAME,
                    new String[]{"last_modified"},
                    MythicDungeon.TABLE_KEY +"=?",
                    new String[]{dungId}
            );
            isInDb = (dun_db.size() > 0);
            if (dun_db.size() > 0) {
                lastModified = dun_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare Call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            // Run call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject blizz_dungeon = resp.body();

                // Prepare Values
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name");
                values.add(blizz_dungeon.getAsJsonObject("dungeon").getAsJsonObject("name").toString());

                columns.add("slug");
                values.add(blizz_dungeon.getAsJsonObject("zone").get("slug").getAsString());

                for(JsonElement upgrade : blizz_dungeon.getAsJsonArray("keystone_upgrades")) {
                    JsonObject upgradeDetail = upgrade.getAsJsonObject();
                    long upLvl = upgradeDetail.get("upgrade_level").getAsLong();
                    String duration = upgradeDetail.get("qualifying_duration").getAsString();
                    columns.add("keystone_upgrades_"+ upLvl);
                    values.add(duration);
                }

                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");

                if (isInDb) { // Update
                    BlizzardUpdate.dbConnect.update(
                            MythicDungeon.TABLE_NAME,
                            columns,
                            values,
                            MythicDungeon.TABLE_KEY +"=?",
                            new String[]{dungId}
                    );
                } else { // Insert
                    columns.add(MythicDungeon.TABLE_KEY);
                    values.add(blizz_dungeon.get("id").getAsString());
                    BlizzardUpdate.dbConnect.insert(
                            MythicDungeon.TABLE_NAME,
                            MythicDungeon.TABLE_KEY,
                            columns,
                            values
                    );
                }

                Logs.infoLog(this.getClass(), "OK - Mythic Dungeon detail is update "+ dungId);


            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Mythic Dungeon detail " + dungId);
                } else if (resp.code() == HttpServletResponse.SC_NOT_FOUND) {
                    // Save low data (blizzard send a previews expansion and in API is empty now
                    if (!isInDb) { // Insert
                        BlizzardUpdate.dbConnect.insert(
                                MythicDungeon.TABLE_NAME,
                                MythicDungeon.TABLE_KEY,
                                new String[]{
                                        "id",
                                        "name",
                                        "slug",
                                        "keystone_upgrades_1",
                                        "keystone_upgrades_2",
                                        "keystone_upgrades_3",
                                        "last_modified"
                                },
                                new String[]{
                                        reference.get("id").getAsString(),
                                        reference.get("name").getAsJsonObject().toString(),
                                        "OLD",
                                        "0",
                                        "0",
                                        "0",
                                        new Date().getTime() +""
                                }
                        );
                    }
                    Logs.infoLog(this.getClass(), "OK/OLD - Mythic Dungeon detail is old, NO HAVE DATA "+ dungId);
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - Mythic Dungeon detail "+ dungId +" - "+ resp.code() +" // "+ call.request());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get Mythic Dungeon detail "+ e);
        }

    }

    /**
     * Load Affixes detail
     * @param reference {"key": {"href": URL}, "id": ID}
     */
    public void affixesDetail(JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.getAsJsonObject("key").get("href").getAsString();
        urlHref = urlHref.split("namespace")[0];
        urlHref += "namespace=static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION");

        String affixId = reference.get("id").getAsString();

        try {
            // Check if dungeon previously exist:
            JsonArray affix_db = BlizzardUpdate.dbConnect.select(
                    MythicAffix.TABLE_NAME,
                    new String[]{"last_modified"},
                    MythicAffix.TABLE_KEY +"=?",
                    new String[]{affixId}
            );
            boolean isInDb = (affix_db.size() > 0);
            long lastModified = 0L;
            if (affix_db.size() > 0) {
                lastModified = affix_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare Call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            // Run call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject blizz_affix = resp.body();

                // Prepare Values
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("name");
                values.add(blizz_affix.getAsJsonObject("name").toString());

                columns.add("description");
                values.add(blizz_affix.getAsJsonObject("description").toString());

                columns.add("media_id");
                values.add(blizz_affix.getAsJsonObject("media").get("id").getAsString());
                BlizzardUpdate.shared.mediaAPI.mediaDetail(Media.type.KEYSTONE_AFFIX, blizz_affix.getAsJsonObject("media"));

                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");

                if (isInDb) { // Update
                    BlizzardUpdate.dbConnect.update(
                            MythicAffix.TABLE_NAME,
                            columns,
                            values,
                            MythicAffix.TABLE_KEY +"=?",
                            new String[]{affixId}
                    );
                } else { // Insert
                    columns.add(MythicAffix.TABLE_KEY);
                    values.add(blizz_affix.get("id").getAsString());
                    BlizzardUpdate.dbConnect.insert(
                            MythicAffix.TABLE_NAME,
                            MythicAffix.TABLE_KEY,
                            columns,
                            values
                    );
                }

                Logs.infoLog(this.getClass(), "OK - Mythic Affix detail "+ affixId);

            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Mythic Affix detail "+ affixId);
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - Mythic Affix detail "+ affixId +" - "+ resp.code() +" // "+ call.request());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get Mythic Affix detail "+ e);
        }

    }

    /**
     * Foreach all seasons and save the periods
     */
    public void seasonsUpdate() {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        Call<JsonObject> call = apiCalls.mythicKeystoneSeasonIndex(
                "dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                BlizzardUpdate.shared.accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray seasons = response.body().getAsJsonArray("seasons");
                    for(JsonElement season : seasons) {
                        season(season.getAsJsonObject());
                    }
                } else {
                    Logs.errorLog(this.getClass(), "ERROR to get a seasons index "+ response.code() +" - "+ call.request());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logs.fatalLog(this.getClass(), "FAILED - to get a seaons index "+ t);
            }
        });
    }

    /**
     * Update a season reference
     * @param reference {"key": {"href": URL}, "id": ID }
     */
    public void season(JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.getAsJsonObject("key").get("href").getAsString();
        String seasonId = reference.get("id").getAsString();

        try {
            JsonArray season_db = BlizzardUpdate.dbConnect.select(
                    MythicSeason.TABLE_NAME,
                    new String[]{MythicSeason.TABLE_KEY, "last_modified"},
                    MythicSeason.TABLE_KEY+"=?",
                    new String[]{seasonId}
            );
            boolean isInDb = (season_db.size() > 0);
            long lastModified = 0L;
            if (season_db.size() > 0) {
                lastModified = season_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject seasonDetail = response.body();

                        // Prepare Values
                        List<Object> columns = new ArrayList<>();
                        List<Object> values = new ArrayList<>();
                        columns.add("start_timestamp");
                        values.add(seasonDetail.get("start_timestamp").getAsString());

                        if (seasonDetail.has("end_timestamp")) {
                            columns.add("end_timestamp");
                            values.add(seasonDetail.get("end_timestamp").getAsString());
                        }

                        columns.add("last_modified");
                        values.add(response.headers().getDate("Last-Modified").getTime() +"");

                        try {
                            if (isInDb) { // Update
                                BlizzardUpdate.dbConnect.update(
                                        MythicSeason.TABLE_NAME,
                                        columns,
                                        values,
                                        MythicSeason.TABLE_KEY +"=?",
                                        new String[]{seasonId}
                                );
                                Logs.infoLog(this.getClass(), "OK - Mythic Season is UPDATE ["+ seasonId +"]");
                            } else { // Insert
                                columns.add("id");
                                values.add(seasonDetail.get("id").getAsString());
                                BlizzardUpdate.dbConnect.insert(
                                        MythicSeason.TABLE_NAME,
                                        MythicSeason.TABLE_KEY,
                                        columns,
                                        values
                                );
                                Logs.infoLog(this.getClass(), "OK - Mythic Season is INSERT ["+ seasonId +"]");
                            }
                        } catch (SQLException | DataException e) {
                            Logs.fatalLog(this.getClass(), "FAILED - to insert/update Mythic Season detail ["+ seasonId +"] - "+ e);
                        }
                    } else {
                        if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                            Logs.infoLog(this.getClass(), "NOT Modified Mythic Season detail "+ seasonId);
                        } else {
                            Logs.errorLog(this.getClass(), "ERROR - Mythic Season detail "+ seasonId +" - "+ response.code() +" // "+ call.request());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Logs.errorLog(this.getClass(), "FAILED - to get a mythic season ["+ seasonId +"] detail "+ t);
                }
            });


        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get Mythic Season detail "+ e);
        }

    }
}
