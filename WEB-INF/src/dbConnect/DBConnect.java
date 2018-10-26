/**
 * File : DBConnect.java
 * Desc : DB Connection controller.
 * @author Sebastián Turen Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.dbConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.zip.DataFormatException;

public class DBConnect implements DBConfig {
	
	//acces info
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	
	private void runUpdate(String sql, String[] values) throws SQLException, ClassNotFoundException
	{
		//Load JDBC Driver
		Class.forName(JDBC_DRIVER);

		//Driver connection
		conn = DriverManager.getConnection(JDBC_URL + DB_NAME,
											DB_USER,
											DB_PASSWORD);
			
		pstmt = conn.prepareStatement(sql);
		for(int i = 0; i < values.length; i++) { pstmt.setString(i+1,values[i]); }					
		//Run Update
		pstmt.executeUpdate();
	}
	
	public void insert(String table, String[] columns, String[] values) throws DataFormatException, SQLException, ClassNotFoundException
	{
		if ((columns.length > 0 && values.length > 0) && 
			(columns.length == values.length))
		{
			String columnsSQL = "";
			for(String c: columns) { columnsSQL += c +','; }
			columnsSQL = columnsSQL.substring(0,columnsSQL.length()-1);
			
			String sql = "insert into "+ table +" ("+ columnsSQL +") values(?,?)";
			
			runUpdate(sql, values);
		}
		else
		{
			throw new DataFormatException("Invalid data");
		}
	}
	
}