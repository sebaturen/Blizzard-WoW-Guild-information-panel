/**
 * File : DBConnect.java
 * Desc : DB Connection controller.
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.dbConnect;

import com.artOfWar.DataException;

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

public class DBConnect implements DBConfig 
{
    //error SQL constant
    public static final int ERROR_FOREIGN_KEY = 1452;
    public static final int ERROR_NULL_ELEMENT = 1048;

    //access info
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private boolean statusConnect = false;

    public DBConnect()
    {
        try
        {
            //Driver connection
            this.conn = DriverManager.getConnection(JDBC_URL + DB_NAME,
                                                    DB_USER,
                                                    DB_PASSWORD);
            statusConnect = true;
        } catch (SQLException e) {
            System.out.println("Fail to generate DB Connection: "+ e);
            statusConnect = false;
        }
    }
	
    /**
     * Run Select Query 
     * @table table
     * @values array how want select 
     * @where where
     */
    public JSONArray select(String table, String[] selected) throws SQLException, DataException { return select(table, selected, null, null, false); }
    public JSONArray select(String table, String[] selected, String where, String[] whereValues) throws SQLException, DataException { return select(table, selected, where, whereValues, false); }
    public JSONArray select(String table, String[] selected, String where, String[] whereValues, boolean disableAphostro) throws SQLException, DataException
    {
        if (statusConnect == true)
        {
            //Prepare QUERY
            String sql = "SELECT ";
            String aphost = (disableAphostro)? "":"`";
            for(String v : selected) { sql += aphost+ v +aphost+","; }
            sql = sql.substring(0,sql.length()-1);
            sql += " FROM "+aphost+ table +aphost;
            
            if(where != null) sql += " WHERE "+ where;
            this.pstmt = this.conn.prepareStatement(sql);
            if(where != null) for(int i = 0; i < whereValues.length; i++) this.pstmt.setString(i+1,whereValues[i]);
            //System.out.println("PSTMT: "+ this.pstmt);
            return resultToJsonConvert(this.pstmt.executeQuery());
        }
        else
        {			
            throw new DataException("DB can't connect");
        }
    }
    
    private String selectLastID() throws SQLException, DataException
    {
        if (statusConnect == true)
        {
            //Prepare QUERY
            String sql = "SELECT LAST_INSERT_ID()";
            this.pstmt = this.conn.prepareStatement(sql);          
            ResultSet rs = this.pstmt.executeQuery();
            rs.next();
            return rs.getString("LAST_INSERT_ID()");
        }
        else
        {			
            throw new DataException("DB can't connect");
        }        
    }
    
    /**
     *Show a ResultSet complate
     */
    private void testRs(ResultSet rs)
    {
        try {
            ResultSet resultSet = rs;
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
    /**
     * Delete data from DB, run a query
     * @table table
     * @where where in SQL, IS MANDAROTY!
     */
    public JSONArray delete(String table, String where, String[] whereValues) throws SQLException, DataException
    {
        if (where == null || where.length() < 3) throw new DataException("Where in DELETE is MANDAROTY!");
        if (statusConnect == true)
        {
            //Prepare QUERY
            String sql = "delete from "+ table +" where "+ where;
            this.pstmt = this.conn.prepareStatement(sql);
            for(int i = 0; i < whereValues.length; i++) { this.pstmt.setString(i+1,whereValues[i]); }
            return resultToJsonConvert(this.pstmt.executeQuery());
        }
        else
        {
            throw new DataException("DB can't connect");
        }
    }
	
    /**
     * Insert Query construct.
     * @table Tabla
     * @columns name of value is change
     * @values values from this insert
     */
    public String insert(String table, String idColum, String[] columns, String[] values) throws DataException, ClassNotFoundException { return insert(table, idColum, columns, values, null, null); }
    public String insert(String table, String idColum, String[] columns, String[] values, String where, String[] whereValues) throws DataException, ClassNotFoundException
    {
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
                    
                    String sql = "insert into "+ table +" ("+ columnsSQL +") values ("+ valuesSQL +")";
                    String[] valuesWithWhereValues = values;
                    if(where != null)
                    {
                        String[] valInSql = new String[values.length + whereValues.length];
                        int i = 0;
                        for(; i < values.length; i++) valInSql[i] = values[i];
                        for(int j = 0; j < whereValues.length; j++,i++) valInSql[i] = whereValues[j];
                        sql += " "+ where;
                        valuesWithWhereValues = valInSql;
                    }
                    
                    //Load JDBC Driver
                    Class.forName(JDBC_DRIVER);
                    
                    this.pstmt = this.conn.prepareStatement(sql);
                    for(int i = 0; i < valuesWithWhereValues.length; i++) { this.pstmt.setString(i+1,valuesWithWhereValues[i]); }
                    //System.out.println("PSTMT: "+ this.pstmt);
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
                        System.out.println("FAIL TO GET ID! "+ this.pstmt);
                        System.exit(-1);
                        return null;
                    }
                    else
                    {                    
                        return ((JSONObject) v.get(0)).get(idColum).toString();
                    }
                } catch (SQLException ex) {
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
     * Update Query
     * @table Tabla
     * @columns name of value is change
     * @values values from this insert
     */
    public void update(String table, String[] columns, 
                        String[] values, String where, String[] whereValues) 
            throws DataException, ClassNotFoundException
    {
        if (statusConnect == true)
        {			
            if ((columns.length > 0 && values.length > 0) && 
            (columns.length == values.length))
            {
                try {
                    String columnsSQL = "";
                    for(String c: columns) { columnsSQL += "`"+ c +"` = ?,";}
                    columnsSQL = columnsSQL.substring(0,columnsSQL.length()-1);                
                    
                    String sql = "Update "+ table +" SET "+ columnsSQL;
                    if(where != null)
                    {
                        sql += " where "+ where;
                        String[] valInSql = new String[values.length + whereValues.length];
                        int i = 0;
                        for(; i < values.length; i++) valInSql[i] = values[i];
                        for(int j = 0; j < whereValues.length; j++,i++) valInSql[i] = whereValues[j];
                        values = valInSql;
                    }
                    
                    //Load JDBC Driver
                    Class.forName(JDBC_DRIVER);
                    
                    this.pstmt = this.conn.prepareStatement(sql);
                    for(int i = 0; i < values.length; i++) { this.pstmt.setString(i+1,values[i]); }
                    //System.out.println("PSTMT: "+ this.pstmt);
                    //Run Update
                    this.pstmt.executeUpdate();
                } catch (SQLException ex) {
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
	
}