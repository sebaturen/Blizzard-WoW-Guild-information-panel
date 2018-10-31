/**
 * File : Guild.java
 * Desc : Guild Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.blizzardAPI.APIInfo;
import com.artOfWar.gameObject.GameObject;
import com.artOfWar.DataException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.sql.SQLException;

public class Guild extends GameObject
{
	//Atribute
	private String name;
	private String battleGroup;
	private long lastModified;
	private long achievementPoints;
	private int level;
	private int side;
	
	//Constante
	private static final String TABLE_NAME = "guild_info";
	private static final String[] TABLE_TRUCTU = {"name","lastModified", "battlegroup", "level", "side", "achievementPoints"};
		
	//Constructor
	public Guild()
	{
		super(TABLE_NAME,TABLE_TRUCTU);
		//Load guild from DB
		loadGuildFromDB();
	}
	
	//Load to JSON
	public Guild(JSONObject guildInfo)
	{
		super(TABLE_NAME,TABLE_TRUCTU);
		saveGuildInfo(guildInfo);
	}
	
	private void saveGuildInfo(JSONObject guildInfo)
	{
		this.name = guildInfo.get("name").toString();
		this.lastModified = Long.parseLong(guildInfo.get("lastModified").toString());
		this.battleGroup = guildInfo.get("battlegroup").toString();
		this.achievementPoints = Long.parseLong(guildInfo.get("achievementPoints").toString());
		if(guildInfo.get("level").getClass() == java.lang.Long.class)
		{
			this.level = ((Long) guildInfo.get("level")).intValue();
			this.side =  ((Long) guildInfo.get("side")).intValue();
		}
		else
		{
			this.level = ((Integer) guildInfo.get("level")).intValue();	
			this.side =  ((Integer) guildInfo.get("side")).intValue();		
		}		
		this.isData = true;		
	}
	
	/**
	 * Get guild info data from DB
	 */
	private void loadGuildFromDB()
	{
		if(dbConnect == null) dbConnect = new DBConnect();	
		try
		{			
			JSONArray guildJSON = dbConnect.select(TABLE_NAME, TABLE_TRUCTU);
												
			if(guildJSON.size() > 0)
			{
				JSONObject guildInfo = (JSONObject) guildJSON.get(0);
				//Contrcutr the guild object
				saveGuildInfo(guildInfo);
			}
			else
			{//REVISAR ERROR SI LA GUILD NO EXISTE!
				System.out.println("Guild not found");	
			}
		} catch (DataException|SQLException e) {
			System.out.println("Error in Load Guild: "+ e);
		}
	}
		
	@Override
	protected boolean isOld()
	{
		Guild actualG = new Guild();
		if(actualG.isData())
		{
			return !equals(actualG);
		}
		return true;		
	}
	
	@Override
	public boolean saveInDB()
	{
		String[] values = { this.name, 
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
