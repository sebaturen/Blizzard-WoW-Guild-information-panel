/**
 * File : Update.java
 * Desc : Update guild and character in guild information
 *
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.blizzardAPI;

import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
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
import com.blizzardPanel.User;
import com.blizzardPanel.gameObject.mythicKeystone.KeystoneDungeon;
import com.blizzardPanel.gameObject.mythicKeystone.KeystoneDungeonRun;
import com.blizzardPanel.gameObject.Realm;
import com.blizzardPanel.gameObject.characters.PlayableSpec;

import java.io.*;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
    private RaiderIOService apiRaiderIOService;
    private AccessToken accessToken;

    private static final DBConnect dbConnect = new DBConnect();

    /**
     * Constructor. Run a generateAccesToken to generate this token
     */
    public Update() {

        // Load Retrofit API calls platform
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
        } catch (DataException | ClassNotFoundException | SQLException e) {
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
        } catch (DataException | ClassNotFoundException | SQLException e) {
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

        Call<JSONObject> call = apiCalls.connectedRealmIndex(
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                "dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONArray connRealm = (JSONArray) response.body().get("connected_realms");
                    for (int i = 0; i < connRealm.size(); i++) {

                        String urlRealmConnect = ((JSONObject) connRealm.get(i)).get("href").toString();
                        int startId = urlRealmConnect.indexOf("connected-realm/") + "connected-realm/".length();
                        int endId = urlRealmConnect.indexOf("?namespace=");

                        int realmId = Integer.parseInt(urlRealmConnect.substring(startId, endId));
                        Call<JSONObject> callRealmInfo = apiCalls.connectedRealm(
                                realmId,
                                "dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                                accessToken.getAuthorization()
                        );

                        callRealmInfo.enqueue(new Callback<JSONObject>() {
                            @Override
                            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                if (response.isSuccessful()) {
                                    JSONObject realmsConn = response.body();
                                    int realmConnectID = ((Long) realmsConn.get("id")).intValue();
                                    JSONArray realms = (JSONArray) realmsConn.get("realms");
                                    for (int j = 0; j < realms.size(); j++) {
                                        JSONObject realmInfo = (JSONObject) realms.get(j);
                                        realmInfo.put("connected_realm", realmConnectID);
                                        int inId = ((Long) realmInfo.get("id")).intValue();
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
                            public void onFailure(Call<JSONObject> call, Throwable throwable) {
                                Logs.infoLog(Update.class, "FAIL - RealmDetail " + throwable);
                            }
                        });
                    }
                } else {
                    Logs.infoLog(Update.class, "ERROR - RealmIndex " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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
    public JSONObject loadKeyDetailFromBlizz(String url) throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        JSONObject keyDetail = null;

        Call<JSONObject> call = apiCalls.freeUrl(
                url,
                accessToken.getAccess_token()
        );

        JSONObject inf = call.execute().body();
        keyDetail.put("name", ((JSONObject) inf.get("name")).get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")).toString());
        keyDetail.put("description", ((JSONObject) inf.get("description")).get(GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE")).toString());

        Call<JSONObject> iconCall = apiCalls.freeUrl(
                ((JSONObject) ((JSONObject) keyDetail.get("media")).get("key")).get("href").toString(),
                accessToken.getAccess_token()
        );
        JSONArray iconAsset = (JSONArray) iconCall.execute().body().get("assets");
        for (int i = 0; i < iconAsset.size(); i++) {
            JSONObject keyAssetDet = (JSONObject) iconAsset.get(i);
            if (keyAssetDet.get("key").toString().equals("icon")) {
                keyDetail.put("icon", keyAssetDet.get("value").toString());
                break;
            }
        }

        return keyDetail;
    }

    /**
     * v2
     * @param url
     * @return
     * @throws IOException
     */
    public KeystoneDungeon getKeyStoneDungeonDetail(String url) throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        KeystoneDungeon kDun = null;

        Call<JSONObject> call = apiCalls.freeUrl(
                url,
                accessToken.getAccess_token()
        );

        kDun = new KeystoneDungeon(call.execute().body());
        kDun.saveInDB();

        return kDun;
    }

    /**
     * v2 Run AH Update information
     */
    public void updateAH() {
        Logs.infoLog(Update.class, "-------Update process is START! (Auction House)------");
        try {
            JSONObject genInfo = getURLAH();
            String lastUpdate = parseUnixTime(genInfo.get("lastModified").toString());
            JSONArray getLastUpdateInDB = dbConnect.select(UPDATE_INTERVAL_TABLE_NAME,
                    UPDATE_INTERVAL_TABLE_STRUCTURE,
                    "type=? AND update_time=?",
                    new String[]{UPDATE_TYPE_AUCTION + "", lastUpdate});
            if (getLastUpdateInDB.isEmpty()) {
                //Clear last auItems
                dbConnect.update(AuctionItem.AUCTION_ITEMS_TABLE_NAME,
                        new String[]{"status"},
                        new String[]{"0"},
                        "status = ?",
                        new String[]{"1"});
                Logs.infoLog(Update.class, "AH last update: " + lastUpdate);
                Logs.infoLog(Update.class, "Get a AH update...");

                Call<JSONObject> call = apiCalls.freeUrl(
                        genInfo.get("url").toString(),
                        accessToken.getAccess_token()
                );


                JSONObject allAH = call.execute().body();
                JSONArray itemsAH = (JSONArray) allAH.get("auctions");

                int iProgres = 1;
                Logs.infoLog(Update.class, "0%");
                for (int i = 0; i < itemsAH.size(); i++) {
                    JSONObject item = (JSONObject) itemsAH.get(i);
                    AuctionItem acObItem = new AuctionItem(item);
                    AuctionItem acObItemDB = new AuctionItem(((Long) item.get("auc")).intValue());
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
        } catch (DataException | IOException | ClassNotFoundException | SQLException ex) {
            Logs.errorLog(Update.class, "Fail to get AH " + ex);
        }
        Logs.infoLog(Update.class, "-------Update process is COMPLATE! (Auction House)------");
    }

    /**
     * See the auc_items and move to History DB if auc finish
     */
    public void moveHistoryAH() {
        Logs.infoLog(Update.class, "-------Update process is Start! (Auction House move to History DB)------");
        try {
            JSONArray aucItem = dbConnect.select(AuctionItem.AUCTION_ITEMS_TABLE_NAME,
                    new String[]{AuctionItem.AUCTION_ITEMS_KEY},
                    "status = ?",
                    new String[]{"0"});
            //Get and delete all auc need save in history DB
            int iProgres = 1;
            Logs.infoLog(Update.class, "0%");
            for (int i = 0; i < aucItem.size(); i++) {
                int aucId = (Integer) ((JSONObject) aucItem.get(i)).get(AuctionItem.AUCTION_ITEMS_KEY);
                AuctionItem aucItemOLD = new AuctionItem(aucId);
                try {
                    //Insert in History if have a price
                    if (aucItemOLD.getBuyout() > 0) {
                        dbConnect.insert(DBStructure.AUCTION_HISTORY_TABLE_NAME,
                                DBStructure.AUCTION_HISTORY_TABLE_KEY,
                                //{"item", "unique_price", "context", "date"};
                                DBStructure.outKey(DBStructure.AUCTION_HISTORY_TABLE_STRUCTURE),
                                new String[]{aucItemOLD.getItem().getId() + "", aucItemOLD.getUniqueBuyoutPrice() + "",
                                        aucItemOLD.getContext() + "", aucItemOLD.getAucDate()});
                    }
                    //Delete from current AH
                    dbConnect.delete(AuctionItem.AUCTION_ITEMS_TABLE_NAME,
                            AuctionItem.AUCTION_ITEMS_KEY + "=?",
                            new String[]{aucItemOLD.getId() + ""});
                } catch (ClassNotFoundException | SQLException | DataException ex) {
                    Logs.errorLog(Update.class, "Fail to save auc history to " + aucItemOLD.getId() + " - " + ex);
                }
                //Show update progress...
                if ((((iProgres * 2) * 10) * aucItem.size()) / 100 < i) {
                    Logs.infoLog(Update.class, "..." + ((iProgres * 2) * 10) + "%");
                    iProgres++;
                }
            }
            Logs.infoLog(Update.class, "...100%");

            /* {"type", "update_time"}; */
            dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                    UPDATE_INTERVAL_TABLE_KEY,
                    DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                    new String[]{UPDATE_TYPE_CLEAR_AH_HISTORY + "", getCurrentTimeStamp()});
        } catch (SQLException | DataException | ClassNotFoundException ex) {
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

        Call<JSONObject> call = apiCalls.accessToken(
                RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "grant_type=client_credentials"),
                Credentials.basic(GeneralConfig.getStringConfig("CLIENT_ID"), GeneralConfig.getStringConfig("CLIENT_SECRET"))
        );

        JSONObject aToken = call.execute().body();

        accessToken = new AccessToken();
        accessToken.setAccess_token((String) aToken.get("access_token"));
        accessToken.setToken_type((String) aToken.get("token_type"));
        accessToken.setExpire_in((int) aToken.get("expires_in"));
    }

    /**
     * v2 Generate a AH information URL
     *
     * @return
     * @throws DataException
     * @throws IOException
     */
    private JSONObject getURLAH() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JSONObject> call = apiCalls.auction(
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                accessToken.getAuthorization()
        );

        return ((JSONObject) ((JSONArray) call.execute().body().get("files")).get(0));
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
        if (accessToken.isExpired()) generateAccessToken();

        Call<JSONObject> call = apiCalls.guildProfile(
                GeneralConfig.getStringConfig("GUILD_REALM"),
                GeneralConfig.getStringConfig("GUILD_NAME"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    Guild aGuild = new Guild(response.body());

                    // Check if is in db
                    try {
                        JSONArray lastModified = dbConnect.select(Guild.GUILD_TABLE_NAME,
                                new String[]{"id", "lastModified", "realm_slug"},
                                "name=? AND realm=?",
                                new String[]{ aGuild.getName(), aGuild.getRealm()});

                        if (lastModified.size() > 0) {
                            Long blizzUpdateTime = Long.parseLong(((JSONObject) lastModified.get(0)).get("lastModified").toString());
                            if (!blizzUpdateTime.equals(aGuild.getLastModified())) {
                                aGuild.setId((Integer) ((JSONObject) lastModified.get(0)).get("id"));
                                aGuild.setIsInternalData(true);
                                String slugRealm = ((JSONObject) lastModified.get(0)).get("realm_slug").toString();
                                if (slugRealm.length() == 0) {
                                    slugRealm = getRealmSlug(GeneralConfig.getStringConfig("SERVER_LOCATION"), aGuild.getRealm());
                                }
                                aGuild.setRealmSlug(slugRealm);
                            }
                        }
                    } catch (IOException | DataException | SQLException e) {
                        try {
                            aGuild.setRealmSlug(getRealmSlug(GeneralConfig.getStringConfig("SERVER_LOCATION"), aGuild.getRealm()));
                        } catch (DataException | IOException ex) {
                            Logs.infoLog(Update.class, "ERROR - getGuildProfile " + ex);
                        }
                    }
                    aGuild.saveInDB();
                } else {
                    Logs.infoLog(Update.class, "ERROR - getGuildProfile " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - getGuildProfile " + throwable);
            }
        });
    }

    /**
     * v2
     * @param region
     * @param realm
     * @return
     * @throws DataException
     * @throws IOException
     */
    private String getRealmSlug(String region, String realm) throws DataException, IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JSONObject> call = apiCalls.realmIndex(
                region,
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                "dynamic-" + GeneralConfig.getStringConfig("SERVER_LOCATION"),
                accessToken.getAuthorization()
        );

        Response<JSONObject> resp = call.execute();
        if (resp.isSuccessful()) {
            JSONArray realmsList = (JSONArray) resp.body().get("realms");

            for (int i = 0; i < realmsList.size(); i++) {
                JSONObject acRealm = (JSONObject) realmsList.get(i);
                if (realm.equals(acRealm.get("name").toString())) {
                    return acRealm.get("slug").toString();
                }
            }

        }

        throw new DataException("Fail to get Server SLUG! - null");
    }

    /**
     * v2 Get a guilds members
     *
     * @throws DataException
     * @throws IOException
     */
    public void getGuildMembers() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JSONObject> call = apiCalls.guild(
                GeneralConfig.getStringConfig("GUILD_REALM"),
                GeneralConfig.getStringConfig("GUILD_NAME"),
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                "members",
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONArray members = (JSONArray) response.body().get("members");
                    try {
                        dbConnect.update(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                new String[]{"in_guild"},
                                new String[]{"0"},
                                "in_guild > ?",
                                new String[]{"0"});

                        // Foreach all members
                        for (int i = 0; i < members.size(); i++) {
                            JSONObject info = (JSONObject) ((JSONObject) members.get(i)).get("character");

                            //Check if have a guild and if set guild, (Blizzard not update a guilds members list)
                            if (info.containsKey("guild") && (info.get("guild").toString()).equals(GeneralConfig.getStringConfig("GUILD_NAME"))) {
                                String rankMember = ((JSONObject) members.get(i)).get("rank").toString();
                                String name = info.get("name").toString();
                                //See if need update or insert
                                JSONArray inDBgMembersID = dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                        new String[]{"internal_id"},
                                        "member_name=? AND realm=?",
                                        new String[]{name, GeneralConfig.getStringConfig("GUILD_REALM")});
                                if (inDBgMembersID.size() > 0) {//Update
                                    dbConnect.update(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                            new String[]{"rank", "in_guild", "isDelete"},
                                            new String[]{rankMember, "1", "0"},
                                            "internal_id=?",
                                            new String[]{((JSONObject) inDBgMembersID.get(0)).get("internal_id").toString()});
                                } else {//Insert
                                    dbConnect.insert(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                            CharacterMember.GMEMBER_ID_NAME_TABLE_KEY,
                                            new String[]{"member_name", "realm", "rank", "in_guild"},
                                            new String[]{name, GeneralConfig.getStringConfig("GUILD_REALM"), rankMember, "1"});
                                }
                            }
                        }
                    } catch (DataException | ClassNotFoundException | SQLException e) {
                        Logs.infoLog(Update.class, "FAIL - getGuildMembers " + e);
                    }
                } else {
                    Logs.infoLog(Update.class, "ERROR - getGuildMembers " + response.code());
                }

            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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

        Call<JSONObject> call = apiCalls.guild(
                GeneralConfig.getStringConfig("GUILD_REALM"),
                GeneralConfig.getStringConfig("GUILD_NAME"),
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                "news",
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONArray news = (JSONArray) response.body().get("news");
                    for (int i = 0; i < news.size(); i++) {
                        JSONObject infoNew = (JSONObject) news.get(i);
                        New guildNew = new New(infoNew.get("type").toString(), infoNew.get("timestamp").toString(), infoNew.get("character").toString());
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
                    } catch (DataException | ClassNotFoundException | SQLException e) {
                        Logs.errorLog(Update.class, "Fail to save update guild new time: " + e);
                    }
                } else {
                    Logs.infoLog(Update.class, "RESPONSE - Guilds NEW Fails " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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

        JSONArray members = dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                CharacterMember.GMEMBER_ID_NAME_TABLE_STRUCTURE,
                "in_guild=?",
                new String[]{"1"});

        int iProgress = 1;
        Logs.infoLog(Update.class, "0%");
        for (int i = 0; i < members.size(); i++) {
            JSONObject member = (JSONObject) members.get(i); //internal DB Members [internal_id, name, rank]
            CharacterMember mbDB = new CharacterMember((Integer) member.get(CharacterMember.GMEMBER_ID_NAME_TABLE_KEY));
            CharacterMember mbBlizz = getMemberFromBlizz(member.get("member_name").toString(), member.get("realm").toString());
            if (mbBlizz != null) {//DB member need update!
                if (!((Long) mbBlizz.getLastModified()).equals(mbDB.getLastModified())) {

                    // Save current updates...
                    mbBlizz.setId(mbDB.getId());
                    mbBlizz.setIsInternalData(mbDB.isInternalData());
                    mbBlizz.saveInDB();

                    // Get additional info:
                    Realm mbRealm = new Realm(mbBlizz.getRealm());

                    // Keystone-runs:
                    Call<JSONObject> callMythicProfile = apiCalls.characterMythicPlusProfile(
                            mbRealm.getSlug(),
                            mbBlizz.getName().toLowerCase(),
                            "profile-us",
                            GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                            accessToken.getAuthorization()
                    );

                    // Raider IO MyhicPlusScore
                    Call<JSONObject> callRaiderIO = apiRaiderIOService.character(
                            GeneralConfig.getStringConfig("SERVER_LOCATION"),
                            mbRealm.getName(),
                            mbBlizz.getName(),
                            RaiderIOService.RAIDER_IO_ACTUAL_SEASON
                    );

                    // Calls to save info:
                    callMythicProfile.enqueue(new Callback<JSONObject>() {
                        @Override
                        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                            if (response.isSuccessful()) {
                                JSONObject currentPeriod = (JSONObject) response.body();
                                if (currentPeriod.get("best_runs") != null) {
                                    JSONArray bestRun = (JSONArray) currentPeriod.get("best_runs");
                                    for (int j = 0; j < bestRun.size(); j++) {
                                        JSONObject runDetail = (JSONObject) bestRun.get(j);
                                        KeystoneDungeonRun keyRunBlizz = new KeystoneDungeonRun(runDetail);
                                        KeystoneDungeonRun keyRunDB = new KeystoneDungeonRun(
                                                (Long) runDetail.get("completed_timestamp"),
                                                (Long) runDetail.get("duration"),
                                                ((Long) runDetail.get("keystone_level")).intValue(),
                                                ((Long) ((JSONObject) runDetail.get("dungeon")).get("id")).intValue(),
                                                (Boolean) runDetail.get("is_completed_within_time"));
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
                        public void onFailure(Call<JSONObject> call, Throwable throwable) {
                            Logs.infoLog(Update.class, "Member " + mbBlizz.getName() + " MyThicPlus BEST Failerus " + throwable);
                        }
                    });

                    callRaiderIO.enqueue(new Callback<JSONObject>() {
                        @Override
                        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                            if (response.isSuccessful()) {
                                // Character detail
                                JSONObject charDet = response.body();

                                //Best mythic plus score (all season!)
                                JSONObject bestMythicPlusScore = new JSONObject();
                                bestMythicPlusScore.put("score", 0);

                                if (((JSONObject) charDet.get("characterDetails")).get("bestMythicPlusScore") != null) {
                                    bestMythicPlusScore = (JSONObject) ((JSONObject) charDet.get("characterDetails")).get("bestMythicPlusScore");
                                }

                                mbBlizz.setBestMythicPlusScore(bestMythicPlusScore);

                                //Mythic plus score current season!
                                JSONObject actualScore = new JSONObject();
                                actualScore.put("all", 0);
                                actualScore.put("dps", 0);
                                actualScore.put("healer", 0);
                                actualScore.put("tank", 0);
                                if (((JSONObject) charDet.get("characterDetails")).get("mythicPlusScores") != null) {
                                    JSONObject acScoreRaiderIO = (JSONObject) ((JSONObject) charDet.get("characterDetails")).get("mythicPlusScores");
                                    actualScore = new JSONObject();
                                    actualScore.put("all", ((JSONObject) acScoreRaiderIO.get("all")).get("score").toString());
                                    actualScore.put("dps", ((JSONObject) acScoreRaiderIO.get("dps")).get("score").toString());
                                    actualScore.put("healer", ((JSONObject) acScoreRaiderIO.get("healer")).get("score").toString());
                                    actualScore.put("tank", ((JSONObject) acScoreRaiderIO.get("tank")).get("score").toString());
                                }

                                mbBlizz.setMythicPlusScorese(actualScore);
                                mbBlizz.saveInDB();
                            } else {
                                Logs.infoLog(Update.class, "Member " + mbBlizz.getName() + " not have a RaiderIO " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<JSONObject> call, Throwable throwable) {
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
    public CharacterMember getMemberFromBlizz(String name, String realm) throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        CharacterMember bPlayer = null;

        Call<JSONObject> call = apiCalls.character(
                realm,
                name,
                accessToken.getAuthorization()
        );

        Response<JSONObject> response = call.execute();
        if (response.isSuccessful()) {
            bPlayer = new CharacterMember(response.body());
        } else {
            Logs.errorLog(Update.class, "BlizzAPI haven a error to '" + name + "'\n\t" + response.code());
            if(response.code() == 404) {
                bPlayer = new CharacterMember(name, realm, false);
                bPlayer.saveInDB();
            }
        }

        return bPlayer;

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

        Call<JSONObject> call = apiCalls.playableClassIndex(
                "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONArray playClass = (JSONArray) response.body().get("classes");
                    for (int i = 0; i < playClass.size(); i++) {
                        JSONObject info = (JSONObject) playClass.get(i);
                        PlayableClass pClassDB = new PlayableClass(((Long) info.get("id")).intValue());
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
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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

        Call<JSONObject> call = apiCalls.playableSpecialization(
                "static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONArray playClass = (JSONArray) response.body().get("character_specializations");
                    for (int i = 0; i < playClass.size(); i++) {
                        JSONObject info = (JSONObject) playClass.get(i);
                        String urlDetail = ((JSONObject) info.get("key")).get("href").toString();
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
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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

        Call<JSONObject> call = apiCalls.freeUrl(
                url,
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    PlayableSpec pSpecBlizz = new PlayableSpec(response.body());
                    PlayableSpec pSpecDB = new PlayableSpec(((Long) response.body().get("id")).intValue());
                    if (pSpecDB.isInternalData()) {
                        pSpecBlizz.setIsInternalData(true);
                    }
                    pSpecBlizz.saveInDB();
                } else {
                    Logs.infoLog(Update.class, "ERROR - loadPlayableSpecDetail " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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

        Call<JSONObject> call = apiCalls.playableRaces(
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONArray races = (JSONArray) response.body().get("races");
                    for (int i = 0; i < races.size(); i++) {
                        JSONObject info = (JSONObject) races.get(i);
                        PlayableRace raceDB = new PlayableRace(((Long) info.get("id")).intValue());
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
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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
    public Spell getSpellInformationBlizz(int id) throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JSONObject> call = apiCalls.spell(
                id,
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        Response<JSONObject> resp = call.execute();
        if (resp.isSuccessful()) {
            Spell spBlizz = new Spell(resp.body());
            spBlizz.saveInDB();
            Logs.infoLog(Update.class, "Spell is save in DB " + id + " - " + spBlizz.getName());
            return spBlizz;
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
        JSONArray spellInDb = dbConnect.select(Spell.SPELLS_TABLE_NAME,
                new String[]{"id"},
                "id != 0",
                new String[]{});
        int iProgress = 1;
        Logs.infoLog(Update.class, "0%");
        for (int i = 0; i < spellInDb.size(); i++) {

            Call<JSONObject> call = apiCalls.spell(
                    (Integer) ((JSONObject) spellInDb.get(i)).get("id"),
                    GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                    accessToken.getAuthorization()
            );

            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    if (response.isSuccessful()) {
                        Spell spBlizz = new Spell(response.body());
                        spBlizz.setIsInternalData(true);
                        spBlizz.saveInDB();
                    } else {
                        Logs.infoLog(Update.class, "ERROR - updateSpellInformation " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable throwable) {
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
        JSONArray itemInDB = dbConnect.select(Item.ITEM_TABLE_NAME,
                new String[]{"id"},
                "id != 0",
                new String[]{});
        int iProgress = 1;
        Logs.infoLog(Update.class, "0%");
        for (int i = 0; i < itemInDB.size(); i++) {
            int id = (Integer) ((JSONObject) itemInDB.get(i)).get("id");

            Call<JSONObject> call = apiCalls.item(
                    id,
                    GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                    accessToken.getAuthorization()
            );

            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    if (response.isSuccessful()) {
                        Item itemBlizz = new Item(response.body());
                        itemBlizz.setIsInternalData(true);
                        itemBlizz.saveInDB();
                    } else {
                        Logs.infoLog(Update.class, "ERROR - updateSpellInformation " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable throwable) {
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
    public Item getItemFromBlizz(int id) throws IOException {

        Call<JSONObject> call = apiCalls.item(
                id,
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        Response<JSONObject> resp = call.execute();
        if (resp.isSuccessful()) {
            return new Item(resp.body());
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

        Call<JSONObject> call = apiCalls.guildAchievements(
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONArray achivGroup = (JSONArray) response.body().get("achievements");
                    saveGuildAchievements(achivGroup);
                } else {
                    Logs.infoLog(Update.class, "ERROR - updateSpellInformation " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - getGuildAchievementsLists " + throwable);
            }
        });
    }

    private void saveGuildAchievements(JSONArray achievGroup) {
        for (int i = 0; i < achievGroup.size(); i++) {
            JSONObject info = (JSONObject) achievGroup.get(i);
            String classification = info.get("name").toString();
            JSONArray achiv = (JSONArray) info.get("achievements");
            for (int j = 0; j < achiv.size(); j++) {
                ((JSONObject) achiv.get(j)).put("classification", classification);

                GuildAchievementsList gaDB = new GuildAchievementsList(((Long) ((JSONObject) achiv.get(j)).get("id")).intValue());
                GuildAchievementsList gaBlizz = new GuildAchievementsList((JSONObject) achiv.get(j));
                if (gaDB.isInternalData()) {
                    gaBlizz.setId(gaDB.getId());
                    gaBlizz.setIsInternalData(true);
                }
                gaBlizz.saveInDB();
            }
            if (info.containsKey("categories")) {
                JSONArray acGroup = (JSONArray) info.get("categories");
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

        Call<JSONObject> call = apiCalls.characterAchievements(
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONArray achivGroup = (JSONArray) response.body().get("achievements");
                    saveCharacterAchievements(achivGroup);
                } else {
                    Logs.infoLog(Update.class, "ERROR - updateSpellInformation " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
                Logs.infoLog(Update.class, "FAIL - getGuildAchievementsLists " + throwable);
            }
        });

    }

    private void saveCharacterAchievements(JSONArray achievGroup) {
        for (int i = 0; i < achievGroup.size(); i++) {
            //Category
            JSONObject info = (JSONObject) achievGroup.get(i);
            saveCharacterAchievement(info, null);
        }
    }

    private void saveCharacterAchievement(JSONObject info, CharacterAchivementsCategory fatherCat) {
        //Category
        int catId = ((Long) info.get("id")).intValue();
        CharacterAchivementsCategory category = new CharacterAchivementsCategory(catId);
        if (!category.isInternalData()) {
            String catName = info.get("name").toString();
            JSONObject catInfo = new JSONObject();
            catInfo.put("id", catId);
            catInfo.put("name", catName);
            if (fatherCat != null)
                catInfo.put("father_id", fatherCat.getId());
            category = new CharacterAchivementsCategory(catInfo);
            category.saveInDB();
        }
        //Achievements
        if (info.containsKey("achievements")) {
            JSONArray achievements = (JSONArray) info.get("achievements");
            for (int i = 0; i < achievements.size(); i++) {
                JSONObject achiInfo = (JSONObject) achievements.get(i);
                CharacterAchivementsList achv = new CharacterAchivementsList(((Long) achiInfo.get("id")).intValue());
                if (!achv.isInternalData()) {
                    achiInfo.put("category_id", catId);
                    achv = new CharacterAchivementsList(achiInfo);
                    achv.saveInDB();
                }
            }
        }
        //If have a sub categories
        if (info.containsKey("categories")) {
            JSONArray subCat = (JSONArray) info.get("categories");
            for (int i = 0; i < subCat.size(); i++) {
                saveCharacterAchievement((JSONObject) subCat.get(i), category);
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

        Call<JSONObject> challenge = apiCalls.guild(
                GeneralConfig.getStringConfig("GUILD_REALM"),
                GeneralConfig.getStringConfig("GUILD_NAME"),
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                "challenge",
                accessToken.getAuthorization()
        );

        challenge.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONArray challenges = (JSONArray) response.body().get("challenge");

                    int iProgress = 1;
                    Logs.infoLog(Update.class, "0%");
                    for (int i = 0; i < challenges.size(); i++) {
                        try {
                            JSONObject challenge = (JSONObject) challenges.get(i);
                            JSONObject map = (JSONObject) challenge.get("map");
                            JSONArray groups = (JSONArray) challenge.get("groups");
                            if (groups.size() > 0) {
                                Challenge ch = new Challenge(map);
                                //Validate is old save this map...
                                JSONArray id = dbConnect.select(Challenge.CHALLENGES_TABLE_NAME,
                                        new String[]{"id"},
                                        "id=?",
                                        new String[]{(map.get("id")).toString()});
                                if (id.size() > 0) ch.setIsInternalData(true);

                                for (int j = 0; j < groups.size(); j++) {
                                    JSONObject group = (JSONObject) groups.get(j);
                                    ChallengeGroup chGroup = new ChallengeGroup(ch.getId(), group);
                                    //Validate if exist this group.
                                    JSONArray idGroup = dbConnect.select(ChallengeGroup.CHALLENGE_GROUPS_TABLE_NAME,
                                            new String[]{"group_id"},
                                            "challenge_id=? AND time_date=?",
                                            new String[]{ch.getId() + "", ChallengeGroup.getDBDate(chGroup.getTimeDate())});
                                    if (idGroup.size() > 0) {
                                        chGroup.setId((Integer) ((JSONObject) idGroup.get(0)).get("group_id"));
                                        chGroup.setIsInternalData(true);
                                    }

                                    //Members
                                    JSONArray members = (JSONArray) group.get("members");
                                    members.forEach((member) -> {

                                        JSONObject inMeb = (JSONObject) member;
                                        if (inMeb.containsKey("character")) {
                                            JSONObject character = (JSONObject) inMeb.get("character");
                                            JSONObject spec = (JSONObject) inMeb.get("spec");
                                            //Get info about this member.

                                            CharacterMember mb = new CharacterMember(character.get("name").toString(), character.get("realm").toString());
                                            if (mb.isData()) {
                                                mb.setActiveSpec(spec.get("name").toString(), spec.get("role").toString());
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
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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
    public void getWowToken() throws IOException {
        if (accessToken.isExpired()) generateAccessToken();

        Call<JSONObject> wowToken = apiCalls.token(
                "dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                GeneralConfig.getStringConfig("LANGUAGE_API_LOCALE"),
                accessToken.getAuthorization()
        );

        wowToken.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONObject wowTokenPrice = (JSONObject) response.body();

                    String lastUpdate = wowTokenPrice.get("last_updated_timestamp").toString();
                    String priceUpdate = wowTokenPrice.get("price").toString();

                    try {
                        JSONArray oldValue = dbConnect.select(DBStructure.WOW_TOKEN_TABLE_NAME,
                                new String[]{"last_updated_timestamp"},
                                "last_updated_timestamp=?",
                                new String[]{lastUpdate});
                        if (oldValue.isEmpty()) {//Not exit this update, save a new infor
                            dbConnect.insert(DBStructure.WOW_TOKEN_TABLE_NAME,
                                    DBStructure.WOW_TOKEN_TABLE_KEY,
                                    DBStructure.WOW_TOKEN_TABLE_STRUCTURE,
                                    new String[]{lastUpdate, priceUpdate});
                        }
                    } catch (SQLException e) {
                        Logs.errorLog(Update.class, "ERROR - WoW Token SQLException "+ e);
                    } catch (DataException e) {
                        Logs.errorLog(Update.class, "ERROR - WoW Token DataException "+ e);
                    } catch (ClassNotFoundException e) {
                        Logs.errorLog(Update.class, "ERROR - WoW Token ClassNotFoundException "+ e);
                    }

                } else {
                    Logs.errorLog(Update.class, "ERROR - getWowToken "+ response.code());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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

        JSONArray users = dbConnect.select(User.USER_TABLE_NAME,
                new String[]{"id", "access_token"},
                "access_token IS NOT NULL AND wowinfo=?",
                new String[]{"1"});
        //For all account have an accessToken and set a member info
        for (int i = 0; i < users.size(); i++) {
            String acToken = ((JSONObject) users.get(i)).get("access_token").toString();
            int userID = (Integer) ((JSONObject) users.get(i)).get("id");
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
        JSONArray allUsers = dbConnect.select(User.USER_TABLE_NAME,
                new String[]{"id", "guild_rank", "discord_user_id"});
        for (int i = 0; i < allUsers.size(); i++) {
            int uderID = (Integer) ((JSONObject) allUsers.get(i)).get("id");
            int actualUserRank = (Integer) ((JSONObject) allUsers.get(i)).get("guild_rank");
            String discUserId = null;
            if (((JSONObject) allUsers.get(i)).get("discord_user_id") != null)
                discUserId = ((JSONObject) allUsers.get(i)).get("discord_user_id").toString();
            int membersRank = -1;
            //Select player have a guild rank
            //select * from gMembers_id_name where user_id = 1 AND rank is not null order by rank limit 1;
            JSONArray charRank = dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                    new String[]{"rank"},
                    "user_id=? AND in_guild=? AND isDelete=? AND rank is not null order by rank limit 1",
                    new String[]{uderID + "", "1", "0"});
            if (charRank.size() > 0) {
                membersRank = (Integer) ((JSONObject) charRank.get(0)).get("rank");
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
                    UpdateRunning.discordBot.removeRank(discUserId);
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

        Call<JSONObject> call = apiCalls.userCharacter(
                userAccessToken,
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONObject blizzInfo = response.body();
                    if (blizzInfo.size() > 0) {
                        JSONArray characters = (JSONArray) blizzInfo.get("characters");

                        // defined all the characters for this player
                        for (int i = 0; i < characters.size(); i++) {
                            JSONObject pj = (JSONObject) characters.get(i);
                            String name = pj.get("name").toString();
                            String realm = pj.get("realm").toString();
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
                                } catch (ClassNotFoundException | SQLException | DataException ex) {
                                    Logs.fatalLog(Update.class, "Fail to insert userID info " + ex);
                                }
                            }
                        }

                        // Get a most 'elevado' rank member, like 0 is GM, 1 is officers ...
                        try {
                            JSONArray guildRank = dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME,
                                    new String[]{"rank"},
                                    "in_guild=? AND user_id=? AND isDelete=? ORDER BY rank ASC LIMIT 1",
                                    new String[]{"1", userID + "", "0"});
                            if (guildRank.size() > 0) {//Save a rank from this player...
                                int rank = (Integer) ((JSONObject) guildRank.get(0)).get("rank");
                                try {
                                    dbConnect.update(User.USER_TABLE_NAME,
                                            new String[]{"guild_rank"},
                                            new String[]{rank + ""},
                                            "id=?",
                                            new String[]{userID + ""});
                                } catch (ClassNotFoundException ex) {
                                    Logs.fatalLog(Update.class, "Fail to save guild rank from user " + userID + " - " + ex);
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
                        } catch (ClassNotFoundException | SQLException | DataException ex) {
                            Logs.fatalLog(Update.class, "Fail to set wowinfo is worikng from " + userID);
                        }

                    }
                } else {
                    Logs.errorLog(Update.class, "ERROR - setMemberCharacterInfo "+ response.code());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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

        Call<JSONObject> call = apiRaiderIOService.guilds(
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                GeneralConfig.getStringConfig("GUILD_REALM"),
                GeneralConfig.getStringConfig("GUILD_NAME")
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {

                    JSONObject raiderIOGuildProgression = response.body();
                    JSONArray raidRankings = (JSONArray) ((JSONObject) raiderIOGuildProgression.get("guildDetails")).get("raidRankings");
                    JSONArray raidProgress = (JSONArray) ((JSONObject) raiderIOGuildProgression.get("guildDetails")).get("raidProgress");

                    for (int i = 0; i < raidProgress.size(); i++) {
                        JSONObject raid = (JSONObject) raidProgress.get(i);
                        //Add rank info
                        raid.put("rank", (JSONObject) ((JSONObject) raidRankings.get(i)).get("ranks"));
                        //Save raid
                        Raid itRaid = new Raid(raid);
                        Raid oldRaid = new Raid(raid.get("raid").toString());
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
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
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

        Call<JSONObject> call = apiCalls.bosses(
                GeneralConfig.getStringConfig("SERVER_LOCATION"),
                accessToken.getAuthorization()
        );

        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONArray bossList = (JSONArray) response.body().get("bosses");
                    for (int i = 0; i < bossList.size(); i++) {
                        JSONObject bossInfo = (JSONObject) bossList.get(i);
                        Boss inDB = new Boss(((Long) bossInfo.get("id")).intValue());
                        JSONObject bossInfoCreate = new JSONObject();
                        bossInfoCreate.put("id", bossInfo.get("id"));
                        bossInfoCreate.put("description", bossInfo.get("description"));
                        bossInfoCreate.put("name", bossInfo.get("name"));
                        bossInfoCreate.put("slug", bossInfo.get("urlSlug"));
                        /*
                        JSONArray npcList = (JSONArray) bossInfo.get("npcs");
                        Logs.infoLog(Update.class, "BOSS LIST - "+ bossInfo.get("urlSlug"));
                        Logs.infoLog(Update.class, "BOSS LIST - "+ bossInfo);
                        for(int j = 0; j < npcList.size(); j++)
                        {
                            JSONObject npcInfo = (JSONObject) npcList.get(j);
                            if(npcInfo.get("id").equals(bossInfo.get("id")))
                            {//If have a NPC and have same ID, change the especial slug and complate name
                                bossInfoCreate.put("name", npcInfo.get("name"));
                                bossInfoCreate.put("slug", npcInfo.get("urlSlug"));
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
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
                Logs.errorLog(Update.class, "FAIL - getBossInformation "+ throwable);
            }
        });

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
