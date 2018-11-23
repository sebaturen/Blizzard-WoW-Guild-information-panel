/**
 * File : User.java
 * Desc : User controller~
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class User 
{
    //User
    public static final String USER_TABLE_NAME = "users";
    public static final String USER_TABLE_KEY = "id";
    public static final String[] USER_TABLE_STRUCTURE = {"id", "battle_tag", "access_token", "guild_rank", "wowinfo"};
    
    //Atribute
    private int id;
    private String battleTag;
    private String accessToken;
    private int guildRank = -1;
    private boolean isLogin = false;
    private boolean isCharsReady = false;
    
    private final DBConnect dbConnect;
    
    public User()
    {
        dbConnect = new DBConnect();
    }
    
    public boolean checkUser() { return checkUser(false); }
    public boolean checkUser(boolean forceCheck)
    {
        if(this.battleTag == null) return false;
        if(this.isLogin && !forceCheck) return this.isLogin;
        try {
            JSONArray validUser = dbConnect.select(User.USER_TABLE_NAME,
                    new String[] {"id", "battle_tag", "access_token", "guild_rank"},
                    "battle_tag=?",
                    new String[] {battleTag});
            if(validUser.size() > 0)
            {
                JSONObject infoUser = (JSONObject) validUser.get(0);
                this.id = (Integer) infoUser.get("id");
                this.battleTag = infoUser.get("battle_tag").toString();
                this.guildRank = (Integer) infoUser.get("guild_rank");
                this.isLogin = true;
                return true;
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLog("Fail to login "+ this.battleTag +" - "+ ex);
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
                Logs.saveLog("Fail to save access token to "+ this.battleTag +" - "+ ex);
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
                Logs.saveLog("Fail to insert user "+ this.battleTag);
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
                    Logs.saveLog("Fail to seve characters info "+ uId +" - "+ ex);
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
            String urlString = String.format(APIInfo.API_OAUTH_URL, APIInfo.SERVER_LOCATION, APIInfo.API_OAUTH_TOKEN);
            String apiInfo = Base64.getEncoder().encodeToString((APIInfo.CLIENT_ID+":"+APIInfo.CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));
         
            String redirectUrl = URLEncoder.encode(APIInfo.MAIN_URL+APIInfo.BLIZZAR_LINK, "UTF-8");
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
            Logs.saveLog("Fail to get user Access Token "+ ex);
        }
        return null;
    }
    
    private String getBlizzBattleTag(String accessToken)
    {
        try {
            //Generate an API URL
            String urlString = String.format(APIInfo.API_OAUTH_URL, APIInfo.SERVER_LOCATION, APIInfo.API_OAUTH_USERINFO);
            
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
            Logs.saveLog("Fail to get BattleTag "+ ex);
        }
        return null;
    }
       
    public List<Member> getCharacterList()
    {
        List<Member> userMember = new ArrayList<>();  
        try {
            JSONArray chars = dbConnect.select(Member.GMEMBER_ID_NAME_TABLE_NAME +" gm, "+ Member.CHARACTER_INFO_TABLE_NAME +" c",
                    new String[] {"gm.internal_id" },
                    "gm.user_id=? AND gm.internal_id = c.internal_id ORDER BY c.level DESC",
                    new String[] { this.id +"" }, true);
            for(int i = 0; i < chars.size(); i++)
            {
                int internalID = (Integer) ((JSONObject)chars.get(i)).get("internal_id");
                Member mb = new Member(internalID);
                if(mb.isData()) userMember.add(mb);
            }
        } catch (SQLException|DataException ex) {
            Logs.saveLog("Error get a character user info "+ this.id +" - "+ ex);
        }
        return userMember;
    }
    
    public int getGuildRank() { return this.guildRank; }
    public String getBattleTag() { return this.battleTag; }
    public boolean isCharsReady() { return this.isCharsReady; }
    private void setIsCharsReady(boolean r) { this.isCharsReady = r; } 
    
}
