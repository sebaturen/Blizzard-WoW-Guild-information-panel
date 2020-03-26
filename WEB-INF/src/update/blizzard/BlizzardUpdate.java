package com.blizzardPanel.update.blizzard;

import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.update.blizzard.gameData.*;
import com.blizzardPanel.update.blizzard.profile.CharacterProfileAPI;
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
    public WoWAPIService apiCalls;
    public WoWOauthService apiOauthCalls;
    public AccessToken accessToken;

    // Profiles
    public GuildAPI guildAPI;
    public CharacterProfileAPI characterProfileAPI;

    // Game Data
    public StaticInformationAPI staticInformationAPI;
    public AchievementAPI achievementAPI;
    public PlayableClassAPI playableClassAPI;
    public PlayableRaceAPI playableRaceAPI;
    public ConnectedRealmAPI connectedRealmAPI;

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
        characterProfileAPI = new CharacterProfileAPI(apiCalls);

        // Game Data
        achievementAPI = new AchievementAPI(apiCalls);
        staticInformationAPI = new StaticInformationAPI(apiCalls);
        playableClassAPI = new PlayableClassAPI(apiCalls);
        playableRaceAPI = new PlayableRaceAPI(apiCalls);
        connectedRealmAPI = new ConnectedRealmAPI(apiCalls);
    }

    // Generate an AccessToken
    public void generateAccessToken() {


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
        Logs.infoLog(BlizzardUpdate.class, "=== Guild Update");
        //guildAPI.update();
        Logs.infoLog(BlizzardUpdate.class, "=== Guild Update END");
    }

    //------------------------------------------
    // Game Data
    //------------------------------------------

    // Achievements
    public void achievements() {
        Logs.infoLog(BlizzardUpdate.class, "=== Achievements Update");
        achievementAPI.update();
        Logs.infoLog(BlizzardUpdate.class, "=== Achievements Update END");
    }

    public static String parseDateFormat(Long unixTime) {
        return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date(unixTime));
    }

}
