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
    public JSONArray select(String table, String[] values) throws SQLException, DataException { return select(table, values, null); }
    public JSONArray select(String table, String[] values, String where) throws SQLException, DataException
    {
        if (statusConnect == true)
        {
            //Prepare QUERY
            String sql = "SELECT ";
            for(String v : values) { sql += v +","; }
            sql = sql.substring(0,sql.length()-1);
            sql += " FROM "+ table;
            if(where != null) sql += " WHERE "+ where;

            this.pstmt = this.conn.prepareStatement(sql);
            return resultToJsonConvert(this.pstmt.executeQuery());
        }
        else
        {			
            throw new DataException("DB can't connect");
        }
    }
	
    /**
     * Delete data from DB, run a query
     * @table table
     * @where where in SQL, IS MANDAROTY!
     */
    public JSONArray delete(String table, String where) throws SQLException, DataException
    {
        if (where == null || where.length() < 3) throw new DataException("Where in DELETE is MANDAROTY!");
        if (statusConnect == true)
        {
            //Prepare QUERY
            String sql = "delete from "+ table +" where "+ where;
            this.pstmt = this.conn.prepareStatement(sql);
            return resultToJsonConvert(this.pstmt.executeQuery());
        }
        else
        {
            throw new DataException("DB can't connect");
        }
    }
	
    /**
     * RUN a SQL query in SQL server. (before call, valide (statusConnect == true))
     * @sql SQL Query [SIN DATOS INTERNOS]
     * @values[] data
     */
    private void runUpdate(String sql, String[] values) throws SQLException, ClassNotFoundException
    {
        //Load JDBC Driver
        Class.forName(JDBC_DRIVER);

        this.pstmt = this.conn.prepareStatement(sql);
        for(int i = 0; i < values.length; i++) { this.pstmt.setString(i+1,values[i]); }					
        //Run Update
        this.pstmt.executeUpdate();
    }
	
    /**
     * Insert Query construct.
     * @table Tabla
     * @columns name of value is change
     * @values values from this insert
     */
    public void insert(String table, String[] columns, String[] values) throws DataException, SQLException, ClassNotFoundException { insert(table, columns, values, null); }
    public void insert(String table, String[] columns, String[] values, String where) throws DataException, SQLException, ClassNotFoundException
    {
        if (statusConnect == true)
        {			
            if ((columns.length > 0 && values.length > 0) && 
            (columns.length == values.length))
            {
                String columnsSQL = "";
                String valuesSQL = "";
                for(String c: columns) { columnsSQL += "`"+ c +"`,"; valuesSQL += "?,"; }
                columnsSQL = columnsSQL.substring(0,columnsSQL.length()-1);
                valuesSQL = valuesSQL.substring(0,valuesSQL.length()-1);

                String sql = "insert into "+ table +" ("+ columnsSQL +") values("+ valuesSQL +")";

                if(where != null) sql += " "+ where;

                runUpdate(sql, values);
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
    public void update(String table, String[] columns, String[] values) throws DataException, SQLException, ClassNotFoundException { update(table, columns, values, null);}
    public void update(String table, String[] columns, String[] values, String where) throws DataException, SQLException, ClassNotFoundException
    {
        if (statusConnect == true)
        {			
            if ((columns.length > 0 && values.length > 0) && 
            (columns.length == values.length))
            {
                String columnsSQL = "";
                for(String c: columns) { columnsSQL += "`"+ c +"` = ?,";}
                columnsSQL = columnsSQL.substring(0,columnsSQL.length()-1);

                String sql = "Update "+ table +" SET "+ columnsSQL;
                if(where != null) sql += " where "+ where;

                runUpdate(sql, values);
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