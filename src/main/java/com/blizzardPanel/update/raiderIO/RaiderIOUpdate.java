package com.blizzardPanel.update.raiderIO;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.characters.CharacterInfo;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.sql.SQLException;

public class RaiderIOUpdate {

    public static final String currentSeason = "season-sl-1";

    public static RaiderIOUpdate shared = new RaiderIOUpdate();
    public static DBConnect dbConnect = new DBConnect();

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

    public void updateCharacterIO(long characterId, String characterName, String realmSlug) {
        Call<JsonObject> call = apiCalls.characterIO(
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                realmSlug,
                characterName,
                currentSeason
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().has("characterDetails")) {
                        JsonObject charDetail = response.body().getAsJsonObject("characterDetails");
                        try {
                            if (charDetail.has("mythicPlusScores")) {
                                RaiderIOUpdate.dbConnect.update(
                                        CharacterInfo.TABLE_NAME,
                                        new String[]{"mythicPlusScores"},
                                        new String[]{charDetail.getAsJsonObject("mythicPlusScores").toString()},
                                        CharacterInfo.TABLE_KEY +"=?",
                                        new String[]{characterId+""}
                                );
                                Logs.infoLog(this.getClass(), "OK raider.io mythic score process update completed (r:"+ realmSlug +"/c:"+ characterName +") ["+ characterId +"] ");
                            }
                            if (charDetail.has("bestMythicPlusScore")
                                    && !charDetail.get("bestMythicPlusScore").isJsonNull()) {
                                RaiderIOUpdate.dbConnect.update(
                                        CharacterInfo.TABLE_NAME,
                                        new String[]{"bestMythicPlusScore"},
                                        new String[]{charDetail.getAsJsonObject("bestMythicPlusScore").toString()},
                                        CharacterInfo.TABLE_KEY +"=?",
                                        new String[]{characterId+""}
                                );
                                Logs.infoLog(this.getClass(), "OK raider.io best mythic plus score update completed (r:"+ realmSlug +"/c:"+ characterName +") ["+ characterId +"] ");
                            }
                        } catch (SQLException | DataException e) {
                            Logs.fatalLog(this.getClass(), "FAILED to update Raider.IO (r:"+ realmSlug +"/c:"+ characterName +") ["+ characterId +"] "+ e);
                        }
                    } else {
                        Logs.infoLog(this.getClass(), "Character NOT have a Rader IO (r:"+ realmSlug +"/c:"+ characterName +") ["+ characterId +"]");
                    }
                } else {
                    Logs.errorLog(this.getClass(), "ERROR to update Raider IO (r:"+ realmSlug +"/c:"+ characterName +") ["+ characterId +"] "+ response.code() +"/"+ call.request());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logs.fatalLog(this.getClass(), "FAILED to update Raider IO (r:"+ realmSlug +"/c:"+ characterName +") ["+ characterId +"] "+ t);
            }
        });

    }
}
