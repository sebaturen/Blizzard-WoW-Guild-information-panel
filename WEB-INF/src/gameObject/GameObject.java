/**
 * File : GameObject.java
 * Desc : General abstract class to game object elements
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class GameObject implements DBStructure
{
    //Variable
    protected static final DBConnect dbConnect = new DBConnect();
    protected boolean isData = false;
    protected boolean isInternalData = false;
    private String tableDB;
    private String tableKey;
    private String[] tableStruct;

    //Constant save data info
    public static final int SAVE_MSG_NO_DATA        = 0;
    public static final int SAVE_MSG_INSERT_ERROR   = 1;
    public static final int SAVE_MSG_INSERT_OK      = 2;
    public static final int SAVE_MSG_UPDATE_ERROR   = 5;
    public static final int SAVE_MSG_UPDATE_OK      = 3;
	
    public GameObject(String tableDB, String tableKey, String[] tableStruct) 
    {
        this.tableDB = tableDB; 
        this.tableKey = tableKey;
        this.tableStruct = tableStruct;
    }
    
    //Abstract method
    protected abstract void saveInternalInfoObject(JsonObject objInfo);
    public abstract boolean saveInDB();
    public abstract void setId(int id);
    public abstract int getId();
	
    //Generic function
    /**
     * Save Game object element in DB
     * @param values from object we need save, use in query.
     * @return status query
     */
    protected int saveInDBObj(String[] values)
    {
        if (this.isData && values.length > 0)
        {
            //Valid if need update or insert...
            if(this.isInternalData) //if have a ID, is in DB
            {//update...
                try 
                {
                    //Update
                    dbConnect.update(this.tableDB,
                            this.tableStruct,
                            values,
                            this.tableKey +"=?",
                            new String[] { getId()+"" });
                    return SAVE_MSG_UPDATE_OK;
                } catch (DataException | SQLException ex) {
                    Logs.errorLog(this.getClass(), "Fail to update "+ getId() +" in table "+ this.tableDB +" - "+ ex);
                    return SAVE_MSG_UPDATE_ERROR;
                }
            }
            else
            {   try 
                {
                    //Not have a defined ID...
                    String id = dbConnect.insert(this.tableDB,
                                                this.tableKey,
                                                this.tableStruct,
                                                values);
                    setId(Integer.parseInt(id));
                    this.isInternalData = true;
                    return SAVE_MSG_INSERT_OK;
                } catch (DataException | ClassNotFoundException | SQLException ex) {
                    Logs.errorLog(this.getClass(), "Fail to insert - "+ ex);
                    return SAVE_MSG_INSERT_ERROR;
                }
            }
        }
        else
        {
            Logs.errorLog(this.getClass(), "Fail to try save!, no data - isData> "+ this.isData +" valueLeng> "+ values.length);
        }
        return SAVE_MSG_NO_DATA;
    }
    
    /**
     * Load from DB use unique values
     * @param uniqued
     * @param uniquedValues
     * @return 
     */
    protected boolean loadFromDBUniqued(String uniqued, String uniquedValues) { return loadFromDBUniqued(new String[] { uniqued }, new String[] {uniquedValues}); }
    protected boolean loadFromDBUniqued(String[] uniqued, String[] value) { return loadFromDBUniqued(uniqued, value, false);}
    protected boolean loadFromDBUniqued(String[] uniqued, String[] value, boolean disableApostrophe) { return loadFromDBUniqued(uniqued, value, null, disableApostrophe);}
    protected boolean loadFromDBUniqued(String[] uniqued, String[] value, String addWhere, boolean disableApostrophe) 
    {
        try
        {
            String whereInSQL = "";
            for(String wh : uniqued) whereInSQL += ((disableApostrophe)? "":"`")+ wh + ((disableApostrophe)? "":"`") +"=? AND ";
            if(addWhere != null) whereInSQL += ((disableApostrophe)? "":"`")+ addWhere + ((disableApostrophe)? "":"`") +" AND ";
            whereInSQL = whereInSQL.substring(0,whereInSQL.length()-5);
            JsonArray dbSelect = dbConnect.select(this.tableDB, this.tableStruct, whereInSQL, value, disableApostrophe);

            if(dbSelect.size() > 0)
            {
                JsonObject infoDB = dbSelect.get(0).getAsJsonObject();
                //Construct a character object
                saveInternalInfoObject(infoDB);
                this.isInternalData = true;
                return isData;                
            }
            else
            {
                //Logs.saveLog("Element not found");
                return false;                
            }
        } catch (DataException|SQLException e) {
            Logs.errorLog(this.getClass(), "Error in Load element 'from Uniqued': "+ e);
        }
        return false;        
    }
	
    /**
     * Get a content from DB to load in object
     * @id element identifier (primary key!)
     * @where add a where clause
     */
    protected boolean loadFromDB(int id) { return loadFromDB(id, null, false); }
    protected boolean loadFromDB(int id, String andWhere, boolean disableApostrophe)
    {
        try
        {
            String whereInSQL = this.tableKey +"=?";
            String[] whereValues = {id+""};
            if(andWhere != null) whereInSQL += " AND "+ andWhere;
            JsonArray dbSelect = dbConnect.select(this.tableDB, this.tableStruct, whereInSQL, whereValues, disableApostrophe);

            if(dbSelect.size() > 0)
            {
                JsonObject infoDB = dbSelect.get(0).getAsJsonObject();
                //Construct a character object
                saveInternalInfoObject(infoDB);
                this.isInternalData = true;
                return isData;
            }
            else
            {
                //Logs.saveLog("Element not found");
                return false;
            }			
        } catch (DataException|SQLException e) {
            Logs.errorLog(this.getClass(), "Error in Load element 'from ID': "+ e);
        }
        return false;
    }
    
    public static String getDBDate(Date date)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(date); 
        return strDate;
    }
	
    //Get/Set method
    public boolean isData() { return this.isData; }
    public boolean isInternalData() { return this.isInternalData; }
    public void setIsData(boolean isData) { this.isData = isData; }
    public void setTableName(String tabName) { this.tableDB = tabName; }
    public void setTableKey(String tableKey) { this.tableKey = tableKey; }
    public void setTableStructur(String[] tabStruc) { this.tableStruct = tabStruc; }
    public void setIsInternalData(boolean stat) { this.isInternalData = stat; }
	
}