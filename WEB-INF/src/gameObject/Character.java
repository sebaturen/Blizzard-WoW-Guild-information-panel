/**
 * File : Character.java
 * Desc : Character Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import org.json.simple.JSONObject;

public class Character
{
	//Atribute
	private int internalID;
	private String name;
	private String realm;
	private String battleGroup;
	private short classCode;
	private short race;
	private short gender;
	private short level;
	private long achievementPoints;
	private String thumbnail;
	private char calcClass;
	private short faction;
	private String guildName;
	private long lastModified;
	private int totalHonorableKills;
	
	//Constructor
	public Character(int internalID)
	{
		//Load Character from DB
		this.internalID = internalID;
		loadPlayerFromDB();
	}
	
	//Load to BlizzAPI
	public Character(int internalID, String name, String realm,
				String battleGroup, short classCode, short race,
				short gender, short level, long achievementPoints,
				String thumbnail, char calcClass, short faction,
				String guildName, long lastModified, int totalHonorableKills)
	{
		this.internalID = internalID;
		this.name = name;
		this.realm = realm;
		this.battleGroup = battleGroup;
		this.classCode = classCode;
		this.race = race;
		this.gender = gender;
		this.level = level;
		this.achievementPoints = achievementPoints;
		this.thumbnail = thumbnail;
		this.calcClass = calcClass;
		this.faction = faction;
		this.guildName = guildName;
		this.lastModified = lastModified;
		this.totalHonorableKills = totalHonorableKills;
	}
	
	//Load to JSON
	public Character(int internalID, JSONObject playerInfo)
	{
		this.internalID = internalID;
		this.name = playerInfo.get("name").toString();
		this.realm = playerInfo.get("realm").toString();
		this.battleGroup = playerInfo.get("battlegroup").toString();
		this.classCode = ((Long) playerInfo.get("class")).shortValue();
		this.race = ((Long) playerInfo.get("race")).shortValue();
		this.gender = ((Long) playerInfo.get("gender")).shortValue();
		this.level = ((Long) playerInfo.get("level")).shortValue();
		this.achievementPoints = ((Long) playerInfo.get("achievementPoints")).shortValue();
		this.thumbnail = playerInfo.get("thumbnail").toString();
		this.calcClass = (playerInfo.get("calcClass").toString()).charAt(0);
		this.faction = ((Long) playerInfo.get("faction")).shortValue();
		this.guildName = ((JSONObject) playerInfo.get("guild")).get("name").toString();
		this.lastModified = (long) playerInfo.get("lastModified");
		this.totalHonorableKills = ((Long) playerInfo.get("totalHonorableKills")).intValue();
	}
	
	/**
	 * Get player info data from DB
	 */
	private void loadPlayerFromDB()
	{
		
	}
	
	public void updateInDB()
	{
		
	}
	
	public void insertInDB()
	{
		
	}
	
	//GETTERS	
	public int getInternalID() { return this.internalID; }
	public String getName() { return this.name; }
	public String getRealm() { return this.realm; }
	public String getBattleGroup() { return this.battleGroup; }
	public short getClassCode() { return this.classCode; }
	public short getRace() { return this.race; }
	public short getGender() { return this.gender; }
	public short getLevel() { return this.level; }
	public long getAchievementPoints() { return this.achievementPoints; }
	public String getThumbnail() { return this.thumbnail; }
	public char getCalcClass() { return this.calcClass; }
	public short getFaction() { return this.faction; }
	public String getGuildName() { return this.guildName; }
	public long getLastModified() { return this.lastModified; }
	public int getTotalHonorableKills() { return this.totalHonorableKills; }
}