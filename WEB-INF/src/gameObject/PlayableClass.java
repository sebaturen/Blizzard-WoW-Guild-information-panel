/**
 * File : PlayableClass.java
 * Desc : Playable class object
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

public class PlayableClass extends GameObject
{	
	//Atribute
	private int id;
	private String enName;
	
	//Constante
	private static final String TABLE_NAME = "playable_class";
	private static final String[] TABLE_TRUCTU = {"id", "en_US"};
		
	public PlayableClass(int id)
	{
		super(TABLE_NAME,TABLE_TRUCTU);
		loadFromDB(id+"");
	}
	
	public PlayableClass(JSONObject exInfo)
	{
		super(TABLE_NAME,TABLE_TRUCTU);
		saveInternalInfoObject(exInfo);
	}
	
	@Override
	protected void saveInternalInfoObject(JSONObject exInfo)
	{		
		this.id = ((Integer) exInfo.get("id")).intValue();
		if(exInfo.containsKey("name")) //if info come to blizzAPI or DB
		{
			this.enName = ((JSONObject) exInfo.get("name")).get("en_US").toString();
		}
		else
		{
			this.enName = exInfo.get("en_US").toString();
		}
		
		this.isData = true;
	}
	
	@Override
	protected boolean isOld()
	{
		//For the amount of data that this object uses, consulting and 
		//validating is more expensive than just updating
		return true;
	}
	
	@Override
	public boolean saveInDB()
	{
		switch (saveInDBObj(new String[] {this.id +"", this.enName}))
		{
			case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
				return true;
		}
		return false;
	}	
	
	//Getters
	public int getId() { return this.id; }
	public String getEnName() { return this.enName; }
	public boolean isData() { return this.isData; }
	
}