package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.mythicKeystone.KeystoneAffix;
import com.blizzardPanel.gameObject.mythicKeystone.KeystoneDungeon;
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
                    KeystoneDungeon.TABLE_NAME,
                    new String[]{"last_modified"},
                    KeystoneDungeon.TABLE_KEY +"=?",
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
                            KeystoneDungeon.TABLE_NAME,
                            columns,
                            values,
                            KeystoneDungeon.TABLE_KEY +"=?",
                            new String[]{dungId}
                    );
                } else { // Insert
                    columns.add(KeystoneDungeon.TABLE_KEY);
                    values.add(blizz_dungeon.get("id").getAsString());
                    BlizzardUpdate.dbConnect.insert(
                            KeystoneDungeon.TABLE_NAME,
                            KeystoneDungeon.TABLE_KEY,
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
                                KeystoneDungeon.TABLE_NAME,
                                KeystoneDungeon.TABLE_KEY,
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
                    KeystoneAffix.TABLE_NAME,
                    new String[]{"last_modified"},
                    KeystoneAffix.TABLE_KEY +"=?",
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
                BlizzardUpdate.shared.mediaAPI.mediaDetail(blizz_affix.getAsJsonObject("media"));

                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");

                if (isInDb) { // Update
                    BlizzardUpdate.dbConnect.update(
                            KeystoneAffix.TABLE_NAME,
                            columns,
                            values,
                            KeystoneAffix.TABLE_KEY +"=?",
                            new String[]{affixId}
                    );
                } else { // Insert
                    columns.add(KeystoneAffix.TABLE_KEY);
                    values.add(blizz_affix.get("id").getAsString());
                    BlizzardUpdate.dbConnect.insert(
                            KeystoneAffix.TABLE_NAME,
                            KeystoneAffix.TABLE_KEY,
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
}
