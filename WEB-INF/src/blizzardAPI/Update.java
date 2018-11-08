/**
 * File : Update.java
 * Desc : Update guild and character in guild information
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;
import com.artOfWar.gameObject.DBStructure;
import com.artOfWar.gameObject.Guild;
import com.artOfWar.gameObject.GuildAchivements;
import com.artOfWar.gameObject.Member;
import com.artOfWar.gameObject.PlayableClass;
import com.artOfWar.gameObject.Race;
import com.artOfWar.gameObject.challenge.Challenge;
import com.artOfWar.gameObject.challenge.ChallengeGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
            Guild apiGuild = new Guild(respond);
            apiGuild.saveInDB();
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
                            "1=?",
                            new String[] {"1"});

            for(int i = 0; i < members.size(); i++)
            {				
                JSONObject info = (JSONObject) ((JSONObject) members.get(i)).get("character");

                //Check if have a guild and if set guild, (Blizzard not update a guilds members list) 
                if(info.containsKey("guild") && (info.get("guild").toString()).equals(GUILD_NAME))
                {	
                    String rankMember = ((JSONObject) members.get(i)).get("rank").toString();
                    dbConnect.insert(DBStructure.GMEMBER_ID_NAME_TABLE_NAME,
                                    DBStructure.GMEMBER_ID_NAME_TABLE_KEY,
                                    DBStructure.outKey(DBStructure.GMEMBER_ID_NAME_TABLE_STRUCTURE),
                                    new String[] {info.get("name").toString(), GUILD_REALM, rankMember, "1"},
                                    "ON DUPLICATE KEY UPDATE realm=?, in_guild=?, rank=?",
                                    new String[] { GUILD_REALM, "1",rankMember+"" });
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
                getMemberFromBlizz((int) member.get("internal_id"), member.get("member_name").toString(), APIInfo.GUILD_REALM);

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
    
    public Member getMemberFromBlizz(int id, String name, String realm) throws UnsupportedEncodingException
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
            blizzPlayerInfo.put("internal_id", id);
            blizzPlayer = new Member(blizzPlayerInfo);
            blizzPlayer.saveInDB();
        } 
        catch (IOException|DataException|ParseException e) //Error in blizzard API, like player not found
        {
            System.out.println("BlizzAPI haven a error to "+ name +"\n\t"+ e);
        }
        return blizzPlayer;
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

                PlayableClass pClass = new PlayableClass(info);
                pClass.saveInDB();
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
                Race race = new Race(info);
                race.saveInDB();		
            }
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
                    GuildAchivements ga = new GuildAchivements((JSONObject) achiv.get(j));
                    ga.saveInDB();
                }
                //Race race = new Race(info);
                //race.saveInDB();		
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
            for(int i = 0; i < challenges.size(); i++)
            {
                JSONObject challeng = (JSONObject) challenges.get(i);
                JSONObject map = (JSONObject) challeng.get("map");
                JSONArray groups = (JSONArray) challeng.get("groups");
                if(groups.size() > 0)
                {
                    Challenge ch = new Challenge(map);

                    for(int j = 0; j < groups.size(); j++)
                    {
                        JSONObject group = (JSONObject) groups.get(j);
                        ChallengeGroup chGroup = new ChallengeGroup(ch.getMapId(), group);

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
            }
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
    
    
    private Member getMemberInfoFromBlizzOrDB(String name, String realm)
    {       
        Member mb = null;
        //1~ get info from DB
        try
        {
            JSONArray inDBgMembersID = dbConnect.select(DBStructure.GMEMBER_ID_NAME_TABLE_NAME, 
                                                        DBStructure.GMEMBER_ID_NAME_TABLE_STRUCTURE, 
                                                        "member_name=? AND realm=?",
                                                        new String[] {name, realm});
            //if exist, load from DB
            if(inDBgMembersID.size() > 0)
            {
                int memberInternalId = (Integer) ((JSONObject)inDBgMembersID.get(0)).get("internal_id");
                mb = new Member( memberInternalId );
                if(!mb.isData()) //error in load time, is in GMEBERS_ID_TABLE but not have information
                { //add if the member not is update...
                    mb = getMemberFromBlizz(memberInternalId, name, realm);
                }
            }
            else
            {   
                String id = dbConnect.insert(DBStructure.GMEMBER_ID_NAME_TABLE_NAME,
                                            DBStructure.GMEMBER_ID_NAME_TABLE_KEY,
                                            DBStructure.outKey(DBStructure.GMEMBER_ID_NAME_TABLE_STRUCTURE),
                                            new String[] {name, realm, "0", "0"},
                                            "ON DUPLICATE KEY UPDATE in_guild=?",
                                            new String[] {"0"}); //asumed is 0 becouse in frist moment, we get all guilds members.
                mb = getMemberFromBlizz(Integer.parseInt(id), name, realm);
            }
        }
        catch (SQLException|DataException|ClassNotFoundException|UnsupportedEncodingException e)
        {
            System.out.println("Error get SQL Query");
        }
        return mb;
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
    
    public static String getCurrentTimeStamp() 
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * Generate URL API connection
     * @urlString : complete API URL
     * @method : GET, POST, DELETE, etc
     * @authorization : API authorization, Bearer, o basic, etc
     * @parameters : URL parameters ("field=member","acctrion=move"....)
     * @bodyData : if have a data in body
     */
    public static JSONObject curl(String urlString, String method, String authorization) throws IOException, ParseException, DataException { return curl(urlString, method, authorization, null, null); }
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
        conn.setRequestProperty("Authorization", authorization);
        conn.setDoOutput(true);
        conn.setDoInput(true);
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
                throw new DataException("Error: "+ conn.getResponseCode() +" - UnAuthorized request, check CLIENT_ID and CLIENT_SECRET in APIInfo.java");
            case HttpURLConnection.HTTP_BAD_REQUEST:
                throw new DataException("Error: "+ conn.getResponseCode() +" - Bad Request request, check the API URL is correct in APIInfo.java");
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new DataException("Error: "+ conn.getResponseCode() +" - Data not found, check the guild name, server location and realm in APIInfo.java");
            default:
                throw new DataException("Error: "+ conn.getResponseCode() +" - Internal Code: 0");
        }
    }
}