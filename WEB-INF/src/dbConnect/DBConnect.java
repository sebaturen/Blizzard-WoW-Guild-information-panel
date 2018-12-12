/**
 * File : DBConnect.java
 * Desc : DB Connection controller.
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.dbConnect;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DBConnect implements DBConfig, GeneralConfig
{
    //error SQL constant
    public static final int ERROR_FOREIGN_KEY   = 1452;
    public static final int ERROR_NULL_ELEMENT  = 1048;
    public static final int ERROR_DUPLICATE_KEY = 1062;

    //access info
    private static Connection conn = null;
    private PreparedStatement pstmt = null;
    private static boolean statusConnect = false;
    //Error controller
    private boolean isErrorDB;
    private String errorMsg;

    public DBConnect()
    {
        //generateConnextion();
    }
    
    private void closeConnection()
    {
        if(conn != null)
        {            
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void generateConnextion()
    {
        try
        {
            //Driver connection
            conn = DriverManager.getConnection(JDBC_URL + DB_NAME,
                                                    DB_USER,
                                                    DB_PASSWORD);
            statusConnect = true;
            isErrorDB = false;
        } catch (SQLException e) {
            String error = "Fail to generate DB Connection: "+ e;
            this.isErrorDB = true;
            this.errorMsg = error;
            Logs.saveLogln(error);
            statusConnect = false;
        }        
    }
    
    public boolean connectionVerification()
    {
        try {
            if(conn == null || conn.isClosed()) generateConnextion();
            //String sql = "SHOW TABLES";
            closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.isErrorDB;
    }
	
    
    /**
     * Run Select Query 
     * @param table
     * @param selected
     * @return JSONArray Select content
     * @throws SQLException
     * @throws DataException 
     */
    public JSONArray select(String table, String[] selected) throws SQLException, DataException { return select(table, selected, null, null, false); }
    public JSONArray select(String table, String[] selected, String where, String[] whereValues) throws SQLException, DataException { return select(table, selected, where, whereValues, false); }
    public JSONArray select(String table, String[] selected, String where, String[] whereValues, boolean disableAphostro) throws SQLException, DataException
    {
        JSONArray result = null;
        if(conn == null || conn.isClosed()) generateConnextion();
        if (statusConnect == true)
        {
            //System.out.print("is close? "+ conn.isClosed());
            //Prepare QUERY
            String sql = "SELECT ";
            String aphost = (disableAphostro)? "":"`";
            for(String v : selected) { sql += aphost + v + aphost +","; }
            sql = sql.substring(0,sql.length()-1);
            sql += " FROM "+ aphost + table + aphost;
            
            if(where != null) sql += " WHERE "+ where;
            this.pstmt = conn.prepareStatement(sql);
            if(where != null) for(int i = 0; i < whereValues.length; i++) this.pstmt.setString(i+1,whereValues[i]);
            //System.out.println("PSTMT: "+ this.pstmt);
            //System.out.println(" - "+ conn.isClosed());
            result = resultToJsonConvert(this.pstmt.executeQuery());
        }
        else
        {
            throw new DataException("DB can't connect");
        }
        closeConnection();
        return result;
    }
    	
    
    /**
     * Delete data from DB Query
     * @param table
     * @param where
     * @param whereValues
     * @throws SQLException
     * @throws DataException 
     */
    public void delete(String table, String where, String[] whereValues) throws SQLException, DataException
    {
        if (where == null || where.length() < 3) throw new DataException("Where in DELETE is MANDAROTY!");
        if(conn == null || conn.isClosed()) generateConnextion();
        if (statusConnect == true)
        {
            //Prepare QUERY
            String sql = "DELETE FROM "+ table +" WHERE "+ where;
            this.pstmt = conn.prepareStatement(sql);
            for(int i = 0; i < whereValues.length; i++) { this.pstmt.setString(i+1,whereValues[i]); }
            this.pstmt.executeQuery();
            closeConnection();
        }
        else
        {
            throw new DataException("DB can't connect");
        }
    }
	
    /**
     * Insert SQL Query.
     * @param table
     * @param idColum
     * @param columns
     * @param values
     * @return insert ID
     * @throws DataException
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public String insert(String table, String idColum, String[] columns, String[] values) throws DataException, ClassNotFoundException, SQLException
    {
        String id = null;
        if(conn == null || conn.isClosed()) generateConnextion();
        if (statusConnect == true)
        {        		
            if ((columns.length > 0 && values.length > 0) && 
            (columns.length == values.length))
            {
                try {
                    String columnsSQL = "";
                    String valuesSQL = "";
                    for(String c: columns) { columnsSQL += "`"+ c +"`,"; valuesSQL += "?,"; }
                    columnsSQL = columnsSQL.substring(0,columnsSQL.length()-1);
                    valuesSQL = valuesSQL.substring(0,valuesSQL.length()-1);
                    
                    String sql = "INSERT INTO "+ table +" ("+ columnsSQL +") values ("+ valuesSQL +")";
                    String[] valuesWithWhereValues = values;
                    
                    //Load JDBC Driver
                    Class.forName(JDBC_DRIVER);
                    
                    this.pstmt = conn.prepareStatement(sql);
                    for(int i = 0; i < valuesWithWhereValues.length; i++) { this.pstmt.setString(i+1,valuesWithWhereValues[i]); }
                    //Logs.saveLog("PSTMT: "+ this.pstmt);
                    //Run Update
                    this.pstmt.executeUpdate();
                    
                    //Get a ID correspondin to this insert
                    String whereID = "";
                    List<String> valusSelect = new ArrayList<>();
                    for(int i = 0; i < columns.length; i++)
                    {
                       if(values[i] != null)
                        {
                            whereID += "`"+ columns[i] +"`=? AND ";
                            valusSelect.add(values[i]);                          
                        } 
                    }
                    String[] stockArr = new String[valusSelect.size()];
                    stockArr = valusSelect.toArray(stockArr);
                    whereID = whereID.substring(0,whereID.length()-5);
                    JSONArray v = select(table,
                                        new String[] { idColum },
                                        whereID,
                                        stockArr);
                    if(v.isEmpty()) 
                    {
                        Logs.saveLogln("FAIL (EXIT) TO GET ID! "+ this.pstmt);
                        System.exit(-1);
                    }
                    else
                    {                    
                        id = ((JSONObject) v.get(0)).get(idColum).toString();
                    }
                } catch (SQLException ex) {
                    DataException er = new DataException("Fail to insert "+ ex +"\n\t"+ this.pstmt);
                    er.setErrorCode(ex.getErrorCode());
                    closeConnection();
                    throw er;
                }

            }
            else
            {
                throw new DataException("Invalid data in SQL Insert");
            }
        }
        else
        {
            throw new DataException("DB can't connect");
        }
        closeConnection();
        return id;
    }
    
    /**
     * Update Query
     * @param table
     * @param columns
     * @param values
     * @param where
     * @param whereValues
     * @throws DataException
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public void update(String table, String[] columns,String[] values, String where, String[] whereValues) throws DataException, ClassNotFoundException, SQLException
    {
        if(conn == null || conn.isClosed()) generateConnextion();
        if (statusConnect == true)
        {		
            if ((columns.length > 0 && values.length > 0) && 
            (columns.length == values.length))
            {
                try {
                    String columnsSQL = "";
                    for(String c: columns) { columnsSQL += "`"+ c +"` = ?,";}
                    columnsSQL = columnsSQL.substring(0,columnsSQL.length()-1);                
                    
                    String sql = "UPDATE "+ table +" SET "+ columnsSQL;
                    if(where != null)
                    {
                        sql += " WHERE "+ where;
                        String[] valInSql = new String[values.length + whereValues.length];
                        int i = 0;
                        for(; i < values.length; i++) valInSql[i] = values[i];
                        for(int j = 0; j < whereValues.length; j++,i++) valInSql[i] = whereValues[j];
                        values = valInSql;
                    }
                    
                    //Load JDBC Driver
                    Class.forName(JDBC_DRIVER);
                    
                    this.pstmt = conn.prepareStatement(sql);
                    for(int i = 0; i < values.length; i++) { this.pstmt.setString(i+1,values[i]); }
                    //Logs.saveLog("PSTMT: "+ this.pstmt);
                    //Run Update
                    this.pstmt.executeUpdate();
                    closeConnection();
                } catch (SQLException ex) {
                    closeConnection();
                    throw new DataException("Fail to insert "+ ex +"\n\t"+ this.pstmt);
                }
            }
            else
            {
                throw new DataException("Invalid data in SQL Insert");
            }
        }
        else
        {
            throw new DataException("DB can't connect");
        }
    }
	
    /**
     * Convert SQL Result to JSONArray
     * @param result set
     * @return
     * @throws SQLException 
     */
    private static JSONArray resultToJsonConvert(ResultSet rs) throws SQLException
    {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();

        while(rs.next()) 
        {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();

            for (int i=1; i<numColumns+1; i++) 
            {
                String column_name = rsmd.getColumnName(i);

                switch (rsmd.getColumnType(i)) 
                {
                    case java.sql.Types.ARRAY:
                        obj.put(column_name, rs.getArray(column_name));
                        break;
                    case java.sql.Types.BIGINT:
                        obj.put(column_name, rs.getLong(column_name));
                        break;
                    case java.sql.Types.BOOLEAN:
                        obj.put(column_name, rs.getBoolean(column_name));
                        break;
                    case java.sql.Types.BLOB:
                        obj.put(column_name, rs.getBlob(column_name));
                        break;
                    case java.sql.Types.DOUBLE:
                        obj.put(column_name, rs.getDouble(column_name));
                        break;
                    case java.sql.Types.FLOAT:
                        obj.put(column_name, rs.getFloat(column_name));
                        break;
                    case java.sql.Types.INTEGER:
                        obj.put(column_name, rs.getInt(column_name));
                        break;
                    case java.sql.Types.NVARCHAR:
                        obj.put(column_name, rs.getNString(column_name));
                        break;
                    case java.sql.Types.VARCHAR:
                        obj.put(column_name, rs.getString(column_name));
                        break;
                    case java.sql.Types.TINYINT:
                        obj.put(column_name, rs.getInt(column_name));
                        break;
                    case java.sql.Types.SMALLINT:
                        obj.put(column_name, rs.getInt(column_name));
                        break;
                    case java.sql.Types.DATE:
                        obj.put(column_name, rs.getDate(column_name));
                        break;
                    case java.sql.Types.TIMESTAMP:
                        obj.put(column_name, rs.getTimestamp(column_name));
                        break;
                    default:
                        obj.put(column_name, rs.getObject(column_name));
                        break;
                }
            }
            json.add(obj);
        }
        return json;
    }
    
    public boolean isErrorDB() { return this.isErrorDB; }
    public String getErrorMsg() { return this.errorMsg; }
	
}