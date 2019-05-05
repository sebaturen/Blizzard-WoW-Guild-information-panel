/**
 * File : DBConnect.java
 * Desc : DB Connection controller.
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.dbConnect;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DBConnect
{
    //error SQL constant
    public static final int ERROR_FOREIGN_KEY   = 1452;
    public static final int ERROR_NULL_ELEMENT  = 1048;
    public static final int ERROR_DUPLICATE_KEY = 1062;
    
    private String lastQuery = null;

    public DBConnect()
    {
        //generateConnextion();
    }

    public boolean connectionVerification()
    {
        try(
            Connection conn = (new Database(Database.DB_CONTROLLER_NAME)).getConnection();
        ) {
            return true;
        } catch (DataException | SQLException e) {
           return false;
        }
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
        //Prepare QUERY
        String sql = "SELECT ";
        String aphost = (disableAphostro)? "":"`";
        for(String v : selected) { sql += aphost + v + aphost +","; }
        sql = sql.substring(0,sql.length()-1);
        sql += " FROM "+ aphost + table + aphost;

        if(where != null) sql += " WHERE "+ where;

        //Prepare Connection and excetute
        try(
            Connection conn = (new Database(Database.DB_CONTROLLER_NAME)).getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            if(where != null) for(int i = 0; i < whereValues.length; i++) pstmt.setString(i+1,whereValues[i]);
            //System.out.println("pstms"+ pstmt);
            this.lastQuery = pstmt.toString();
            result = resultToJsonConvert(pstmt.executeQuery());
        } catch (DataException e) {
           throw e; //Can get a connection
        }
        
        return result;
    }
    
    /**
     * Run query in DB!, becareful!
     * @param query
     * @return 
     */
    public JSONArray selectQuery(String query) throws SQLException, DataException
    {
        JSONArray result = null;
        
        //Prepare Connection and excetute
        try(
            Connection conn = (new Database(Database.DB_CONTROLLER_NAME)).getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
        ) {
            this.lastQuery = pstmt.toString();
            result = resultToJsonConvert(pstmt.executeQuery());
        } catch (DataException e) {
           throw e; //Can get a connection
        }        
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
        //Prepare QUERY
        String sql = "DELETE FROM "+ table +" WHERE "+ where;
        
        //Prepare Connection and excetute
        try(
            Connection conn = (new Database(Database.DB_CONTROLLER_NAME)).getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            for(int i = 0; i < whereValues.length; i++) { pstmt.setString(i+1,whereValues[i]); }
            this.lastQuery = pstmt.toString();
            pstmt.executeQuery();
        } catch (DataException e) {
           throw e; //Can get a connection
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
        if ((columns.length > 0 && values.length > 0) && 
            (columns.length == values.length))
        {
            String columnsSQL = "";
            String valuesSQL = "";
            for(String c: columns) { columnsSQL += "`"+ c +"`,"; valuesSQL += "?,"; }
            columnsSQL = columnsSQL.substring(0,columnsSQL.length()-1);
            valuesSQL = valuesSQL.substring(0,valuesSQL.length()-1);

            String sql = "INSERT INTO "+ table +" ("+ columnsSQL +") values ("+ valuesSQL +")";
            String[] valuesWithWhereValues = values;
            
            //Run insert...
            String tempLastQuery = sql;
            try(
                Connection conn = (new Database(Database.DB_CONTROLLER_NAME)).getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
            ) {
                for(int i = 0; i < valuesWithWhereValues.length; i++) { pstmt.setString(i+1,valuesWithWhereValues[i]); }
                //Logs.saveLog("PSTMT: "+ this.pstmt);
                tempLastQuery = pstmt.toString();
                pstmt.executeUpdate();  
            } catch (DataException e) {
               throw e; //Can get a connection
            }
            //Get ID after insert...
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
            this.lastQuery = tempLastQuery + this.lastQuery; //save insert and select
            if(v.isEmpty()) 
            {
                Logs.fatalLog(this.getClass(), "FAIL (EXIT) TO GET ID! - "+ sql);
                System.exit(-1);
            }
            else
            {                    
                id = ((JSONObject) v.get(0)).get(idColum).toString();
            }
        }
        else
        {
            throw new DataException("Invalid data in SQL Insert '"+ this.getClass() +"'");
        }
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
        if ((columns.length > 0 && values.length > 0) && 
            (columns.length == values.length))
        {            
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
            
            //Run update...
            try(
                Connection conn = (new Database(Database.DB_CONTROLLER_NAME)).getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
            ) {
                for(int i = 0; i < values.length; i++) { pstmt.setString(i+1,values[i]); }
                //Logs.saveLog("PSTMT: "+ this.pstmt);
                //Run Update  
                this.lastQuery = pstmt.toString();
                pstmt.executeUpdate(); 
            } catch (DataException e) {
               throw e; //Can get a connection
            }
        }
        else
        {
            throw new DataException("Invalid data in SQL Insert");
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

    public String getLastQuery()
    {
        return this.lastQuery;
    }
}