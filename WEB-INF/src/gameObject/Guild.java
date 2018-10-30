/**
 * File : Guild.java
 * Desc : Guild Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.blizzardAPI.APIInfo;
import com.artOfWar.DataException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.sql.SQLException;

public class Guild
{
	//Atribute
	private String name;
	private String battleGroup;
	private long lastModified;
	private long achievementPoints;
	private short level;
	private short side;
	
	//Variable
	private static DBConnect dbConnect;
	private boolean isData = false;
	
	//Constructor
	public Guild()
	{
		//Load guild from DB
		loadGuildFromDB();
	}
	
	//Load to BlizzAPI
	public Guild(String name, long lastModified, String battleGroup,
				short level, short side, long achievementPoints)
	{
		this.name = name;
		this.battleGroup = battleGroup;
		this.lastModified = lastModified;
		this.achievementPoints = achievementPoints;		
		this.level = level;
		this.side = side;
		this.isData = true;
	}
	
	//Load to JSON
	public Guild(JSONObject guildInfo)
	{
		this.name = guildInfo.get("name").toString();
		this.lastModified = (long) guildInfo.get("lastModified");
		this.battleGroup = guildInfo.get("battlegroup").toString();
		this.level = ((Long) guildInfo.get("level")).shortValue();
		this.side = ((Long) guildInfo.get("side")).shortValue();
		this.achievementPoints = (long) guildInfo.get("achievementPoints");
	}
	
	/**
	 * Get guild info data from DB
	 */
	private void loadGuildFromDB()
	{
		if(dbConnect == null) dbConnect = new DBConnect();	
		try
		{			
			JSONArray guildJSON = dbConnect.select("guild_info", 
									new String[] {"name", "lastModified", "battlegroup",
												"level", "side", "achievementPoints"});
												
			if(guildJSON.size() > 0)
			{
				JSONObject guildInfo = (JSONObject) guildJSON.get(0);
				//Contrcutr the guild object
				this.name 				= guildInfo.get("name").toString();
				this.battleGroup 		= guildInfo.get("battlegroup").toString();
				this.lastModified 		= ((Double) (guildInfo.get("lastModified"))).longValue();
				this.achievementPoints 	= ((Double) (guildInfo.get("achievementPoints"))).longValue();
				this.level 				= ((Integer) guildInfo.get("level")).shortValue();
				this.side 				= ((Integer) guildInfo.get("side")).shortValue();
				this.isData 			= true;
			}
			else
			{//REVISAR ERROR SI LA GUILD NO EXISTE!
				System.out.println("Guild not found");	
			}
		} catch (DataException|SQLException e) {
			System.out.println("Error in Load Guild: "+ e);
		}
	}
	
	public void updateInDB() throws DataException, SQLException, ClassNotFoundException
	{
		if(dbConnect == null) dbConnect = new DBConnect();		
		dbConnect.update("guild_info",
						new String[] {"lastModified", "battlegroup", "level", "side", "achievementPoints"},
						new String[] { 	this.lastModified +"",
										this.battleGroup,
										this.level +"",
										this.side +"",
										this.achievementPoints +"" });
	}
	
	public void insertInDB() throws DataException, SQLException, ClassNotFoundException
	{
		if(dbConnect == null) dbConnect = new DBConnect();
		
		dbConnect.insert("guild_info",
						new String[] {"name","lastModified", "battlegroup", "level", "side", "achievementPoints"},
						new String[] { 	this.name, 
										this.lastModified +"",
										this.battleGroup,
										this.level +"",
										this.side +"",
										this.achievementPoints +"" });
		
	}
	
	//GETTERS
	public String getName() { return this.name; }
	public String getBattleGroup() { return this.battleGroup; }
	public long getLastModified() { return this.lastModified; }
	public long getAchivementPoints() { return this.achievementPoints; }
	public short getLevel() { return this.level; }
	public short getSide() { return this.side; }
	public boolean isData() { return this.isData; }
	
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
