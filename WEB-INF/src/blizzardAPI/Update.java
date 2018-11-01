/**
 * File : Update.java
 * Desc : Update guild and character in guild information
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;
import com.artOfWar.gameObject.Guild;
import com.artOfWar.gameObject.Member;
import com.artOfWar.gameObject.PlayableClass;
import com.artOfWar.gameObject.Race;
import com.artOfWar.gameObject.challenge.Challenges;
import com.artOfWar.gameObject.challenge.ChallengeGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Update implements APIInfo
{

    //Constant	
    public static final int DYNAMIC_UPDATE = 0;
    public static final int STATIC_UPDATE = 1;
    public static final String GMEMBERS_ID_TABLE = "gMembers_id_name";

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
        System.out.println("-------Update process is START! (Dynamic)------");
        /*
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
        //Guild challenges update!*/
        System.out.println("Guild challenges update!");
        try { getGuildChallenges(); } 
        catch (IOException|ParseException|DataException ex) { System.out.println("Fail get a CharacterS Info: "+ ex); }
        System.out.println("-------Update process is COMPLATE! (Dynamic)------");

        //Save log update in DB
        try 
        {			
            Calendar cal = Calendar.getInstance();  
            Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());

            dbConnect.insert("update_timeline",
                            new String[] {"type", "update_time"},
                            new String[] {DYNAMIC_UPDATE +"", timestamp.toString()});
        } 
        catch(DataException|SQLException|ClassNotFoundException e)
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
        System.out.println("Races Information update!");
        try { getRaces(); } 
        catch (IOException|ParseException|SQLException|DataException ex) { System.out.println("Fail update Races Info: "+ ex); }		
        System.out.println("-------Update process is COMPLATE! (Static)------");

        //Save log update in DB
        try 
        {
            Calendar cal = Calendar.getInstance();  
            Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());

            dbConnect.insert("update_timeline",
                            new String[] {"type", "update_time"},
                            new String[] {STATIC_UPDATE +"", timestamp.toString()});
        } 
        catch(DataException|SQLException|ClassNotFoundException e)
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
        String urlString = String.format(API_OAUTH_TOKEN_URL, SERVER_LOCATION);
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
    public void getGuildProfile() throws IOException, ParseException, SQLException, ClassNotFoundException, DataException
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
    public void getGuildMembers() throws DataException, IOException, ParseException, SQLException, ClassNotFoundException
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
            dbConnect.update(GMEMBERS_ID_TABLE,
                            new String[] {"in_guild"},
                            new String[] {"0"});

            for(int i = 0; i < members.size(); i++)
            {				
                JSONObject info = (JSONObject) ((JSONObject) members.get(i)).get("character");

                //Check if have a guild and if set guild, (Blizzard not update a guilds members list) 
                if(info.containsKey("guild") && (info.get("guild").toString()).equals(GUILD_NAME))
                {	
                    String rankMember = ((JSONObject) members.get(i)).get("rank").toString();
                    dbConnect.insert(GMEMBERS_ID_TABLE,
                                    new String[] {"member_name","rank","in_guild"},
                                    new String[] {info.get("name").toString(), rankMember, "1"},
                                    "ON DUPLICATE KEY UPDATE in_guild='1', rank='"+ rankMember +"'");
                }				
            }
        }
    }

    /**
     * get a player information
     */
    public void getCharacterInfo() throws SQLException, DataException, IOException, ParseException
    {
        if(this.accesToken.length() == 0) throw new DataException("Access Token Not Found");
        else
        {
            JSONArray members = dbConnect.select(GMEMBERS_ID_TABLE, 
                                                new String[] {"internal_id", "member_name"},
                                                "in_guild=1");

            int iProgres = 1;
            System.out.print("0%");
            for(int i = 0; i < members.size(); i++)
            {
                JSONObject member = (JSONObject) members.get(i); //internal DB Members [internal_id, name, rank]				
                //Generate an API URL
                String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_CHARACTER_PROFILE, 
                                                URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
                                                URLEncoder.encode(member.get("member_name").toString(), "UTF-8").replace("+", "%20")));
                try 
                {
                    //Call Blizzard API
                    JSONObject blizzPlayerInfo = curl(urlString, //DataException possible trigger
                                                    "GET",
                                                    "Bearer "+ this.accesToken,
                                                    new String[] {"fields=guild"});
                    blizzPlayerInfo.put("internal_id", (int) member.get("internal_id"));
                    Member blizzPlayer = new Member(blizzPlayerInfo);
                    blizzPlayer.saveInDB();
                } 
                catch (DataException e) //Error in blizzard API, like player not found
                {
                    System.out.println("BlizzAPI haven a error to "+ member.get("member_name") +"\n\t"+ e);
                }

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

    /**
     * get a playable class information 
     */
    public void getPlayableClass() throws SQLException, DataException, IOException, ParseException
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
    public void getRaces() throws SQLException, DataException, IOException, ParseException
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
     * Guild challenges information
     */
    private void getGuildChallenges() throws IOException, ParseException, DataException
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
                    Challenges ch = new Challenges();
                    ch.setMapName(map.get("name").toString());

                    for(int j = 0; j < groups.size(); j++)
                    {
                        JSONObject group = (JSONObject) groups.get(j);
                        ChallengeGroup chGroup = new ChallengeGroup();
                        //Data 
                        chGroup.setTimeDate((Date) group.get("date"));
                        JSONObject dataRun = (JSONObject) group.get("time");
                        chGroup.setTimeHours((Integer) dataRun.get("hours"));
                        chGroup.setTimeMinutes((Integer) dataRun.get("minutes"));
                        chGroup.setTimeSeconds((Integer) dataRun.get("seconds"));
                        chGroup.setTimeMilliseconds((Integer) dataRun.get("milliseconds"));
                        chGroup.setPositive((Boolean) dataRun.get("isPositive"));

                        //Members
                        JSONArray members = (JSONArray) group.get("members");
                        for(int k = 0; k < members.size(); k++)
                        {
                            JSONObject member = (JSONObject) members.get(j);
                            JSONObject character = (JSONObject) member.get("character");
                            JSONObject spec = (JSONObject) member.get("spec");
                            Member mb = new Member(character.get("name"));
                            if(mb.isData())
                            {
                                mb.setSpecRole(spec.get("role"));
                                mb.setSpecName(spec.get("name"));
                                //Add Member 
                                chGroup.addMember(mb);								
                            }
                        }
                        //Add Group
                        ch.addChallengeGroup(chGroup);
                    }
                    //ch.saveInDBObj();
                }
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
    private JSONObject curl(String urlString, String method, String authorization) throws IOException, ParseException, DataException { return curl(urlString, method, authorization, null, null); }
    private JSONObject curl(String urlString, String method, String authorization, String[] parameters) throws IOException, ParseException, DataException { return curl(urlString, method, authorization, parameters, null); }
    private JSONObject curl(String urlString, String method, String authorization, String[] parameters, byte[] bodyData) throws IOException, ParseException, DataException
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