/**
 * File : User.java
 * Desc : User controller~
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel;

import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User 
{
    //User
    public static final String USER_TABLE_NAME = "users";
    public static final String USER_TABLE_KEY = "id";
    public static final String[] USER_TABLE_STRUCTURE = {"id", "battle_tag", "access_token", "discord_user_id", "guild_rank", 
                                                        "main_character", "wowinfo", "last_login", "last_alters_update"};
    
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
    private String discordUserId;
    private List<CharacterMember> characters = new ArrayList<>();
    private CharacterMember mainCharacter;
    
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
    
    public User(String accesToken) throws DataException
    {
        loadFromDBAccesToken(accesToken);
    }
    
    private void loadUser(JsonObject userInfo)
    {
        this.id = userInfo.get("id").getAsInt();
        this.battleTag = userInfo.get("battle_tag").getAsString();
        if(this.accessToken == null) //not load old AccToken if new exist
            this.accessToken = userInfo.get("access_token").getAsString();
        this.guildRank = userInfo.get("guild_rank").getAsInt();
        this.isLogin = true;
        if(!userInfo.get("main_character").isJsonNull())
            this.idMainChar = userInfo.get("main_character").getAsInt();
        this.isCharsReady = true;
        if(userInfo.has("last_login") && !userInfo.get("last_login").isJsonNull())
            this.lastLogin = userInfo.get("last_login").getAsString();
        if(userInfo.has("last_login") && !userInfo.get("last_alters_update").isJsonNull())
            this.lastAlterUpdate = userInfo.get("last_alters_update").getAsString();
        if(userInfo.has("last_login") && !userInfo.get("discord_user_id").isJsonNull())
            this.discordUserId = userInfo.get("discord_user_id").getAsString();
    }
    
    private void loadFromDB(int id)
    {
        try {
            JsonArray info = dbConnect.select(USER_TABLE_NAME,
                    USER_TABLE_STRUCTURE,
                    "id=?",
                    new String [] {id+""});
            if(info.size() > 0)
            {
                JsonObject userInfo = info.get(0).getAsJsonObject();
                loadUser(userInfo);
            }
            else
            {
                Logs.errorLog(User.class, "Fail to load user from ID "+ id +" - Data not found");
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(User.class, "Fail to load user from ID "+ id +" - "+ ex);
        }        
    }
    
    private void loadFromDBAccesToken(String acToken) throws DataException
    {
        try {
            JsonArray info = dbConnect.select(USER_TABLE_NAME,
                    USER_TABLE_STRUCTURE,
                    "access_token=?",
                    new String [] {acToken});
            if(info.size() > 0)
            {
                JsonObject userInfo = info.get(0).getAsJsonObject();
                loadUser(userInfo);
            }
            else
            {
                throw new DataException("Fail to load user from token, data not found "+ acToken);
            }
        } catch (SQLException ex) {
            Logs.errorLog(User.class, "Fail to load user from token '"+ acToken +"' - "+ ex);
        }  
        
    }
    
    public boolean checkUser() { return checkUser(false); }
    private boolean checkUser(boolean forceCheck)
    {
        if(this.battleTag == null) return false;
        if(this.isLogin && !forceCheck) return this.isLogin;
        try 
        {
            JsonArray validUser = dbConnect.select(
                    User.USER_TABLE_NAME,
                    USER_TABLE_STRUCTURE,
                    "battle_tag=?",
                    new String[] {this.battleTag});
            if(validUser.size() > 0)
            {
                JsonObject infoUser = validUser.get(0).getAsJsonObject();
                loadUser(infoUser);
                return true;
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(User.class, "Fail to login "+ this.battleTag +" - "+ ex);
        }
        return false;
    }
    
    public boolean setDiscordUserId(String discUserId)
    {
        if(checkUser() && this.discordUserId == null) //Valid if account exit in DB
        {//exist...   
            try {
                this.discordUserId = discUserId;
                dbConnect.update(
                        User.USER_TABLE_NAME,
                        new String[] {"discord_user_id"},
                        new String[] {this.discordUserId},
                        "id=?",
                        new String[] {this.id +""});
                return true;
            } catch (DataException | SQLException ex) {
                Logs.errorLog(User.class, "Fail to save discord user id - "+ ex +" user id: "+ this.id);
                return false;
            }   
        }
        Logs.errorLog(User.class, "User not is instance");
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
            } catch (DataException | SQLException ex) {
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
            } catch (DataException | SQLException ex) {
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
            public void run() {
                try {
                    Update.shared.setMemberCharacterInfo(accToken, uId);
                } catch (IOException ex) {
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
        return Update.shared.getUserAccessToken(code);
    }
    
    private String getBlizzBattleTag(String accessToken)
    {
        return Update.shared.getBattleTag(accessToken);
    }
    
    private void loadMainCharFromDB()
    {
        this.mainCharacter = new CharacterMember(this.idMainChar);
    }
    
    private void loadCharacters()
    {
        try {
            JsonArray chars = dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME +" gm, "+ CharacterMember.CHARACTER_INFO_TABLE_NAME +" c",
                    new String[] {"gm.internal_id" },
                    "gm.user_id=? AND gm.internal_id = c.internal_id AND c.lastModified != 0 AND"+
                    " gm.isDelete = 0 ORDER BY c.level DESC, gm.member_name ASC",
                    new String[] { this.id +""}, true);
            for(int i = 0; i < chars.size(); i++)
            {
                int internalID = chars.get(i).getAsJsonObject().get("internal_id").getAsInt();
                CharacterMember mb = new CharacterMember(internalID);
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
    public String getAccessToken() { return this.accessToken; }
    public String getDiscordUserId() { return this.discordUserId; }
    public List<CharacterMember> getCharacters() { if(this.characters.isEmpty()) loadCharacters(); return this.characters; }
    public boolean isCharsReady() { return this.isCharsReady; }
    public CharacterMember getMainCharacter() 
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
        for(CharacterMember m : this.characters) 
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
                } catch (DataException | SQLException ex) {
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
