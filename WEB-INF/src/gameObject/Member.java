/**
 * File : Member.java
 * Desc : Character Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.blizzardAPI.APIInfo;
import com.artOfWar.gameObject.GameObject;
import com.artOfWar.gameObject.Race;
import com.artOfWar.gameObject.PlayableClass;
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
		loadFromDB(internalID+"");
	}
	
	//Load to JSON
	public Member(JSONObject playerInfo)
	{
		super(TABLE_NAME,TABLE_TRUCTU);
		saveInternalInfoObject(playerInfo);
	}
	
	@Override
	protected void saveInternalInfoObject(JSONObject playerInfo)
	{		
		this.internalID = ((Integer) playerInfo.get("internal_id")).intValue();
		this.name = playerInfo.get("name").toString();
		this.realm = playerInfo.get("realm").toString();
		this.battleGroup = playerInfo.get("battlegroup").toString();
		this.achievementPoints = (long) playerInfo.get("achievementPoints");
		this.thumbnail = playerInfo.get("thumbnail").toString();
		this.calcClass = (playerInfo.get("calcClass").toString()).charAt(0);
		this.lastModified = (long) playerInfo.get("lastModified");
		this.totalHonorableKills = (long) playerInfo.get("totalHonorableKills");
		
		int classID, raceID;
		if(playerInfo.get("gender").getClass() == java.lang.Long.class)
		{//if info come to blizzAPI or DB
			this.gender = ((Long) playerInfo.get("gender")).intValue();
			this.level = ((Long) playerInfo.get("level")).intValue();
			this.faction = ((Long) playerInfo.get("faction")).intValue();
			this.guildName = ((JSONObject) playerInfo.get("guild")).get("name").toString();
			classID = ((Long) playerInfo.get("class")).intValue();
			raceID = ((Long) playerInfo.get("race")).intValue();
		}
		else
		{
			this.gender = ((Integer) playerInfo.get("gender")).intValue();
			this.level = ((Integer) playerInfo.get("level")).intValue();
			this.faction = ((Integer) playerInfo.get("faction")).intValue();
			this.guildName = playerInfo.get("guild_name").toString();
			classID = ((Integer) playerInfo.get("class")).intValue();
			raceID = ((Integer) playerInfo.get("race")).intValue();
		}
		
		this.memberClass = new PlayableClass(classID);
		this.race = new Race(raceID);
		
		this.isData = true;		
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
		String[] val = new String[] { 	this.internalID +"", this.name, this.realm +"", this.lastModified +"", this.battleGroup, this.memberClass.getId() +"",
										this.race.getId() +"", this.gender +"", this.level +"", this.achievementPoints +"", this.thumbnail, this.calcClass +"", 
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
	public long getTotalHonorableKills() { return this.totalHonorableKills; }
	public boolean isData() { return this.isData; }
	
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