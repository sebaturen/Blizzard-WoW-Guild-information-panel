/**
 * File : PlayableClass.java
 * Desc : Playable class object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.sql.SQLException;

public class PlayableClass
{	
	//Atribute
	private short id;
	private String enName;
	
	//Variable	
	private static DBConnect dbConnect;
	private boolean isData;
	
	public PlayableClass(short id)
	{
		//LOAD FROM DB
	}
	
	public PlayableClass(JSONObject exInfo)
	{
		this.id = ((Long) exInfo.get("id")).shortValue();
		this.enName = ((JSONObject) exInfo.get("name")).get("en_US").toString();
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
				JSONArray lastModifiedOldPlayClass = dbConnect.select("playable_class",
												new String[] {"id"},
												"id="+this.id );	
				if(lastModifiedOldPlayClass.size() > 0)	haveOtherData = true;
			}
			catch (SQLException|DataException er)
			{
				System.out.println("Error wen try get a old playable class: "+ er);
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
					System.out.println("Error Other when try uplote a playable class information: "+ e);
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
					System.out.println("Error to insert playable class "+ this.enName +": "+ e);
				}
			}
		}
		return resultSave;
	}	
	
	private void updateInDB() throws DataException, SQLException, ClassNotFoundException
	{
		if(dbConnect == null) dbConnect = new DBConnect();
		
		dbConnect.update("playable_class",
						new String[] {	"en_US"},
						new String[] { 	this.enName },
						"id="+ this.id);	
	}
	
	private void insertInDB() throws DataException, SQLException, ClassNotFoundException
	{
		if(dbConnect == null) dbConnect = new DBConnect();
		
		dbConnect.insert("playable_class",
						new String[] {	"id", "en_US"},
						new String[] { 	this.id +"", this.enName});
	}
	
	//Getters
	public int getId() { return this.id; }
	public String getEnName() { return this.enName; }
	public boolean isData() { return this.isData; }
	
}