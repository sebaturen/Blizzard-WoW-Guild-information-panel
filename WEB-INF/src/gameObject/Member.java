/**
 * File : Member.java
 * Desc : Character Object
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

public class Member extends GameObject
{
	//Atribute
	private int internalID;
	private String name;
	private String realm;
	private String battleGroup;
	private int classCode;
	private int race;
	private int gender;
	private int level;
	private long achievementPoints;
	private String thumbnail;
	private char calcClass;
	private int faction;
	private String guildName;
	private long lastModified;
	private int totalHonorableKills;
	
	//Constante
	private static final String TABLE_NAME = "character_info";
	private static final String[] TABLE_TRUCTU = {"internal_id", "name", "realm", "lastModified", "battlegroup", "class", 
										"race", "gender", "level", "achievementPoints", "thumbnail", "calcClass", 
										"faction", "totalHonorableKills", "guild_name"};
	
	//Constructor load from DB if have a ID
	public Member(int internalID)
	{
		super(TABLE_NAME,TABLE_TRUCTU);
		//Load Character from DB
		loadPlayerFromDB(internalID);
	}
	
	//Load to JSON
	public Member(JSONObject playerInfo)
	{
		super(TABLE_NAME,TABLE_TRUCTU);
		this.internalID = ((Long) playerInfo.get("internalID")).intValue();
		saveInfoMember(playerInfo);
	}
	
	//Load to JSON exept id
	public Member(int internalID, JSONObject playerInfo)
	{
		super(TABLE_NAME,TABLE_TRUCTU);
		this.internalID = internalID;
		saveInfoMember(playerInfo);
	}
	
	private void saveInfoMember(JSONObject playerInfo)
	{
		this.name = playerInfo.get("name").toString();
		this.realm = playerInfo.get("realm").toString();
		this.battleGroup = playerInfo.get("battlegroup").toString();
		this.classCode = ((Long) playerInfo.get("class")).shortValue();
		this.race = ((Long) playerInfo.get("race")).shortValue();
		this.gender = ((Long) playerInfo.get("gender")).shortValue();
		this.level = ((Long) playerInfo.get("level")).shortValue();
		this.achievementPoints = (long) playerInfo.get("achievementPoints");
		this.thumbnail = playerInfo.get("thumbnail").toString();
		this.calcClass = (playerInfo.get("calcClass").toString()).charAt(0);
		this.faction = ((Long) playerInfo.get("faction")).shortValue();
		this.guildName = ((JSONObject) playerInfo.get("guild")).get("name").toString();
		this.lastModified = (long) playerInfo.get("lastModified");
		this.totalHonorableKills = ((Long) playerInfo.get("totalHonorableKills")).intValue();
		this.isData = true;		
	}
	
	/**
	 * Get player info data from DB
	 */
	private void loadPlayerFromDB(int internalID)
	{
		if(dbConnect == null) dbConnect = new DBConnect();
		try
		{		
			JSONArray playerJSON = dbConnect.select(TABLE_NAME, TABLE_TRUCTU,"internal_id="+ internalID);
												
			if(playerJSON.size() > 0)
			{
				JSONObject playerInfo = (JSONObject) playerJSON.get(0);
				//Contruct a character object
				this.internalID = internalID;
				saveInfoMember(playerInfo);
			}
			else
			{
				System.out.println("Character not found");	
			}			
		} catch (DataException|SQLException e) {
			System.out.println("Error in Load Char: "+ e);
		}
	}
	
	@Override
	protected boolean isOld()
	{
		Member oldMeber = new Member(this.internalID);
		if(oldMeber.isData())
		{
			return !equals(oldMeber);
		}
		return true;
	}
	
	@Override
	public boolean saveInDB()
	{
		String[] val = new String[] { 	this.internalID +"", this.name, this.realm +"", this.lastModified +"", this.battleGroup, this.classCode +"",
										this.race +"", this.gender +"", this.level +"", this.achievementPoints +"", this.thumbnail, this.calcClass +"", 
										this.faction +"", this.totalHonorableKills +"", this.guildName };
		switch (saveInDBObj(val))
		{
			case SAVE_MSG_UPDATE_FOREIGN_KEY_ERROR:
				try
				{//delte player in character_info and gMembers_id_name becouse this character not in the guild NOW
					System.out.println("Character change guild");
					dbConnect.delete("character_info","internal_id="+ this.internalID);
					dbConnect.delete("gMembers_id_name","internal_id="+ this.internalID);
					return true;
				}
				catch (SQLException|DataException ex)
				{
					System.out.println("Error when try remove a user not in guild: "+ ex);
					return false;
				}
			case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
				return true;
		}
		return false;
	}
	
	//GETTERS	
	public int getInternalID() { return this.internalID; }
	public String getName() { return this.name; }
	public String getRealm() { return this.realm; }
	public String getBattleGroup() { return this.battleGroup; }
	public int getClassCode() { return this.classCode; }
	public int getRace() { return this.race; }
	public int getGender() { return this.gender; }
	public int getLevel() { return this.level; }
	public long getAchievementPoints() { return this.achievementPoints; }
	public String getThumbnail() { return this.thumbnail; }
	public char getCalcClass() { return this.calcClass; }
	public int getFaction() { return this.faction; }
	public String getGuildName() { return this.guildName; }
	public long getLastModified() { return this.lastModified; }
	public int getTotalHonorableKills() { return this.totalHonorableKills; }
	public boolean isData() { return this.isData; }
	
	//two members equals method
	@Override
	public boolean equals(Object o) 
	{
		if(o == this) return true;
		if(o == null || (this.getClass() != o.getClass())) return false;
		
		int oId = ((Member) o).getInternalID();
		long oLastModified = ((Guild) o).getLastModified();
		return (  
					oId == this.internalID
					&&
					(Long.compare(oLastModified, this.lastModified) == 0)
				);
	}
}