package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.Realm;
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

public class ConnectedRealmAPI extends BlizzardAPI {

    public ConnectedRealmAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    public void index() {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        Call<JsonObject> call = apiCalls.connectedRealmIndex(
                "dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                BlizzardUpdate.shared.accessToken.getAuthorization()
        );

        try {

            JsonObject resp = call.execute().body();

            for(JsonElement connectedRealms : resp.getAsJsonArray("connected_realms")) {
                detail(connectedRealms.getAsJsonObject());
            }

        } catch (IOException e) {
            Logs.fatalLog(ConnectedRealmAPI.class, "FAILED - to get all realm index "+ e);
        }

    }

    private void detail(JsonObject connectedRealms) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = connectedRealms.get("href").getAsString();

        try {
            Call<JsonObject> call = apiCalls.freeUrl(
                    urlHref,
                    BlizzardUpdate.shared.accessToken.getAuthorization()
            );

            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {

                JsonObject realmsDetail = resp.body();


                for (JsonElement realm: realmsDetail.getAsJsonArray("realms")) {
                    JsonObject realmDetail = realm.getAsJsonObject();
                    load(realmDetail, resp.headers().getDate("Last-Modified").getTime());
                }
            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(ConnectedRealmAPI.class, "NOT Modified Realms Index detail "+ connectedRealms);
                } else {
                    Logs.errorLog(ConnectedRealmAPI.class, "ERROR - Realms Index detail "+ connectedRealms +" - "+ resp.code());
                }
            }

        } catch (IOException e) {
            Logs.fatalLog(ConnectedRealmAPI.class, "FAILED - to get Realms Index detail "+ e);
        }

    }

    public void load(JsonObject realmDetail) {
        if (BlizzardUpdate.shared.accessToken == null || BlizzardUpdate.shared.accessToken.isExpired()) BlizzardUpdate.shared.generateAccessToken();

        String urlHref = realmDetail.getAsJsonObject("key").get("href").getAsString();
        urlHref = urlHref.split("namespace")[0];
        urlHref += "namespace=dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION");
        String realmId = realmDetail.get("id").getAsString();

        try {
            // Check if exist realm in DB
            JsonArray realm_db = BlizzardUpdate.dbConnect.select(
                    Realm.TABLE_NAME,
                    new String[]{"last_modified"},
                    Realm.TABLE_KEY +"=?",
                    new String[]{realmId+""}
            );
            boolean isInDb = (realm_db.size() > 0);
            long lastModified = 0L;
            if (realm_db.size() > 0) {
                lastModified = realm_db.get(0).getAsJsonObject().get("last_modified").getAsLong();
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
                load(resp.body(), lastModified);
            } else {
                if (resp.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                    Logs.infoLog(ConnectedRealmAPI.class, "NOT Modified realm detail "+ realmId);
                } else {
                    Logs.errorLog(ConnectedRealmAPI.class, "ERROR - realm detail "+ realmId +" - "+ resp.code());
                }
            }
        } catch (IOException | DataException | SQLException e) {
            Logs.fatalLog(ConnectedRealmAPI.class, "FAILED - to get realm detail "+ e);
        }

    }

    public void load(JsonObject realmDetail, long lastModified) {

        try {
            // check if exist in DB:
            JsonArray realm_db = BlizzardUpdate.dbConnect.select(
                    Realm.TABLE_NAME,
                    new String[]{"id"},
                    Realm.TABLE_KEY +" = ?",
                    new String[]{realmDetail.get("id").getAsString()}
            );
            boolean isInDb = (realm_db.size() > 0);

            // Prepare values:
            List<Object> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            columns.add("slug");
            values.add(realmDetail.get("slug").getAsString());
            columns.add("name");
            values.add(realmDetail.getAsJsonObject("name").toString());
            columns.add("locale");
            values.add(realmDetail.get("locale").getAsString());
            columns.add("timezone");
            values.add(realmDetail.get("timezone").getAsString());

            columns.add("type_type");
            values.add(realmDetail.getAsJsonObject("type").get("type").getAsString());
            BlizzardUpdate.shared.staticInformationAPI.type(realmDetail.getAsJsonObject("type"));

            columns.add("last_modified");
            values.add(lastModified +"");

            if (isInDb) { // Update
                BlizzardUpdate.dbConnect.update(
                        Realm.TABLE_NAME,
                        columns,
                        values,
                        Realm.TABLE_KEY+"=?",
                        new String[]{realmDetail.get("id").getAsString()}
                );
            } else { // Insert
                columns.add(Realm.TABLE_KEY);
                values.add(realmDetail.get("id").getAsString());
                BlizzardUpdate.dbConnect.insert(
                        Realm.TABLE_NAME,
                        Realm.TABLE_KEY,
                        columns,
                        values
                );
            }

            Logs.infoLog(ConnectedRealmAPI.class, "Realm info OK "+ realmDetail.get("slug").getAsString());
        } catch (SQLException | DataException e) {
            Logs.fatalLog(ConnectedRealmAPI.class, "FAILED - real load detail "+ e +" -- "+ realmDetail);
        }
    }
}
