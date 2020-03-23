/**
 * File : Update.java
 * Desc : Update guild and character in guild information
 *
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.update.blizzard;

import com.blizzardPanel.*;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.AuctionItem;
import com.blizzardPanel.gameObject.Boss;
import com.blizzardPanel.gameObject.Item;
import com.blizzardPanel.gameObject.guild.Guild;
import com.blizzardPanel.gameObject.guild.achievement.GuildAchievementsList;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.blizzardPanel.gameObject.characters.PlayableClass;
import com.blizzardPanel.gameObject.characters.PlayableRace;
import com.blizzardPanel.gameObject.Spell;
import com.blizzardPanel.gameObject.characters.achievement.CharacterAchivementsCategory;
import com.blizzardPanel.gameObject.characters.achievement.CharacterAchivementsList;
import com.blizzardPanel.gameObject.guild.New;
import com.blizzardPanel.gameObject.guild.challenges.Challenge;
import com.blizzardPanel.gameObject.guild.challenges.ChallengeGroup;
import com.blizzardPanel.gameObject.guild.raids.Raid;
import com.blizzardPanel.gameObject.mythicKeystone.KeystoneDungeon;
import com.blizzardPanel.gameObject.mythicKeystone.KeystoneDungeonRun;
import com.blizzardPanel.gameObject.Realm;
import com.blizzardPanel.gameObject.characters.PlayableSpec;

import java.io.*;

import com.blizzardPanel.update.raiderIO.RaiderIOService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Update {

    // Share object
    public static Update shared = new Update();

    // Update interval DB
    public static final String UPDATE_INTERVAL_TABLE_NAME = "update_timeline";
    public static final String UPDATE_INTERVAL_TABLE_KEY = "id";
    public static final String[] UPDATE_INTERVAL_TABLE_STRUCTURE = {"id", "type", "update_time"};

    // Constant
    public static final int UPDATE_TYPE_DYNAMIC = 0;
    public static final int UPDATE_TYPE_STATIC = 1;
    public static final int UPDATE_TYPE_AUCTION = 2;
    public static final int UPDATE_TYPE_CLEAR_AH_HISTORY = 3;
    public static final int UPDATE_TYPE_GUILD_NEWS = 4;
    public static final int UPDATE_TYPE_AUCTION_CHECK = 5;

    // API call lib
    private WoWAPIService apiCalls;
    private WoWOauthService apiOauthCalls;
    private RaiderIOService apiRaiderIOService;
    private AccessToken accessToken;

    private static final DBConnect dbConnect = new DBConnect();

    /**
     * Constructor. Run a generateAccessToken to generate this token
     */
    public Update() {

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
        Retrofit apiRaiderIOCallsRetrofit = new Retrofit.Builder()
                .baseUrl(RaiderIOService.RAIDER_IO_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiRaiderIOService = apiRaiderIOCallsRetrofit.create(RaiderIOService.class);

        // Get access Token...
        try {
            generateAccessToken();
        } catch (IOException e) {
            Logs.infoLog(Update.class, "FAIL - Update " + e);
        }
    }

    /**
     * Run Dynamic element update method and save the register the last update.
     */
    private void updateDynamicAll() {
        Logs.infoLog(Update.class, "-------Update process is START! (Dynamic)------");
        //Guild information update!
        Logs.infoLog(Update.class, "Guild Information update!");
        try {
            getGuildProfile();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail update Guild Info: " + ex);
        }
        //Guild members information update!
        Logs.infoLog(Update.class, "Guild members information update!");
        try {
            getGuildMembers();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail update Guild Members Info: " + ex);
        }
        //Character information update!
        Logs.infoLog(Update.class, "Character information update!");
        try {
            getCharacterInfo();
        } catch (IOException | SQLException | DataException ex) {
            Logs.errorLog(Update.class, "Fail get a CharacterS Info: " + ex);
        }
        //Guild challenges update!
        Logs.infoLog(Update.class, "Guild challenges update!");
        try {
            getGuildChallenges();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail get a CharacterS Info: " + ex);
        }
        //Guild news update!
        Logs.infoLog(Update.class, "Guild new update!");
        try {
            getGuildNews();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail update guild news Info " + ex);
        }
        //Wow Token
        Logs.infoLog(Update.class, "Wow token information update!");
        try {
            getWowToken();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail update Wow Token Info: " + ex);
        }
        //Users player
        Logs.infoLog(Update.class, "Users characters information update!");
        try {
            getUsersCharacters();
        } catch (IOException | SQLException | DataException | ClassNotFoundException ex) {
            Logs.errorLog(Update.class, "Fail update user characters Info: " + ex);
        }
        //Guild progression RaiderIO
        Logs.infoLog(Update.class, "Guild progression update!");
        getGuildProgression();
        Logs.infoLog(Update.class, "-------Update process is COMPLATE! (Dynamic)------");

        //Save log update in DB
        try {
            /* {"type", "update_time"}; */
            dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                    UPDATE_INTERVAL_TABLE_KEY,
                    DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                    new String[]{UPDATE_TYPE_DYNAMIC + "", getCurrentTimeStamp()});
        } catch (DataException | SQLException e) {
            Logs.errorLog(Update.class, "Fail to save update time: " + e);
        }
    }

    /**
     * Run Static element update
     */
    private void updateStaticAll() {
        Logs.infoLog(Update.class, "-------Update process is START! (Static)------");
        //Realms
        Logs.infoLog(Update.class, "Realms index load...");
        try {
            getRealmIndex();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail get realms index: " + ex);
        }
        //Playable Class
        Logs.infoLog(Update.class, "Playable class Information update!");
        try {
            getPlayableClass();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail update Playable class Info: " + ex);
        }
        //Races
        Logs.infoLog(Update.class, "Playable Races Information update!");
        try {
            getPlayableRaces();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail update Races Info: " + ex);
        }
        //Guild Achivements lists
        Logs.infoLog(Update.class, "Guild Achievements lists information update!");
        try {
            getGuildAchievementsLists();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail update Achievements Info: " + ex);
        }
        //Character Achivements lists
        Logs.infoLog(Update.class, "Characters Achievements lists information update!");
        try {
            getCharacterAchievementsLists();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail update Characters Achievements Info: " + ex);
        }
        //Update Spell information
        Logs.infoLog(Update.class, "Spell information update!");
        try {
            updateSpellInformation();
        } catch (IOException | SQLException | DataException ex) {
            Logs.errorLog(Update.class, "Fail update spell Info: " + ex);
        }
        //Boss DB Upate info
        Logs.infoLog(Update.class, "Boss DB Update");
        try {
            getBossInformation();
        } catch (IOException ex) {
            Logs.errorLog(Update.class, "Fail get boss DB Info: " + ex);
        }
        Logs.infoLog(Update.class, "Item informatio update!");
        try {
            updateItemInformation();
        } catch (IOException | SQLException | DataException ex) {
            Logs.errorLog(Update.class, "Fail update item Info: " + ex);
        }
        Logs.infoLog(Update.class, "Playable Spec update!");
        try {
            getPlayableSpec();
        } catch (IOException | DataException ex) {
            Logs.errorLog(Update.class, "Fail update playable spec Info: " + ex);
        }
        Logs.infoLog(Update.class, "-------Update process is COMPLATE! (Static)------");


        //Save log update in DB
        try {
            /* {"type", "update_time"}; */
            dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                    UPDATE_INTERVAL_TABLE_KEY,
                    DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                    new String[]{UPDATE_TYPE_STATIC + "", getCurrentTimeStamp()});
        } catch (DataException | SQLException e) {
            Logs.errorLog(Update.class, "Fail to save update time: " + e);
        }
    }

    public void setUpdate(String[] args) {
        try {
            int upParam = -1;
            String upInternal = "null";
            if (args.length > 0) upParam = Integer.parseInt(args[0]);
            if (args.length > 1) upInternal = args[1];

            switch (upParam) {
                case Update.UPDATE_TYPE_DYNAMIC:
                    switch (upInternal) {
                        case "GuildProfile":
                            Logs.infoLog(Update.class, "Guild Profile Update...");
                            getGuildProfile();
                            break;
                        case "GuildMembers":
                            Logs.infoLog(Update.class, "Guild Members Update...");
                            getGuildMembers();
                            break;
                        case "CharacterInfo":
                            Logs.infoLog(Update.class, "Character info Update...");
                            getCharacterInfo();
                            break;
                        case "GuildChallenges":
                            Logs.infoLog(Update.class, "Guild Challenges Update...");
                            getGuildChallenges();
                            break;
                        case "GuildNews":
                            Logs.infoLog(Update.class, "Guild News Update...");
                            getGuildNews();
                            break;
                        case "WowToken":
                            Logs.infoLog(Update.class, "Wow Token Update...");
                            getWowToken();
                            break;
                        case "UsersCharacters":
                            Logs.infoLog(Update.class, "User Characters Update...");
                            getUsersCharacters();
                            break;
                        case "GuildProgression":
                            Logs.infoLog(Update.class, "Guild Progression Update...");
                            getGuildProgression();
                            break;
                        default:
                            updateDynamicAll();
                            break;
                    }
                    break;
                case Update.UPDATE_TYPE_STATIC:
                    switch (upInternal) {
                        case "RealmIndex":
                            Logs.infoLog(Update.class, "Realm index....");
                            getRealmIndex();
                            break;
                        case "PlayableClass":
                            Logs.infoLog(Update.class, "Playable Class Update...");
                            getPlayableClass();
                            break;
                        case "PlayableSpec":
                            Logs.infoLog(Update.class, "Playable Spec Update...");
                            getPlayableSpec();
                            break;
                        case "PlayableRaces":
                            Logs.infoLog(Update.class, "Playable Races Update...");
                            getPlayableRaces();
                            break;
                        case "GuildAchievementsLists":
                            Logs.infoLog(Update.class, "Guild Achievements Update...");
                            getGuildAchievementsLists();
                            break;
                        case "CharacterAchievementsLists":
                            Logs.infoLog(Update.class, "Character Achievements Update...");
                            getCharacterAchievementsLists();
                            break;
                        case "BossInformation":
                            Logs.infoLog(Update.class, "Bosses info Update...");
                            getBossInformation();
                            break;
                        case "updateSpellInformation":
                            Logs.infoLog(Update.class, "Spells info Update...");
                            updateSpellInformation();
                            break;
                        case "updateItemInformation":
                            Logs.infoLog(Update.class, "Items info Update...");
                            updateItemInformation();
                            break;
                        default:
                            updateStaticAll();
                            break;
                    }
                    break;
                case Update.UPDATE_TYPE_AUCTION:
                    updateAH();
                    break;
                case Update.UPDATE_TYPE_CLEAR_AH_HISTORY:
                    moveHistoryAH();
                    break;
                default:
                    Logs.errorLog(Update.class, "Not update parametter detected!");
                    break;
            }
            Logs.infoLog(Update.class, "=========== Update proces complete! ====================");
        } catch (IOException | DataException | SQLException | ClassNotFoundException ex) {
            Logs.errorLog(Update.class, "Fail to update information - " + ex);
        }
    }

    /**
     * v2
     * @throws DataException
     * @throws IOException
     */
    public void getRealmIndex() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.connectedRealmIndex(
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                "dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray connRealm = response.body().getAsJsonArray("connected_realms");
                    for (int i = 0; i < connRealm.size(); i++) {


                        String urlRealmConnect = ((JsonObject) connRealm.get(i)).get("href").getAsString();
                        int startId = urlRealmConnect.indexOf("connected-realm/") + "connected-realm/".length();
                        int endId = urlRealmConnect.indexOf("?namespace=");

                        int realmId = Integer.parseInt(urlRealmConnect.substring(startId, endId));
                        Call<JsonObject> callRealmInfo = apiCalls.connectedRealm(
                                realmId,
                                "dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                                accessToken.getAuthorization()
                        );

                        callRealmInfo.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful()) {
                                    JsonObject realmsConn = response.body();
                                    JsonElement realmConnectID = realmsConn.get("id");
                                    JsonArray realms = realmsConn.get("realms").getAsJsonArray();
                                    for (int j = 0; j < realms.size(); j++) {
                                        JsonObject realmInfo = realms.get(j).getAsJsonObject();
                                        realmInfo.add("connected_realm", realmConnectID);
                                        int inId = realmInfo.get("id").getAsInt();
                                        Realm realm = new Realm(inId);
                                        Realm realmBlizz = new Realm(realmInfo);
                                        if (realm.isInternalData()) {
                                            realmBlizz.setIsInternalData(true);
                                        }
                                        realmBlizz.saveInDB();
                                    }
                                } else {
                                    Logs.infoLog(Update.class, "ERROR - RealmDetail " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                                Logs.infoLog(Update.class, "FAIL - RealmDetail " + throwable);
                            }
                        });
                    }
                } else {
                    Logs.infoLog(Update.class, "ERROR - RealmIndex " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - RealmIndex " + throwable);

            }
        });
    }

    /**
     * v2 key detail
     * @param url
     * @return {name, description, icon}
     * @throws IOException
     */
    @Nullable
    public JsonObject loadKeyDetailFromBlizz(String url) {

        try {
            if (accessToken.isExpired()) generateAccessToken();

            JsonObject keyDetail = null;

            Call<JsonObject> call = apiCalls.freeUrl(
                    url,
                    accessToken.getAccess_token()
            );

            JsonObject inf = call.execute().body();
            keyDetail.add("name", inf.get("name").getAsJsonObject().get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")));
            keyDetail.add("description", inf.get("description").getAsJsonObject().get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")));

            Call<JsonObject> iconCall = apiCalls.freeUrl(
                    keyDetail.get("media").getAsJsonObject().get("key").getAsJsonObject().get("href").getAsString(),
                    accessToken.getAccess_token()
            );

            JsonArray iconAsset = iconCall.execute().body().get("assets").getAsJsonArray();
            for (int i = 0; i < iconAsset.size(); i++) {
                JsonObject keyAssetDet = iconAsset.get(i).getAsJsonObject();
                if (keyAssetDet.get("key").getAsString().equals("icon")) {
                    keyDetail.add("icon", keyAssetDet.get("value"));
                    break;
                }
            }

            return keyDetail;
        } catch (IOException e) {
            Logs.infoLog(Update.class, "FAIL - loadKeyDetailFromBlizz " + e);
        }

        return null;
    }

    /**
     * v2
     * @param url
     * @return
     * @throws IOException
     */
    @Nullable
    public KeystoneDungeon getKeyStoneDungeonDetail(String url) {

        try {
            if (accessToken.isExpired()) generateAccessToken();

            KeystoneDungeon kDun = null;

            Call<JsonObject> call = apiCalls.freeUrl(
                    url,
                    accessToken.getAccess_token()
            );

            kDun = new KeystoneDungeon(call.execute().body());
            kDun.saveInDB();

        } catch (IOException e) {
            Logs.infoLog(Update.class, "FAIL - getKeyStoneDungeonDetail " + e);
        }

        return null;
    }

    /**
     * v2 Run AH Update information
     */
    public void updateAH() {
        Logs.infoLog(Update.class, "-------Update process is START! (Auction House)------");
        try {
            JsonObject genInfo = getURLAH();
            String lastUpdate = parseUnixTime(genInfo.get("lastModified").getAsString());
            JsonArray getLastUpdateInDB = dbConnect.select(UPDATE_INTERVAL_TABLE_NAME,
                    UPDATE_INTERVAL_TABLE_STRUCTURE,
                    "type=? AND update_time=?",
                    new String[]{UPDATE_TYPE_AUCTION + "", lastUpdate});
            if (getLastUpdateInDB.size() == 0) {
                //Clear last auItems
                dbConnect.update(AuctionItem.AUCTION_ITEMS_TABLE_NAME,
                        new String[]{"status"},
                        new String[]{"0"},
                        "status = ?",
                        new String[]{"1"});
                Logs.infoLog(Update.class, "AH last update: " + lastUpdate);
                Logs.infoLog(Update.class, "Get a AH update...");

                Call<JsonObject> call = apiCalls.freeUrl(
                        genInfo.get("url").getAsString(),
                        accessToken.getAccess_token()
                );


                JsonObject allAH = call.execute().body();
                JsonArray itemsAH = allAH.get("auctions").getAsJsonArray();

                int iProgres = 1;
                Logs.infoLog(Update.class, "0%");
                for (int i = 0; i < itemsAH.size(); i++) {
                    JsonObject item = itemsAH.get(i).getAsJsonObject();
                    AuctionItem acObItem = new AuctionItem(item);
                    AuctionItem acObItemDB = new AuctionItem(item.get("auc").getAsInt());
                    if (acObItemDB.isInternalData()) {
                        acObItem.setIsInternalData(true);
                        acObItem.setAucDate(acObItemDB.getAucDate());
                    }
                    acObItem.setAucDate(lastUpdate);
                    acObItem.saveInDB();

                    //Show update progress...
                    if ((((iProgres * 2) * 10) * itemsAH.size()) / 100 < i) {
                        Logs.infoLog(Update.class, "..." + ((iProgres * 2) * 10) + "%");
                        iProgres++;
                    }
                }
                Logs.infoLog(Update.class, "...100%");

                /* {"type", "update_time"}; */
                dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                        UPDATE_INTERVAL_TABLE_KEY,
                        DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                        new String[]{UPDATE_TYPE_AUCTION + "", lastUpdate});
            }
            /* {"type", "update_time"}; */
            dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                    UPDATE_INTERVAL_TABLE_KEY,
                    DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                    new String[]{UPDATE_TYPE_AUCTION_CHECK + "", getCurrentTimeStamp()});
        } catch (DataException | IOException | SQLException ex) {
            Logs.errorLog(Update.class, "Fail to get AH " + ex);
        }
        Logs.infoLog(Update.class, "-------Update process is COMPLETE! (Auction House)------");
    }

    /**
     * See the auc_items and move to History DB if auc finish
     */
    public void moveHistoryAH() {
        Logs.infoLog(Update.class, "-------Update process is Start! (Auction House move to History DB)------");
        try {
            JsonArray aucItem = dbConnect.select(AuctionItem.AUCTION_ITEMS_TABLE_NAME,
                    new String[]{AuctionItem.AUCTION_ITEMS_KEY},
                    "status = ?",
                    new String[]{"0"});
            //Get and delete all auc need save in history DB
            int iProgress = 1;
            Logs.infoLog(Update.class, "0%");
            for (int i = 0; i < aucItem.size(); i++) {
                int aucId = aucItem.get(i).getAsJsonObject().get(AuctionItem.AUCTION_ITEMS_KEY).getAsInt();
                AuctionItem aucItemOLD = new AuctionItem(aucId);
               // WORK
                //Show update progress...
                if ((((iProgress * 2) * 10) * aucItem.size()) / 100 < i) {
                    Logs.infoLog(Update.class, "..." + ((iProgress * 2) * 10) + "%");
                    iProgress++;
                }
            }
            Logs.infoLog(Update.class, "...100%");

            /* {"type", "update_time"}; */
            dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                    UPDATE_INTERVAL_TABLE_KEY,
                    DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                    new String[]{UPDATE_TYPE_CLEAR_AH_HISTORY + "", getCurrentTimeStamp()});
        } catch (SQLException | DataException ex) {
            Logs.errorLog(Update.class, "Fail to get current auc items " + ex);
        }
        Logs.infoLog(Update.class, "-------Update process is Complete! (Auction House move to History DB)------");
    }

    /**
     * v2 Blizzard API need a token to access to API, this token you can
     * get if have a ClientID and ClientSecret of the application
     *
     * @throws IOException
     * @throws DataException
     */
    private void generateAccessToken() throws IOException {

        Call<JsonObject> call = apiOauthCalls.accessToken(
                "client_credentials",
                Credentials.basic(GeneralConfig.getStringConfig("CLIENT_ID"), GeneralConfig.getStringConfig("CLIENT_SECRET"))
        );

        JsonObject aToken = call.execute().body();

        accessToken = new AccessToken();
        accessToken.setAccess_token(aToken.get("access_token").getAsString());
        accessToken.setToken_type(aToken.get("token_type").getAsString());
        accessToken.setExpire_in(Double.parseDouble(aToken.get("expires_in").getAsString()));

        System.out.println(accessToken);
    }

    /**
     * v2 Generate a AH information URL
     *
     * @return
     * @throws DataException
     * @throws IOException
     */
    @Nullable
    private JsonObject getURLAH() {

        try {
            if (accessToken.isExpired()) generateAccessToken();

            Call<JsonObject> call = apiCalls.auction(
                    GeneralConfig.getStringConfig("GUILD_REALM"),
                    accessToken.getAuthorization()
            );

            Response<JsonObject> resp = call.execute();
            JsonObject r = resp.body();
            return r.get("files").getAsJsonArray().get(0).getAsJsonObject();
        } catch (IOException e) {
            Logs.infoLog(Update.class, "FAIL - getURLAH " + e);
        }

        return null;
    }

    /**
     * v2 Get a guild profile
     *
     * @throws IOException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws DataException
     */
    public void getGuildProfile() throws IOException {
        BlizzardUpdate.shared.guild();
        /*
        if (accessToken.isExpired()) generateAccessToken();

        Realm guildRealm = new Realm(GeneralConfig.getStringConfig("GUILD_REALM"));
        String guildSlug = GeneralConfig.getStringConfig("GUILD_NAME").toLowerCase();
        guildSlug = guildSlug.replace(" ", "-");

        Call<JsonObject> call = apiCalls.guildAchievements(
                guildRealm.getSlug(),
                guildSlug,
                "profile-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Guild aGuild = new Guild(response.body().getAsJsonObject("guild"));

                    // Check if is in db
                    try {
                        JsonArray lastModified = dbConnect.select(Guild.GUILD_TABLE_NAME,
                                new String[]{"id", "lastModified", "realm_slug"},
                                "name=? AND realm=?",
                                new String[]{ aGuild.getName(), aGuild.getRealm()});

                        if (lastModified.size() > 0) {
                            Long blizzUpdateTime = Long.parseLong( lastModified.get(0).getAsJsonObject().get("lastModified").getAsString() );
                            if (!blizzUpdateTime.equals(aGuild.getLastModified())) {
                                aGuild.setId( lastModified.get(0).getAsJsonObject().get("id").getAsInt() );
                                aGuild.setIsInternalData(true);
                                String slugRealm = lastModified.get(0).getAsJsonObject().get("realm_slug").getAsString();
                                if (slugRealm.length() == 0) {
                                    slugRealm = getRealmSlug(GeneralConfig.getStringConfig("SERVER_LOCATION"), aGuild.getRealm());
                                }
                                aGuild.setRealmSlug(slugRealm);
                                aGuild.saveInDB();
                            }
                        }
                    } catch (DataException | SQLException e) {
                        Logs.infoLog(Update.class, "FAIL - getGuildProfile DB " + response.code() + " - "+ e);
                    }
                } else {
                    System.out.println("Fail Guild! "+ call.request());
                    System.out.println("Fail Guild! "+ response.body());
                    Logs.infoLog(Update.class, "ERROR - getGuildProfile " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - getGuildProfile " + throwable);
            }
        }); */
    }

    /**
     * v2
     * @param region
     * @param realm
     * @return
     * @throws DataException
     * @throws IOException
     */
    @Nullable
    private String getRealmSlug(String region, String realm) {

        try {
            if (accessToken.isExpired()) generateAccessToken();

            Call<JsonObject> call = apiCalls.realmIndex(
                    region,
                    GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                    "dynamic-" + GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    accessToken.getAuthorization()
            );

            JsonArray realmsList = (JsonArray) call.execute().body().get("realms");

            for (int i = 0; i < realmsList.size(); i++) {
                JsonObject acRealm = realmsList.get(i).getAsJsonObject();
                if (realm.equals(acRealm.get("name").getAsString())) {
                    return acRealm.get("slug").getAsString();
                }
            }

        } catch (IOException e) {
            Logs.infoLog(Update.class, "FAIL - getRealmSlug " + e);
        }

        return null;
    }

    /**
     * v2 Get a guilds members
     *
     * @throws DataException
     * @throws IOException
     */
    public void getGuildMembers() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.guild(
                GeneralConfig.getStringConfig("GUILD_REALM"),
                GeneralConfig.getStringConfig("GUILD_NAME"),
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                "members",
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray members = response.body().get("members").getAsJsonArray();
                    try {
                        dbConnect.update(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                new String[]{"in_guild"},
                                new String[]{"0"},
                                "in_guild > ?",
                                new String[]{"0"});

                        // Foreach all members
                        for (int i = 0; i < members.size(); i++) {
                            JsonObject info = members.get(i).getAsJsonObject().get("character").getAsJsonObject();

                            //Check if have a guild and if set guild, (Blizzard not update a guilds members list)
                            if (info.has("guild") && (info.get("guild").getAsString()).equals(GeneralConfig.getStringConfig("GUILD_NAME"))) {
                                String rankMember = members.get(i).getAsJsonObject().get("rank").getAsString();
                                String name = info.get("name").getAsString();
                                //See if need update or insert
                                JsonArray inDBgMembersID = dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                        new String[]{"internal_id"},
                                        "member_name=? AND realm=?",
                                        new String[]{name, GeneralConfig.getStringConfig("GUILD_REALM")});
                                if (inDBgMembersID.size() > 0) { // Update
                                    dbConnect.update(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                            new String[]{"rank", "in_guild", "isDelete"},
                                            new String[]{rankMember, "1", "0"},
                                            "internal_id=?",
                                            new String[]{ inDBgMembersID.get(0).getAsJsonObject().get("internal_id").getAsString() });
                                } else { // Insert
                                    dbConnect.insert(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                            CharacterMember.GMEMBER_ID_NAME_TABLE_KEY,
                                            new String[]{"member_name", "realm", "rank", "in_guild"},
                                            new String[]{name, GeneralConfig.getStringConfig("GUILD_REALM"), rankMember, "1"});
                                }
                            }
                        }
                    } catch (DataException | SQLException e) {
                        Logs.infoLog(Update.class, "FAIL - getGuildMembers " + e);
                    }
                } else {
                    Logs.infoLog(Update.class, "ERROR - getGuildMembers " + response.code());
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - getGuildMembers " + throwable);
            }
        });
    }

    /**
     * v2 Get a guild news
     *
     * @throws IOException
     * @throws DataException
     * @throws SQLException
     */
    public void getGuildNews() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.guild(
                GeneralConfig.getStringConfig("GUILD_REALM"),
                GeneralConfig.getStringConfig("GUILD_NAME"),
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                "news",
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray news = response.body().get("news").getAsJsonArray();
                    for (int i = 0; i < news.size(); i++) {
                        JsonObject infoNew = news.get(i).getAsJsonObject();
                        New guildNew = new New(infoNew.get("type").getAsString(), infoNew.get("timestamp").getAsString(), infoNew.get("character").getAsString());
                        if (!guildNew.isInternalData()) {
                            guildNew = new New(infoNew);
                            guildNew.saveInDB();
                            //debug mode!
                            if (guildNew.getType().equals("itemLoot") && guildNew.getItem().getId() == 0) {
                                Logs.errorLog(Update.class, "ERROR GUILD NEW! \t" + infoNew + "\n\t\t" + news);
                            }
                        }
                    }
                    //Save update time in DB
                    try {
                        /* {"type", "update_time"}; */
                        dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                                UPDATE_INTERVAL_TABLE_KEY,
                                DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                                new String[]{UPDATE_TYPE_GUILD_NEWS + "", getCurrentTimeStamp()});
                    } catch (DataException | SQLException e) {
                        Logs.errorLog(Update.class, "Fail to save update guild new time: " + e);
                    }
                } else {
                    Logs.infoLog(Update.class, "RESPONSE - Guilds NEW Fails " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - Guilds NEW Fails " + throwable);
            }
        });
    }

    /**
     * v2 Get a player information IN GUILD!
     *
     * @throws SQLException
     * @throws DataException
     * @throws IOException
     */
    public void getCharacterInfo() throws SQLException, DataException, IOException {
        if (accessToken.isExpired()) generateAccessToken();

        JsonArray members = dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                CharacterMember.GMEMBER_ID_NAME_TABLE_STRUCTURE,
                "in_guild=?",
                new String[]{"1"});

        int iProgress = 1;
        Logs.infoLog(Update.class, "0%");
        for (int i = 0; i < members.size(); i++) {
            JsonObject member = members.get(i).getAsJsonObject(); //internal DB Members [internal_id, name, rank]
            CharacterMember mbDB = new CharacterMember( member.get(CharacterMember.GMEMBER_ID_NAME_TABLE_KEY).getAsInt());
            CharacterMember mbBlizz = getMemberFromBlizz(member.get("member_name").getAsString(), member.get("realm").getAsString());
            if (mbBlizz != null) { // DB member need update!
                if (!((Long) mbBlizz.getLastModified()).equals(mbDB.getLastModified())) {

                    // Save current updates...
                    mbBlizz.setId(mbDB.getId());
                    mbBlizz.setIsInternalData(mbDB.isInternalData());
                    mbBlizz.saveInDB();

                    // Get additional info:
                    Realm mbRealm = new Realm(mbBlizz.getRealm());

                    // Keystone-runs:
                    Call<JsonObject> callMythicProfile = apiCalls.characterMythicPlusProfile(
                            mbRealm.getSlug(),
                            mbBlizz.getName().toLowerCase(),
                            "profile-us",
                            GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                            accessToken.getAuthorization()
                    );

                    // Raider IO MyhicPlusScore
                    Call<JsonObject> callRaiderIO = apiRaiderIOService.character(
                            GeneralConfig.getStringConfig("SERVER_LOCATION"),
                            mbRealm.getName(),
                            mbBlizz.getName(),
                            RaiderIOService.RAIDER_IO_ACTUAL_SEASON
                    );

                    // Calls to save info:
                    callMythicProfile.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                JsonObject currentPeriod = response.body().get("current_period").getAsJsonObject();
                                if (currentPeriod.has("best_runs") && !currentPeriod.get("best_runs").isJsonNull()) {
                                    JsonArray bestRun = currentPeriod.get("best_runs").getAsJsonArray();
                                    for (int j = 0; j < bestRun.size(); j++) {
                                        JsonObject runDetail = bestRun.get(j).getAsJsonObject();
                                        KeystoneDungeonRun keyRunBlizz = new KeystoneDungeonRun(runDetail);
                                        KeystoneDungeonRun keyRunDB = new KeystoneDungeonRun(
                                                runDetail.get("completed_timestamp").getAsLong(),
                                                runDetail.get("duration").getAsLong(),
                                                runDetail.get("keystone_level").getAsInt(),
                                                runDetail.get("dungeon").getAsJsonObject().get("id").getAsInt(),
                                                runDetail.get("is_completed_within_time").getAsBoolean());
                                        if (keyRunDB.isInternalData()) {
                                            keyRunBlizz.setId(keyRunDB.getId());
                                            keyRunBlizz.setIsInternalData(true);
                                        }
                                        //If preview not have correct info, this fix it~
                                        keyRunBlizz.saveInDB();
                                    }
                                }
                            } else {
                                Logs.infoLog(Update.class, "Member " + mbBlizz.getName() + " not have a keystone run " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable throwable) {
                            Logs.infoLog(Update.class, "Member " + mbBlizz.getName() + " MyThicPlus BEST Failerus " + throwable);
                        }
                    });

                    callRaiderIO.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                // Character detail
                                JsonObject charDet = response.body();

                                //Best mythic plus score (all season!)
                                JsonObject bestMythicPlusScore = new JsonObject();
                                bestMythicPlusScore.addProperty("score", 0);

                                if (charDet.get("characterDetails").getAsJsonObject().has("bestMythicPlusScore") &&
                                !charDet.get("characterDetails").getAsJsonObject().get("bestMythicPlusScore").isJsonNull()) {
                                    bestMythicPlusScore = charDet.get("characterDetails").getAsJsonObject().get("bestMythicPlusScore").getAsJsonObject();
                                }

                                mbBlizz.setBestMythicPlusScore(bestMythicPlusScore);

                                //Mythic plus score current season!
                                JsonObject actualScore = new JsonObject();
                                actualScore.addProperty("all", 0);
                                actualScore.addProperty("dps", 0);
                                actualScore.addProperty("healer", 0);
                                actualScore.addProperty("tank", 0);
                                if (charDet.get("characterDetails").getAsJsonObject().has("mythicPlusScores")) {
                                    JsonObject acScoreRaiderIO = charDet.get("characterDetails").getAsJsonObject().get("mythicPlusScores").getAsJsonObject();
                                    actualScore = new JsonObject();
                                    actualScore.add("all", acScoreRaiderIO.get("all").getAsJsonObject().get("score"));
                                    actualScore.add("dps", acScoreRaiderIO.get("dps").getAsJsonObject().get("score"));
                                    actualScore.add("healer", acScoreRaiderIO.get("healer").getAsJsonObject().get("score"));
                                    actualScore.add("tank", acScoreRaiderIO.get("tank").getAsJsonObject().get("score"));
                                }

                                mbBlizz.setMythicPlusScores(actualScore);
                                mbBlizz.saveInDB();
                            } else {
                                Logs.infoLog(Update.class, "Member " + mbBlizz.getName() + " not have a RaiderIO " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable throwable) {
                            Logs.infoLog(Update.class, "Member " + mbBlizz.getName() + " RaiderIO Score " + throwable);
                        }
                    });
                }
            }
            //Show update progress...
            if ((((iProgress * 2) * 10) * members.size()) / 100 < i) {
                Logs.infoLog(Update.class, "..." + ((iProgress * 2) * 10) + "%");
                iProgress++;
            }
        }
        Logs.infoLog(Update.class, "...100%");
    }

    /**
     * v2 Member from blizzard
     * @param name
     * @param realm
     * @return
     * @throws IOException
     */
    @Nullable
    public CharacterMember getMemberFromBlizz(String name, String realm) {
        try {
            if (accessToken.isExpired()) generateAccessToken();

            CharacterMember bPlayer = null;

            Call<JsonObject> call = apiCalls.character(
                    realm,
                    name,
                    "guild,talents,items,stats",
                    accessToken.getAuthorization()
            );

            Response<JsonObject> response = call.execute();
            if (response.isSuccessful()) {
                bPlayer = new CharacterMember(response.body());
            } else {
                Logs.errorLog(Update.class, "BlizzAPI haven a error to '" + name + "' --> " + response.code());
                if(response.code() == 404) {
                    bPlayer = new CharacterMember(name, realm, false);
                    bPlayer.saveInDB();
                }
            }

            return bPlayer;
        } catch (IOException e) {
            Logs.infoLog(Update.class, "FAIL - getMemberFromBlizz " + e);
        }

        return null;

    }

    /**
     * v2 Get a playable class information
     *
     * @throws SQLException
     * @throws DataException
     * @throws IOException
     */
    public void getPlayableClass() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.playableClassIndex(
                "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray playClass = response.body().get("classes").getAsJsonArray();
                    for (int i = 0; i < playClass.size(); i++) {
                        JsonObject info = playClass.get(i).getAsJsonObject();
                        PlayableClass pClassDB = new PlayableClass( info.get("id").getAsInt() );
                        PlayableClass pClassBlizz = new PlayableClass(info);
                        if (pClassDB.isInternalData()) {
                            pClassBlizz.setId(pClassDB.getId());
                            pClassBlizz.setIsInternalData(true);
                        }
                        pClassBlizz.saveInDB();
                    }
                } else {
                    Logs.infoLog(Update.class, "ERRER - PlayableClass " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - PlayableClass " + throwable);
            }
        });
    }

    /**
     * v2 Playable spec
     * @throws DataException
     * @throws IOException
     */
    public void getPlayableSpec() throws DataException, IOException {
        if (accessToken == null) throw new DataException("Acces Token Not Found");
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.playableSpecialization(
                "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray playClass = response.body().get("character_specializations").getAsJsonArray();
                    for (int i = 0; i < playClass.size(); i++) {
                        JsonObject info = playClass.get(i).getAsJsonObject();
                        String urlDetail = info.get("key").getAsJsonObject().get("href").getAsString();
                        try {
                            loadPlayableSpecDetail(urlDetail);
                        } catch (IOException e) {
                            Logs.infoLog(Update.class, "ERROR - loadPlayableSpec " + response.code());
                        }
                    }
                } else {
                    Logs.infoLog(Update.class, "ERROR - getPlayableSpec " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - getPlayableSpec " + throwable);
            }
        });
    }

    /**
     * v2
     * @param url
     * @throws DataException
     * @throws IOException
     */
    private void loadPlayableSpecDetail(String url) throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.freeUrl(
                url,
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    PlayableSpec pSpecBlizz = new PlayableSpec(response.body());
                    PlayableSpec pSpecDB = new PlayableSpec( response.body().get("id").getAsInt() );
                    if (pSpecDB.isInternalData()) {
                        pSpecBlizz.setIsInternalData(true);
                    }
                    pSpecBlizz.saveInDB();
                } else {
                    Logs.infoLog(Update.class, "ERROR - loadPlayableSpecDetail " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - loadPlayableSpecDetail " + throwable);
            }
        });
    }

    /**
     * v2 Get a Characters races information
     *
     * @throws SQLException
     * @throws DataException
     * @throws IOException
     */
    public void getPlayableRaces() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.playableRaces(
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray races = response.body().get("races").getAsJsonArray();
                    for (int i = 0; i < races.size(); i++) {
                        JsonObject info = races.get(i).getAsJsonObject();
                        PlayableRace raceDB = new PlayableRace( info.get("id").getAsInt() );
                        PlayableRace raceBlizz = new PlayableRace(info);
                        if (raceDB.isInternalData()) {
                            raceBlizz.setId(raceDB.getId());
                            raceBlizz.setIsInternalData(true);
                        }
                        raceBlizz.saveInDB();
                    }
                } else {
                    Logs.infoLog(Update.class, "ERROR - getPlayableRaces " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - getPlayableRaces " + throwable);
            }
        });
    }

    /**
     * v2 Get a spell information from blizzard API.
     *
     * @param id
     * @return Spell object (content blizzard api information about spell)
     * @throws DataException
     * @throws IOException
     */
    @Nullable
    public Spell getSpellInformationBlizz(int id) {

        try {
            if (accessToken.isExpired()) generateAccessToken();

            Call<JsonObject> call = apiCalls.spell(
                    id,
                    GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                    accessToken.getAuthorization()
            );

            Spell spBlizz = new Spell(call.execute().body());
            spBlizz.saveInDB();
            Logs.infoLog(Update.class, "Spell is save in DB " + id + " - " + spBlizz.getName());
            return spBlizz;

        } catch (IOException e) {
            Logs.infoLog(Update.class, "FAIL - getSpellInformationBlizz " + e);
        }

        return null;
    }

    /**
     * v2 Update all spell information from blizzard
     *
     * @throws DataException
     * @throws SQLException
     * @throws IOException
     */
    public void updateSpellInformation() throws DataException, SQLException, IOException {
        if (accessToken.isExpired()) generateAccessToken();
        JsonArray spellInDb = dbConnect.select(Spell.SPELLS_TABLE_NAME,
                new String[]{"id"},
                "id != 0",
                new String[]{});
        int iProgress = 1;
        Logs.infoLog(Update.class, "0%");
        for (int i = 0; i < spellInDb.size(); i++) {

            JsonObject spDb = spellInDb.get(i).getAsJsonObject();
            Call<JsonObject> call = apiCalls.spell(
                    spDb.get("id").getAsInt(),
                    GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                    accessToken.getAuthorization()
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Spell spBlizz = new Spell(response.body());
                        spBlizz.setIsInternalData(true);
                        spBlizz.saveInDB();
                    } else {
                        Logs.infoLog(Update.class, "ERROR - updateSpellInformation - "+ spDb.get("id") +" --> "+ response.code());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    Logs.infoLog(Update.class, "FAIL - updateSpellInformation " + throwable);
                }
            });

            //Show update progress...
            if ((((iProgress * 2) * 10) * spellInDb.size()) / 100 < i) {
                Logs.infoLog(Update.class, "..." + ((iProgress * 2) * 10) + "%");
                iProgress++;
            }
        }
        Logs.infoLog(Update.class, "...100%");
    }

    /**
     * v2 Update all item information from blizzard
     *
     * @throws DataException
     * @throws SQLException
     * @throws IOException
     */
    public void updateItemInformation() throws DataException, SQLException, IOException {
        if (accessToken.isExpired()) generateAccessToken();
        JsonArray itemInDB = dbConnect.select(Item.ITEM_TABLE_NAME,
                new String[]{"id"},
                "id != 0",
                new String[]{});
        int iProgress = 1;
        Logs.infoLog(Update.class, "0%");
        for (int i = 0; i < itemInDB.size(); i++) {
            int id = itemInDB.get(i).getAsJsonObject().get("id").getAsInt();

            Call<JsonObject> call = apiCalls.item(
                    id,
                    GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                    accessToken.getAuthorization()
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Item itemBlizz = new Item(response.body());
                        itemBlizz.setIsInternalData(true);
                        itemBlizz.saveInDB();
                    } else {
                        Logs.infoLog(Update.class, "ERROR - updateSpellInformation " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    Logs.infoLog(Update.class, "FAIL - updateItemInformation " + throwable);
                }
            });

            //Show update progress...
            if ((((iProgress * 2) * 10) * itemInDB.size()) / 100 < i) {
                Logs.infoLog(Update.class, "..." + ((iProgress * 2) * 10) + "%");
                iProgress++;
            }
        }
        Logs.infoLog(Update.class, "...100%");
    }

    /**
     * v2 Get information from item from blizzard api
     *
     * @param id
     * @return Item object (blizzard information)
     */
    @Nullable
    public Item getItemFromBlizz(int id) {

        try {
            if (accessToken.isExpired()) generateAccessToken();
            Call<JsonObject> call = apiCalls.item(
                    id,
                    GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                    accessToken.getAuthorization()
            );
            return new Item(call.execute().body());
        } catch (IOException e) {
            Logs.infoLog(Update.class, "FAIL - getItemFromBlizz " + e);
        }

        return null;
    }

    /**
     * v2 Get guild achievements
     *
     * @throws IOException
     * @throws DataException
     */
    public void getGuildAchievementsLists() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.guildAchievements(
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray achivGroup = response.body().get("achievements").getAsJsonArray();
                    saveGuildAchievements(achivGroup);
                } else {
                    Logs.infoLog(Update.class, "ERROR - updateSpellInformation " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - getGuildAchievementsLists " + throwable);
            }
        });
    }

    private void saveGuildAchievements(JsonArray achievGroup) {
        for (int i = 0; i < achievGroup.size(); i++) {
            JsonObject info = achievGroup.get(i).getAsJsonObject();
            String classification = info.get("name").getAsString();
            JsonArray achiv = info.get("achievements").getAsJsonArray();
            for (int j = 0; j < achiv.size(); j++) {
                (achiv.get(j).getAsJsonObject()).addProperty("classification", classification);

                GuildAchievementsList gaDB = new GuildAchievementsList( achiv.get(j).getAsJsonObject().get("id").getAsInt() );
                GuildAchievementsList gaBlizz = new GuildAchievementsList(achiv.get(j).getAsJsonObject());
                if (gaDB.isInternalData()) {
                    gaBlizz.setId(gaDB.getId());
                    gaBlizz.setIsInternalData(true);
                }
                gaBlizz.saveInDB();
            }
            if (info.has("categories")) {
                JsonArray acGroup = info.get("categories").getAsJsonArray();
                saveGuildAchievements(acGroup);
            }
        }
    }

    /**
     * v2 character achievements
     * @throws IOException
     * @throws DataException
     */
    public void getCharacterAchievementsLists() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.characterAchievements(
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray achivGroup = response.body().get("achievements").getAsJsonArray();
                    saveCharacterAchievements(achivGroup);
                } else {
                    Logs.infoLog(Update.class, "ERROR - updateSpellInformation " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - getGuildAchievementsLists " + throwable);
            }
        });

    }

    private void saveCharacterAchievements(JsonArray achievGroup) {
        for (int i = 0; i < achievGroup.size(); i++) {
            //Category
            JsonObject info = achievGroup.get(i).getAsJsonObject();
            saveCharacterAchievement(info, null);
        }
    }

    private void saveCharacterAchievement(JsonObject info, CharacterAchivementsCategory fatherCat) {
        //Category
        int catId = info.get("id").getAsInt();
        CharacterAchivementsCategory category = new CharacterAchivementsCategory(catId);
        if (!category.isInternalData()) {
            String catName = info.get("name").getAsString();
            JsonObject catInfo = new JsonObject();
            catInfo.addProperty("id", catId);
            catInfo.addProperty("name", catName);
            if (fatherCat != null)
                catInfo.addProperty("father_id", fatherCat.getId());
            category = new CharacterAchivementsCategory(catInfo);
            category.saveInDB();
        }
        //Achievements
        if (info.has("achievements")) {
            JsonArray achievements = info.get("achievements").getAsJsonArray();
            for (int i = 0; i < achievements.size(); i++) {
                JsonObject achiInfo = achievements.get(i).getAsJsonObject();
                CharacterAchivementsList achv = new CharacterAchivementsList( achiInfo.get("id").getAsInt() );
                if (!achv.isInternalData()) {
                    achiInfo.addProperty("category_id", catId);
                    achv = new CharacterAchivementsList(achiInfo);
                    achv.saveInDB();
                }
            }
        }
        //If have a sub categories
        if (info.has("categories")) {
            JsonArray subCat = (JsonArray) info.get("categories");
            for (int i = 0; i < subCat.size(); i++) {
                saveCharacterAchievement(subCat.get(i).getAsJsonObject(), category);
            }
        }
    }

    /**
     * v2 Guild challenges information
     *
     * @throws IOException
     * @throws DataException
     * @throws java.text.ParseException
     * @throws SQLException
     */
    public void getGuildChallenges() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> challenge = apiCalls.guild(
                GeneralConfig.getStringConfig("GUILD_REALM"),
                GeneralConfig.getStringConfig("GUILD_NAME"),
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                "challenge",
                accessToken.getAuthorization()
        );

        challenge.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray challenges = response.body().get("challenge").getAsJsonArray();

                    int iProgress = 1;
                    Logs.infoLog(Update.class, "0%");
                    for (int i = 0; i < challenges.size(); i++) {
                        try {
                            JsonObject challenge = challenges.get(i).getAsJsonObject();
                            JsonObject map = challenge.get("map").getAsJsonObject();
                            JsonArray groups = challenge.get("groups").getAsJsonArray();
                            if (groups.size() > 0) {
                                Challenge ch = new Challenge(map);
                                //Validate is old save this map...
                                JsonArray id = dbConnect.select(Challenge.CHALLENGES_TABLE_NAME,
                                        new String[]{"id"},
                                        "id=?",
                                        new String[]{(map.get("id")).getAsString()});
                                if (id.size() > 0) ch.setIsInternalData(true);

                                for (int j = 0; j < groups.size(); j++) {
                                    JsonObject group = groups.get(j).getAsJsonObject();
                                    ChallengeGroup chGroup = new ChallengeGroup(ch.getId(), group);
                                    //Validate if exist this group.
                                    JsonArray idGroup = dbConnect.select(ChallengeGroup.CHALLENGE_GROUPS_TABLE_NAME,
                                            new String[]{"group_id"},
                                            "challenge_id=? AND time_date=?",
                                            new String[]{ch.getId() + "", ChallengeGroup.getDBDate(chGroup.getTimeDate())});
                                    if (idGroup.size() > 0) {
                                        chGroup.setId( idGroup.get(0).getAsJsonObject().get("group_id").getAsInt() );
                                        chGroup.setIsInternalData(true);
                                    }

                                    //Members
                                    JsonArray members = (JsonArray) group.get("members");
                                    members.forEach((member) -> {
                                        JsonObject inMeb = member.getAsJsonObject();
                                        if (inMeb.has("character")) {
                                            JsonObject character = inMeb.get("character").getAsJsonObject();
                                            JsonObject spec = inMeb.get("spec").getAsJsonObject();
                                            //Get info about this member.

                                            CharacterMember mb = new CharacterMember(character.get("name").getAsString(), character.get("realm").getAsString());
                                            if (mb.isData()) {
                                                mb.setActiveSpec(spec.get("name").getAsString(), spec.get("role").getAsString());
                                                //Add Member
                                                chGroup.addMember(mb);
                                            }
                                        }
                                    });
                                    //Add Group
                                    ch.addChallengeGroup(chGroup);
                                }
                                ch.saveInDB();
                            }
                        } catch (DataException e) {
                            Logs.errorLog(Update.class, "ERROR - Guild challenges DATAECEPTION "+ e);
                        } catch (SQLException e) {
                            Logs.errorLog(Update.class, "ERROR - Guild challenges SQLEXCEPTION "+ e);
                        }

                        //Show update progress...
                            if ((((iProgress * 2) * 10) * challenges.size()) / 100 < i) {
                                Logs.infoLog(Update.class, "..." + ((iProgress * 2) * 10) + "%");
                                iProgress++;
                            }
                        }
                    Logs.infoLog(Update.class, "...100%");

                } else {
                    Logs.errorLog(Update.class, "ERROR - BlizzAPI haven a error to guild Challengses "+ response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.errorLog(Update.class, "FAILERUS - BlizzAPI haven a error to guild Challengses "+ throwable);
            }
        });
    }

    /**
     * v2 Get wow token price
     *
     * @throws DataException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void getWowToken() throws IOException  {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> wowToken = apiCalls.token(
                "dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        wowToken.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject wowTokenPrice = response.body();

                    String lastUpdate = wowTokenPrice.get("last_updated_timestamp").getAsString();
                    String priceUpdate = wowTokenPrice.get("price").getAsString();

                    try {
                        JsonArray oldValue = dbConnect.select(DBStructure.WOW_TOKEN_TABLE_NAME,
                                new String[]{"last_updated_timestamp"},
                                "last_updated_timestamp=?",
                                new String[]{lastUpdate});
                        if (oldValue.size() == 0) { // Not exit this update, save a new info
                            dbConnect.insert(DBStructure.WOW_TOKEN_TABLE_NAME,
                                    DBStructure.WOW_TOKEN_TABLE_KEY,
                                    DBStructure.WOW_TOKEN_TABLE_STRUCTURE,
                                    new String[]{lastUpdate, priceUpdate});
                        }
                    } catch (SQLException e) {
                        Logs.errorLog(Update.class, "ERROR - WoW Token SQLException "+ e);
                    } catch (DataException e) {
                        Logs.errorLog(Update.class, "ERROR - WoW Token DataException "+ e);
                    }

                } else {
                    Logs.errorLog(Update.class, "ERROR - getWowToken "+ response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.errorLog(Update.class, "FAIL - getWowToken "+ throwable);
            }
        });
    }

    /**
     * Get a user characters information (see all users in DB and try search the characters)
     *
     * @throws SQLException
     * @throws DataException
     * @throws ClassNotFoundException
     */
    public void getUsersCharacters() throws SQLException, DataException, ClassNotFoundException, IOException {

        JsonArray users = dbConnect.select(User.USER_TABLE_NAME,
                new String[]{"id", "access_token"},
                "access_token IS NOT NULL AND wowinfo=?",
                new String[]{"1"});
        //For all account have an accessToken and set a member info
        for (int i = 0; i < users.size(); i++) {
            String acToken = users.get(i).getAsJsonObject().get("access_token").getAsString();
            int userID = users.get(i).getAsJsonObject().get("id").getAsInt();
            setMemberCharacterInfo(acToken, userID);
        }
        //Re set a guild rank from ALL members~
        /**
         * SELECT id, guild_rank as user_rank, rank as member_rank
         *  FROM (SELECT u.id, u.guild_rank, gm.rank, ROW_NUMBER() OVER (PARTITION BY u.id ORDER BY gm.rank ASC) rn
         *           FROM  users u, gMembers_id_name gm
         *           WHERE u.id = gm.user_id ORDER BY u.id ASC, gm.rank ASC) tab
         *   WHERE rn = 1;
         */
        JsonArray allUsers = dbConnect.select(User.USER_TABLE_NAME,
                new String[]{"id", "guild_rank", "discord_user_id"});
        for (int i = 0; i < allUsers.size(); i++) {
            int uderID = allUsers.get(i).getAsJsonObject().get("id").getAsInt();
            int actualUserRank = allUsers.get(i).getAsJsonObject().get("guild_rank").getAsInt();
            String discUserId = null;
            if (!allUsers.get(i).getAsJsonObject().get("discord_user_id").isJsonNull())
                discUserId = allUsers.get(i).getAsJsonObject().get("discord_user_id").getAsString();
            int membersRank = -1;
            //Select player have a guild rank
            //select * from gMembers_id_name where user_id = 1 AND rank is not null order by rank limit 1;
            JsonArray charRank = dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                    new String[]{"rank"},
                    "user_id=? AND in_guild=? AND isDelete=? AND rank is not null order by rank limit 1",
                    new String[]{uderID + "", "1", "0"});
            if (charRank.size() > 0) {
                membersRank = charRank.get(0).getAsJsonObject().get("rank").getAsInt();
            }
            //Save if is different
            if (membersRank != actualUserRank) {
                dbConnect.update(User.USER_TABLE_NAME,
                        new String[]{"guild_rank"},
                        new String[]{membersRank + ""},
                        "id=?",
                        new String[]{uderID + ""});
                //if new is -1, remove discord rank
                if (discUserId != null && membersRank == -1) {
                    DiscordBot.shared.removeRank(discUserId);
                }
            }
        }
    }

    /**
     * v2 Set member character info
     *
     * @param userAccessToken String member access Token
     * @param userID      internal user ID
     */
    public void setMemberCharacterInfo(String userAccessToken, int userID) throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.userCharacter(
                userAccessToken
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject blizzInfo = response.body();
                    if (blizzInfo.has("characters")) {

                        JsonArray characters = blizzInfo.get("characters").getAsJsonArray();

                        // defined all the characters for this player
                        for (int i = 0; i < characters.size(); i++) {
                            JsonObject pj = characters.get(i).getAsJsonObject();
                            String name = pj.get("name").getAsString();
                            String realm = pj.get("realm").getAsString();
                            CharacterMember mb = new CharacterMember(name, realm);

                            if (mb != null && mb.isData()) {
                                try {
                                    dbConnect.update(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                            new String[]{"user_id"},
                                            new String[]{userID + ""},
                                            "internal_id=?",
                                            new String[]{mb.getId() + ""});
                                    dbConnect.update(User.USER_TABLE_NAME,
                                            new String[]{"last_alters_update"},
                                            new String[]{getCurrentTimeStamp()},
                                            User.USER_TABLE_KEY + "=?",
                                            new String[]{userID + ""});
                                } catch (SQLException | DataException ex) {
                                    Logs.fatalLog(Update.class, "Fail to insert userID info " + ex);
                                }
                            }
                        }

                        // Get a most 'elevado' rank member, like 0 is GM, 1 is officers ...
                        try {
                            JsonArray guildRank = dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                    new String[]{"rank"},
                                    "in_guild=? AND user_id=? AND isDelete=? ORDER BY rank ASC LIMIT 1",
                                    new String[]{"1", userID + "", "0"});
                            if (guildRank.size() > 0) {//Save a rank from this player...
                                int rank = guildRank.get(0).getAsJsonObject().get("rank").getAsInt();
                                try {
                                    dbConnect.update(User.USER_TABLE_NAME,
                                            new String[]{"guild_rank"},
                                            new String[]{rank + ""},
                                            "id=?",
                                            new String[]{userID + ""});
                                } catch (DataException e) {
                                    Logs.fatalLog(Update.class, "Fail to save guild rank from user Data Exception " + userID + " - " + e);
                                }
                            }
                        } catch (SQLException | DataException ex) {
                            Logs.fatalLog(Update.class, "Fail to select characters from user " + userID + " - " + ex);
                        }
                        //Set accessToken is working yet~
                        try {
                            dbConnect.update(User.USER_TABLE_NAME,
                                    new String[]{"wowinfo"},
                                    new String[]{"1"},
                                    "id=?",
                                    new String[]{userID + ""});
                            Logs.infoLog(Update.class, "Wow access token is update!");
                        } catch (SQLException | DataException ex) {
                            Logs.fatalLog(Update.class, "Fail to set wowinfo is worikng from " + userID);
                        }

                    }
                } else {
                    Logs.errorLog(Update.class, "ERROR - setMemberCharacterInfo "+ response.code());
                    try {
                        dbConnect.update(User.USER_TABLE_NAME,
                                new String[]{"wowinfo"},
                                new String[]{"0"},
                                "id=?",
                                new String[]{userID + ""});
                    } catch (SQLException | DataException e) {
                        Logs.fatalLog(Update.class, "FAIL - Update access token disabled "+ userID +" - "+ userAccessToken +" - "+ e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.errorLog(Update.class, "FAIL - setMemberCharacterInfo "+ throwable);
            }
        });
    }

    /**
     * v2 From RaiderIO get a guild progression information
     *
     * @throws DataException
     * @throws IOException
     */
    public void getGuildProgression() {

        Call<JsonObject> call = apiRaiderIOService.guilds(
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                GeneralConfig.getStringConfig("GUILD_REALM"),
                GeneralConfig.getStringConfig("GUILD_NAME")
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    JsonObject raiderIOGuildProgression = response.body();
                    JsonArray raidRankings = raiderIOGuildProgression.get("guildDetails").getAsJsonObject().get("raidRankings").getAsJsonArray();
                    JsonArray raidProgress = raiderIOGuildProgression.get("guildDetails").getAsJsonObject().get("raidProgress").getAsJsonArray();

                    for (int i = 0; i < raidProgress.size(); i++) {
                        JsonObject raid = raidProgress.get(i).getAsJsonObject();
                        //Add rank info
                        raid.add("rank", raidRankings.get(i).getAsJsonObject().get("ranks"));
                        //Save raid
                        Raid itRaid = new Raid(raid);
                        Raid oldRaid = new Raid(raid.get("raid").getAsString());
                        if (oldRaid.isInternalData()) {
                            itRaid.setName(oldRaid.getName());
                            itRaid.setTotalBoss(oldRaid.getTotalBoss());
                            itRaid.setId(oldRaid.getId());
                            itRaid.setIsInternalData(true);
                        }
                        itRaid.saveInDB();
                    }

                } else {
                    Logs.errorLog(Update.class, "ERROR - GuildProgress "+ response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.errorLog(Update.class, "FAIL - GuildProgress "+ throwable);
            }
        });
    }

    /**
     * v2 Get boss master list from blizzard api
     *
     * @throws DataException
     * @throws IOException
     */
    public void getBossInformation() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JsonObject> call = apiCalls.bosses(
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray bossList = (JsonArray) response.body().get("bosses");
                    for (int i = 0; i < bossList.size(); i++) {
                        JsonObject bossInfo = bossList.get(i).getAsJsonObject();
                        Boss inDB = new Boss( bossInfo.get("id").getAsInt() );
                        JsonObject bossInfoCreate = new JsonObject();
                        bossInfoCreate.add("id", bossInfo.get("id"));
                        bossInfoCreate.add("description", bossInfo.get("description"));
                        bossInfoCreate.add("name", bossInfo.get("name"));
                        bossInfoCreate.add("slug", bossInfo.get("urlSlug"));
                        /*
                        JSONArray npcList = (JSONArray) bossInfo.get("npcs");
                        Logs.infoLog(Update.class, "BOSS LIST - "+ bossInfo.get("urlSlug"));
                        Logs.infoLog(Update.class, "BOSS LIST - "+ bossInfo);
                        for(int j = 0; j < npcList.size(); j++)
                        {
                            JSONObject npcInfo = (JSONObject) npcList.get(j);
                            if(npcInfo.get("id").equals(bossInfo.get("id")))
                            {//If have a NPC and have same ID, change the especial slug and complate name
                                bossInfoCreate.addProperty("name", npcInfo.get("name"));
                                bossInfoCreate.addProperty("slug", npcInfo.get("urlSlug"));
                                break;
                            }
                        }*/
                        Boss b = new Boss(bossInfoCreate);
                        if (inDB.isInternalData()) {
                            b.setIsInternalData(true);
                        }
                        b.saveInDB();

                    }
                } else {
                    Logs.errorLog(Update.class, "ERROR - getBossInformation "+ response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                Logs.errorLog(Update.class, "FAIL - getBossInformation "+ throwable);
            }
        });

    }

    /**
     * v2
     * @param code
     * @return
     * @throws IOException
     */
    @Nullable
    public String getUserAccessToken(String code) {

        try {

            if (accessToken.isExpired()) generateAccessToken();

            String redirectUrl = GeneralConfig.getStringConfig("MAIN_URL") +
                            GeneralConfig.getStringConfig("BLIZZAR_LINK");

            Call<JsonObject> call = apiOauthCalls.userToken(
                    "authorization_code",
                    "wow.profile",
                    redirectUrl,
                    code,
                    Credentials.basic(GeneralConfig.getStringConfig("CLIENT_ID"), GeneralConfig.getStringConfig("CLIENT_SECRET"))
            );

            JsonObject bInfo = call.execute().body();
            if (bInfo.has("access_token")) {
                return bInfo.get("access_token").getAsString();
            }
        } catch (IOException e) {
            Logs.errorLog(Update.class, "FAIL - getUserAccessToken "+ e);
        }

        return null;
    }

    /**
     * v2 battle tag
     * @param userAccessToken
     * @return
     */
    @Nullable
    public String getBattleTag(String userAccessToken) {

        try {
            Call<JsonObject> call = apiOauthCalls.userInfo(
                    GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    "Bearer "+ userAccessToken
            );

            JsonObject bInfo = call.execute().body();
            if (bInfo.has("battletag")) {
                return bInfo.get("battletag").getAsString();
            }
        } catch (IOException ex) {
            Logs.errorLog(User.class, "FAIL - getBattleTag "+ ex);
        }
        return null;
    }

    /**
     * Get a current time string yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * Parse unix time to actual server time.
     *
     * @param unixTime
     * @return
     */
    public static String parseUnixTime(String unixTime) {
        Date time = new Date(Long.parseLong(unixTime));
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }

}
