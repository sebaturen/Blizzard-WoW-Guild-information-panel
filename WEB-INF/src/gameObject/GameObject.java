/**
 * File : GameObject.java
 * Desc : General abstract class to game object elements
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.sql.SQLException;

public abstract class GameObject
{
	//Variable
	protected static DBConnect dbConnect;
	protected boolean isData = false;
	private String tableDB;
	private String[] tableStruct;
	
	//Constant save data info
	public static final int SAVE_MSG_NO_DATA 					= 0;
	public static final int SAVE_MSG_INSERT_ERROR 				= 1;
	public static final int SAVE_MSG_INSERT_OK 					= 2;
	public static final int SAVE_MSG_UPDATE_OK 					= 3;
	public static final int SAVE_MSG_UPDATE_FOREIGN_KEY_ERROR 	= 4;
	public static final int SAVE_MSG_UPDATE_ERROR				= 5;
	public static final int SAVE_MSG_NOT_UPDATE_OK				= 6;
	public static final int SAVE_MSG_SQL_INSERT_ERROR			= 7;
	
	public GameObject(String tableDB, String[] tableStruct) 
	{ 
		this.tableDB = tableDB; 
		this.tableStruct = tableStruct;
	}
	
	//Abstract
	protected abstract void saveInternalInfoObject(JSONObject guildInfo);
	protected abstract boolean isOld();
	public abstract boolean saveInDB();
	
	//Generic function
	/**
	 * Save Game object element in DB
	 * @values values from object we need save, use in query.
	 */
	protected int saveInDBObj(String[] values)
	{
		if(dbConnect == null) dbConnect = new DBConnect();
		if (this.isData && values.length > 0)
		{
			//need Update or Insert?
			boolean updateOrInsert = false;
			try
			{
				JSONArray exist = dbConnect.select(
												this.tableDB, 
												this.tableStruct,
												this.tableStruct[0]+"=\""+values[0]+"\"" );	
				if(exist.size() > 0) updateOrInsert = true;
			}
			catch (SQLException|DataException er)
			{
				System.out.println("Error wen try get a last modified old character: "+ er);
			}
			//If updateOrInsert is tru, we need update, if is false, insert
			if(updateOrInsert)
			{
				if(isOld())
				{
					//Update
					try
					{
						updateInDB(removeFirstElement(values));
						return SAVE_MSG_UPDATE_OK;
					}
					catch (SQLException e)
					{
						if(e.getErrorCode() == DBConnect.ERROR_FOREIGN_KEY || 
							e.getErrorCode() == DBConnect.ERROR_NULL_ELEMENT)
						{
							return SAVE_MSG_UPDATE_FOREIGN_KEY_ERROR;
						}
						return SAVE_MSG_SQL_INSERT_ERROR;						
					}
					catch (DataException|ClassNotFoundException e)
					{
						System.out.println("Error Other when try uplote a race information: "+ e);
						return SAVE_MSG_UPDATE_ERROR;
					}					
				}
				return SAVE_MSG_NOT_UPDATE_OK;
			}
			else
			{
				//Insert
				try
				{
					insertInDB(values);
					return SAVE_MSG_INSERT_OK;
				}
				catch (SQLException e)
				{
					if(e.getErrorCode() == DBConnect.ERROR_FOREIGN_KEY || 
						e.getErrorCode() == DBConnect.ERROR_NULL_ELEMENT)
					{
						return SAVE_MSG_UPDATE_FOREIGN_KEY_ERROR;
					}
					return SAVE_MSG_SQL_INSERT_ERROR;
				}
				catch (DataException|ClassNotFoundException e)
				{
					System.out.println("Error to insert race "+ values.toString() +": "+ e);
					return SAVE_MSG_INSERT_ERROR;
				}
			}
		}
		return SAVE_MSG_NO_DATA;
	}
	
	/**
	 * Get a content from DB to load in object
	 * @id element identifier (primary key!)
	 */
	protected void loadFromDB(String id)
	{
		if(dbConnect == null) dbConnect = new DBConnect();
		try
		{		
			JSONArray dbSelect = dbConnect.select(this.tableDB, this.tableStruct ,this.tableStruct[0] +"=\""+ id +"\"");
												
			if(dbSelect.size() > 0)
			{
				JSONObject infoDB = (JSONObject) dbSelect.get(0);
				//Contruct a character object
				saveInternalInfoObject(infoDB);
			}
			else
			{
				System.out.println("Element not found");	
			}			
		} catch (DataException|SQLException e) {
			System.out.println("Error in Load element: "+ e);
		}
	}
	
	/**
	 * Update a object in DB
	 * @values values for query
	 */
	private void updateInDB(String[] values) throws DataException, SQLException, ClassNotFoundException
	{
		if(dbConnect == null) dbConnect = new DBConnect();		
		dbConnect.update(this.tableDB, removeFirstElement(this.tableStruct), values, this.tableStruct[0] +"=\""+ values[0] +"\"");
	}

	/**
	 * Inser a object in DB
	 * @values values for query
	 */
	private void insertInDB(String[] values) throws DataException, SQLException, ClassNotFoundException
	{
		if(dbConnect == null) dbConnect = new DBConnect();
		dbConnect.insert(this.tableDB, this.tableStruct, values);
	}
	
	/**
	 * Remove a first element in array.
	 * The idea is remove key in table array, when this object is load, save a
	 * table structure, and when we need insert, use all values, but when need update, put all except the key.
	 * @arrayElem array that wants to delete the first value 
	 */
	private String[] removeFirstElement(String[] arryElem)
	{
		String[] columnNotKey = new String[arryElem.length-1];
		for(int i = 0; i < columnNotKey.length; i++)
		{
			columnNotKey[i] = arryElem[i+1];
		}
		return columnNotKey;
	}
	
	//Get/Set method
	public boolean isData() { return this.isData; }
	
}