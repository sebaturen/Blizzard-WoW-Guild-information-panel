/**
 * File : Alters.java
 * Desc : Get information abaouth other members characters
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.User;
import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.google.gson.JsonArray;

import java.sql.SQLException;

public class Alters 
{    
    private final DBConnect dbConnect = new DBConnect();
    
    private User[] users;
    
    private void getAltersList()
    {
        try {
            JsonArray alters = dbConnect.select(User.USER_TABLE_NAME,
                    new String[] { "id" },
                    "guild_rank >= ? ORDER BY guild_rank ASC",
                    new String[] { "0" });
            
            users = new User[alters.size()];
            for(int i = 0; i < alters.size(); i++)
            {
                users[i] = new User( alters.get(i).getAsJsonObject().get("id").getAsInt() );
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(Alters.class, "Fail to get users list - "+ ex);
        }
    }
    
    public User[] getUsers() { getAltersList(); return this.users; }
}
