package com.blizzardPanel.update.blizzard;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.update.blizzard.gameData.AchievementAPI;
import com.blizzardPanel.update.blizzard.profile.GuildAPI;
import com.google.gson.JsonObject;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BlizzardUpdate {

    // Share object
    public static BlizzardUpdate shared = new BlizzardUpdate();
    public static DBConnect dbConnect = new DBConnect();

    // API calls lib
    private WoWAPIService apiCalls;
    private WoWOauthService apiOauthCalls;
    private AccessToken accessToken;

    // Profiles
    private GuildAPI guildAPI;

    // Game Data
    private AchievementAPI achievementAPI;

    // Constructor
    private BlizzardUpdate() {

        // Load Retrofit API calls platform
        Retrofit apiOauthCallsRetrofit = new Retrofit.Builder()
                .baseUrl(String.format(WoWOauthService.API_OAUTH_URL, GeneralConfig.getStringConfig("SERVER_LOCATION")))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiOauthCalls = apiOauthCallsRetrofit.create(WoWOauthService.class);
        Retrofit apiCallsRetrofit = new Retrofit.Builder()
                .baseUrl(String.format(WoWAPIService.API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION")))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiCalls = apiCallsRetrofit.create(WoWAPIService.class);

        // Get token
        generateAccessToken();

        // Profiles
        guildAPI = new GuildAPI(apiCalls);

        // Game Data
        achievementAPI = new AchievementAPI(apiCalls);
    }

    // Generate an AccessToken
    private void generateAccessToken() {

        Call<JsonObject> call = apiOauthCalls.accessToken(
                "client_credentials",
                Credentials.basic(GeneralConfig.getStringConfig("CLIENT_ID"), GeneralConfig.getStringConfig("CLIENT_SECRET"))
        );

        try {

            JsonObject aToken = call.execute().body();
            System.out.println("==== New AccessToken "+ aToken);

            accessToken = new AccessToken();
            accessToken.setAccess_token(aToken.get("access_token").getAsString());
            accessToken.setToken_type(aToken.get("token_type").getAsString());
            accessToken.setExpire_in(aToken.get("expires_in").getAsDouble());

        } catch (IOException e) {
            Logs.infoLog(BlizzardUpdate.class, "FAIL - Get AccessToken "+ e);
        }

    }

    //------------------------------------------
    // Profile
    //------------------------------------------

    // Guild
    public void guild() {
        if (accessToken == null || accessToken.isExpired()) generateAccessToken();
        Logs.infoLog(BlizzardUpdate.class, "=== Guild Update");
        guildAPI.update(accessToken);
        Logs.infoLog(BlizzardUpdate.class, "=== Guild Update END");
    }

    //------------------------------------------
    // Game Data
    //------------------------------------------

    // Achievements
    public void achievements() {
        if (accessToken == null || accessToken.isExpired()) generateAccessToken();
        Logs.infoLog(BlizzardUpdate.class, "=== Achievements Update");
        achievementAPI.update(accessToken);
        Logs.infoLog(BlizzardUpdate.class, "=== Achievements Update END");
    }

    public static String parseDateFormat(Long unixTime) {
        return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date(unixTime));
    }

}
