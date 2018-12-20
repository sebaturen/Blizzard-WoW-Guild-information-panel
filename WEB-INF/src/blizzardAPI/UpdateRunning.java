/**
 * File : UpdateRunning.java
 * Desc : Update Running get actuali information every X time.
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.blizzardAPI;

import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
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
    private Update update;
    private DBConnect dbConnect;
    private ServletContext context;
    private int count = 0;

    public UpdateRunning()
    {
        dbConnect = new DBConnect();
        try
        {
            update = new Update();
        } catch (IOException | ParseException | DataException ex) {
            Logs.errorLog(UpdateRunning.class, "Fail to create Update object! "+ ex);
            System.exit(-1);
        }
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
                            update.setUpdate(new String[] {Update.UPDATE_TYPE_STATIC+""});
                        if(needDynamicUpdate())
                            update.setUpdate(new String[] {Update.UPDATE_TYPE_DYNAMIC+""});
                        if(needGuildNewUpdate())
                            update.setUpdate(new String[] {Update.UPDATE_TYPE_DYNAMIC+"", "GuildNews"});
                        if(needAHUpdate())
                            update.setUpdate(new String[] {Update.UPDATE_TYPE_AUCTION+""});
                        if(needAHMove())
                            update.setUpdate(new String[] {Update.UPDATE_TYPE_CLEAR_AH_HISTORY+""});
                        Thread.sleep(60000); //every minute
                    }
                } catch (Exception ex) {
                    Logs.errorLog(UpdateRunning.class, "FAIL IN UPDATE TIMELINE! "+ ex);
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
        int timeDynamicUpdate = GeneralConfig.getIntConfig("TIME_INTERVAL_DYNAMIC_UPDATE");
        if(timeDynamicUpdate <= 0)
        {
            Logs.infoLog(UpdateRunning.class, "Dynamic Update time not found or have and error config, set default time (60)");
            timeDynamicUpdate = 60;
        }
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_DYNAMIC);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -timeDynamicUpdate);
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.errorLog(UpdateRunning.class, ex.getMessage());
            return true;
        }
    }

    private boolean needGuildNewUpdate()
    {
        int timeGuildNewUpdate = GeneralConfig.getIntConfig("TIME_INTERVAL_GUILD_NEW_UPDATE");
        if(timeGuildNewUpdate <= 0)
        {
            Logs.infoLog(UpdateRunning.class, "Guild New Update time not found or have and error config, set default time (10)");
            timeGuildNewUpdate = 10;
        }
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_GUILD_NEWS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -timeGuildNewUpdate);
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.errorLog(UpdateRunning.class, ex.getMessage());
            return true;
        }
    }

    private boolean needStatycUpdate()
    {
        int timeStatycUpdate = GeneralConfig.getIntConfig("TIME_INTERVAL_STATIC_UPDATE");
        if(timeStatycUpdate <= 0)
        {
            Logs.infoLog(UpdateRunning.class, "Static Update time not found or have and error config, set default time (30)");
            timeStatycUpdate = 30;
        }
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_STATIC);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -timeStatycUpdate);
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.errorLog(UpdateRunning.class, ex.getMessage());
            return true;
        }
    }

    private boolean needAHUpdate()
    {
        int timeAHUpdate = GeneralConfig.getIntConfig("TIME_INTERVAL_AUCTION_HOUSE_UPDATE");
        if(timeAHUpdate <= 0)
        {
            Logs.infoLog(UpdateRunning.class, "Auction House Update time not found or have and error config, set default time (10)");
            timeAHUpdate = 10;
        }
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_AUCTION_CHECK);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -timeAHUpdate);
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.errorLog(UpdateRunning.class, ex.getMessage());
            return true;
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
            Logs.errorLog(UpdateRunning.class, ex.getMessage());
            needUpdate = true;
        }
        return needUpdate;
    }
}
