/**
 * File : Member.java
 * Desc : Character Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.blizzardAPI.APIInfo;
import com.artOfWar.blizzardAPI.Update;
import com.artOfWar.DataException;

import org.json.simple.JSONObject;
import java.sql.SQLException;
import java.util.Date;

public class Member extends GameObject
{
    //Attribute
    private int internalID;
    private String name;
    private String realm;
    private String battleGroup;
    private PlayableClass memberClass;
    private Race race;
    private int gender;
    private int level;
    private long achievementPoints;
    private String thumbnail;
    private char calcClass;
    private int faction;
    private String guildName;
    private long lastModified;
    private long totalHonorableKills;
    private String specName;
    private String specRole;
    private boolean isGuildMember;

    //Constant
    private static final String TABLE_NAME = "character_info";
    private static final String[] TABLE_STRUCTURE = {"internal_id", "lastModified", "battlegroup", "class", 
                                                    "race", "gender", "level", "achievementPoints", "thumbnail", "calcClass", 
                                                    "faction", "totalHonorableKills", "guild_name"};

    private static final String COMBIEN_TABLE_NAME = TABLE_NAME +" c, "+Update.GMEMBERS_ID_TABLE +" gm";
    private static final String[] COMBIEN_TABLE_STRUCTURE = {"c.internal_id", "gm.realm", "c.lastModified", "c.battlegroup", "c.class", 
                                                            "c.race", "c.gender", "c.level", "c.achievementPoints", "c.thumbnail", "c.calcClass", 
                                                            "c.faction", "c.totalHonorableKills", "c.guild_name", "gm.member_name", "gm.in_guild"};
	
    //Constructor load from DB if have a ID
    public Member(int internalID)
    {
        super(COMBIEN_TABLE_NAME,COMBIEN_TABLE_STRUCTURE);
        //Load Character from DB
        loadFromDB(internalID+"", "gm.internal_id = c.internal_id");
    }

    //Load to JSON
    public Member(JSONObject playerInfo)
    {
        super(TABLE_NAME,TABLE_STRUCTURE);
        saveInternalInfoObject(playerInfo);
    }
	
    @Override
    protected void saveInternalInfoObject(JSONObject playerInfo)
    {		
        this.internalID = (Integer) playerInfo.get("internal_id");
        this.realm = playerInfo.get("realm").toString();
        this.battleGroup = playerInfo.get("battlegroup").toString();
        this.achievementPoints = (long) playerInfo.get("achievementPoints");
        this.thumbnail = playerInfo.get("thumbnail").toString();
        this.calcClass = (playerInfo.get("calcClass").toString()).charAt(0);
        this.lastModified = (long) playerInfo.get("lastModified");
        this.totalHonorableKills = (long) playerInfo.get("totalHonorableKills");

        int classID, raceID;
        if(playerInfo.get("gender").getClass() == java.lang.Long.class)
        {//if info come to blizzAPI
            this.name = playerInfo.get("name").toString();
            this.gender = ((Long) playerInfo.get("gender")).intValue();
            this.level = ((Long) playerInfo.get("level")).intValue();
            this.faction = ((Long) playerInfo.get("faction")).intValue();
            classID = ((Long) playerInfo.get("class")).intValue();
            raceID = ((Long) playerInfo.get("race")).intValue();
            //If have a guild...
            this.guildName = "";
            this.isGuildMember = false;
            if(playerInfo.containsKey("guild"))	this.guildName = ((JSONObject) playerInfo.get("guild")).get("name").toString();
            if( this.guildName.length() > 0 && this.guildName.equals(APIInfo.GUILD_NAME)) this.isGuildMember = true;
        }
        else
        {//if come to DB
            this.name = playerInfo.get("member_name").toString();
            this.gender = (Integer) playerInfo.get("gender");
            this.level = (Integer) playerInfo.get("level");
            this.faction = (Integer) playerInfo.get("faction");
            this.guildName = playerInfo.get("guild_name").toString();
            classID = (Integer) playerInfo.get("class");
            raceID = (Integer) playerInfo.get("race");
            this.isGuildMember = (Boolean) playerInfo.get("in_guild");
        }

        this.memberClass = new PlayableClass(classID);
        this.race = new Race(raceID);

        this.isData = true;	
    }
	
    @Override
    public boolean saveInDB()
    {
        String[] val = new String[] {this.internalID +"", this.lastModified +"", this.battleGroup, this.memberClass.getId() +"",
                                    this.race.getId() +"", this.gender +"", this.level +"", this.achievementPoints +"", this.thumbnail, this.calcClass +"", 
                                    this.faction +"", this.totalHonorableKills +"", this.guildName };
        //Valid if have a data this object, and guild is null (if we try update, and put null in query, the DB not update this column, for this use this IF)
        if(this.isData)
        {
            if (!this.guildName.equals(APIInfo.GUILD_NAME)) deleteFromDB(); //prevent save in guild/internalID members table if not is a guild member
            int vSave = saveInDBObj(val);
            return ((vSave == SAVE_MSG_INSERT_OK) || (vSave == SAVE_MSG_UPDATE_OK));
        }
        return false;
    }

    /**
    * Delete from DB
    */
    private boolean deleteFromDB()
    {
        try
        {//change player in character_info in_guild because is change
            System.out.println("Character "+ this.name +" change guild");
            dbConnect.update(com.artOfWar.blizzardAPI.Update.GMEMBERS_ID_TABLE,
                            new String[] {"in_guild", "rank"},
                            new String[] {"0", "0"},
                            "internal_id="+ this.internalID);
            return true;
        }
        catch (SQLException|DataException|ClassNotFoundException ex)
        {
            System.out.println("Error when try remove a user not in guild: "+ ex);
            return false;
        }				
    }
	
    //GETTERS	
    public int getInternalID() { return this.internalID; }
    public String getName() { return this.name; }
    public String getRealm() { return this.realm; }
    public String getBattleGroup() { return this.battleGroup; }
    public PlayableClass getmemberClass() { return this.memberClass; }
    public Race getRace() { return this.race; }
    public int getGender() { return this.gender; }
    public int getLevel() { return this.level; }
    public long getAchievementPoints() { return this.achievementPoints; }
    public String getThumbnail() { return this.thumbnail; }
    public char getCalcClass() { return this.calcClass; }
    public int getFaction() { return this.faction; }
    public String getGuildName() { return this.guildName; }
    public long getLastModified() { return this.lastModified; }
    public Date getLastModifiedDate() {
        //All lastModified in blizzard API is added 3 cero more...
        String val = this.lastModified+"";
        val = val.substring(0, val.length()-3);
        return new Date((Long.parseLong(val))*1000); 
    }
    public long getTotalHonorableKills() { return this.totalHonorableKills; }
    public String getSpecName() { return this.specName; }
    public String getSpecRole() { return this.specRole; }

    //Setters
    public void setSpecName(String sName) { this.specName = sName; }
    public void setSpecRole(String sRole) { this.specRole = sRole; }
    @Override
    public void setId(String id) { this.internalID = Integer.parseInt(id); }

    //two members equals method
    @Override
    public boolean equals(Object o) 
    {
        if(o == this) return true;
        if(o == null || (this.getClass() != o.getClass())) return false;

        int oId = ((Member) o).getInternalID();
        long oLastModified = ((Member) o).getLastModified();
        return (  
                oId == this.internalID
                &&
                (Long.compare(oLastModified, this.lastModified) == 0)
                );
    }
}