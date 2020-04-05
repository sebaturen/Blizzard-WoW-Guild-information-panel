package com.blizzardPanel.update;

import com.blizzardPanel.update.blizzard.BlizzardUpdate;
import com.blizzardPanel.update.blizzard.UpdateType;
import com.blizzardPanel.viewController.GuildController;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateService implements ServletContextListener {

    // Update Timeline DB
    public static final String TABLE_NAME = "update_timeline";
    public static final String TABLE_KEY = "id";

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // Attribute
        GuildController.getInstance().loadActivities(10);
        sce.getServletContext().setAttribute("guild", GuildController.getInstance());

        // Prepare Schedule
        ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();

        // Profile update
        schedule.scheduleAtFixedRate(new BlizzardUpdate(UpdateType.GUILD), 0, 1, TimeUnit.HOURS);
        schedule.scheduleAtFixedRate(new BlizzardUpdate(UpdateType.GUILD_ACTIVITIES), 0, 10, TimeUnit.MINUTES);
        schedule.scheduleAtFixedRate(new BlizzardUpdate(UpdateType.WOW_TOKEN), 0, 10, TimeUnit.MINUTES);
        schedule.scheduleAtFixedRate(new BlizzardUpdate(UpdateType.FULL_SYNC_ROSTERS), 1, 1, TimeUnit.HOURS);

        // Game Data Update
        schedule.scheduleAtFixedRate(new BlizzardUpdate(UpdateType.PLAYABLE_CLASS), 0, 30, TimeUnit.DAYS);
        schedule.scheduleAtFixedRate(new BlizzardUpdate(UpdateType.MYTHIC_KEYSTONE_SEASON), 0, 1, TimeUnit.DAYS);

    }



    @Override
    public void contextDestroyed(ServletContextEvent sce) {


    }
}
