package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Item;
import com.blizzardPanel.gameObject.Media;
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
import java.util.List;

public class ItemAPI extends BlizzardAPI {

    public ItemAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    /**
     * Load all item detail
     */
    public void update() {
        try {
            JsonArray items_db = BlizzardUpdate.dbConnect.select(
                    Item.TABLE_NAME,
                    new String[]{Item.TABLE_KEY, "last_modified"}
            );

            for(JsonElement item : items_db) {
                asyncLoad(
                        item.getAsJsonObject().get(Item.TABLE_KEY).getAsLong(),
                        item.getAsJsonObject().get("last_modified").getAsLong(),
                        true
                );
            }

        } catch (SQLException | DataException e) {
            Logs.fatalLog(this.getClass(), "FAILED to get all items in DB");
        }
    }

    /**
     * Load item detail
     * @param detail {"href": xx, "id": ID}
     */
    public void itemDetail(JsonObject detail) {
        load(detail.get("id").getAsLong());
    }

    private void load(long id) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String itemId = id+"";

        try {
            // Check is category previously exist:
            JsonArray item_db = BlizzardUpdate.dbConnect.select(
                    Item.TABLE_NAME,
                    new String[] {"last_modified"},
                    Item.TABLE_KEY +" = ?",
                    new String[] {itemId}
            );
            boolean isInDb = (item_db.size() > 0);
            long lastModified = 0L;
            if (item_db.size() > 0) {
                lastModified = item_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
            }

            // Prepare call
            Call<JsonObject> call = apiCalls.item(
                    itemId,
                    "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastModified)
            );

            // Run call
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject blizz_item = resp.body();
                saveDetail(
                        blizz_item,
                        resp.headers().getDate("Last-Modified").getTime(),
                        isInDb
                );
            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Item Detail "+ itemId);
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - Item detail "+ itemId +" - "+ resp.code() +" // "+ call.request());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get Item detail "+ e);
        }

    }

    private void asyncLoad(long id, long lastUpdate, boolean isInDb) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String itemId = id+"";

        // Prepare call
        Call<JsonObject> call = apiCalls.item(
                itemId,
                "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                BlizzardUpdate.shared.accessToken.getAuthorization(),
                BlizzardUpdate.parseDateFormat(lastUpdate)
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject blizz_item = response.body();
                    saveDetail(
                            blizz_item,
                            response.headers().getDate("Last-Modified").getTime(),
                            isInDb
                    );
                } else {
                    if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                        Logs.infoLog(this.getClass(), "NOT Modified Item Detail "+ itemId);
                    } else {
                        Logs.errorLog(this.getClass(), "ERROR - Item detail "+ itemId +" - "+ response.code() +" // "+ call.request());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.fatalLog(this.getClass(), "FAILED to get Item detail ("+ itemId +") - "+ throwable);
            }
        });
    }

    //******
    private void saveDetail(JsonObject detail, long lastModified, boolean isInDb) {

        String itemId = detail.get("id").getAsString();

        // Prepare Values
        List<Object> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        columns.add("name");
        values.add(detail.getAsJsonObject("name").toString());

        columns.add("is_stackable");
        values.add(detail.get("is_stackable").getAsBoolean()? "1":"0");

        columns.add("quality_type");
        values.add(detail.getAsJsonObject("quality").get("type").getAsString());
        BlizzardUpdate.shared.staticInformationAPI.quality(detail.getAsJsonObject("quality"));

        columns.add("level");
        values.add(detail.get("level").getAsString());

        columns.add("required_level");
        values.add(detail.get("required_level").getAsString());

        columns.add("media_id");
        values.add(detail.getAsJsonObject("media").get("id").getAsString());
        BlizzardUpdate.shared.mediaAPI.mediaDetail(Media.type.ITEM, detail.getAsJsonObject("media"));

        columns.add("inventory_type");
        values.add(detail.getAsJsonObject("inventory_type").get("type").getAsString());
        BlizzardUpdate.shared.staticInformationAPI.quality(detail.getAsJsonObject("inventory_type"));

        columns.add("is_equippable");
        values.add(detail.get("is_equippable").getAsBoolean()? "1":"0");

        columns.add("last_modified");
        values.add(lastModified +"");

        try {
            if (isInDb) { // Update
                BlizzardUpdate.dbConnect.update(
                        Item.TABLE_NAME,
                        columns,
                        values,
                        Item.TABLE_KEY +"=?",
                        new String[]{itemId}
                );
            } else { // Insert
                columns.add(Item.TABLE_KEY);
                values.add(itemId);
                BlizzardUpdate.dbConnect.insert(
                        Item.TABLE_NAME,
                        Item.TABLE_KEY,
                        columns,
                        values
                );
            }

            Logs.infoLog(this.getClass(), "OK Item is update "+ itemId);

        }  catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to save Item detail "+ e);
        }
    }
}
