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
    public static final int SAVE_MSG_NO_DATA                    = 0;
    public static final int SAVE_MSG_INSERT_ERROR               = 1;
    public static final int SAVE_MSG_INSERT_OK                  = 2;
    public static final int SAVE_MSG_UPDATE_OK                  = 3;
    public static final int SAVE_MSG_UPDATE_FOREIGN_KEY_ERROR   = 4;
    public static final int SAVE_MSG_UPDATE_ERROR               = 5;
    public static final int SAVE_MSG_NOT_UPDATE_OK              = 6;
    public static final int SAVE_MSG_SQL_INSERT_ERROR           = 7;
	
    public GameObject(String tableDB, String[] tableStruct) 
    { 
        this.tableDB = tableDB; 
        this.tableStruct = tableStruct;
    }
	
    //Abstract method
    protected abstract void saveInternalInfoObject(JSONObject guildInfo);
    public abstract boolean saveInDB();
    public abstract void setId(String id);
	
    //Generic function
    /**
     * Save Game object element in DB
     * @values values from object we need save, use in query.
     */
    protected int saveInDBObj(String[] values) { return saveInDBObj(values, null);}
    protected int saveInDBObj(String[] values, String lastId)
    {
        if(dbConnect == null) dbConnect = new DBConnect();
        if (this.isData && values.length > 0)
        {
            String updateDuplicate = "ON DUPLICATE KEY UPDATE ";
            if(lastId != null) updateDuplicate += lastId +"=LAST_INSERT_ID("+ lastId +"),";
            else updateDuplicate += this.tableStruct[0] +"=LAST_INSERT_ID("+ this.tableStruct[0] +"),";
            String[] whereValues = new String[values.length-1];
            for(int i = 1; i < values.length; i++) //start in 1 omitted key!
            {
                whereValues[i-1] = values[i];
                System.out.println("Where value: "+ values[i]);
                updateDuplicate += " "+ this.tableStruct[i] +"=?,";
            }
            System.out.println("Where values: "+ whereValues[0]);
            updateDuplicate = updateDuplicate.substring(0,updateDuplicate.length()-1); //remove the last ','
           
            try
            {
                String id = dbConnect.insert(this.tableDB,
                                this.tableStruct,
                                values,
                                updateDuplicate,
                                whereValues);
                setId(id);
                return SAVE_MSG_INSERT_OK;
            }
            catch (DataException|SQLException|ClassNotFoundException e)
            {
                System.out.println("E: "+ e);
                return SAVE_MSG_INSERT_ERROR;
            }
        }
        return SAVE_MSG_NO_DATA;
    }
	
    /**
     * Get a content from DB to load in object
     * @id element identifier (primary key!)
     * @where add a where clause
     */
    protected boolean loadFromDB(String id) { return loadFromDB(id, null); }
    protected boolean loadFromDB(String id, String where)
    {
        if(dbConnect == null) dbConnect = new DBConnect();
        try
        {		
            String whereInSQL = this.tableStruct[0] +"=?";
            String[] whereValues = {id};
            if(where != null) whereInSQL += " AND "+ where;
            JSONArray dbSelect = dbConnect.select(this.tableDB, this.tableStruct, whereInSQL, whereValues);

            if(dbSelect.size() > 0)
            {
                JSONObject infoDB = (JSONObject) dbSelect.get(0);
                //Construct a character object
                saveInternalInfoObject(infoDB);
                return isData;
            }
            else
            {
                System.out.println("Element not found");
                return false;
            }			
        } catch (DataException|SQLException e) {
            System.out.println("Error in Load element: "+ e);
        }
        return false;
    }
	
    //Get/Set method
    public boolean isData() { return this.isData; }
    public void setData(boolean isData) { this.isData = isData; }
    public void setTableStructur(String[] tabStruc) { this.tableStruct = tabStruc; }
	
}