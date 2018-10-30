/**
 * File : Character.java
 * Desc : Character Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;
import com.artOfWar.DataException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.sql.SQLException;

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
	
	//Variable	
	private static DBConnect dbConnect;
	private boolean isData = false;
	
	//Constructor
	public Character(int internalID)
	{
		//Load Character from DB
		loadPlayerFromDB(internalID);
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
		this.isData = true;
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
			JSONArray playerJSON = dbConnect.select("character_info", 
									new String[] {"internal_id", "name", "realm", "lastModified", "battlegroup",
												"class", "race", "gender", "level", "achievementPoints",
												"thumbnail", "calcClass", "faction", "totalHonorableKills",
												"guild_name"},
								"internal_id="+ internalID);
												
			if(playerJSON.size() > 0)
			{
				JSONObject playerInfo = (JSONObject) playerJSON.get(0);
				//Contruct a character object
				this.internalID = internalID;
				this.name = playerInfo.get("name").toString();
				this.realm = playerInfo.get("realm").toString();
				this.battleGroup = playerInfo.get("battlegroup").toString();
				this.classCode = ((Integer) playerInfo.get("class")).shortValue();
				this.race = ((Integer) playerInfo.get("race")).shortValue();
				this.gender = ((Integer) playerInfo.get("gender")).shortValue();
				this.level = ((Integer) playerInfo.get("level")).shortValue();
				this.achievementPoints = ((Double) (playerInfo.get("achievementPoints"))).longValue();
				this.thumbnail = playerInfo.get("thumbnail").toString();
				this.calcClass = (playerInfo.get("calcClass").toString()).charAt(0);
				this.faction = ((Integer) playerInfo.get("faction")).shortValue();
				this.guildName = playerInfo.get("guild_name").toString();
				this.lastModified = ((Double) (playerInfo.get("lastModified"))).longValue();
				this.totalHonorableKills = ((Integer) playerInfo.get("totalHonorableKills")).intValue();
				this.isData = true;
			}
			else
			{
				System.out.println("Character not found");	
			}			
		} catch (DataException|SQLException e) {
			System.out.println("Error in Load Char: "+ e);
		}
	}
	
	public void updateInDB() throws DataException, SQLException, ClassNotFoundException
	{//TEST
		//insert into character_info values (1,"seba","ragnaros","123",1,2,1,1,1,"asd","x",1,1,"Art of War",1);
		if(dbConnect == null) dbConnect = new DBConnect();
		
		dbConnect.update("character_info",
						new String[] {	"name", "realm", "lastModified", "battlegroup", "class", 
										"race", "gender", "level", "achievementPoints", "thumbnail", "calcClass", 
										"faction", "totalHonorableKills", "guild_name"},
						new String[] { 	this.name, this.realm +"", this.lastModified +"", this.battleGroup, this.classCode +"",
										this.race +"", this.gender +"", this.level +"", this.achievementPoints +"", this.thumbnail, this.calcClass +"", 
										this.faction +"", this.totalHonorableKills +"", this.guildName },
						"internal_id="+ this.internalID);
	}
	
	public void insertInDB() throws DataException, SQLException, ClassNotFoundException
	{//TEST
		//insert into character_info values (1,"seba","ragnaros","123",1,2,1,1,1,"asd","x",1,1,"Art of War",1);
		if(dbConnect == null) dbConnect = new DBConnect();
		
		dbConnect.insert("character_info",
						new String[] {	"internal_id", "name", "realm", "lastModified", "battlegroup", "class", 
										"race", "gender", "level", "achievementPoints", "thumbnail", "calcClass", 
										"faction", "totalHonorableKills", "guild_name"},
						new String[] { 	this.internalID +"", this.name, this.realm +"", this.lastModified +"", this.battleGroup, this.classCode +"",
										this.race +"", this.gender +"", this.level +"", this.achievementPoints +"", this.thumbnail, this.calcClass +"", 
										this.faction +"", this.totalHonorableKills +"", this.guildName });
		
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
	public boolean isData() { return this.isData; }
	
	//test...
	public void setLevel(short level) { this.level = level; }
	public void setGuildName(String nName) { this.guildName = nName; }
}