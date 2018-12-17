/**
 * File : User.java
 * Desc : User controller~
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel;

import com.blizzardPanel.blizzardAPI.APIInfo;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.characters.Character;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class User 
{
    //User
    public static final String USER_TABLE_NAME = "users";
    public static final String USER_TABLE_KEY = "id";
    public static final String[] USER_TABLE_STRUCTURE = {"id", "battle_tag", "access_token", "guild_rank", "main_character", "wowinfo", "last_login", "last_alters_update"};
    
    //Atribute
    private int id;
    private String battleTag;
    private String accessToken;
    private String lastLogin;
    private String lastAlterUpdate;
    private int guildRank = -1;
    private int idMainChar = -1;
    private boolean isLogin = false;
    private boolean isCharsReady = false;
    private List<Character> characters = new ArrayList<>();
    private Character mainCharacter;
    
    private final DBConnect dbConnect = new DBConnect();
    
    //Empty constructor NEED FROM SERVERLET!, becouse the serverlet automaticale instance a object.
    public User()
    {
        //NOT DELETE NEED FROM SERVERLET!
    }
            
    public User(int id)
    {
        loadFromDB(id);
    }
    
    private void loadUser(JSONObject userInfo)
    {
        this.id = (Integer) userInfo.get("id");
        this.battleTag = userInfo.get("battle_tag").toString();
        if(this.accessToken == null) //not load old AccToken if new exist
            this.accessToken = userInfo.get("access_token").toString();
        this.guildRank = (Integer) userInfo.get("guild_rank");
        this.isLogin = true;
        if(userInfo.get("main_character") != null)
            this.idMainChar = (Integer) userInfo.get("main_character");
        this.isCharsReady = true;
        if(userInfo.get("last_login") != null)
            this.lastLogin = userInfo.get("last_login").toString();
        if(userInfo.get("last_alters_update") != null)
            this.lastAlterUpdate = userInfo.get("last_alters_update").toString();
    }
    
    private void loadFromDB(int id)
    {
        try {
            JSONArray info = dbConnect.select(USER_TABLE_NAME,
                    USER_TABLE_STRUCTURE,
                    "id=?",
                    new String [] {id+""});
            if(info.size() > 0)
            {
                JSONObject userInfo = (JSONObject) info.get(0);
                loadUser(userInfo);
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(User.class, "Fail to load user from ID "+ id +" - "+ ex);
        }        
    }
    
    public boolean checkUser() { return checkUser(false); }
    private boolean checkUser(boolean forceCheck)
    {
        if(this.battleTag == null) return false;
        if(this.isLogin && !forceCheck) return this.isLogin;
        try 
        {
            JSONArray validUser = dbConnect.select(
                    User.USER_TABLE_NAME,
                    USER_TABLE_STRUCTURE,
                    "battle_tag=?",
                    new String[] {this.battleTag});
            if(validUser.size() > 0)
            {
                JSONObject infoUser = (JSONObject) validUser.get(0);
                loadUser(infoUser);
                return true;
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(User.class, "Fail to login "+ this.battleTag +" - "+ ex);
        }
        return false;
    }
    
    public boolean setUserCode(String code)
    {
        this.accessToken = getAccessToken(code);
        if(this.accessToken == null) return false;
        this.battleTag = getBlizzBattleTag(this.accessToken);
        //If have a info~
        if(this.accessToken == null || this.battleTag == null) return false;
        //Save a info~
        boolean vRet = false;
        if(checkUser()) //Valid if account exit in DB
        {//exist... 
            try 
            {
                dbConnect.update(
                        User.USER_TABLE_NAME,
                        new String[] {"access_token", "last_login"},
                        new String[] {this.accessToken, Update.getCurrentTimeStamp()},
                        "id=?",
                        new String[] {this.id +""}); 
                vRet = true;
            } catch (DataException | ClassNotFoundException | SQLException ex) {
                Logs.errorLog(User.class, "Fail to save access token to "+ this.battleTag +" - "+ ex);
            }            
        }
        else
        {//not exist...   
            try 
            {            
                String userIdDB = dbConnect.insert(
                                User.USER_TABLE_NAME,
                                User.USER_TABLE_KEY,
                                new String[] {"battle_tag", "access_token", "last_login"},
                                new String[] { this.battleTag, this.accessToken, Update.getCurrentTimeStamp()});
                this.id = Integer.parseInt(userIdDB);
                vRet = true;
            } catch (DataException | ClassNotFoundException | SQLException ex) {
                Logs.errorLog(User.class, "Fail to insert user "+ this.battleTag);
            }
        }
        //Try get a member rank...   
        updateUserCharacters();
        return vRet;
    }
    
    private void updateUserCharacters()
    {
        final String accToken = this.accessToken;
        final int uId = this.id;
        Thread upChar = new Thread() {
            @Override
            public void run()
            {               
                try {
                    Update up = new Update();
                    up.setMemberCharacterInfo(accToken, uId);
                } catch (IOException | ParseException | DataException ex) {
                    Logs.errorLog(User.class, "Fail to seve characters info "+ uId +" - "+ ex);
                }
                checkUser(true);
                setIsCharsReady(true);
            }
        };
        upChar.start();
    }
    
    private String getAccessToken(String code)
    {
        try {
            String urlString = String.format(APIInfo.API_OAUTH_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), APIInfo.API_OAUTH_TOKEN);
            String apiInfo = Base64.getEncoder().encodeToString((GeneralConfig.getStringConfig("CLIENT_ID")+":"+GeneralConfig.getStringConfig("CLIENT_SECRET")).getBytes(StandardCharsets.UTF_8));
         
            String redirectUrl = URLEncoder.encode(GeneralConfig.getStringConfig("MAIN_URL")+GeneralConfig.getStringConfig("BLIZZAR_LINK"), "UTF-8");
            //prepare info
            String bodyData = "redirect_uri="+redirectUrl+"&"
                    + "scope=wow.profile&"
                    + "grant_type=authorization_code&"
                    + "code="+ code;
            byte[] postDataBytes = bodyData.getBytes("UTF-8");
            
            JSONObject blizzInfo = Update.curl(urlString,
                                                "POST",
                                                "Basic "+ apiInfo,
                                                null,
                                                postDataBytes);
            /* blizzInfo 
             * {"access_token":"asdasd",
             * "scope":"wow.profile",
             * "token_type":"bearer",
             * "expires_in":86399}
             */
            if(blizzInfo.size() > 0)
            {
                return blizzInfo.get("access_token").toString();
            }
        } catch (IOException|DataException ex) {
            Logs.errorLog(User.class, "Fail to get user Access Token "+ ex);
        }
        return null;
    }
    
    private String getBlizzBattleTag(String accessToken)
    {
        try {
            //Generate an API URL
            String urlString = String.format(APIInfo.API_OAUTH_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), APIInfo.API_OAUTH_USERINFO);
            
            if(accessToken.length() > 0)
            {
                //Call Blizzard API
                JSONObject respond = Update.curl(urlString, 
                                            "GET",
                                            "Bearer "+ accessToken,
                                            new String[] {"locale="+ GeneralConfig.getStringConfig("LENGUAJE_API_LOCALE")});
                if(respond.containsKey("battletag"))
                {
                    return respond.get("battletag").toString();
                }
            }
        } catch (IOException|ParseException|DataException ex) {
            Logs.errorLog(User.class, "Fail to get BattleTag "+ ex);
        }
        return null;
    }
    
    private void loadMainCharFromDB()
    {
        this.mainCharacter = new Character(this.idMainChar);
    }
    
    private void loadCharacters()
    {
        try {
            JSONArray chars = dbConnect.select(Character.GMEMBER_ID_NAME_TABLE_NAME +" gm, "+ Character.CHARACTER_INFO_TABLE_NAME +" c",
                    new String[] {"gm.internal_id" },
                    "gm.user_id=? AND gm.internal_id = c.internal_id ORDER BY c.level DESC",
                    new String[] { this.id +"" }, true);
            for(int i = 0; i < chars.size(); i++)
            {
                int internalID = (Integer) ((JSONObject)chars.get(i)).get("internal_id");
                Character mb = new Character(internalID);
                if(mb.isData())
                {
                    if(mb.getId() == this.idMainChar) 
                    {
                        mb.setIsMain(true);
                        mainCharacter = mb;
                    }
                    this.characters.add(mb);
                }
            }
        } catch (SQLException|DataException ex) {
            Logs.errorLog(User.class, "Error get a character user info "+ this.id +" - "+ ex);
        }
    }
    
    public int getId() { return this.id; }
    public String getLastLogin() { return this.lastLogin; }
    public String getLastAltersUpdate() { return this.lastAlterUpdate; }
    public int getGuildRank() { return this.guildRank; }
    public String getBattleTag() { return this.battleTag; }
    public List<Character> getCharacters() { if(this.characters.isEmpty()) loadCharacters(); return this.characters; }
    public boolean isCharsReady() { return this.isCharsReady; }
    public Character getMainCharacter() 
    {
        if(this.mainCharacter == null && this.idMainChar > 0)
        {
            loadMainCharFromDB();
        } 
        return this.mainCharacter; 
    }
    
    private void setIsCharsReady(boolean r) { this.isCharsReady = r; }
    public boolean setMainCharacter(int id) 
    {
        if(this.characters.isEmpty()) loadCharacters();
        boolean stateChange = false;
        //valid if this id is from this member and remove old main Character
        for(Character m : this.characters) 
        {
            if(m.getId() == id && m.isGuildMember())
            {
                try {
                    m.setIsMain(true);
                    dbConnect.update(USER_TABLE_NAME,
                            new String[] {"main_character"},
                            new String[] {id+""}, 
                            USER_TABLE_KEY+"=?",
                            new String[] {this.id+""});
                    stateChange = true;
                    this.mainCharacter = m;
                } catch (DataException | ClassNotFoundException | SQLException ex) {
                    Logs.errorLog(User.class, "Fail to save main character from user - "+ ex);
                }
            }
            else
                m.setIsMain(false);
        }
        if(!stateChange) this.mainCharacter = null;
        return stateChange;
        
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(o == this) return true;
        if(o == null || (this.getClass() != o.getClass())) return false;

        int oId = ((User) o).getId();
        return (oId == this.id);
    }
    
}
