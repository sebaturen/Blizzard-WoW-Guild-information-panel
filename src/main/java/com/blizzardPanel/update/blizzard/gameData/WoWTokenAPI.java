package com.blizzardPanel.update.blizzard.gameData;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.WoWToken;
import com.blizzardPanel.update.blizzard.BlizzardAPI;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public class WoWTokenAPI extends BlizzardAPI {

    public WoWTokenAPI(WoWAPIService apiCalls) {
        super(apiCalls);
    }

    /**
     * Update a WoWToken Price
     */
    public void update() {
        try {
            JsonArray token_db = BlizzardUpdate.dbConnect.select(
                    WoWToken.TABLE_NAME,
                    new String[]{WoWToken.TABLE_KEY},
                    "1=1 ORDER BY "+ WoWToken.TABLE_KEY +" desc LIMIT 1",
                    new String[]{}
            );

            long lastUpdate = 0L;
            if (token_db.size() == 1) {
                lastUpdate = token_db.get(0).getAsJsonObject().get(WoWToken.TABLE_KEY).getAsLong();
            }

            Call<JsonObject> call = apiCalls.wowTokenIndex(
                    "dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    BlizzardUpdate.shared.accessToken.getAuthorization(),
                    BlizzardUpdate.parseDateFormat(lastUpdate)
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject wowTokenDetail = response.body();
                        try {
                            BlizzardUpdate.dbConnect.insert(
                                    WoWToken.TABLE_NAME,
                                    WoWToken.TABLE_KEY,
                                    new String[]{
                                            WoWToken.TABLE_KEY,
                                            "price"
                                    },
                                    new String[]{
                                            wowTokenDetail.get("last_updated_timestamp").getAsString(),
                                            wowTokenDetail.get("price").getAsString()
                                    }
                            );
                            Logs.infoLog(this.getClass(), "OK - WoW token is UPDATE");
                        } catch (DataException | SQLException e) {
                            Logs.fatalLog(this.getClass(), "FAILED - to INSERT new wow token value "+ e);
                        }
                    } else {
                        if (response.code() == HttpServletResponse.SC_NOT_MODIFIED) {
                            Logs.infoLog(this.getClass(), "NOT Modified WoW Token ");
                        } else {
                            Logs.errorLog(this.getClass(), "ERROR - WoW Token - "+ response.code() +" // "+ call.request());
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Logs.fatalLog(this.getClass(), "FAILED - to update wow token "+ t);
                }
            });
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get History wow token "+ e);
        }
    }
}
