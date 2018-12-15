/**
 * File : Update.java
 * Desc : Update guild and character in guild information
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.blizzardAPI;

import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.exceptions.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.AuctionItem;
import com.blizzardPanel.gameObject.Boss;
import com.blizzardPanel.gameObject.Item;
import com.blizzardPanel.gameObject.guild.Guild;
import com.blizzardPanel.gameObject.guild.achievement.GuildAchievementsList;
import com.blizzardPanel.gameObject.characters.Member;
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
import com.blizzardPanel.exceptions.ConfigurationException;
import com.blizzardPanel.gameObject.characters.PlayableSpec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Update implements APIInfo
{
    //Update interval DB
    public static final String UPDATE_INTERVAL_TABLE_NAME = "update_timeline";
    public static final String UPDATE_INTERVAL_TABLE_KEY = "id";
    public static final String[] UPDATE_INTERVAL_TABLE_STRUCTURE = {"id", "type", "update_time"};

    //Constant	
    public static final int UPDATE_TYPE_DYNAMIC  = 0;
    public static final int UPDATE_TYPE_STATIC   = 1;
    public static final int UPDATE_TYPE_AUCTION  = 2;
    public static final int UPDATE_TYPE_CLEAR_AH_HISTORY = 3;
    public static final int UPDATE_TYPE_GUILD_NEWS = 4;
    public static final int UPDATE_TYPE_AUCTION_CHECK = 5;
    
    private static int blizzAPICallCounter = 0;

    //Attribute
    private static String accesToken = null;
    private static final DBConnect dbConnect = new DBConnect();

    /**
     * Constructor. Run a generateAccesToken to generate this token
     * @throws IOException
     * @throws ParseException
     * @throws DataException 
     */
    public Update() throws IOException, ParseException, DataException
    {
        try {
            generateAccesToken();
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
        }
    }

    /**
     * Run Dynamic element update method and save the register the last update.
     */
    public void updateDynamicAll()
    {
        try {
            blizzAPICallCounter = 0;                 
            Logs.saveLogln("-------Update process is START! (Dynamic)------");
            //Guild information update!
            Logs.saveLogln("Guild Information update!");
            try { getGuildProfile(); }
            catch (IOException|ParseException|SQLException|ClassNotFoundException|DataException ex) { Logs.saveLogln("Fail update Guild Info: "+ ex); }
            //Guild members information update!
            Logs.saveLogln("Guild members information update!");
            try { getGuildMembers(); }
            catch (IOException|ParseException|SQLException|ClassNotFoundException|DataException ex) { Logs.saveLogln("Fail update Guild Members Info: "+ ex); }
            //Character information update!						
            Logs.saveLogln("Character information update!");
            try { getCharacterInfo(); }
            catch (IOException|ParseException|SQLException|DataException ex) { Logs.saveLogln("Fail get a CharacterS Info: "+ ex); }
            //Guild challenges update!
            Logs.saveLogln("Guild challenges update!");
            try { getGuildChallenges(); }
            catch (IOException | ParseException | DataException | SQLException ex) { Logs.saveLogln("Fail get a CharacterS Info: "+ ex); }
            //Guild news update!
            Logs.saveLogln("Guild new update!");
            try { getGuildNews(); } 
            catch (IOException | ParseException | DataException | SQLException ex) { Logs.saveLogln("Fail update guild news Info "+ ex); }
            //Wow Token
            Logs.saveLogln("Wow token information update!");
            try { getWowToken(); }
            catch (ClassNotFoundException|IOException|ParseException|DataException|SQLException ex) { Logs.saveLogln("Fail update Wow Token Info: "+ ex); }		
            //Users player
            Logs.saveLogln("Users characters information update!");
            try { getUsersCharacters(); }
            catch (SQLException|DataException|ClassNotFoundException ex) { Logs.saveLogln("Fail update user characters Info: "+ ex); }
            //Guild progression RaiderIO
            Logs.saveLogln("Guild progression update!");
            try { getGuildProgression(); }
            catch (IOException|ParseException|DataException ex) { Logs.saveLogln("Fail update guild progression Info: "+ ex); }
            Logs.saveLogln("-------Update process is COMPLATE! (Dynamic)------");	
            Logs.saveLogln("TOTAL Blizzard API Call: "+ blizzAPICallCounter);

            //Save log update in DB
            try 
            {
                /* {"type", "update_time"}; */
                dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                                UPDATE_INTERVAL_TABLE_KEY,
                                DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                                new String[] {UPDATE_TYPE_DYNAMIC +"", getCurrentTimeStamp()});
            }
            catch(DataException|ClassNotFoundException|SQLException e)
            {
                Logs.saveLogln("Fail to save update time: "+ e);
            }
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
        }        
    }

    /**
     * Run Static element update
     */
    public void updateStaticAll()
    {
        try {
            blizzAPICallCounter = 0;
            Logs.saveLogln("-------Update process is START! (Static)------");
            //Playable Class
            Logs.saveLogln("Playable class Information update!");
            try { getPlayableClass(); } 
            catch (IOException|ParseException|SQLException|DataException ex) { Logs.saveLogln("Fail update Playable class Info: "+ ex); }
            //Races
            Logs.saveLogln("Playable Races Information update!");
            try { getPlayableRaces(); } 
            catch (IOException|ParseException|SQLException|DataException ex) { Logs.saveLogln("Fail update Races Info: "+ ex); }		
            //Guild Achivements lists
            Logs.saveLogln("Guild Achievements lists information update!");
            try { getGuildAchievementsLists(); } 
            catch (IOException|ParseException|DataException ex) { Logs.saveLogln("Fail update Achievements Info: "+ ex); }		
            //Character Achivements lists
            Logs.saveLogln("Characters Achievements lists information update!");
            try { getCharacterAchievementsLists(); } 
            catch (IOException|ParseException|DataException ex) { Logs.saveLogln("Fail update Characters Achievements Info: "+ ex); }	
            //Update Spell information
            Logs.saveLogln("Spell information update!");
            try { updateSpellInformation(); } 
            catch (IOException|ParseException|SQLException|DataException ex) { Logs.saveLogln("Fail update spell Info: "+ ex); }		
            //Boss DB Upate info
            Logs.saveLogln("Boss DB Update");
            try { getBossInformation(); }
            catch (IOException|ParseException|DataException ex) { Logs.saveLogln("Fail get boss DB Info: "+ ex); }
            Logs.saveLogln("Item informatio update!");
            try{ updateItemInformation(); }
            catch (IOException|ParseException|SQLException|DataException ex) { Logs.saveLogln("Fail update item Info: "+ ex); }
            Logs.saveLogln("Playable Spec update!");
            try{ getPlayableSpec(); }
            catch (IOException|ParseException|DataException ex) { Logs.saveLogln("Fail update playable spec Info: "+ ex); }
            Logs.saveLogln("-------Update process is COMPLATE! (Static)------");	
            Logs.saveLogln("TOTAL Blizzard API Call: "+ blizzAPICallCounter);


            //Save log update in DB
            try 
            {
                /* {"type", "update_time"}; */
                dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                                UPDATE_INTERVAL_TABLE_KEY,
                                DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                                new String[] {UPDATE_TYPE_STATIC +"", getCurrentTimeStamp()});
            } 
            catch(DataException|ClassNotFoundException|SQLException e)
            {
                Logs.saveLogln("Fail to save update time: "+ e);
            }        
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
        }	
    }

    /**
     * Run AH Update information 
     */
    public void updateAH()
    {
        Logs.saveLogln("-------Update process is START! (Auction House)------");
        try 
        {
            JSONObject genInfo = getURLAH();
            String lastUpdate = parseUnixTime(genInfo.get("lastModified").toString());
            JSONArray getLastUpdateInDB = dbConnect.select(UPDATE_INTERVAL_TABLE_NAME, 
                                                            UPDATE_INTERVAL_TABLE_STRUCTURE, 
                                                            "type=? AND update_time=?", 
                                                            new String[] { UPDATE_TYPE_AUCTION+"", lastUpdate });
            if(getLastUpdateInDB.isEmpty()) 
            {
                //Clear last auItems
                dbConnect.update(AuctionItem.AUCTION_ITEMS_TABLE_NAME,
                            new String[] {"status"},
                            new String[] {"0"},
                            "status = ?",
                            new String[] {"1"});                
                Logs.saveLogln("AH last update: "+ lastUpdate);
                Logs.saveLogln("Get a AH update...");
                JSONObject allAH = curl(genInfo.get("url").toString(), "GET");
                JSONArray itemsAH = (JSONArray) allAH.get("auctions");

                int iProgres = 1;
                Logs.saveLog("0%");
                for(int i = 0; i < itemsAH.size(); i++)
                {
                    JSONObject item = (JSONObject) itemsAH.get(i);
                    AuctionItem acObItem = new AuctionItem(item);
                    AuctionItem acObItemDB = new AuctionItem(((Long) item.get("auc")).intValue());
                    if(acObItemDB.isInternalData())
                    {
                        acObItem.setIsInternalData(true);
                        acObItem.setAucDate(acObItemDB.getAucDate());
                    }
                    acObItem.setAucDate(lastUpdate);
                    acObItem.saveInDB();

                    //Show update progress...
                    if ( (((iProgres*2)*10)*itemsAH.size())/100 < i )
                    {
                        Logs.saveLog("..."+ ((iProgres*2)*10) +"%");
                        iProgres++;
                    }
                }
                Logs.saveLogln("...100%");

                /* {"type", "update_time"}; */
                dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                                UPDATE_INTERVAL_TABLE_KEY,
                                DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                                new String[] {UPDATE_TYPE_AUCTION +"", lastUpdate});  
            }
            /* {"type", "update_time"}; */
            dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                            UPDATE_INTERVAL_TABLE_KEY,
                            DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                            new String[] {UPDATE_TYPE_AUCTION_CHECK +"", getCurrentTimeStamp()});
        } catch (DataException | IOException | ParseException |ClassNotFoundException|SQLException ex) {
            Logs.saveLogln("Fail to get AH "+ ex);
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
        }
        Logs.saveLogln("-------Update process is COMPLATE! (Auction House)------");
    }
    
    /**
     * See the auc_items and move to History DB if auc finish
     */
    public void moveHistoryAH()
    {
        Logs.saveLogln("-------Update process is Start! (Auction House move to History DB)------");
        try {
            JSONArray aucItem = dbConnect.select(AuctionItem.AUCTION_ITEMS_TABLE_NAME,
                    new String[] {AuctionItem.AUCTION_ITEMS_KEY},
                    "status = ?",
                    new String[] { "0" });
            //Get and delete all auc need save in history DB
            int iProgres = 1;
            Logs.saveLog("0%");
            for(int i = 0; i < aucItem.size(); i++)
            {
                int aucId = (Integer) ((JSONObject) aucItem.get(i)).get(AuctionItem.AUCTION_ITEMS_KEY);
                AuctionItem aucItemOLD = new AuctionItem(aucId);
                try 
                {
                    //Insert in History if have a price
                    if(aucItemOLD.getBuyout() > 0)
                    {
                        dbConnect.insert(DBStructure.AUCTION_HISTORY_TABLE_NAME,
                                DBStructure.AUCTION_HISTORY_TABLE_KEY,
                                //{"item", "unique_price", "context", "date"};
                                DBStructure.outKey(DBStructure.AUCTION_HISTORY_TABLE_STRUCTURE),
                                new String[] { aucItemOLD.getItem().getId()+"", aucItemOLD.getUniqueBuyoutPrice()+"", 
                                                aucItemOLD.getContext()+"", aucItemOLD.getAucDate() });
                    }
                    //Delete from current AH
                    dbConnect.delete(AuctionItem.AUCTION_ITEMS_TABLE_NAME,
                            AuctionItem.AUCTION_ITEMS_KEY +"=?",
                            new String[] { aucItemOLD.getId()+"" });
                } catch (ClassNotFoundException|SQLException|DataException ex) {
                    Logs.saveLogln("Fail to save auc history to "+ aucItemOLD.getId() +" - "+ ex);
                }                
                //Show update progress...
                if ( (((iProgres*2)*10)*aucItem.size())/100 < i )
                {
                    Logs.saveLog("..."+ ((iProgres*2)*10) +"%");
                    iProgres++;
                }
            }
            Logs.saveLogln("...100%");
            
            /* {"type", "update_time"}; */
            dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                            UPDATE_INTERVAL_TABLE_KEY,
                            DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                            new String[] {UPDATE_TYPE_CLEAR_AH_HISTORY +"", getCurrentTimeStamp()}); 
        } catch (SQLException | DataException | ClassNotFoundException ex) {
            Logs.saveLogln("Fail to get current auc items "+ ex);
        }
        Logs.saveLogln("-------Update process is Complete! (Auction House move to History DB)------");
    }
    
    /**
     * Blizzard API need a token to access to API, this token you can
     * get if have a ClinetID and ClientSecret of the application
     * @throws IOException
     * @throws ParseException
     * @throws DataException 
     */
    private void generateAccesToken() throws IOException, ParseException, DataException, ConfigurationException
    {
        if(accesToken == null)
        {
            String urlString = String.format(API_OAUTH_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), API_OAUTH_TOKEN);
            String apiInfo = Base64.getEncoder().encodeToString((GeneralConfig.getStringConfig("CLIENT_ID")+":"+GeneralConfig.getStringConfig("CLIENT_SECRET")).getBytes(StandardCharsets.UTF_8));

            //prepare info
            String boodyDate = "grant_type=client_credentials";
            byte[] postDataBytes = boodyDate.getBytes("UTF-8");

            //Get an Access Token
            accesToken = (String) (curl(urlString,
                                            "POST",
                                            "Basic "+ apiInfo,
                                            null,
                                            postDataBytes)).get("access_token");            
        }
    }
    
    /**
     * Generate a AH information URL
     * @return
     * @throws DataException
     * @throws IOException
     * @throws ParseException 
     */
    private JSONObject getURLAH() throws DataException, IOException, ParseException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Access Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), String.format(API_AUCTION, 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_REALM"), "UTF-8").replace("+", "%20")));
            //Call blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ accesToken);
            
            return ((JSONObject)((JSONArray) respond.get("files")).get(0));
            
        }
    }
    
    /**
     * Get a guild profile
     * @throws IOException
     * @throws ParseException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws DataException 
     */
    public void getGuildProfile() throws IOException, ParseException, SQLException, ClassNotFoundException, DataException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Access Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), String.format(API_GUILD_PROFILE, 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_REALM"), "UTF-8").replace("+", "%20"), 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_NAME"), "UTF-8").replace("+", "%20")));
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ accesToken,
                                    new String[] {"fields=achievements", "locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});
            //Chek if is in db
            JSONArray lastModified = dbConnect.select(Guild.GUILD_TABLE_NAME,
                                                    new String[] {"id", "lastModified"},
                                                    "name=? AND realm=?",
                                                    new String[] { respond.get("name").toString(), respond.get("realm").toString() });
            Guild apiGuild = new Guild(respond);
            if (lastModified.size() > 0)
            {//posible update
                Long blizzUpdateTime = Long.parseLong(((JSONObject)lastModified.get(0)).get("lastModified").toString());
                if( !blizzUpdateTime.equals(apiGuild.getLastModified()) )
                {//if last save is diferent, update.
                    apiGuild.setId((Integer) ((JSONObject)lastModified.get(0)).get("id"));
                    apiGuild.setIsInternalData(true);
                    apiGuild.saveInDB();
                }
            }
            else
            {//Not in DB, only save.
                apiGuild.saveInDB();
            }
        }
    }

    /**
     * Get a guilds members
     * @throws DataException
     * @throws IOException
     * @throws ParseException
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public void getGuildMembers() throws DataException, IOException, ParseException, SQLException, ClassNotFoundException, ConfigurationException 
    {
        if(accesToken == null) throw new DataException("Access Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), String.format(API_GUILD_PROFILE, 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_REALM"), "UTF-8").replace("+", "%20"), 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_NAME"), "UTF-8").replace("+", "%20")));
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ accesToken,
                                    new String[] {"fields=members", "locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});

            JSONArray members = (JSONArray) respond.get("members");

            //Reset 0 in_guild all members...
            dbConnect.update(Member.GMEMBER_ID_NAME_TABLE_NAME,
                            new String[] {"in_guild"},
                            new String[] {"0"},
                            "in_guild > ?",
                            new String[] {"0"});
            for(int i = 0; i < members.size(); i++)
            {				
                JSONObject info = (JSONObject) ((JSONObject) members.get(i)).get("character");

                //Check if have a guild and if set guild, (Blizzard not update a guilds members list) 
                if(info.containsKey("guild") && (info.get("guild").toString()).equals(GeneralConfig.getStringConfig("GUILD_NAME")))
                {	
                    String rankMember = ((JSONObject) members.get(i)).get("rank").toString();
                    String name = info.get("name").toString();
                    //See if need update or insert
                    JSONArray inDBgMembersID = dbConnect.select(Member.GMEMBER_ID_NAME_TABLE_NAME, 
                                                        new String[] {"internal_id"}, 
                                                        "member_name=? AND realm=?",
                                                        new String[] {name, GeneralConfig.getStringConfig("GUILD_REALM")});
                    if(inDBgMembersID.size() > 0)
                    {//Update
                        dbConnect.update(Member.GMEMBER_ID_NAME_TABLE_NAME, 
                                        new String[] {"rank", "in_guild"},
                                        new String[] {rankMember, "1"},
                                        "internal_id=?",
                                        new String[] { ((JSONObject)inDBgMembersID.get(0)).get("internal_id").toString() });
                    }
                    else
                    {//Insert
                        dbConnect.insert(Member.GMEMBER_ID_NAME_TABLE_NAME, 
                                        Member.GMEMBER_ID_NAME_TABLE_KEY,
                                        new String[] { "member_name", "realm", "rank", "in_guild" },
                                        new String[] { name, GeneralConfig.getStringConfig("GUILD_REALM"), rankMember, "1" });
                    }
                }				
            }
        }
    }
     
    /**
     * Get a guild news
     * @throws IOException
     * @throws ParseException
     * @throws DataException
     * @throws java.text.ParseException
     * @throws SQLException 
     */
    public void getGuildNews() throws IOException, ParseException, DataException, ParseException, SQLException, ConfigurationException 
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), String.format(API_GUILD_PROFILE, 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_REALM"), "UTF-8").replace("+", "%20"), 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_NAME"), "UTF-8").replace("+", "%20")));
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ accesToken,
                                    new String[] {"fields=news", "locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});

            JSONArray news = (JSONArray) respond.get("news");
            for(int i = 0; i < news.size(); i++)
            {
                JSONObject infoNew = (JSONObject)news.get(i);
                New guildNew = new New(infoNew.get("type").toString(), infoNew.get("timestamp").toString(), infoNew.get("character").toString());
                if(!guildNew.isInternalData())
                {
                    guildNew = new New(infoNew);
                    guildNew.saveInDB();
                    //debug mode!
                    if (guildNew.getType().equals("itemLoot") && guildNew.getItem().getId() == 0)
                    {
                        Logs.saveLogln("ERROR GUILD NEW! \t"+ infoNew +"\n\t\t"+ news);
                    }
                }
            }
            //Save update time in DB
            try 
            {
                /* {"type", "update_time"}; */
                dbConnect.insert(UPDATE_INTERVAL_TABLE_NAME,
                                UPDATE_INTERVAL_TABLE_KEY,
                                DBStructure.outKey(UPDATE_INTERVAL_TABLE_STRUCTURE),
                                new String[] {UPDATE_TYPE_GUILD_NEWS +"", getCurrentTimeStamp()});
            }
            catch(DataException|ClassNotFoundException|SQLException e)
            {
                Logs.saveLogln("Fail to save update guild new time: "+ e);
            }
        }
    }

    /**
     * Get a player information IN GUILD!
     * @throws SQLException
     * @throws DataException
     * @throws IOException
     * @throws ParseException 
     */
    public void getCharacterInfo() throws SQLException, DataException, IOException, ParseException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Access Token Not Found");
        else
        {
            JSONArray members = dbConnect.select(Member.GMEMBER_ID_NAME_TABLE_NAME,
                                                Member.GMEMBER_ID_NAME_TABLE_STRUCTURE,
                                                "in_guild=?",
                                                new String[] {"1"});

            int iProgres = 1;
            Logs.saveLog("0%");
            for(int i = 0; i < members.size(); i++)
            {
                JSONObject member = (JSONObject) members.get(i); //internal DB Members [internal_id, name, rank]
                Member mbDB = new Member((Integer) member.get(Member.GMEMBER_ID_NAME_TABLE_KEY));
                Member mbBlizz = getMemberFromBlizz(member.get("member_name").toString(), member.get("realm").toString());
                if(mbBlizz != null)
                {//DB member need update!
                    if (!((Long)mbBlizz.getLastModified()).equals(mbDB.getLastModified()))
                    {
                        mbBlizz.setId(mbDB.getId());
                        mbBlizz.setIsInternalData(mbDB.isInternalData());
                        mbBlizz.saveInDB();
                    }
                }
                
                
                //Show update progress...
                if ( (((iProgres*2)*10)*members.size())/100 < i )
                {
                    Logs.saveLog("..."+ ((iProgres*2)*10) +"%");
                    iProgres++;
                }
            }
            Logs.saveLogln("...100%");
        }
    }
    
    public Member getMemberFromBlizz(String name, String realm) throws UnsupportedEncodingException, ConfigurationException 
    {
        Member blizzPlayer = null;
        //Generate an API URL
        String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), String.format(API_CHARACTER_PROFILE, 
                                        URLEncoder.encode(realm, "UTF-8").replace("+", "%20"), 
                                        URLEncoder.encode(name, "UTF-8").replace("+", "%20")));
        try 
        {
            //Call Blizzard API
            JSONObject blizzPlayerInfo = curl(urlString, //DataException possible trigger
                                            "GET",
                                            "Bearer "+ accesToken,
                                            new String[] {"fields=guild,talents,items,stats", "locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});
            blizzPlayer = new Member(blizzPlayerInfo);
        } 
        catch (IOException|DataException|ParseException e) //Error in blizzard API, like player not found
        {
            Logs.saveLogln("BlizzAPI haven a error to '"+ name +"'\n\t"+ e);
        }
        return blizzPlayer;
    }
    

    /**
     * Get a playable class information 
     * @throws SQLException
     * @throws DataException
     * @throws IOException
     * @throws ParseException 
     */
    public void getPlayableClass() throws SQLException, DataException, IOException, ParseException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), API_PLAYABLE_CLASS);
            //Call Blizzard API
            JSONObject blizzPlayableClass = curl(urlString, //DataException possible trigger
                                                "GET",
                                                "Bearer "+ accesToken,
                                                new String[] {"namespace=static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION")});

            JSONArray playClass = (JSONArray) blizzPlayableClass.get("classes");
            for(int i = 0; i < playClass.size(); i++)
            {
                JSONObject info = (JSONObject) playClass.get(i);
                PlayableClass pClassDB = new PlayableClass(((Long) info.get("id")).intValue());
                PlayableClass pClassBlizz = new PlayableClass(info);
                if(pClassDB.isInternalData())
                {
                    pClassBlizz.setId(pClassDB.getId());
                    pClassBlizz.setIsInternalData(true);
                }
                pClassBlizz.saveInDB();               
            }
        }
    }
    
    public void getPlayableSpec() throws DataException, IOException, ParseException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), API_PLAYEBLE_SPECIALIZATION);
            //Call Blizzard API
            JSONObject blizzPlayableSpec = curl(urlString, //DataException possible trigger
                                                "GET",
                                                "Bearer "+ accesToken,
                                                new String[] {"namespace=static-"+ GeneralConfig.getStringConfig("SERVER_LOCATION")});

            JSONArray playClass = (JSONArray) blizzPlayableSpec.get("character_specializations");
            for(int i = 0; i < playClass.size(); i++)
            {
                JSONObject info = (JSONObject) playClass.get(i);
                String urlDetail = ((JSONObject) info.get("key")).get("href").toString();
                loadPlayableSpecDetail(urlDetail);
            }
        }        
    }
    
    private void loadPlayableSpecDetail(String url) throws DataException, IOException, ParseException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Call Blizzard API
            JSONObject specInfoBlizz = curl(url,
                                                "GET",
                                                "Bearer "+ accesToken);
            PlayableSpec pSpecBlizz = new PlayableSpec(specInfoBlizz);
            PlayableSpec pSpecDB = new PlayableSpec(((Long) specInfoBlizz.get("id")).intValue());
            if(pSpecDB.isInternalData())
            {
                pSpecBlizz.setIsInternalData(true);
            }
            pSpecBlizz.saveInDB();
        }
    }

    /**
     * Get a Characters races information 
     * @throws SQLException
     * @throws DataException
     * @throws IOException
     * @throws ParseException 
     */
    public void getPlayableRaces() throws SQLException, DataException, IOException, ParseException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), API_CHARACTER_RACES);
            //Call Blizzard API
            JSONObject blizzRaces = curl(urlString, //DataException possible trigger
                                        "GET",
                                        "Bearer "+ accesToken,
                                        new String[] {"locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});

            JSONArray races = (JSONArray) blizzRaces.get("races");
            for(int i = 0; i < races.size(); i++)
            {
                JSONObject info = (JSONObject) races.get(i);
                PlayableRace raceDB = new PlayableRace(((Long) info.get("id")).intValue());
                PlayableRace raceBlizz = new PlayableRace(info);
                if(raceDB.isInternalData())
                {
                    raceBlizz.setId(raceDB.getId());
                    raceBlizz.setIsInternalData(true);
                }
                raceBlizz.saveInDB();
            }
        }
    }
    
    /**
     * Get a spell information from blizzard API.
     * @param id
     * @return Spell object (content blizzard api information about spell)
     * @throws DataException
     * @throws IOException
     * @throws ParseException 
     */
    public Spell getSpellInformationBlizz(int id) throws DataException, IOException, ParseException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), 
                                        String.format(API_SPELL, id ));
            //Call Blizzard API
            JSONObject blizzSpell = curl(urlString, //DataException possible trigger
                                        "GET",
                                        "Bearer "+ accesToken,
                                        new String[] {"locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});
            Spell spBlizz = new Spell(blizzSpell);
            spBlizz.saveInDB(); 
            Logs.saveLogln("New spell is save in DB "+ id +" - "+ spBlizz.getName());
            return spBlizz;
        }
    }

    /**
     * Update all spell informatio from blizzard
     * @throws DataException
     * @throws SQLException
     * @throws IOException
     * @throws ParseException 
     */
    public void updateSpellInformation() throws DataException, SQLException, IOException, ParseException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            JSONArray spellInDb = dbConnect.select(Spell.SPELLS_TABLE_NAME,
                                                    new String[] {"id"},
                                                    "id != 0",
                                                    new String[] {});            
            int iProgres = 1;
            Logs.saveLog("0%");
            for(int i = 0; i < spellInDb.size(); i++)
            {
                //Generate an API URL
                String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), 
                                            String.format(API_SPELL, (Integer) ((JSONObject) spellInDb.get(i)).get("id") ));
                //Call Blizzard API
                try
                {
                    JSONObject blizzSpell = curl(urlString, //DataException possible trigger
                                                "GET",
                                                "Bearer "+ accesToken,
                                                new String[] {"locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});
                    Spell spBlizz = new Spell(blizzSpell);
                    spBlizz.setIsInternalData(true);
                    spBlizz.saveInDB();                    
                } catch(DataException e) {
                    Logs.saveLogln("Fail to get information Spell URL ("+ urlString +") - "+ e);
                }
                
                //Show update progress...
                if ( (((iProgres*2)*10)*spellInDb.size())/100 < i )
                {
                    Logs.saveLog("..."+ ((iProgres*2)*10) +"%");
                    iProgres++;
                }              
            }
            Logs.saveLogln("...100%");
        }
    }
    
    /**
     * Update all item information from blizzard
     * @throws DataException
     * @throws SQLException
     * @throws IOException
     * @throws ParseException 
     */
    public void updateItemInformation() throws DataException, SQLException, IOException, ParseException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            JSONArray itemInDB = dbConnect.select(Item.ITEM_TABLE_NAME,
                                                    new String[] {"id"},
                                                    "id != 0",
                                                    new String[] {});            
            int iProgres = 1;
            Logs.saveLog("0%");
            for(int i = 0; i < itemInDB.size(); i++)
            {
                int id = (Integer) ((JSONObject) itemInDB.get(i)).get("id");
                Item itemBlizz = getItemFromBlizz(id);
                itemBlizz.setIsInternalData(true);
                itemBlizz.saveInDB();
                
                //Show update progress...
                if ( (((iProgres*2)*10)*itemInDB.size())/100 < i )
                {
                    Logs.saveLog("..."+ ((iProgres*2)*10) +"%");
                    iProgres++;
                }              
            }
            Logs.saveLogln("...100%");
        }
    }
    
    /**
     * Get inforamtion from item from blizzard api
     * @param id
     * @return Item object (blizzard information)
     */
    public Item getItemFromBlizz(int id)
    {
        Item itemBlizz = null;
        try 
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"),
                    String.format(API_ITEM, id));
            //Call Blizzard API
            JSONObject blizzItem = curl(urlString, //DataException possible trigger
                    "GET",
                    "Bearer "+ accesToken,
                    new String[] {"locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});
            itemBlizz = new Item(blizzItem);
        } catch (IOException | ParseException | DataException ex) {
            Logs.saveLogln("Fail to get information item ("+ id +") - "+ ex);
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
        }
        return itemBlizz;
    }
    
    /**
     * Get guild achivements
     * @throws IOException
     * @throws ParseException
     * @throws DataException 
     */
    public void getGuildAchievementsLists() throws IOException, ParseException, DataException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), API_GUILD_ACHIEVEMENTS);
            //Call Blizzard API
            JSONObject blizzAchiv = curl(urlString, //DataException possible trigger
                                        "GET",
                                        "Bearer "+ accesToken,
                                        new String[] {"locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});

            JSONArray achivGroup = (JSONArray) blizzAchiv.get("achievements");
            saveGuildAchievements(achivGroup);
        }        
    }
    
    private void saveGuildAchievements(JSONArray achivGroup)
    {        
        for(int i = 0; i < achivGroup.size(); i++)
        {
            JSONObject info = (JSONObject) achivGroup.get(i);
            String classification = info.get("name").toString();
            JSONArray achiv = (JSONArray) info.get("achievements");
            for (int j = 0; j < achiv.size(); j++) 
            {
                ((JSONObject) achiv.get(j)).put("classification", classification);                    

                GuildAchievementsList gaDB = new GuildAchievementsList( ((Long) ((JSONObject) achiv.get(j)).get("id")).intValue() );
                GuildAchievementsList gaBlizz = new GuildAchievementsList((JSONObject) achiv.get(j));
                if(gaDB.isInternalData())
                {
                    gaBlizz.setId(gaDB.getId());
                    gaBlizz.setIsInternalData(true);
                }
                gaBlizz.saveInDB();
            }
            if(info.containsKey("categories"))
            {
                JSONArray acGroup = (JSONArray) info.get("categories");
                saveGuildAchievements(acGroup);
            }
        }
    }
    
    public void getCharacterAchievementsLists() throws IOException, ParseException, DataException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), API_CHARACTER_ACHIVEMENTS);
            //Call Blizzard API
            JSONObject blizzAchiv = curl(urlString, //DataException possible trigger
                                        "GET",
                                        "Bearer "+ accesToken,
                                        new String[] {"locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});

            JSONArray achivGroup = (JSONArray) blizzAchiv.get("achievements");
            for(int i = 0; i < achivGroup.size(); i++)
            {
                saveCharacterAchivements( (JSONObject) achivGroup.get(i) , null);
            }
        }        
    }
    
    private void saveCharacterAchivements(JSONObject info, CharacterAchivementsCategory fatherCat)
    {
        //Category
        int catId = ((Long) info.get("id")).intValue();
        CharacterAchivementsCategory category = new CharacterAchivementsCategory(catId);
        if(!category.isInternalData())
        {
            String catName = info.get("name").toString();
            JSONObject catInfo = new JSONObject();
            catInfo.put("id", catId);
            catInfo.put("name", catName);
            if(fatherCat != null)
                catInfo.put("father_id", fatherCat.getId());
            category = new CharacterAchivementsCategory(catInfo);
            category.saveInDB();
        }
        //Achivements
        if(info.containsKey("achievements"))
        {
            JSONArray achivements = (JSONArray) info.get("achievements");
            for(int i = 0; i < achivements.size(); i++)
            {
                JSONObject achiInfo = (JSONObject) achivements.get(i);
                CharacterAchivementsList achv = new CharacterAchivementsList(((Long) achiInfo.get("id")).intValue());
                if(!achv.isInternalData())
                {
                    achiInfo.put("category_id", catId);
                    achv = new CharacterAchivementsList(achiInfo);
                    achv.saveInDB();
                }
            }
        }
        //If have a sub categories
        if(info.containsKey("categories"))
        {
            JSONArray subCat = (JSONArray) info.get("categories");
            for(int i = 0; i < subCat.size(); i++)
            {
                saveCharacterAchivements( (JSONObject) subCat.get(i), category );
            }
        }
    }    
    
    /**
     * Guild challenges information
     * @throws IOException
     * @throws ParseException
     * @throws DataException
     * @throws java.text.ParseException
     * @throws SQLException 
     */
    public void getGuildChallenges() throws IOException, ParseException, DataException, ParseException, SQLException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), String.format(API_GUILD_PROFILE, 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_REALM"), "UTF-8").replace("+", "%20"), 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_NAME"), "UTF-8").replace("+", "%20")));
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ accesToken,
                                    new String[] {"fields=challenge", "locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});

            JSONArray challenges = (JSONArray) respond.get("challenge");
                 
            int iProgres = 1;
            Logs.saveLog("0%");
            for(int i = 0; i < challenges.size(); i++)
            {
                JSONObject challeng = (JSONObject) challenges.get(i);
                JSONObject map = (JSONObject) challeng.get("map");
                JSONArray groups = (JSONArray) challeng.get("groups");
                if(groups.size() > 0)
                {
                    Challenge ch = new Challenge(map);
                    //Validate is old save this map...
                    JSONArray id = dbConnect.select(Challenge.CHALLENGES_TABLE_NAME,
                                                    new String[] {"id"},
                                                    "id=?",
                                                    new String[] { (map.get("id")).toString() });
                    if(id.size() > 0) ch.setIsInternalData(true);

                    for(int j = 0; j < groups.size(); j++)
                    {
                        JSONObject group = (JSONObject) groups.get(j);
                        ChallengeGroup chGroup = new ChallengeGroup(ch.getId(), group);
                        //Validate if exist this group.
                        JSONArray idGroup = dbConnect.select(ChallengeGroup.CHALLENGE_GROUPS_TABLE_NAME,
                                                            new String[] { "group_id" },
                                                            "challenge_id=? AND time_date=?",
                                                            new String[] { ch.getId()+"", ChallengeGroup.getDBDate(chGroup.getTimeDate()) });
                        if(idGroup.size() > 0)
                        {
                            chGroup.setId((Integer) ((JSONObject) idGroup.get(0)).get("group_id"));
                            chGroup.setIsInternalData(true);
                        }

                        //Members
                        JSONArray members = (JSONArray) group.get("members");
                        members.forEach((member) -> {
                            
                            JSONObject inMeb = (JSONObject) member;
                            if( inMeb.containsKey("character") )
                            {
                                JSONObject character = (JSONObject) inMeb.get("character");
                                JSONObject spec = (JSONObject) inMeb.get("spec");
                                //Get info about this member.
                                
                                Member mb = new Member(character.get("name").toString() , character.get("realm").toString() );
                                if (mb.isData()) 
                                {
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
                //Show update progress...
                if ( (((iProgres*2)*10)*challenges.size())/100 < i )
                {
                    Logs.saveLog("..."+ ((iProgres*2)*10) +"%");
                    iProgres++;
                }
            }
            Logs.saveLogln("...100%");
        }
    }    
       
    /**
     * Get playar Achivements
     * @throws IOException
     * @throws ParseException
     * @throws DataException
     * @throws java.text.ParseException
     * @throws SQLException 
     */
    private void getPlayerAchivements() throws IOException, ParseException, DataException, ParseException, SQLException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), String.format(API_GUILD_PROFILE, 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_REALM"), "UTF-8").replace("+", "%20"), 
                                            URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_NAME"), "UTF-8").replace("+", "%20")));
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ accesToken,
                                    new String[] {"fields=news", "locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});

            JSONArray news = (JSONArray) respond.get("news");
            for(int i = 0; i < news.size(); i++)
            {
                int type = -1;
                switch((Integer) ((JSONObject)news.get(i)).get("type"))
                {
                    
                }
                Logs.saveLogln("Newss!!!"+ ((JSONObject)news.get(i)).get("character"));
            }
        }
    }
    
    /**
     * Get wow token price
     * @throws DataException
     * @throws IOException
     * @throws ParseException
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public void getWowToken() throws DataException, IOException, ParseException, ClassNotFoundException, SQLException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {            
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), API_WOW_TOKEN);
            //Call Blizzard API
            JSONObject wowToken = curl(urlString, //DataException possible trigger
                                        "GET",
                                        "Bearer "+ accesToken,
                                        new String[] {"namespace=dynamic-"+ GeneralConfig.getStringConfig("SERVER_LOCATION"),
                                                      "locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});
            String lastUpdate = wowToken.get("last_updated_timestamp").toString();
            String priceUpdate = wowToken.get("price").toString();
            
            JSONArray oldValue = dbConnect.select(DBStructure.WOW_TOKEN_TABLE_NAME,
                                                    new String[] {"last_updated_timestamp"},
                                                    "last_updated_timestamp=?",
                                                    new String[] {lastUpdate});
            if(oldValue.isEmpty())
            {//Not exit this update, save a new infor
                dbConnect.insert(DBStructure.WOW_TOKEN_TABLE_NAME, 
                                DBStructure.WOW_TOKEN_TABLE_KEY, 
                                DBStructure.WOW_TOKEN_TABLE_STRUCTURE, 
                                new String[] { lastUpdate, priceUpdate });
            }
        }
    }
    
    /**
     * Get a user characters information (see all users in DB and try search the characters)
     * @throws SQLException
     * @throws DataException
     * @throws ClassNotFoundException 
     */
    public void getUsersCharacters() throws SQLException, DataException, ClassNotFoundException
    {
        JSONArray users = dbConnect.select(User.USER_TABLE_NAME, 
                                           new String[] {"id", "access_token"},
                                           "access_token IS NOT NULL AND wowinfo=?",
                                           new String[] {"1"});
        //For all account have an accessToken and set a member info
        for(int i = 0; i < users.size(); i++)
        {                    
            String acToken = ((JSONObject)users.get(i)).get("access_token").toString();
            int userID = (Integer) ((JSONObject)users.get(i)).get("id");
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
                                            new String[] { "id", "guild_rank" });
        for(int i = 0; i < allUsers.size(); i++)
        {
            int uderID = (Integer) ((JSONObject) allUsers.get(i)).get("id");
            int actualUserRank = (Integer) ((JSONObject) allUsers.get(i)).get("guild_rank");
            int membersRank = -1;
            //Select player have a better guild rank 
            //select * from gMembers_id_name where user_id = 1 AND rank is not null order by rank limit 1;
            JSONArray charRank = dbConnect.select(Member.GMEMBER_ID_NAME_TABLE_NAME, 
                                                new String[] {"rank"},
                                                "user_id=? AND rank is not null order by rank limit 1",
                                                new String[] { uderID +"" });
            if(charRank.size() > 0)
            {
                membersRank = (Integer) ((JSONObject) charRank.get(0)).get("rank");
            }
            //Save if is different
            if(membersRank != actualUserRank)
            {
                dbConnect.update(User.USER_TABLE_NAME, 
                            new String[] {"guild_rank"},
                            new String[] {membersRank +""},
                            "id=?",
                            new String[] {uderID +""});
            }            
        }       
    }
    
    /**
     * Set member character info
     * @param accessToken String member access Token
     * @param userID internal user ID
     */
    public void setMemberCharacterInfo(String accessToken, int userID)
    {
        try 
        {
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), API_WOW_OAUTH_PROFILE);
            //prepare info
            JSONObject blizzInfo = Update.curl(urlString,
                                            "GET",
                                            new String[] {"access_token="+ accessToken});
            if(blizzInfo.size()>0)
            {
                JSONArray characters = (JSONArray) blizzInfo.get("characters");
                //we defined all the characters for this player
                for(int i = 0; i < characters.size(); i++)
                {
                    JSONObject pj = (JSONObject) characters.get(i);
                    String name = pj.get("name").toString();
                    String realm = pj.get("realm").toString();
                    Member mb = new Member(name, realm);
                    
                    if(mb != null && mb.isData())
                    {
                        try 
                        {
                            dbConnect.update(Member.GMEMBER_ID_NAME_TABLE_NAME,
                                            new String[] { "user_id" },
                                            new String[] { userID+""},
                                            "internal_id=?", 
                                            new String[] { mb.getId()+""});
                            dbConnect.update(User.USER_TABLE_NAME,
                                            new String[] {"last_alters_update"},
                                            new String[] { getCurrentTimeStamp() },
                                            User.USER_TABLE_KEY+"=?",
                                            new String[] { userID+"" } );
                        } catch (ClassNotFoundException|SQLException ex) {
                            Logs.saveLogln("Fail to insert userID info "+ ex);
                        }
                    }
                }
                //Get a most elevet rank member, like 0 is GM, 1 is officers ... 
                try 
                {
                    JSONArray guildRank = dbConnect.select(Member.GMEMBER_ID_NAME_TABLE_NAME,
                                                        new String[] {"rank"},
                                                        "in_guild=? AND user_id=? ORDER BY rank ASC LIMIT 1",
                                                        new String[] {"1", userID +""});
                    if(guildRank.size() > 0)
                    {//Save a rank from this player...
                        int rank = (Integer)((JSONObject) guildRank.get(0)).get("rank");                        
                        try {
                            dbConnect.update(User.USER_TABLE_NAME,
                                    new String[] {"guild_rank"},
                                    new String[] { rank +""},
                                    "id=?",
                                    new String[] { userID +"" });
                        } catch (ClassNotFoundException ex) {
                            Logs.saveLogln("Fail to save guild rank from user "+ userID +" - "+ ex);
                        }
                    }
                } catch (SQLException ex) {
                    Logs.saveLogln("Fail to select characters from user "+ userID +" - "+ ex);
                }
                //Set accessToken is working yet~
                try 
                {
                    dbConnect.update(User.USER_TABLE_NAME,
                            new String[] { "wowinfo" },
                            new String[] { "1" },
                            "id=?",
                            new String[] { userID +""});
                    Logs.saveLogln("Wow access token is update!");
                } catch (ClassNotFoundException|SQLException ex) {
                    Logs.saveLogln("Fail to set wowinfo is worikng from "+ userID);
                }
            }
        } catch(DataException e) {
            if(e.getErrorCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                Logs.saveLogln("User block or not get access wow~ "+ e);                    
                try {
                    dbConnect.update(User.USER_TABLE_NAME,
                            new String[] {"wowinfo"},
                            new String[] {"0"},
                            "id=?",
                            new String[] {userID +""});
                } catch (DataException | ClassNotFoundException |SQLException ex) {
                    Logs.saveLogln("Fail to update wowinfo false from "+ userID +" - "+ ex);
                }                                
            }
        } catch (IOException|ParseException ex) {
            Logs.saveLogln("Fail to get user Access Token "+ ex);
        } catch(ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
        }
    }
    
    /**
     * From RaiderIO get a guild progression information
     * @throws DataException
     * @throws IOException
     * @throws ParseException 
     */
    public void getGuildProgression() throws DataException, IOException, ParseException, ConfigurationException
    {
        //Generate an API URL
        String urlString = String.format(RAIDER_IO_API_URL, 
                                    GeneralConfig.getStringConfig("SERVER_LOCATION"), 
                                    URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_REALM"), "UTF-8").replace("+", "%20"), 
                                    URLEncoder.encode(GeneralConfig.getStringConfig("GUILD_NAME"), "UTF-8").replace("+", "%20"));
        //Call RaiderIO API
        JSONObject raiderIOGuildProgression = curl(urlString, "GET");
        JSONArray raidRankings = (JSONArray) ((JSONObject) raiderIOGuildProgression.get("guildDetails")).get("raidRankings");         
        JSONArray raidProgress = (JSONArray) ((JSONObject) raiderIOGuildProgression.get("guildDetails")).get("raidProgress");
        for(int i = 0; i < raidProgress.size(); i++)
        {
            JSONObject raid = (JSONObject) raidProgress.get(i);
            //Add rank info
            raid.put("rank", (JSONObject) ((JSONObject) raidRankings.get(i)).get("ranks"));
            //Save raid
            Raid itRaid = new Raid(raid);
            Raid oldRaid = new Raid(raid.get("raid").toString());
            if(oldRaid.isInternalData())
            {
                itRaid.setName(oldRaid.getName());
                itRaid.setTotalBoss(oldRaid.getTotalBoss());
                itRaid.setId(oldRaid.getId());
                itRaid.setIsInternalData(true);
            }            
            itRaid.saveInDB();
        }
        
    }
    
    /**
     * Get boss master list from blizzard api
     * @throws DataException
     * @throws IOException
     * @throws ParseException 
     */
    public void getBossInformation() throws DataException, IOException, ParseException, ConfigurationException
    {
        if(accesToken == null) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), API_BOSS_MASTER_LIST);
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ accesToken,
                                    new String[] {"locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});

            JSONArray bossList = (JSONArray) respond.get("bosses");
            for(int i = 0; i < bossList.size(); i++)
            {
                JSONObject bossInfo = (JSONObject)bossList.get(i);
                Boss inDB = new Boss( ((Long) bossInfo.get("id")).intValue() );
                JSONObject bossInfoCreate = new JSONObject();
                bossInfoCreate.put("id", bossInfo.get("id"));
                bossInfoCreate.put("description", bossInfo.get("description"));
                bossInfoCreate.put("name", bossInfo.get("name"));
                bossInfoCreate.put("slug", bossInfo.get("urlSlug"));
                JSONArray npcList = (JSONArray) bossInfo.get("npcs");
                for(int j = 0; j < npcList.size(); j++)
                {
                    JSONObject npcInfo = (JSONObject) npcList.get(j);
                    if(npcInfo.get("id").equals(bossInfo.get("id")))
                    {//If have a NPC and have same ID, change the especial slug and complate name
                        bossInfoCreate.put("name", npcInfo.get("name"));
                        bossInfoCreate.put("slug", npcInfo.get("urlSlug"));
                        break;
                    }
                }
                Boss b = new Boss(bossInfoCreate);
                if(inDB.isInternalData())
                {
                    b.setIsInternalData(true);
                }
                b.saveInDB();
                
            }
        }
    }

    /**
     * Generate URL API connection
     * @param urlString complete API URL
     * @param method GET, POST, DELETE, etc
     * @return Blizzard JSONObject content
     * @throws IOException
     * @throws ParseException
     * @throws DataException 
     */
    public static JSONObject curl(String urlString, String method) throws IOException, ParseException, DataException { return curl(urlString, method, null, null); }
    /**
     * Generate URL API connection
     * @param urlString complete API URL
     * @param method GET, POST, DELETE, etc
     * @param authorization API authorization, Bearer, o basic, etc
     * @return Blizzard JSONObject content
     * @throws IOException
     * @throws ParseException
     * @throws DataException 
     */
    public static JSONObject curl(String urlString, String method, String authorization) throws IOException, ParseException, DataException { return curl(urlString, method, authorization, null, null); }
    /**
     * Generate URL API connection
     * @param urlString complete API URL
     * @param method GET, POST, DELETE, et
     * @param parameters URL parameters ("field=member","acctrion=move"....)
     * @return Blizzard JSONObject content
     * @throws IOException
     * @throws ParseException
     * @throws DataException 
     */
    public static JSONObject curl(String urlString, String method, String[] parameters) throws IOException, ParseException, DataException { return curl(urlString, method, null, parameters, null); }
    /**
     * Generate URL API connection
     * @param urlString complete API URL
     * @param method GET, POST, DELETE, et
     * @param authorization API authorization, Bearer, o basic, etc
     * @param parameters URL parameters ("field=member","acctrion=move"....)
     * @return Blizzard JSONObject content
     * @throws IOException
     * @throws ParseException
     * @throws DataException 
     */
    public static JSONObject curl(String urlString, String method, String authorization, String[] parameters) throws IOException, ParseException, DataException { return curl(urlString, method, authorization, parameters, null); }
    /**
     * Generate URL API connection
     * @param urlString complete API URL
     * @param method GET, POST, DELETE, et
     * @param authorization API authorization, Bearer, o basic, etc
     * @param parameters URL parameters ("field=member","acctrion=move"....)
     * @param bodyData if have a data in body
     * @return Blizzard JSONObject content
     * @throws IOException
     * @throws DataException 
     */
    public static JSONObject curl(String urlString, String method, String authorization, String[] parameters, byte[] bodyData) throws IOException, DataException
    {        
        //Add parameters
        String urlPrepared = urlString;
        if(parameters != null)
        {
            urlPrepared += "?";
            for(String param : parameters) { urlPrepared += param +"&"; }
            urlPrepared = urlPrepared.substring(0,urlPrepared.length()-1);
        }

        URL url = new URL(urlPrepared);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set Connection
        conn.setRequestMethod(method);
        if(authorization != null) conn.setRequestProperty("Authorization", authorization);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        //User agent
        conn.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
        //body data
        if(bodyData != null) 
        {			
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(bodyData.length));
            conn.getOutputStream().write(bodyData);
        }
        
        //return Object
        JSONObject json;
        blizzAPICallCounter++;
        
        //Error Request controller
        switch(conn.getResponseCode())
        {
            case HttpURLConnection.HTTP_OK:
                //get result
                BufferedReader reader = new BufferedReader ( new InputStreamReader(conn.getInputStream()));
                StringBuilder results = new StringBuilder();
                String rLine;
                while ((rLine = reader.readLine()) != null) 
                {
                    results.append(rLine);
                }
                reader.close();

                //Parse JSON Object                
                try 
                {
                    JSONParser parser = new JSONParser();
                    json = (JSONObject) parser.parse(results.toString());                
                    return json;
                } catch(ParseException ex) {
                    throw new DataException("Fail to parse result!, check the URL"+ urlString +" - "+ ex);
                }
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                DataException ex = new DataException("Error: "+ conn.getResponseCode() +" - UnAuthorized request, check CLIENT_ID and CLIENT_SECRET in APIInfo.java");
                ex.setErrorCode(conn.getResponseCode());
                throw ex;
            case HttpURLConnection.HTTP_BAD_REQUEST:
                throw new DataException("Error: "+ conn.getResponseCode() +" - Bad Request request, check the API URL is correct in APIInfo.java");
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new DataException("Error: "+ conn.getResponseCode() +" - Data not found, check the guild name, server location and realm in APIInfo.java");
            case HttpURLConnection.HTTP_UNAVAILABLE:
                throw new DataException("Error: "+ conn.getResponseCode() +" - Blizzard API Error... try again later");
            case API_SECOND_LIMIT_ERROR:
                try {
                    Logs.saveLogln("Too many call to API Blizz... wait a second");
                    TimeUnit.SECONDS.sleep(1);
                    return curl(urlString, method, authorization, parameters, bodyData);
                } catch (InterruptedException e) { }
            default:
                throw new DataException("Error: "+ conn.getResponseCode() +" - Internal Code: 0 - URL> "+ urlString);
        }
    }
    
    private static Date addSeconds(Date date, int second)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, second);
        return cal.getTime();
    }
    
    /**
     * Get a current time string yyyy-MM-dd HH:mm:ss
     * @return 
     */
    public static String getCurrentTimeStamp() 
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    
    /**
     * Parse unix time to actual server time.
     * @param unixTime
     * @return 
     */
    public static String parseUnixTime(String unixTime)
    {
        Date time = new Date(Long.parseLong(unixTime));
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }
    
    /**
     * Counter blizzard aplication call
     * @return 
     */
    public static int getBlizzAPICallCounter() { return blizzAPICallCounter; }
}