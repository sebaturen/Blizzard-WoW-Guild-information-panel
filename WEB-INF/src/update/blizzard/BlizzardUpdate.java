package com.blizzardPanel.update.blizzard;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.Item;
import com.blizzardPanel.gameObject.characters.CharacterMember;
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
    public PlayableSpecializationAPI playableSpecializationAPI;
    public ConnectedRealmAPI connectedRealmAPI;
    public MediaAPI mediaAPI;
    public SpellAPI spellAPI;
    public ItemAPI itemAPI;
    public MythicKeystoneDungeonAPI mythicKeystoneDungeonAPI;

    // Constructor
    private BlizzardUpdate() {

        // Load Retrofit API calls platform
        Retrofit apiOauthCallsRetrofit = new Retrofit.Builder()
                .baseUrl(String.format(WoWOauthService.API_OAUTH_URL, GeneralConfig.getStringConfig("SERVER_LOCATION")))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiOauthCalls = apiOauthCallsRetrofit.create(WoWOauthService.class);
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(BlizzardAPI.maxSecondRequest/2/2);
        dispatcher.setMaxRequestsPerHost(BlizzardAPI.maxHourRequest);
        Retrofit apiCallsRetrofit = new Retrofit.Builder()
                .baseUrl(String.format(WoWAPIService.API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION")))
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .dispatcher(dispatcher)
                        .addInterceptor(new RequestInterceptor())
                        .build()
                )
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
        playableSpecializationAPI = new PlayableSpecializationAPI(apiCalls);
        connectedRealmAPI = new ConnectedRealmAPI(apiCalls);
        mediaAPI = new MediaAPI(apiCalls);
        spellAPI = new SpellAPI(apiCalls);
        itemAPI = new ItemAPI(apiCalls);
        mythicKeystoneDungeonAPI = new MythicKeystoneDungeonAPI(apiCalls);
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
        //BlizzardUpdate.shared.guildAPI.info("Ragnaros", "Art of War");
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

    // Reload Items info
    public void items() {
        Logs.infoLog(BlizzardUpdate.class, "=== Items Update");
        itemAPI.update();
        Logs.infoLog(BlizzardUpdate.class, "=== Items Update END");
    }

    public static String parseDateFormat(Long unixTime) {
        return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date(unixTime));
    }

    public void resetIndex() {
        try {

            JsonArray vals = BlizzardUpdate.dbConnect.select(
                    CharacterMember.TABLE_NAME,
                    new String[]{"id"}
            );
            long i = 1;
            for(JsonElement mem : vals) {
                long id = mem.getAsJsonObject().get("id").getAsLong();
                BlizzardUpdate.dbConnect.update(
                        CharacterMember.TABLE_NAME,
                        new String[]{"id"},
                        new String[]{i+""},
                        "id=?",
                        new String[]{id+""}
                );
                System.out.println("Move ["+ id +"] -> "+ i);
                i++;
            }

        }catch (SQLException | DataException e ) {
            System.out.println("FAILED "+ e);
        }
    }

    /*------- UPDATE OLD INFO
    public static int counter = 0;
    public void loadAllCharacterDetail() {
        try {
            JsonArray allMembers = dbConnect.selectQuery(
                    "SELECT " +
                    "   c.id, "+
                    "   c.name, " +
                    "   r.slug " +
                    "FROM " +
                    "   `characters` c, " +
                    "   realms r " +
                    "WHERE " +
                    "   c.realm_id = r.id"
            );

            counter = 0;
            for(JsonElement chara : allMembers) {
                JsonObject charactMinDet = chara.getAsJsonObject();
                new Thread(() -> {
                    if (!characterProfileAPI.summary(charactMinDet.get("slug").getAsString(), charactMinDet.get("name").getAsString())) {
                        try {
                            dbConnect.update(
                                    CharacterMember.TABLE_NAME,
                                    new String[]{"is_valid"},
                                    new String[]{"0"},
                                    "id=?",
                                    new String[]{charactMinDet.get("id").getAsString()}
                            );
                        } catch (DataException | SQLException e) {
                            System.out.println("fail minimal update");
                        }
                    }
                    counter--;
                }).start();
                counter++;
                while (counter >= 50) {
                    System.out.println("ESPERANDO------");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException | DataException e) {
            System.out.println("Fail to update ");
        }
    }
    public void updateOld() {
        try {
            JsonArray duplicateMembers = dbConnect.selectQuery(
                    "SELECT " +
                    "   `name`, " +
                    "   realm_id, " +
                    "   count(*) " +
                    "FROM " +
                    "   `characters` " +
                    "GROUP BY " +
                    "   `name`, " +
                    "   realm_id " +
                    "HAVING " +
                    "   count(*) > 1"
            );

            for(JsonElement dup : duplicateMembers) {

                JsonArray members = dbConnect.select(
                        CharacterMember.TABLE_NAME,
                        new String[]{"id", "name", "realm_id"},
                        "`name` = ? AND realm_id = ?",
                        new String[]{dup.getAsJsonObject().get("name").getAsString(), dup.getAsJsonObject().get("realm_id").getAsString()}
                );

                int oldId = 0;
                String oldName = "";
                int oldRealmId = 0;
                for(JsonElement mem : members) {
                    JsonObject m = mem.getAsJsonObject();
                    System.out.println("Member "+ m.get("id") +" == "+ m.get("name") +" // "+ m.get("realm_id"));

                    if (oldName.equals(m.get("name").getAsString()) && oldRealmId == m.get("realm_id").getAsInt()) {

                        System.out.println("Update OLD/NEW "+ oldId +"/"+ m.get("id") +" -- "+ oldName + "/"+ m.get("name") +" -- "+ oldRealmId +"/"+ m.get("realm_id"));
                        if (false) {

                            try {
                                BlizzardUpdate.dbConnect.selectQuery("update character_info set character_id = "+ m.get("id") +", faction_type = 'ALLIANCE' where character_id = "+ oldId +"; ");
                                System.out.println("Char INFO - OK");
                            } catch (DataException | SQLException e) {
                                System.out.println("Char info FAILED - "+ e);
                            }

                            try {
                                BlizzardUpdate.dbConnect.selectQuery("update character_items set character_id = "+ m.get("id") +" where character_id = "+ oldId +"; ");
                                System.out.println("Char ITEM - OK");
                            } catch (DataException | SQLException e) {
                                System.out.println("Char ITEM FAILED - "+ e);
                            }

                            try {
                                BlizzardUpdate.dbConnect.selectQuery("update character_specs set character_id = "+ m.get("id") +" where character_id = "+ oldId +"; ");
                                System.out.println("Char SPEC - OK");
                            } catch (DataException | SQLException e) {
                                System.out.println("Char SPEC FAILED - "+ e);
                            }

                            try {
                                BlizzardUpdate.dbConnect.selectQuery("update guild_activities set character_id = "+ m.get("id") +" where character_id = "+ oldId +"; ");
                                System.out.println("Char ACTIVITIES - OK");
                            } catch (DataException | SQLException e) {
                                System.out.println("Char ACTIVITIES FAILED - "+ e);
                            }

                            try {
                                BlizzardUpdate.dbConnect.selectQuery("update keystone_dungeon_run_members set character_id = "+ m.get("id") +" where character_id = "+ oldId +"; ");
                                System.out.println("Char KEYRUNS - OK");
                            } catch (DataException | SQLException e) {
                                System.out.println("Char KEYRUNS FAILED - "+ e);
                            }

                            try {
                                BlizzardUpdate.dbConnect.selectQuery("delete `characters` where id = "+ oldId +";");
                                System.out.println("Char DELETE - OK");
                            } catch (DataException | SQLException e) {
                                System.out.println("Char DELETE FAILED - "+ e);
                            }
                        }

                    } else {
                        oldName = m.get("name").getAsString();
                        oldRealmId = m.get("realm_id").getAsInt();
                        oldId = m.get("id").getAsInt();
                    }
                }
            }
        } catch (SQLException | DataException e) {
            System.out.println("Failed to get a duplicate members "+ e);
        }
    } */

}
