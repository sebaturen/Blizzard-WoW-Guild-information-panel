/**
 * File : Update.java
 * Desc : Update guild and character in guild information
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;
import com.artOfWar.dbConnect.DBStructure;
import com.artOfWar.gameObject.Boss;
import com.artOfWar.gameObject.guild.Guild;
import com.artOfWar.gameObject.guild.GuildAchivements;
import com.artOfWar.gameObject.characters.Member;
import com.artOfWar.gameObject.characters.PlayableClass;
import com.artOfWar.gameObject.characters.Race;
import com.artOfWar.gameObject.characters.Spell;
import com.artOfWar.gameObject.guild.challenges.Challenge;
import com.artOfWar.gameObject.guild.challenges.ChallengeGroup;
import com.artOfWar.gameObject.guild.raids.Raid;

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
import java.util.Date;

public class Update implements APIInfo
{

    //Constant	
    public static final int DYNAMIC_UPDATE = 0;
    public static final int STATIC_UPDATE = 1;

    //Attribute
    private String accesToken = "";
    private static DBConnect dbConnect;

    /**
     * Constructor. Run a generateAccesToken to generate this token
     */
    public Update() throws IOException, ParseException, DataException
    {
        dbConnect = new DBConnect();
        generateAccesToken();
    }

    /**
     * Run Dynamic element update method and save the register the last update.
     */
    public void updateDynamicAll()
    {
        
        
        /*System.out.println("Guild new information update!");
        try { getGuildNews();}
        catch (IOException|ParseException|SQLException|DataException|java.text.ParseException ex) { System.out.println("Fail update guild news Info: "+ ex); }		
        */
        
        System.out.println("-------Update process is START! (Dynamic)------");        
        //Guild information update!
        System.out.println("Guild Information update!");
        try { getGuildProfile(); } 
        catch (IOException|ParseException|SQLException|ClassNotFoundException|DataException ex) { System.out.println("Fail update Guild Info: "+ ex); }
        //Guild members information update!
        System.out.println("Guild members information update!");
        try { getGuildMembers(); } 
        catch (IOException|ParseException|SQLException|ClassNotFoundException|DataException ex) { System.out.println("Fail update Guild Members Info: "+ ex); }
        //Character information update!						
        System.out.println("Character information update!");
        try { getCharacterInfo(); } 
        catch (IOException|ParseException|SQLException|DataException ex) { System.out.println("Fail get a CharacterS Info: "+ ex); }
        //Guild challenges update!
        System.out.println("Guild challenges update!");
        try { getGuildChallenges(); } 
        catch (IOException|ParseException|SQLException|DataException|java.text.ParseException ex) { System.out.println("Fail get a CharacterS Info: "+ ex); }
        //Wow Token
        System.out.println("Wow token information update!");
        try { getWowToken(); } 
        catch (ClassNotFoundException|IOException|ParseException|DataException ex) { System.out.println("Fail update Wow Token Info: "+ ex); }		
        //Users player
        System.out.println("Users characters information update!");
        try { getUsersCharacters(); } 
        catch (SQLException|DataException ex) { System.out.println("Fail update user characters Info: "+ ex); }
        //Guild progression RaiderIO
        System.out.println("Guild progression update!");
        try { getGuildProgression(); } 
        catch (IOException|ParseException|DataException ex) { System.out.println("Fail update guild progression Info: "+ ex); }	
        System.out.println("-------Update process is COMPLATE! (Dynamic)------");

        //Save log update in DB
        try 
        {
            /* {"type", "update_time"}; */
            dbConnect.insert(DBStructure.UPDATE_INTERVAL_TABLE_NAME,
                            DBStructure.UPDATE_INTERVAL_TABLE_KEY,
                            DBStructure.outKey(DBStructure.UPDATE_INTERVAL_TABLE_STRUCTURE),
                            new String[] {DYNAMIC_UPDATE +"", getCurrentTimeStamp()});
        } 
        catch(DataException|ClassNotFoundException e)
        {
            System.out.println("Fail to save update time: "+ e);
        }
    }

    /**
     * Run Static element update
     */
    public void updateStaticAll()
    {
        System.out.println("-------Update process is START! (Static)------");
        //Playable Class
        System.out.println("Playable class Information update!");
        try { getPlayableClass(); } 
        catch (IOException|ParseException|SQLException|DataException ex) { System.out.println("Fail update Playable class Info: "+ ex); }
        //Races
        System.out.println("Races Information update!");
        try { getRaces(); } 
        catch (IOException|ParseException|SQLException|DataException ex) { System.out.println("Fail update Races Info: "+ ex); }		
        //Guild Achivements lists
        System.out.println("Guild Achivements lists information update!");
        try { getGuildAchivementsLists(); } 
        catch (IOException|ParseException|DataException ex) { System.out.println("Fail update Achivements Info: "+ ex); }		
        //Update Spell information
        System.out.println("Spell information update!");
        try { updateSpellInformation(); } 
        catch (IOException|ParseException|SQLException|DataException ex) { System.out.println("Fail update spell Info: "+ ex); }		
        //Boss DB Upate info
        System.out.println("Boss DB Update");
        try { getBossInformation(); }
        catch (IOException|ParseException|DataException ex) { System.out.println("Fail get boss DB Info: "+ ex); }
        System.out.println("-------Update process is COMPLATE! (Static)------");
        
        //Save log update in DB
        try 
        {
            /* {"type", "update_time"}; */
            dbConnect.insert(DBStructure.UPDATE_INTERVAL_TABLE_NAME,
                            DBStructure.UPDATE_INTERVAL_TABLE_KEY,
                            DBStructure.outKey(DBStructure.UPDATE_INTERVAL_TABLE_STRUCTURE),
                            new String[] {STATIC_UPDATE +"", getCurrentTimeStamp()});
        } 
        catch(DataException|ClassNotFoundException e)
        {
            System.out.println("Fail to save update time: "+ e);
        }		
    }

    /**
     * Blizzard API need a token to access to API, this token you can
     * get if have a ClinetID and ClientSecret of the application
     */
    private void generateAccesToken() throws IOException, ParseException, DataException
    {
        String urlString = String.format(API_OAUTH_URL, SERVER_LOCATION, API_OAUTH_TOKEN);
        String apiInfo = Base64.getEncoder().encodeToString((CLIENT_ID+":"+CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));

        //prepare info
        String boodyDate = "grant_type=client_credentials";
        byte[] postDataBytes = boodyDate.getBytes("UTF-8");

        //Get an Access Token
        this.accesToken = (String) (curl(urlString,
                                        "POST",
                                        "Basic "+ apiInfo,
                                        null,
                                        postDataBytes)).get("access_token");
    }

    /**
     * Get a guild profile
     */
    private void getGuildProfile() throws IOException, ParseException, SQLException, ClassNotFoundException, DataException
    {
        if(this.accesToken.length() == 0) throw new DataException("Access Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_GUILD_PROFILE, 
                                            URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
                                            URLEncoder.encode(GUILD_NAME, "UTF-8").replace("+", "%20")));
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ this.accesToken);
            //Chek if is in db
            JSONArray lastModified = dbConnect.select(DBStructure.GUILD_TABLE_NAME,
                                                    new String[] {"id", "lastModified"},
                                                    "name=? AND realm=?",
                                                    new String[] { respond.get("name").toString(), respond.get("realm").toString() });
            Guild apiGuild = new Guild(respond);
            if (lastModified.size() > 0)
            {//posible update
                Long blizzUpdateTime = Long.parseLong(((JSONObject)lastModified.get(0)).get("lastModified").toString());
                if( !blizzUpdateTime.equals(apiGuild.getLastModified()) )
                {//if last save is diferent, update.
                    apiGuild.setId(((JSONObject)lastModified.get(0)).get("id").toString());
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
     * get a guilds members
     */
    private void getGuildMembers() throws DataException, IOException, ParseException, SQLException, ClassNotFoundException
    {
        if(this.accesToken.length() == 0) throw new DataException("Access Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_GUILD_PROFILE, 
                                            URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
                                            URLEncoder.encode(GUILD_NAME, "UTF-8").replace("+", "%20")));
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ this.accesToken,
                                    new String[] {"fields=members"});

            JSONArray members = (JSONArray) respond.get("members");

            //Reset 0 in_guild all members...
            dbConnect.update(DBStructure.GMEMBER_ID_NAME_TABLE_NAME,
                            new String[] {"in_guild"},
                            new String[] {"0"},
                            "in_guild > ?",
                            new String[] {"0"});
            for(int i = 0; i < members.size(); i++)
            {				
                JSONObject info = (JSONObject) ((JSONObject) members.get(i)).get("character");

                //Check if have a guild and if set guild, (Blizzard not update a guilds members list) 
                if(info.containsKey("guild") && (info.get("guild").toString()).equals(GUILD_NAME))
                {	
                    String rankMember = ((JSONObject) members.get(i)).get("rank").toString();
                    String name = info.get("name").toString();
                    //See if need update or insert
                    JSONArray inDBgMembersID = dbConnect.select(DBStructure.GMEMBER_ID_NAME_TABLE_NAME, 
                                                        new String[] {"internal_id"}, 
                                                        "member_name=? AND realm=?",
                                                        new String[] {name, GUILD_REALM});
                    if(inDBgMembersID.size() > 0)
                    {//Update
                        dbConnect.update(DBStructure.GMEMBER_ID_NAME_TABLE_NAME, 
                                        new String[] {"rank", "in_guild"},
                                        new String[] {rankMember, "1"},
                                        "internal_id=?",
                                        new String[] { ((JSONObject)inDBgMembersID.get(0)).get("internal_id").toString() });
                    }
                    else
                    {//Insert
                        dbConnect.insert(DBStructure.GMEMBER_ID_NAME_TABLE_NAME, 
                                        DBStructure.GMEMBER_ID_NAME_TABLE_KEY,
                                        new String[] { "member_name", "realm", "rank", "in_guild" },
                                        new String[] { name, GUILD_REALM, rankMember, "1" });
                    }
                }				
            }
        }
    }

    /**
     * get a player information IN GUILD!
     */
    private void getCharacterInfo() throws SQLException, DataException, IOException, ParseException
    {
        if(this.accesToken.length() == 0) throw new DataException("Access Token Not Found");
        else
        {
            JSONArray members = dbConnect.select(DBStructure.GMEMBER_ID_NAME_TABLE_NAME,
                                                DBStructure.GMEMBER_ID_NAME_TABLE_STRUCTURE,
                                                "in_guild=?",
                                                new String[] {"1"});

            int iProgres = 1;
            System.out.print("0%");
            for(int i = 0; i < members.size(); i++)
            {
                JSONObject member = (JSONObject) members.get(i); //internal DB Members [internal_id, name, rank]
                getMemberInfoFromBlizzOrDB(member.get("member_name").toString(), member.get("realm").toString());
                
                //Show update progress...
                if ( (((iProgres*2)*10)*members.size())/100 < i )
                {
                    System.out.print("..."+ ((iProgres*2)*10) +"%");
                    iProgres++;
                }
            }
            System.out.println("...100%");
        }
    }
    
    public Member getMemberFromBlizz(String name, String realm) throws UnsupportedEncodingException
    {
        Member blizzPlayer = null;
        //Generate an API URL
        String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_CHARACTER_PROFILE, 
                                        URLEncoder.encode(realm, "UTF-8").replace("+", "%20"), 
                                        URLEncoder.encode(name, "UTF-8").replace("+", "%20")));
        try 
        {
            //Call Blizzard API
            JSONObject blizzPlayerInfo = curl(urlString, //DataException possible trigger
                                            "GET",
                                            "Bearer "+ this.accesToken,
                                            new String[] {"fields=guild","fields=talents"});
            blizzPlayer = new Member(blizzPlayerInfo);
        } 
        catch (IOException|DataException|ParseException e) //Error in blizzard API, like player not found
        {
            System.out.println("BlizzAPI haven a error to "+ name +"\n\t"+ e);
        }
        return blizzPlayer;
    }    
    
    private Member getMemberInfoFromBlizzOrDB(String name, String realm)
    {       
        Member mb = null;
        //1~ get info from DB
        try
        {
            JSONArray inDBgMembersID = dbConnect.select(DBStructure.GMEMBER_ID_NAME_TABLE_NAME, 
                                                        new String[] {"internal_id"}, 
                                                        "member_name=? AND realm=?",
                                                        new String[] {name, realm});
            //if exist, load from DB
            if(inDBgMembersID.size() > 0)
            {
                String memberInternalId = ((JSONObject)inDBgMembersID.get(0)).get("internal_id").toString();
                mb = new Member( Integer.parseInt(memberInternalId));
                Member blizzMember = getMemberFromBlizz(name, realm);
                if(blizzMember != null && blizzMember.isData()) //Error in load from blizz, 
                {//not have a info or get another error.
                   //error in load time, is in GMEBERS_ID_TABLE but not have information
                   if(!mb.isData() || !((Long)blizzMember.getLastModified()).equals(mb.getLastModified())) 
                    {//add if the member not is update...
                        blizzMember.setId(memberInternalId);
                        if(mb.isInternalData()) blizzMember.setIsInternalData(true);
                        blizzMember.checkSpeckIDInDb();
                        blizzMember.saveInDB();
                        mb = blizzMember;
                    }
                }
                
            }
            else
            {   
                /* {"internal_id", "member_name", "realm", "rank", "in_guild", "user_id"}; */                
                String id = dbConnect.insert(DBStructure.GMEMBER_ID_NAME_TABLE_NAME, 
                                DBStructure.GMEMBER_ID_NAME_TABLE_KEY,
                                new String[] { "member_name", "realm", "in_guild" },
                                new String[] { name, realm, "0" });//asumed is 0 becouse in frist moment, we get all guilds members.
                mb = getMemberFromBlizz(name, realm);
                if(mb != null && mb.isData())
                {
                    mb.setId(id);
                    mb.saveInDB();                    
                }
            }
        }
        catch (SQLException|DataException|ClassNotFoundException|UnsupportedEncodingException e)
        {
            System.out.println("Error get SQL Query member from blizz or DB "+ e);
        }
        return mb;
    }
    

    /**
     * get a playable class information 
     */
    private void getPlayableClass() throws SQLException, DataException, IOException, ParseException
    {
        if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, API_PLAYABLE_CLASS);
            //Call Blizzard API
            JSONObject blizzPlayableClass = curl(urlString, //DataException possible trigger
                                                "GET",
                                                "Bearer "+ this.accesToken,
                                                new String[] {"namespace=static-us"});

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

    /**
     * get a Characters races information 
     */
    private void getRaces() throws SQLException, DataException, IOException, ParseException
    {
        if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, API_CHARACTER_RACES);
            //Call Blizzard API
            JSONObject blizzRaces = curl(urlString, //DataException possible trigger
                                        "GET",
                                        "Bearer "+ this.accesToken);

            JSONArray races = (JSONArray) blizzRaces.get("races");
            for(int i = 0; i < races.size(); i++)
            {
                JSONObject info = (JSONObject) races.get(i);
                Race raceDB = new Race(((Long) info.get("id")).intValue());
                Race raceBlizz = new Race(info);
                if(raceDB.isInternalData())
                {
                    raceBlizz.setId(raceDB.getId());
                    raceBlizz.setIsInternalData(true);
                }
                raceBlizz.saveInDB();
            }
        }
    }

    private void updateSpellInformation() throws DataException, SQLException, IOException, ParseException
    {
        if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
        else
        {
            JSONArray spellInDb = dbConnect.select(DBStructure.SPELLS_TABLE_NAME,
                                                    new String[] {"id"});            
            int iProgres = 1;
            System.out.print("0%");
            for(int i = 0; i < spellInDb.size(); i++)
            {
                //Generate an API URL
                String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, 
                                            String.format(API_SPELL, (Integer) ((JSONObject) spellInDb.get(i)).get("id") ));
                //Call Blizzard API
                JSONObject blizzSpell = curl(urlString, //DataException possible trigger
                                            "GET",
                                            "Bearer "+ this.accesToken);
                Spell spBlizz = new Spell(blizzSpell);
                spBlizz.setIsInternalData(true);
                spBlizz.saveInDB();  
                
                //Show update progress...
                if ( (((iProgres*2)*10)*spellInDb.size())/100 < i )
                {
                    System.out.print("..."+ ((iProgres*2)*10) +"%");
                    iProgres++;
                }              
            }
            System.out.println("...100%");
        }        
    }
    /**
     * Get guild achivements
     */
    private void getGuildAchivementsLists() throws IOException, ParseException, DataException
    {
        if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, API_GUILD_ACHIVEMENTS);
            //Call Blizzard API
            JSONObject blizzAchiv = curl(urlString, //DataException possible trigger
                                        "GET",
                                        "Bearer "+ this.accesToken);

            JSONArray achivGroup = (JSONArray) blizzAchiv.get("achievements");
            for(int i = 0; i < achivGroup.size(); i++)
            {
                JSONObject info = (JSONObject) achivGroup.get(i);
                String classification = info.get("name").toString();
                JSONArray achiv = (JSONArray) info.get("achievements");
                for (int j = 0; j < achiv.size(); j++) {
                    ((JSONObject) achiv.get(j)).put("classification", classification);                    
                    
                    GuildAchivements gaDB = new GuildAchivements( ((Long) ((JSONObject) achiv.get(j)).get("id")).intValue() );
                    GuildAchivements gaBlizz = new GuildAchivements((JSONObject) achiv.get(j));
                    if(gaDB.isInternalData())
                    {
                        gaBlizz.setId(gaDB.getId());
                        gaBlizz.setIsInternalData(true);
                    }
                    gaBlizz.saveInDB();
                }		
            }
        }        
    }
    
    /**
     * Guild challenges information
     */
    private void getGuildChallenges() throws IOException, ParseException, DataException, java.text.ParseException, SQLException
    {
        if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_GUILD_PROFILE, 
                                            URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
                                            URLEncoder.encode(GUILD_NAME, "UTF-8").replace("+", "%20")));
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ this.accesToken,
                                    new String[] {"fields=challenge"});

            JSONArray challenges = (JSONArray) respond.get("challenge");
                 
            int iProgres = 1;
            System.out.print("0%");
            for(int i = 0; i < challenges.size(); i++)
            {
                JSONObject challeng = (JSONObject) challenges.get(i);
                JSONObject map = (JSONObject) challeng.get("map");
                JSONArray groups = (JSONArray) challeng.get("groups");
                if(groups.size() > 0)
                {
                    Challenge ch = new Challenge(map);
                    //Validate is old save this map...
                    JSONArray id = dbConnect.select(DBStructure.CHALLENGES_TABLE_NAME,
                                                    new String[] {"id"},
                                                    "id=?",
                                                    new String[] { (map.get("id")).toString() });
                    if(id.size() > 0) ch.setIsInternalData(true);

                    for(int j = 0; j < groups.size(); j++)
                    {
                        JSONObject group = (JSONObject) groups.get(j);
                        ChallengeGroup chGroup = new ChallengeGroup(ch.getMapId(), group);
                        //Validate if exist this group.
                        JSONArray idGroup = dbConnect.select(DBStructure.CHALLENGE_GROUPS_TABLE_NAME,
                                                            new String[] { "group_id" },
                                                            "challenge_id=? AND time_date=?",
                                                            new String[] { ch.getId(), ChallengeGroup.getDBDate(chGroup.getTimeDate()) });
                        if(idGroup.size() > 0)
                        {
                            chGroup.setId( ((JSONObject) idGroup.get(0)).get("group_id").toString());
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
                                
                                Member mb = getMemberInfoFromBlizzOrDB( character.get("name").toString() , character.get("realm").toString() );
                                if (mb != null && mb.isData()) {
                                    mb.setSpec(spec.get("name").toString(), spec.get("role").toString());
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
                    System.out.print("..."+ ((iProgres*2)*10) +"%");
                    iProgres++;
                }
            }
            System.out.println("...100%");
        }		
    }    
    
    private void getGuildNews() throws IOException, ParseException, DataException, java.text.ParseException, SQLException
    {
        if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_GUILD_PROFILE, 
                                            URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
                                            URLEncoder.encode(GUILD_NAME, "UTF-8").replace("+", "%20")));
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ this.accesToken,
                                    new String[] {"fields=news"});

            JSONArray news = (JSONArray) respond.get("news");
            for(int i = 0; i < news.size(); i++)
            {
               
                System.out.println("Newss!!!"+ ((JSONObject)news.get(i)).get("character"));
            }
        }
    }
    
    private void getPlayerAchivements() throws IOException, ParseException, DataException, java.text.ParseException, SQLException
    {
        if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_GUILD_PROFILE, 
                                            URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
                                            URLEncoder.encode(GUILD_NAME, "UTF-8").replace("+", "%20")));
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ this.accesToken,
                                    new String[] {"fields=news"});

            JSONArray news = (JSONArray) respond.get("news");
            for(int i = 0; i < news.size(); i++)
            {
                int type = -1;
                switch((Integer) ((JSONObject)news.get(i)).get("type"))
                {
                    
                }
                System.out.println("Newss!!!"+ ((JSONObject)news.get(i)).get("character"));
            }
        }
    }
    
    private void getWowToken() throws DataException, IOException, ParseException, ClassNotFoundException
    {
        if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
        else
        {            
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, API_WOW_TOKEN);
            //Call Blizzard API
            JSONObject wowToken = curl(urlString, //DataException possible trigger
                                        "GET",
                                        "Bearer "+ this.accesToken,
                                        new String[] {"namespace=dynamic-us"});            
            dbConnect.insert(DBStructure.WOW_TOKEN_TABLE_NAME,
                            DBStructure.WOW_TOKEN_TABLE_KEY,
                            DBStructure.WOW_TOKEN_TABLE_STRUCTURE,
                            new String[] {wowToken.get("last_updated_timestamp").toString(), wowToken.get("price").toString()},
                            "ON DUPLICATE KEY UPDATE price=?",
                            new String[] {wowToken.get("price").toString()});
        }        
    }
    
    private void getUsersCharacters() throws SQLException, DataException
    {
        JSONArray users = dbConnect.select(DBStructure.USER_TABLE_NAME, 
                                           new String[] {"id", "access_token"},
                                           "access_token IS NOT NULL AND wowinfo=?",
                                           new String[] {"1"});
        if(users.size() > 0)
        {
            for(int i = 0; i < users.size(); i++)
            {                    
                String acToken = ((JSONObject)users.get(i)).get("access_token").toString();
                int userID = (Integer) ((JSONObject)users.get(i)).get("id");
                setMemberCharacterInfo(acToken, userID);
            }
        }
    }
    
    /**
     * Get a information from member account
     * @accessToken String member access Token
     * Return: guild rank
     */
    public void setMemberCharacterInfo(String accessToken, int userID)
    {
        try 
        {
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, API_WOW_OAUTH_PROFILE);
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
                    Member mb = getMemberInfoFromBlizzOrDB(name, realm);
                    
                    if(mb != null && mb.isData())
                    {
                        try 
                        {
                            dbConnect.update(DBStructure.GMEMBER_ID_NAME_TABLE_NAME,
                                            new String[] { "user_id" },
                                            new String[] { userID+""},
                                            "internal_id=?", 
                                            new String[] { mb.getId()});
                        } catch (ClassNotFoundException ex) {
                            System.out.println("Fail to insert userID info "+ ex);
                        }
                    }
                }
                //Get a most elevet rank member, like 0 is GM, 1 is officers ... 
                try 
                {
                    JSONArray guildRank = dbConnect.select(DBStructure.GMEMBER_ID_NAME_TABLE_NAME,
                                                        new String[] {"rank"},
                                                        "in_guild=? AND user_id=? ORDER BY rank ASC LIMIT 1",
                                                        new String[] {"1", userID +""});
                    if(guildRank.size() > 0)
                    {//Save a rank from this player...
                        int rank = (Integer)((JSONObject) guildRank.get(0)).get("rank");                        
                        try {
                            dbConnect.update(DBStructure.USER_TABLE_NAME,
                                    new String[] {"guild_rank"},
                                    new String[] { rank +""},
                                    "id=?",
                                    new String[] { userID +"" });
                        } catch (ClassNotFoundException ex) {
                            System.out.println("Fail to save guild rank from user "+ userID +" - "+ ex);
                        }
                    }
                } catch (SQLException ex) {
                    System.out.println("Fail to select characters from user "+ userID +" - "+ ex);
                }
                //Set accessToken is working yet~
                try 
                {
                    dbConnect.update(DBStructure.USER_TABLE_NAME,
                            new String[] { "wowinfo" },
                            new String[] { "1" },
                            "id=?",
                            new String[] { userID +""});
                    System.out.println("Wow access token is update!");
                } catch (ClassNotFoundException ex) {
                    System.out.println("Fail to set wowinfo is worikng from "+ userID);
                }
            }
        } catch(DataException e) {
            if(e.getErrorCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                System.out.println("User block or not get access wow~ "+ e);                    
                try {
                    dbConnect.update(DBStructure.USER_TABLE_NAME,
                            new String[] {"wowinfo"},
                            new String[] {"0"},
                            "id=?",
                            new String[] {userID +""});
                } catch (DataException | ClassNotFoundException ex) {
                    System.out.println("Fail to update wowinfo false from "+ userID +" - "+ ex);
                }                                
            }
        } catch (IOException|ParseException ex) {
            System.out.println("Fail to get user Access Token "+ ex);
        }
    }
    
    private void getGuildProgression() throws DataException, IOException, ParseException
    {                
        //Generate an API URL
        String urlString = String.format(RAIDER_IO_API_URL, 
                                    SERVER_LOCATION, 
                                    URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
                                    URLEncoder.encode(GUILD_NAME, "UTF-8").replace("+", "%20"));
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
                itRaid.setId(oldRaid.getId());
                itRaid.setIsInternalData(true);
            }            
            itRaid.saveInDB();
        }
        
    }
    
    private void getBossInformation() throws DataException, IOException, ParseException
    {
        if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
        else
        {
            //Generate an API URL
            String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, API_BOSS_MASTER_LIST);
            //Call Blizzard API
            JSONObject respond = curl(urlString, 
                                    "GET",
                                    "Bearer "+ this.accesToken);

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
     * @urlString : complete API URL
     * @method : GET, POST, DELETE, etc
     * @authorization : API authorization, Bearer, o basic, etc
     * @parameters : URL parameters ("field=member","acctrion=move"....)
     * @bodyData : if have a data in body
     */
    public static JSONObject curl(String urlString, String method) throws IOException, ParseException, DataException { return curl(urlString, method, null, null); }
    public static JSONObject curl(String urlString, String method, String authorization) throws IOException, ParseException, DataException { return curl(urlString, method, authorization, null, null); }
    public static JSONObject curl(String urlString, String method, String[] parameters) throws IOException, ParseException, DataException { return curl(urlString, method, null, parameters, null); }
    public static JSONObject curl(String urlString, String method, String authorization, String[] parameters) throws IOException, ParseException, DataException { return curl(urlString, method, authorization, parameters, null); }
    public static JSONObject curl(String urlString, String method, String authorization, String[] parameters, byte[] bodyData) throws IOException, ParseException, DataException
    {
        //Add parameters
        if(parameters != null)
        {
            String url = urlString +"?";
            for(String param : parameters) { url += param +"&"; }
            urlString = url.substring(0,url.length()-1);
        }

        URL url = new URL(urlString);
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

        //Error Request controller
        switch(conn.getResponseCode())
        {
            case HttpURLConnection.HTTP_OK:
                //get result
                BufferedReader reader = new BufferedReader ( new InputStreamReader(conn.getInputStream()));
                String result = reader.readLine();
                reader.close();

                //Parse JSON Object
                JSONParser parser = new JSONParser();
                json = (JSONObject) parser.parse(result);
                return json;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                DataException ex = new DataException("Error: "+ conn.getResponseCode() +" - UnAuthorized request, check CLIENT_ID and CLIENT_SECRET in APIInfo.java");
                ex.setErrorCode(conn.getResponseCode());
                throw ex;
            case HttpURLConnection.HTTP_BAD_REQUEST:
                throw new DataException("Error: "+ conn.getResponseCode() +" - Bad Request request, check the API URL is correct in APIInfo.java");
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new DataException("Error: "+ conn.getResponseCode() +" - Data not found, check the guild name, server location and realm in APIInfo.java");
            default:
                throw new DataException("Error: "+ conn.getResponseCode() +" - Internal Code: 0");
        }
    }    
    
    public static String getCurrentTimeStamp() 
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}