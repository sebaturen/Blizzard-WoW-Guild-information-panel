package com.blizzardPanel.update.raiderIO;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RaiderIOUpdate {

    public static RaiderIOUpdate shared = new RaiderIOUpdate();

    // Api calls
    private RaiderIOService apiCalls;

    private RaiderIOUpdate() {
        Retrofit apiRaiderCalls = new Retrofit.Builder()
                .baseUrl(RaiderIOService.API_ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiCalls = apiRaiderCalls.create(RaiderIOService.class);

    }

    public JsonObject getCurrentAffixes() {
        Call<JsonObject> call = apiCalls.currentAffixes(GeneralConfig.getStringConfig("SERVER_LOCATION"));

        try {
            return call.execute().body();
        } catch (IOException e) {
            Logs.errorLog(this.getClass(), "Error to get Current Affixes "+ e);
        }
        return null;
    }
}
