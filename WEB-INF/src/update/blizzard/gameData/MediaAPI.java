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
     * @param reference {"key": {"kref": URL}, "id": ID}
     */
    public void mediaDetail(JsonObject reference) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = reference.getAsJsonObject("key").get("href").getAsString();
        urlHref = urlHref.split("namespace")[0];
        urlHref += "namespace=static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION");

        String achievMediaId = reference.get("id").getAsString();

        try {

            // Check is category previously exist:
            JsonArray achievMedia_db = BlizzardUpdate.dbConnect.select(
                    Media.TABLE_NAME,
                    new String[]{"last_modified"},
                    Media.TABLE_KEY +" = ?",
                    new String[]{achievMediaId}
            );
            boolean isInDb = (achievMedia_db.size() > 0);
            long lastModified = 0L;
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
                            Media.TABLE_NAME,
                            columns,
                            values,
                            Media.TABLE_KEY+" = ?",
                            new String[]{achievMediaId}
                    );
                } else { // INSERT
                    columns.add(Media.TABLE_KEY);
                    values.add(achievMediaId);
                    BlizzardUpdate.dbConnect.insert(
                            Media.TABLE_NAME,
                            Media.TABLE_KEY,
                            columns,
                            values
                    );
                }

                Logs.infoLog(this.getClass(), "Achievement media OK "+ achievMediaId);

            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(this.getClass(), "NOT Modified Achievement media "+ achievMediaId);
                } else {
                    Logs.errorLog(this.getClass(), "ERROR - achievement media "+ achievMediaId +" - "+ resp.code() +" // "+ call.request());
                }
            }

        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get achievement media "+ e);
        }
    }

}
