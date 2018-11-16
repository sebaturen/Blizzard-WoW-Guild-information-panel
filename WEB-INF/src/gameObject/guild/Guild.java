/**
 * File : Guild.java
 * Desc : Guild Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject.guild;

import com.artOfWar.dbConnect.DBStructure;
import com.artOfWar.gameObject.GameObject;

import org.json.simple.JSONObject;

public class Guild extends GameObject
{
    //Guild DB
    public static final String GUILD_TABLE_NAME = "guild_info";
    public static final String GUILD_TABLE_KEY = "id";
    public static final String[] GUILD_TABLE_STRUCTURE = {"id", "name", "realm","lastModified", "battlegroup", 
                                                        "level", "side", "achievementPoints"};
    //Attribute
    private int id;
    private String name;
    private String realm;
    private String battleGroup;
    private long lastModified;
    private long achievementPoints;
    private int level;
    private int side;
	
    //Constructor
    public Guild()
    {
        super(GUILD_TABLE_NAME, GUILD_TABLE_KEY, GUILD_TABLE_STRUCTURE);
        //Load guild from DB
        loadFromDB(1 +""); //asumed the first guild is only this guild (this plataform)
    }

    //Load to JSON
    public Guild(JSONObject guildInfo)
    {
        super(GUILD_TABLE_NAME, GUILD_TABLE_KEY, GUILD_TABLE_STRUCTURE);
        saveInternalInfoObject(guildInfo);
    }
	
    @Override
    protected void saveInternalInfoObject(JSONObject guildInfo)
    {
        this.name = guildInfo.get("name").toString();
        this.lastModified = Long.parseLong(guildInfo.get("lastModified").toString());
        this.battleGroup = guildInfo.get("battlegroup").toString();
        this.achievementPoints = Long.parseLong(guildInfo.get("achievementPoints").toString());
        this.realm = guildInfo.get("realm").toString();
        if(guildInfo.get("level").getClass() == java.lang.Long.class)
        {//if info come to blizzAPI or DB
            this.level = ((Long) guildInfo.get("level")).intValue();
            this.side =  ((Long) guildInfo.get("side")).intValue();
        }
        else
        {
            this.id = (Integer) guildInfo.get("id");
            this.level = (Integer) guildInfo.get("level");	
            this.side =  (Integer) guildInfo.get("side");		
        }		
        this.isData = true;
    }
	
    @Override
    public boolean saveInDB()
    {
        /* {"name", "realm","lastModified", "battlegroup", 
         * "level", "side", "achievementPoints"};
         */
        setTableStructur(DBStructure.outKey(GUILD_TABLE_STRUCTURE));
        String[] values = { this.name,
                            this.realm,
                            this.lastModified +"",
                            this.battleGroup,
                            this.level +"",
                            this.side +"",
                            this.achievementPoints +"" };
        switch (saveInDBObj(values))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;		
    }
	
    //GETTERS
    public String getName() { return this.name; }
    public String getBattleGroup() { return this.battleGroup; }
    public long getLastModified() { return this.lastModified; }
    public long getAchivementPoints() { return this.achievementPoints; }
    public int getLevel() { return this.level; }
    public int getSide() { return this.side; }
    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }
    @Override
    public String getId() { return this.id+""; }
    
    //two guild equals method
    @Override
    public boolean equals(Object o) 
    {
        if(o == this) return true;
        if(o == null || (this.getClass() != o.getClass())) return false;

        String oName = ((Guild) o).getName();
        long oLastModified = ((Guild) o).getLastModified();
        return (  
                oName.equals(this.name) 
                &&
                (Long.compare(oLastModified, this.lastModified) == 0)
                );
    }
	
}