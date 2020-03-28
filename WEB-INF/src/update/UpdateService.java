/**
 * File : UpdateRunning.java
 * Desc : Update Running get actuali information every X time.
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.update;

import com.blizzardPanel.DataException;
import com.blizzardPanel.DiscordBot;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.FactionAssaultControl;
import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.Update;
import com.google.gson.JsonArray;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class UpdateService implements ServletContextListener
{
    private Thread updateInterval = null;
    private DBConnect dbConnect;
    private ServletContext context;
    private int count = 0;
    private boolean isAssaultNotification = false;

    public UpdateService()
    {
        dbConnect = new DBConnect();
    }

    @Override
    public void contextInitialized(ServletContextEvent contextEvent)
    {
        //BlizzardUpdate.shared.guild();
        //BlizzardUpdate.shared.guildAPI.info("Ragnaros", "Art of War");
        BlizzardUpdate.shared.loadAllCharacterDetail();
        //BlizzardUpdate.shared.characterProfileAPI.copyReIndex();
        //task
        updateInterval = new Thread(() -> {
            try {
                while(true) {
                    assaultNotification();
                    try {
                        if(needStaticUpdate()) {
                            //Update.shared.setUpdate(new String[] {Update.UPDATE_TYPE_STATIC+""});
                        }
                    } catch (Exception e) { Logs.errorLog(UpdateService.class, "FAIL TO UPDATE - STATIC UPDATE "+ e); }
                    try {
                        if(needDynamicUpdate()) {
                            //Update.shared.setUpdate(new String[] {Update.UPDATE_TYPE_DYNAMIC+""});
                        }
                    } catch (Exception e) { Logs.errorLog(UpdateService.class, "FAIL TO UPDATE - DYNAMIC UPDATE "+ e); }
                    try {
                        if(needGuildNewUpdate()) {
                            //Update.shared.setUpdate(new String[] {Update.UPDATE_TYPE_DYNAMIC+"", "GuildNews"});
                            //Update.shared.setUpdate(new String[] {Update.UPDATE_TYPE_DYNAMIC+"", "WowToken"});
                        }
                    } catch (Exception e) { Logs.errorLog(UpdateService.class, "FAIL TO UPDATE - GET GUILD NEWS "+ e); }
                    try {
                        if(needAHUpdate()) {
                            //Update.shared.setUpdate(new String[] {Update.UPDATE_TYPE_AUCTION+""});
                        }
                    } catch (Exception e) { Logs.errorLog(UpdateService.class, "FAIL TO UPDATE - ACTION HOUSE "+ e); }
                    try {
                        if(needAHMove()) {
                            //Update.shared.setUpdate(new String[] {Update.UPDATE_TYPE_CLEAR_AH_HISTORY+""});
                        }
                    } catch (Exception e) { Logs.errorLog(UpdateService.class, "FAIL TO UPDATE - CLEAR ACTION HOUSE"+ e); }
                    Thread.sleep(60000); //every minute
                }
            } catch (Exception ex) {
                Logs.errorLog(UpdateService.class, "FAIL IN UPDATE TIMELINE! "+ ex);
            }
        });
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
    
    private void assaultNotification()
    {
        FactionAssaultControl fAssault = new FactionAssaultControl();
        if(fAssault.isCurrent())
        {
            if(!this.isAssaultNotification)
            {
                DiscordBot.shared.sendMessajeNotification("The assault has started");
                this.isAssaultNotification = true;
            }
            int[] timeRemain = fAssault.getTimeRemainingCurrentAssault( );
            if(timeRemain[0] == 0 && timeRemain[1] == 30)
            {
                DiscordBot.shared.sendMessajeNotification(timeRemain[1] +"m remain for the assault to end");
            }
        }
        else
        {
            if(this.isAssaultNotification)
            {
                DiscordBot.shared.sendMessajeNotification("The assault has finished");
                this.isAssaultNotification = false;                
            }
            int[] timeRemain = fAssault.getTimeRemaining(fAssault.getNextAssault());
            if(timeRemain[0] == 1 && timeRemain[1] == 0)
            {
                DiscordBot.shared.sendMessajeNotification(timeRemain[0] +"h:"+ timeRemain[1] +"m to start the assault");
            }
            else if(timeRemain[0] == 0 && timeRemain[1] == 30)
            {
                DiscordBot.shared.sendMessajeNotification(timeRemain[1] +"m to start the assault");
            }
        }
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
            JsonArray dateUpdate = dbConnect.select(
                    Update.UPDATE_INTERVAL_TABLE_NAME,
                    new String[] {"update_time"},
                    "type=? order by id desc limit 1",
                    new String[] {val +""});
            if (dateUpdate.size() > 0)
            {
                String dbDate = ( dateUpdate.get(0).getAsJsonObject().get("update_time")).getAsString();
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
            Logs.infoLog(UpdateService.class, "Dynamic Update time not found or have and error config, set default time (60)");
            timeDynamicUpdate = 60;
        }
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_DYNAMIC);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -timeDynamicUpdate);
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.errorLog(UpdateService.class, ex.getMessage());
            return true;
        }
    }

    private boolean needGuildNewUpdate()
    {
        int timeGuildNewUpdate = GeneralConfig.getIntConfig("TIME_INTERVAL_GUILD_NEW_UPDATE");
        if(timeGuildNewUpdate <= 0)
        {
            Logs.infoLog(UpdateService.class, "Guild New Update time not found or have and error config, set default time (10)");
            timeGuildNewUpdate = 10;
        }
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_GUILD_NEWS);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -timeGuildNewUpdate);
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.errorLog(UpdateService.class, ex.getMessage());
            return true;
        }
    }

    private boolean needStaticUpdate()
    {
        int timeStatycUpdate = GeneralConfig.getIntConfig("TIME_INTERVAL_STATIC_UPDATE");
        if(timeStatycUpdate <= 0)
        {
            Logs.infoLog(UpdateService.class, "Static Update time not found or have and error config, set default time (30)");
            timeStatycUpdate = 30;
        }
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_STATIC);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -timeStatycUpdate);
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.errorLog(UpdateService.class, ex.getMessage());
            return true;
        }
    }

    private boolean needAHUpdate()
    {
        int timeAHUpdate = GeneralConfig.getIntConfig("TIME_INTERVAL_AUCTION_HOUSE_UPDATE");
        if(timeAHUpdate <= 0)
        {
            Logs.infoLog(UpdateService.class, "Auction House Update time not found or have and error config, set default time (10)");
            timeAHUpdate = 10;
        }
        try {
            Date lastUpdate = getLastUpdate(Update.UPDATE_TYPE_AUCTION_CHECK);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -timeAHUpdate);
            Date nTimeAgo = cal.getTime();
            return (lastUpdate.compareTo(nTimeAgo) < 0);
        } catch (DataException ex) {
            Logs.errorLog(UpdateService.class, ex.getMessage());
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
            Logs.errorLog(UpdateService.class, ex.getMessage());
            needUpdate = true;
        }
        return needUpdate;
    }
}
