/**
 * File : Guild.java
 * Desc : Update player and guild information
 * @author Sebastián Turen Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.blizzardAPI.APIInfo;
import com.artOfWar.DataException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.sql.SQLException;

public class Guild implements APIInfo
{
	//Atribute
	private String name;
	private String battlegroup;
	private long lastModified;
	private long achievementPoints;
	private int level;
	private int side;
	private boolean isData = false;
	
	/**
	 * If not set data, is load from DB.
	 */
	public Guild()
	{
		//Load guild from DB
		loadGuildFromDB();
	}
	
	public Guild(String name, long lastModified, String battlegroup,
				int level, int side, long achievementPoints)
	{
		this.name = name;
		this.battlegroup = battlegroup;
		this.lastModified = lastModified;
		this.achievementPoints = achievementPoints;		
		this.level = level;
		this.side = side;
		this.isData = true;
	}
	
	/**
	 * Get guild info data from DB
	 */
	private void loadGuildFromDB()
	{
		try
		{			
			DBConnect dbConnect = new DBConnect();
			JSONArray guildJSON = dbConnect.select("guild_info", 
									new String[] {"name", "lastModified", "battlegroup",
												"level", "side", "achievementPoints"});
												
			if(guildJSON.size() > 0)
			{
				//Contrcutr the guild object
				this.name 				= ((JSONObject) guildJSON.get(0)).get("name").toString();
				this.battlegroup 		= ((JSONObject) guildJSON.get(0)).get("battlegroup").toString();
				this.lastModified 		= ((Double) (((JSONObject) guildJSON.get(0)).get("lastModified"))).longValue();
				this.achievementPoints 	= ((Double) (((JSONObject) guildJSON.get(0)).get("achievementPoints"))).longValue();
				this.level 				= (int) (((JSONObject) guildJSON.get(0)).get("level"));
				this.side 				= (int) (((JSONObject) guildJSON.get(0)).get("side"));	
				this.isData 			= true;
			}
			else
			{
				System.out.println("Guild not found");	
			}
		} catch (DataException|SQLException e) {
			System.out.println("Error in Load Guild: "+ e);
		}
	}
	
	//GETTERS
	public String getName() { return this.name; }
	public String getBattlegroup() { return this.battlegroup; }
	public long getLastModified() { return this.lastModified; }
	public long getAchivementPoints() { return this.achievementPoints; }
	public int getLevel() { return this.level; }
	public int getSide() { return this.side; }
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
