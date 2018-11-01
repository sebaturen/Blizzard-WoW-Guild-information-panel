/**
 * File : Race.java
 * Desc : Race object
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

public class Race extends GameObject
{	
	//Atribute
	private int id;
	private int mask;
	private String side;
	private String name;
	
	//Constante
	private static final String TABLE_NAME = "races";
	private static final String[] TABLE_TRUCTU = {"id", "mask", "side", "name"};
	
	public Race(int id)
	{
		super(TABLE_NAME,TABLE_TRUCTU);
		loadFromDB(id+"");
	}
	
	public Race(JSONObject exInfo)
	{
		super(TABLE_NAME,TABLE_TRUCTU);
		saveInternalInfoObject(exInfo);
	}
	
	@Override
	protected void saveInternalInfoObject(JSONObject exInfo)
	{
		if(exInfo.get("id").getClass() == java.lang.Long.class) //if info come to blizzAPI or DB
		{			
			this.id = ((Long) exInfo.get("id")).intValue();
			this.mask = ((Long) exInfo.get("mask")).intValue();
		}
		else
		{
			this.id = ((Integer) exInfo.get("id")).intValue();
			this.mask = ((Integer) exInfo.get("mask")).intValue();
		}
		this.side = exInfo.get("side").toString();
		this.name = exInfo.get("name").toString();
		this.isData = true;		
	}
	
	@Override
	public boolean saveInDB()
	{
		switch (saveInDBObj(new String[] {this.id +"", this.mask +"", this.side +"", this.name}))
		{
			case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
				return true;
		}
		return false;
	}
	
	//Getters
	public int getId() { return this.id; }
	public int getMask() { return this.mask; }
	public String getSide() { return this.side; }
	public String getName() { return this.name; }
	
}