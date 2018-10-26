/**
 * File : DBConfig.java
 * Desc : Configure to DB Connection info.
 * @author Sebastián Turen Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.dbConnect;

public interface DBConfig {
	
	//jdbc information
	public static final String JDBC_DRIVER 	= ""; //com.mysql.jdbc.Driver
	public static final String JDBC_URL 	= ""; //jdbc:mysql://127.0.0.1/
	
	//DB information
	public static final String DB_NAME		= "";
	public static final String DB_USER		= "";
	public static final String DB_PASSWORD	= "";
	
}