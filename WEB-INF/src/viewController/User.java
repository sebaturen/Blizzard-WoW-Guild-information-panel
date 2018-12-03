/**
 * File : User.java
 * Desc : User controller~
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.blizzardAPI.APIInfo;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.characters.Member;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class User 
{
    //User
    public static final String USER_TABLE_NAME = "users";
    public static final String USER_TABLE_KEY = "id";
    public static final String[] USER_TABLE_STRUCTURE = {"id", "battle_tag", "access_token", "guild_rank", "main_character", "wowinfo"};
    
    //Atribute
    private int id;
    private String battleTag;
    private String accessToken;
    private int guildRank = -1;
    private int idMainChar;
    private boolean isLogin = false;
    private boolean isCharsReady = false;
    private List<Member> characters;
    
    private final DBConnect dbConnect = new DBConnect();
    
    public User()
    {
        
    }
        
    public User(int id)
    {
        loadFromDB(id);
    }
    
    private void loadUser(JSONObject userInfo)
    {
        this.id = (Integer) userInfo.get("id");
        this.battleTag = userInfo.get("battle_tag").toString();
        this.accessToken = userInfo.get("access_token").toString();
        this.guildRank = (Integer) userInfo.get("guild_rank");
        this.isLogin = true;
        if(userInfo.get("main_character") != null)
            this.idMainChar = (Integer) userInfo.get("main_character");
        loadCharacters();
        this.isCharsReady = true;
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
            Logs.saveLogln("Fail to load user from ID "+ id +" - "+ ex);
        }        
    }
    
    public boolean checkUser() { return checkUser(false); }
    public boolean checkUser(boolean forceCheck)
    {
        if(this.battleTag == null) return false;
        if(this.isLogin && !forceCheck) return this.isLogin;
        try {
            JSONArray validUser = dbConnect.select(User.USER_TABLE_NAME,
                    USER_TABLE_STRUCTURE,
                    "battle_tag=?",
                    new String[] {battleTag});
            if(validUser.size() > 0)
            {
                JSONObject infoUser = (JSONObject) validUser.get(0);
                loadUser(infoUser);
                return true;
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to login "+ this.battleTag +" - "+ ex);
        }
        return false;
    }
    
    public boolean setUserCode(String code)
    {
        this.accessToken = getAccessToken(code);
        this.battleTag = getBlizzBattleTag(accessToken);
        //If have a info~
        if(this.accessToken == null || this.battleTag == null) return false;
        //Save a info~
        boolean vRet = false;
        if(checkUser()) //Valid if account exit in DB
        {//exist... 
            try {                
                dbConnect.update(User.USER_TABLE_NAME,
                        new String[] {"access_token"},
                        new String[] {this.accessToken},
                        "id=?",
                        new String[] {this.id +""}); 
                vRet = true;
            } catch (DataException | ClassNotFoundException | SQLException ex) {
                Logs.saveLogln("Fail to save access token to "+ this.battleTag +" - "+ ex);
            }            
        }
        else
        {//not exist...   
            try {            
                String userIdDB = dbConnect.insert(User.USER_TABLE_NAME,
                        User.USER_TABLE_KEY,
                        new String[] {"battle_tag", "access_token"},
                        new String[] { this.battleTag, this.accessToken});
                this.id = Integer.parseInt(userIdDB);
                vRet = true;
            } catch (DataException | ClassNotFoundException | SQLException ex) {
                Logs.saveLogln("Fail to insert user "+ this.battleTag);
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
                    Logs.saveLogln("Fail to seve characters info "+ uId +" - "+ ex);
                }
                checkUser(true);
                setIsCharsReady(true);
                loadCharacters();
            }
        };
        upChar.start();
    }
    
    private String getAccessToken(String code)
    {
        try {
            String urlString = String.format(APIInfo.API_OAUTH_URL, GeneralConfig.SERVER_LOCATION, APIInfo.API_OAUTH_TOKEN);
            String apiInfo = Base64.getEncoder().encodeToString((APIInfo.CLIENT_ID+":"+APIInfo.CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));
         
            String redirectUrl = URLEncoder.encode(GeneralConfig.MAIN_URL+GeneralConfig.BLIZZAR_LINK, "UTF-8");
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
            if(blizzInfo.size()>0)
            {
                return blizzInfo.get("access_token").toString();
            }
        } catch (IOException|DataException ex) {
            Logs.saveLogln("Fail to get user Access Token "+ ex);
        }
        return null;
    }
    
    private String getBlizzBattleTag(String accessToken)
    {
        try {
            //Generate an API URL
            String urlString = String.format(APIInfo.API_OAUTH_URL, GeneralConfig.SERVER_LOCATION, APIInfo.API_OAUTH_USERINFO);
            
            if(accessToken.length() > 0)
            {
                //Call Blizzard API
                JSONObject respond = Update.curl(urlString, 
                                            "GET",
                                            "Bearer "+ accessToken);
                if(respond.containsKey("battletag"))
                {
                    return respond.get("battletag").toString();
                }
            }
        } catch (IOException|ParseException|DataException ex) {
            Logs.saveLogln("Fail to get BattleTag "+ ex);
        }
        return null;
    }
    
    private void loadCharacters()
    {
        this.characters = new ArrayList<>();
        try {
            JSONArray chars = dbConnect.select(Member.GMEMBER_ID_NAME_TABLE_NAME +" gm, "+ Member.CHARACTER_INFO_TABLE_NAME +" c",
                    new String[] {"gm.internal_id" },
                    "gm.user_id=? AND gm.internal_id = c.internal_id ORDER BY c.level DESC",
                    new String[] { this.id +"" }, true);
            for(int i = 0; i < chars.size(); i++)
            {
                int internalID = (Integer) ((JSONObject)chars.get(i)).get("internal_id");
                Member mb = new Member(internalID);
                if(mb.isData())
                {
                    if(mb.getId() == this.idMainChar) mb.setIsMain(true);
                    this.characters.add(mb);
                }
            }
        } catch (SQLException|DataException ex) {
            Logs.saveLogln("Error get a character user info "+ this.id +" - "+ ex);
        }
    }
    
    public int getGuildRank() { return this.guildRank; }
    public String getBattleTag() { return this.battleTag; }
    public List<Member> getCharacters() { if(this.characters == null) loadCharacters(); return this.characters; }
    public boolean isCharsReady() { return this.isCharsReady; }
    private void setIsCharsReady(boolean r) { this.isCharsReady = r; }
    public boolean setMainCharacter(int id) 
    {
        if(this.characters == null) loadCharacters();
        boolean stateChange = false;
        //valid if this id is from this member and remove old main Character
        for(Member m : this.characters) 
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
                } catch (DataException | ClassNotFoundException | SQLException ex) {
                    Logs.saveLogln("Fail to save main character from user - "+ ex);
                }
            }
            else
                m.setIsMain(false);
        }
        return stateChange;
        
    }
    
}
