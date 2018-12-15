/**
 * File : Database.java
 * Desc : Database Connection controller.
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.dbConnect;

import com.blizzardPanel.DataException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Database 
{
    public static final String DB_CONTROLLER_NAME = "jdbc/db";
    
    private DataSource dataSource;
    
    public Database(String jndiname) 
    {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/" + jndiname);
        } catch (NamingException e) {
            // Handle error that it's not configured in JNDI.
            throw new IllegalStateException(jndiname + " is missing in JNDI!", e);
        }
    }
    
    public Connection getConnection() throws DataException 
    {
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            throw new DataException("DB can't connect");
        }
    }    
}
