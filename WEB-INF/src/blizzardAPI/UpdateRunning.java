/**
 * File : UpdateRunning.java
 * Desc : Update Running get actuali information every X time.
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.blizzardAPI;

import com.blizzardPanel.exceptions.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.exceptions.ConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class UpdateRunning implements ServletContextListener 
{
    private Thread updateInterval = null;
    private DBConnect dbConnect = null;
    private ServletContext context;
    private int count = 0;
    
    public UpdateRunning()
    {
        dbConnect = new DBConnect();
    }

    @Override
    public void contextInitialized(ServletContextEvent contextEvent)
    {
        updateInterval =  new Thread()
        {
            //task
            public void run()
            {
                try 
                {
                    while(true)
                    {
                        if(needStatycUpdate())
                            update(new String[] {Update.UPDATE_TYPE_STATIC+""});
                        if(needDynamicUpdate())
                            update(new String[] {Update.UPDATE_TYPE_DYNAMIC+""});
                        if(needGuildNewUpdate())
                            update(new String[] {Update.UPDATE_TYPE_DYNAMIC+"", "GuildNews"});
                        if(needAHUpdate())
                            update(new String[] {Update.UPDATE_TYPE_AUCTION+""});
                        if(needAHMove())
                            update(new String[] {Update.UPDATE_TYPE_CLEAR_AH_HISTORY+""});
                        Thread.sleep(60000); //every minute
                    }
                } catch (Exception ex) {
                    Logs.saveLogln("FAIL IN UPDATE TIMELINE! "+ ex);
                }
            }
        };
        updateInterval.start();
        context = contextEvent.getServletContext();
        // you can set a context variable just like this
        context.setAttribute("TEST", count);
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent)
    {
        // context is destroyed interrupts the thread
        updateInterval.interrupt();
    }    
            
    /**
     * Get last update time from DB
     * @param val Typ update (constante in Update.java)
     * @return
     * @throws DataException 
     */
    private Date getLastUpdate(int val) throws DataException
    {
        try 
        {
            JSONArray dateUpdate = dbConnect.select(
                    Update.UPDATE_INTERVAL_TABLE_NAME,
                    new String[] {"update_time"},
                    "type=? order by id desc limit 1",
                    new String[] {val +""});
            if (dateUpdate.size() > 0)
            {
                String dbDate = (((JSONObject)dateUpdate.get(0)).get("update_time")).toString();
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dbDate);
            }
            else
            {
                throw new DataException("Last update not found! (Type: "+ val +")");                
            }
        } catch (SQLException | DataException | java.text.ParseException ex) {
            throw new DataException("Fail to get last Update (Type: "+ val +") - "+ ex);
        }
    }
    
    private boolean needDynamicUpdate()
    {        
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_DYNAMIC);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, - GeneralConfig.getINTConfig("TIME_INTERVAL_DYNAMIC_UPDATE"));
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.saveLogln(ex.getMessage());
            return true;
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
            return false;
        }
    }
    
    
    private boolean needGuildNewUpdate()
    {        
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_GUILD_NEWS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, - GeneralConfig.getINTConfig("TIME_INTERVAL_GUILD_NEW_UPDATE"));
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.saveLogln(ex.getMessage());
            return true;
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
            return false;
        }
    }
    
    private boolean needStatycUpdate()
    {        
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_STATIC);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, - GeneralConfig.getINTConfig("TIME_INTERVAL_STATIC_UPDATE"));
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.saveLogln(ex.getMessage());
            return true;
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
            return false;
        }
    }   
    
    private boolean needAHUpdate()
    {        
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_AUCTION_CHECK);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, - GeneralConfig.getINTConfig("TIME_INTERVAL_AUCTION_HOUSE_UPDATE"));
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.saveLogln(ex.getMessage());
            return true;
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
            return false;
        }
    }    
            
    private boolean needAHMove()
    {        
        boolean needUpdate = false;
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_CLEAR_AH_HISTORY); 
            Calendar c = Calendar.getInstance();
            // set the calendar to start of today
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            Date toDay = c.getTime();
            return lastUpdate.before(toDay);
        } catch (DataException ex) {
            Logs.saveLogln(ex.getMessage());
            needUpdate = true;
        }
        return needUpdate;
    }
    
    private void update(String[] args)
    {
        try
        {
            Update blizzUp = new Update();
            int upParam = -1;
            String upInternal = "null";
            if(args.length > 0) upParam = Integer.parseInt(args[0]);
            if(args.length > 1) upInternal = args[1];
            
            switch(upParam)
            {
                case Update.UPDATE_TYPE_DYNAMIC:
                    switch(upInternal)
                    {
                        case "GuildProfile":        Logs.saveLogln("Guild Profile Update..."); blizzUp.getGuildProfile(); break;
                        case "GuildMembers":        Logs.saveLogln("Guild Members Update...");  blizzUp.getGuildMembers(); break;
                        case "CharacterInfo":       Logs.saveLogln("Character info Update...");  blizzUp.getCharacterInfo(); break;
                        case "GuildChallenges":     Logs.saveLogln("Guild Challenges Update...");  blizzUp.getGuildChallenges(); break;
                        case "GuildNews":           Logs.saveLogln("Guild News Update...");  blizzUp.getGuildNews(); break;
                        case "WowToken":            Logs.saveLogln("Wow Token Update..."); blizzUp.getWowToken(); break;
                        case "UsersCharacters":     Logs.saveLogln("User Characters Update...");  blizzUp.getUsersCharacters(); break;
                        case "GuildProgression":    Logs.saveLogln("Guild Progression Update...");  blizzUp.getGuildProgression(); break;
                        default:                    
                            blizzUp.updateDynamicAll();                        
                    }
                    break;
                case Update.UPDATE_TYPE_STATIC:
                    switch(upInternal)
                    {
                        case "PlayableClass":               Logs.saveLogln("Playable Class Update...");  blizzUp.getPlayableClass(); break;
                        case "PlayableSpec":                Logs.saveLogln("Playable Spec Update...");  blizzUp.getPlayableSpec(); break;
                        case "PlayableRaces":               Logs.saveLogln("Playable Races Update...");  blizzUp.getPlayableRaces(); break;
                        case "GuildAchievementsLists":      Logs.saveLogln("Guild Achievements Update...");  blizzUp.getGuildAchievementsLists(); break;
                        case "CharacterAchievementsLists":  Logs.saveLogln("Character Achievements Update...");  blizzUp.getCharacterAchievementsLists(); break;
                        case "BossInformation":             Logs.saveLogln("Bosses info Update...");  blizzUp.getBossInformation(); break;
                        case "updateSpellInformation":      Logs.saveLogln("Spells info Update...");  blizzUp.updateSpellInformation(); break;
                        case "updateItemInformation":       Logs.saveLogln("Items info Update...");  blizzUp.updateItemInformation(); break;
                        default:                    
                            blizzUp.updateStaticAll();                      
                    }				
                    break;	
                case Update.UPDATE_TYPE_AUCTION:
                    blizzUp.updateAH();
                    break;
                case Update.UPDATE_TYPE_CLEAR_AH_HISTORY:
                    blizzUp.moveHistoryAH();
                    break;                    
                default:
                    Logs.saveLogln("Not update parametter detected!");
                    break;
            }
        } catch (IOException|ParseException|DataException|SQLException | ClassNotFoundException ex) {
            Logs.saveLogln("Fail to update information - "+ ex);
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
        }
    }
}
