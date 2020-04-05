package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Media;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MediaAPI extends BlizzardAPI {

    public MediaAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    /**
     * Load media details
     * @param type Media TYPE
     * @param reference {"key": {"kref": URL}, "id": ID}
     */
    public void mediaDetail(Media.type type, JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.getAsJsonObject("key").get("href").getAsString();
        urlHref = urlHref.split("namespace")[0];
        urlHref += "namespace=static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION");

        String mediaId = reference.get("id").getAsString();

        // Prepare save type
        String tableName = "";
        String tableKey = "";
        switch (type) {
            case ITEM:
                tableName = Media.ITEM_TABLE_NAME;
                tableKey = Media.ITEM_TABLE_KEY;
                break;
            case P_SPEC:
                tableName = Media.P_SPEC_TABLE_NAME;
                tableKey = Media.P_SPEC_TABLE_KEY;
                break;
            case P_CLASS:
                tableName = Media.P_CLASS_TABLE_NAME;
                tableKey = Media.P_CLASS_TABLE_KEY;
                break;
            case ACHIEVEMENT:
                tableName = Media.ACHIEVEMENT_TABLE_NAME;
                tableKey = Media.ACHIEVEMENT_TABLE_KEY;
                break;
            case SPELL:
                tableName = Media.SPELL_TABLE_NAME;
                tableKey = Media.SPELL_TABLE_KEY;
                break;
            case KEYSTONE_AFFIX:
                tableName = Media.KEYSTONE_AFFIX_TABLE_NAME;
                tableKey = Media.KEYSTONE_AFFIX_TABLE_KEY;
                break;
            case INSTANCE:
                tableName = Media.INSTANCE_TABLE_NAME;
                tableKey = Media.INSTANCE_TABLE_KEY;
                break;
            case CREATURE:
                tableName = Media.CREATURE_TABLE_NAME;
                tableKey = Media.CREATURE_TABLE_KEY;
                break;
            default:
                Logs.fatalLog(this.getClass(), "MEDIA TYPE IS NOT PROVIDED");
                System.exit(1);
        }

        try {

            // Check is category previously exist:
            JsonArray media_db = BlizzardUpdate.dbConnect.select(
                    tableName,
                    new String[]{"last_modified"},
                    tableKey +"=?",
                    new String[]{mediaId}
            );
            boolean isInDb = (media_db.size() > 0);
            long lastModified = 0L;
            if (media_db.size() > 0) {
                lastModified = media_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
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
                JsonObject blizz_mediaData = resp.body();
                JsonObject blizz_mediaAsset = blizz_mediaData.getAsJsonArray("assets").get(0).getAsJsonObject();

                // Prepare values:
                List<Object> columns = new ArrayList<>();
                List<Object> values = new ArrayList<>();
                columns.add("key");
                values.add(blizz_mediaAsset.get("key").getAsString());
                columns.add("value");
                values.add(blizz_mediaAsset.get("value").getAsString());
                columns.add("last_modified");
                values.add(resp.headers().getDate("Last-Modified").getTime() +"");

                if (isInDb) { // UPDATE
                    BlizzardUpdate.dbConnect.update(
                            tableName,
                            columns,
                            values,
                            tableKey+" = ?",
                            new String[]{mediaId}
                    );
                    Logs.infoLog(this.getClass(), type +" media IS UPDATE "+ mediaId);
                } else { // INSERT
                    columns.add(tableKey);
                    values.add(mediaId);
                    BlizzardUpdate.dbConnect.insert(
                            tableName,
                            tableKey,
                            columns,
                            values
                    );
                    Logs.infoLog(this.getClass(), type +" media IS INSERT "+ mediaId);
                }
            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified "+ type +" media "+ mediaId);
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - "+ type +" media "+ mediaId +" - "+ resp.code() +" // "+ call.request());
                }
            }

        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get/update "+ type +" media ("+ tableName +"/"+ tableKey +") "+ e);
        }
    }

}
