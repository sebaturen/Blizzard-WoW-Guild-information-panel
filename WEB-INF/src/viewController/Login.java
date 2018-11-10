/**
 * File : Login.java
 * Desc : Login.jsp login controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.DataException;
import com.artOfWar.blizzardAPI.APIInfo;
import com.artOfWar.blizzardAPI.Update;
import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.dbConnect.DBStructure;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Login implements APIInfo
{
    private final DBConnect dbConnect;
    
    private int id;
    private String email;
    private String password;
    private String battleTag;
    private String memberAccesToken;
    private boolean wowInfo = false;
    private int guildRank = -1;
    
    public Login()
    {
        this.dbConnect = new DBConnect();
    }
    
    public boolean checkUser() 
    {
        if(this.email == null || this.password == null) return false;
        try {            
            //{"email", "password", "battle_tag", "access_token", "guild_rank"};
            JSONArray validUser = dbConnect.select(DBStructure.USER_TABLE_NAME,
                                                DBStructure.USER_TABLE_STRUCTURE,
                                                "email=? AND password=?",
                                                new String[] {this.email, Register.encodePass(this.password)});
            if(validUser.size() > 0) 
            {//valid if have a info
                JSONObject infoUser = (JSONObject) validUser.get(0);
                if ((infoUser.get("email").toString()).equals(this.email))
                {//valid the return is same email info
                    this.id =  (Integer) infoUser.get("id");
                    this.guildRank = (Integer) infoUser.get("guild_rank");
                    if(infoUser.get("battle_tag") != null)
                    {
                        this.memberAccesToken = infoUser.get("access_token").toString();
                        this.battleTag = infoUser.get("battle_tag").toString();
                        this.wowInfo = (Boolean) infoUser.get("wowinfo");
                    }
                    return true;
                }
            }
        } catch (SQLException | DataException ex) {
            System.out.println("Fail to get user info..."+ ex);
        }
        return false;
    }
    
    private boolean saveBlizzardInfo(String code)
    {
        String accessToken = getAccessToken(code);
        String bTag = getBattleTag(accessToken);
        try {
            dbConnect.update(DBStructure.USER_TABLE_NAME,
                            new String[] {"battle_tag", "access_token"},
                            new String[] { bTag, accessToken },
                            "id=?",
                            new String[] { this.id +"" });
            this.battleTag = bTag;
            this.memberAccesToken = accessToken;
                  
            //Try get a member rank...
            try {
                Update up = new Update();
                up.setMemberCharacterInfo(this.memberAccesToken, this.id);
                checkUser();
            } catch (IOException | ParseException ex) {
                System.out.println("Fail to seve characters info "+ this.id +" - "+ ex);
            }            
            return true;
        } catch (DataException | ClassNotFoundException ex) {
            System.out.println("Fail to save code from user "+ this.email +" - "+ ex);
        }
        return false;
    }
    
    public JSONArray getCharacterList()
    {
        try {
            JSONArray chars = dbConnect.select(DBStructure.GMEMBER_ID_NAME_TABLE_NAME,
                    new String[] {"member_name", "realm", "rank"},
                    "user_id=? order by realm",
                    new String[] { this.id +"" } );
            if(chars.size() > 0)
            {
                return chars;
            }
        } catch (SQLException|DataException ex) {
            System.out.println("Error get a character user info "+ this.id +" - "+ ex);
        }
        return null;
    }
    
    
    private String getBattleTag(String accessToken)
    {
        try {
            //Generate an API URL
            String urlString = String.format(APIInfo.API_OAUTH_URL, SERVER_LOCATION, API_OAUTH_USERINFO);
            
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
            System.out.println("Fail to get BattleTag "+ ex);
        }
        return null;
    }
    
    private String getAccessToken(String code)
    {
        try {
            String urlString = String.format(API_OAUTH_URL, SERVER_LOCATION, API_OAUTH_TOKEN);
            String apiInfo = Base64.getEncoder().encodeToString((CLIENT_ID+":"+CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));
         
            String redirectUrl = URLEncoder.encode(MAIN_URL+BLIZZAR_LINK, "UTF-8");
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
        } catch (IOException|ParseException|DataException ex) {
            System.out.println("Fail to get user Access Token "+ ex);
        }
        return null;
    }
    
 
    
    //Getters and Setters
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public boolean setAccessCode(String code) { return saveBlizzardInfo(code); }
    public String getEmail() { return this.email; }
    public String getBattleTag() { return this.battleTag; }
    public boolean getWowInfo() { return this.wowInfo; }
    public int getGuildRank() { return this.guildRank; }
}
