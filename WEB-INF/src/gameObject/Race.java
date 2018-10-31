/**
 * File : Race.java
 * Desc : Race object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.sql.SQLException;

public class Race
{	
	//Atribute
	private short id;
	private int mask;
	private String side;
	private String name;
	
	//Variable	
	private static DBConnect dbConnect;
	private boolean isData;
	
	public Race(short id)
	{
		//LOAD FROM DB
	}
	
	public Race(JSONObject exInfo)
	{
		this.id = ((Long) exInfo.get("id")).shortValue();
		this.mask = ((Long) exInfo.get("mask")).intValue();
		this.side = exInfo.get("side").toString();
		this.name = exInfo.get("name").toString();
		this.isData = true;
	}
	
	public boolean saveInDB()
	{
		if(dbConnect == null) dbConnect = new DBConnect();
		boolean resultSave = false;
		if (isData)
		{
			boolean haveOtherData = false;
			
			//exits preview data?
			try
			{
				JSONArray lastModifiedOldRace = dbConnect.select("races",
												new String[] {"id"},
												"id="+this.id );	
				if(lastModifiedOldRace.size() > 0)	haveOtherData = true;
			}
			catch (SQLException|DataException er)
			{
				System.out.println("Error wen try get a old race: "+ er);
			}
			
			if(haveOtherData)
			{			
				try
				{
					updateInDB();
					resultSave = true;
				}
				catch (DataException|ClassNotFoundException|SQLException e)
				{
					System.out.println("Error Other when try uplote a race information: "+ e);
				}
			}
			else
			{
				try
				{
					insertInDB();
					resultSave = true;
				}
				catch (SQLException|DataException|ClassNotFoundException e)
				{
					System.out.println("Error to insert race "+ this.name +": "+ e);
				}
			}
		}
		return resultSave;
	}
	
	private void updateInDB() throws DataException, SQLException, ClassNotFoundException
	{
		if(dbConnect == null) dbConnect = new DBConnect();
		
		dbConnect.update("races",
						new String[] {	"mask", "side", "name"},
						new String[] { 	this.mask +"", this.side +"", this.name},
						"id="+ this.id);	
	}
	
	private void insertInDB() throws DataException, SQLException, ClassNotFoundException
	{
		if(dbConnect == null) dbConnect = new DBConnect();
		
		dbConnect.insert("races",
						new String[] {	"id", "mask", "side", "name"},
						new String[] { 	this.id +"", this.mask +"", this.side +"", this.name});
	}
	
	//Getters
	public int getId() { return this.id; }
	public int getMask() { return this.mask; }
	public String getSide() { return this.side; }
	public String getName() { return this.name; }
	public boolean isData() { return this.isData; }
	
}