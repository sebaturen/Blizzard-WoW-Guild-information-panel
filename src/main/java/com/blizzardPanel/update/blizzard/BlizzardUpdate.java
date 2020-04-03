package com.blizzardPanel.update.blizzard;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.update.UpdateService;
import com.blizzardPanel.update.blizzard.gameData.*;
import com.blizzardPanel.update.blizzard.profile.CharacterProfileAPI;
import com.blizzardPanel.update.blizzard.profile.GuildAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.Credentials;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BlizzardUpdate implements Runnable {

    // Share object
    public static BlizzardUpdate shared = BlizzardUpdate.build();
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
    public PlayableSpecializationAPI playableSpecializationAPI;
    public ConnectedRealmAPI connectedRealmAPI;
    public MediaAPI mediaAPI;
    public SpellAPI spellAPI;
    public ItemAPI itemAPI;
    public MythicKeystoneDungeonAPI mythicKeystoneDungeonAPI;
    public WoWTokenAPI woWToken;

    // Runnable Type
    private UpdateType type;

    // Constructor
    public BlizzardUpdate(UpdateType type) {
        this.type = type;
    }

    // Only use for a singleton
    private BlizzardUpdate() {

    }

    // Build a singleton
    private static BlizzardUpdate build() {
        BlizzardUpdate newBlizzUpdate = new BlizzardUpdate();

        // Load Retrofit API calls platform
        Retrofit apiOauthCallsRetrofit = new Retrofit.Builder()
                .baseUrl(String.format(WoWOauthService.API_OAUTH_URL, GeneralConfig.getStringConfig("SERVER_LOCATION")))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        newBlizzUpdate.apiOauthCalls = apiOauthCallsRetrofit.create(WoWOauthService.class);
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(BlizzardAPI.maxSecondRequest/2/2);
        dispatcher.setMaxRequestsPerHost(BlizzardAPI.maxHourRequest);
        Retrofit apiCallsRetrofit = new Retrofit.Builder()
                .baseUrl(String.format(WoWAPIService.API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION")))
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .dispatcher(dispatcher)
                        .build()
                )
                .build();
        newBlizzUpdate.apiCalls = apiCallsRetrofit.create(WoWAPIService.class);

        // Get token
        newBlizzUpdate.generateAccessToken();

        // Profiles
        newBlizzUpdate.guildAPI = new GuildAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.characterProfileAPI = new CharacterProfileAPI(newBlizzUpdate.apiCalls);

        // Game Data
        newBlizzUpdate.achievementAPI = new AchievementAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.staticInformationAPI = new StaticInformationAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.playableClassAPI = new PlayableClassAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.playableRaceAPI = new PlayableRaceAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.playableSpecializationAPI = new PlayableSpecializationAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.connectedRealmAPI = new ConnectedRealmAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.mediaAPI = new MediaAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.spellAPI = new SpellAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.itemAPI = new ItemAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.mythicKeystoneDungeonAPI = new MythicKeystoneDungeonAPI(newBlizzUpdate.apiCalls);
        newBlizzUpdate.woWToken = new WoWTokenAPI(newBlizzUpdate.apiCalls);

        return newBlizzUpdate;
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

    //-----------------------------------------
    // Runnable Update process
    //-----------------------------------------
    @Override
    public void run() {
        switch (type) {
            case GUILD:
                BlizzardUpdate.shared.guildUpdate();
                break;
            case GUILD_ACTIVITIES:
                BlizzardUpdate.shared.guildNewsUpdate();
                break;
            case WOW_TOKEN:
                BlizzardUpdate.shared.wowTokenUpdate();
                break;
            case PLAYABLE_CLASS:
                BlizzardUpdate.shared.playableClassUpdate();
                break;
            case MYTHIC_KEYSTONE_SEASON:
                BlizzardUpdate.shared.mythicKeystoneSeasonUpdate();
                break;
        }
    }

    //------------------------------------------
    // Profile
    //------------------------------------------

    // Guild
    public void guildUpdate() {
        Logs.infoLog(this.getClass(), "=== Guild Update");
        BlizzardUpdate.shared.guildAPI.info(
                GeneralConfig.getStringConfig("GUILD_REALM"),
                GeneralConfig.getStringConfig("GUILD_NAME")
        );
        // Save update process is finished...
        saveUpdateProcessComplete(UpdateType.GUILD);
        Logs.infoLog(this.getClass(), "=== Guild Update END");
    }

    // Guild news
    public void guildNewsUpdate() {
        Logs.infoLog(this.getClass(), "=== Guild News Update");
        // Get all guild we need update
        try {
            JsonArray guilds_db = BlizzardUpdate.dbConnect.selectQuery(
                    "SELECT " +
                    "    g.id, "+
                    "    g.name, " +
                    "    r.slug " +
                    "FROM " +
                    "    guild_info g, " +
                    "    realms r " +
                    "WHERE " +
                    "    g.realm_id = r.id " +
                    "    AND g.full_sync = TRUE;"
            );

            for (JsonElement guild : guilds_db) {
                JsonObject guildDetail = guild.getAsJsonObject();
                BlizzardUpdate.shared.guildAPI.activities(
                        guildDetail.get("slug").getAsString(),
                        guildDetail.get("name").getAsString(),
                        guildDetail.get("id").getAsLong()
                );
            }

            // Save process is complete
            saveUpdateProcessComplete(UpdateType.GUILD_ACTIVITIES);
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED - to get a guild to update");
        }
        Logs.infoLog(this.getClass(), "=== Guild News Update END");
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

    // Reload Items info
    public void items() {
        Logs.infoLog(BlizzardUpdate.class, "=== Items Update");
        itemAPI.update();
        Logs.infoLog(BlizzardUpdate.class, "=== Items Update END");
    }

    // Wow Token
    public void wowTokenUpdate() {
        BlizzardUpdate.shared.woWToken.update();
        saveUpdateProcessComplete(UpdateType.WOW_TOKEN);
    }

    // Playable Class
    public void playableClassUpdate() {
        BlizzardUpdate.shared.playableClassAPI.update();
        saveUpdateProcessComplete(UpdateType.PLAYABLE_CLASS);
    }

    // Mythic Keystone Season
    public void mythicKeystoneSeasonUpdate() {
        BlizzardUpdate.shared.mythicKeystoneDungeonAPI.seasonsUpdate();
        saveUpdateProcessComplete(UpdateType.MYTHIC_KEYSTONE_SEASON);
    }

    //------------------------------------------
    // Static Methods
    //------------------------------------------

    public static void saveUpdateProcessComplete(UpdateType type) {
        try {
            BlizzardUpdate.dbConnect.insert(
                    UpdateService.TABLE_NAME,
                    UpdateService.TABLE_KEY,
                    new String[]{"type", "update_time"},
                    new String[]{type.toString(), new Date().getTime() +""}
            );
            Logs.infoLog(BlizzardUpdate.class, type +" update process completed");
        } catch (DataException | SQLException e) {
            Logs.fatalLog(BlizzardUpdate.class, "FAILED - to save "+ type +" update process. "+ e);
        }
    }

    public static String parseDateFormat(Long unixTime) {
        return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date(unixTime));
    }

}
