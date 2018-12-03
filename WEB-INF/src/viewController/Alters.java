/**
 * File : Alters.java
 * Desc : Get information abaouth other members characters
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Alters 
{    
    private final DBConnect dbConnect = new DBConnect();
    
    private User[] users;
    
    private void getAltersList()
    {
        try {
            JSONArray alters = dbConnect.select(User.USER_TABLE_NAME,
                    new String[] { "id" },
                    "guild_rank >= ? ORDER BY guild_rank ASC",
                    new String[] { "0" });
            
            users = new User[alters.size()];
            for(int i = 0; i < alters.size(); i++)
            {
                users[i] = new User( (Integer) ((JSONObject) alters.get(i)).get("id") );
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to get users list - "+ ex);
        }
    }
    
    public User[] getUsers() { getAltersList(); return this.users; }
}
