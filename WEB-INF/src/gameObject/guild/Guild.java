/**
 * File : Guild.java
 * Desc : Guild Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild;

import com.blizzardPanel.gameObject.guild.achievement.GuildAchievement;
import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import static com.blizzardPanel.update.blizzard.Update.parseUnixTime;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Guild extends GameObject
{
    //Guild DB
    public static final String TABLE_NAME = "guild_info";
    public static final String TABLE_KEY = "id";
    public static final String[] TABLE_STRUCTURE = {"id", "name", "realm", "realm_slug", "lastModified", "battlegroup",
                                                        "level", "side", "achievementPoints"};
    //Attribute
    private int id;
    private String name;
    private String realm;
    private String realmSlug;
    private String battleGroup;
    private long lastModified;
    private long achievementPoints;
    private int level;
    private int side;
    private List<GuildAchievement> achievements = new ArrayList<>();
    private Date lastNewsUpdate;
    private List<Activity> news = new ArrayList<>();

    //Constructor
    public Guild()
    {
        super(TABLE_NAME, TABLE_KEY, TABLE_STRUCTURE);
        //Load guild from DB
        loadFromDB(1); //asumed the first guild is only this guild (this plataform)
    }

    //Load to JSON
    public Guild(JsonObject guildInfo)
    {
        super(TABLE_NAME, TABLE_KEY, TABLE_STRUCTURE);
        saveInternalInfoObject(guildInfo);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject guildInfo)
    {
        this.name = guildInfo.get("name").getAsString();
        this.lastModified = Long.parseLong(guildInfo.get("lastModified").getAsString());
        this.battleGroup = guildInfo.get("battlegroup").getAsString();
        this.achievementPoints = Long.parseLong(guildInfo.get("achievementPoints").getAsString());
        this.realm = guildInfo.get("realm").getAsString();
        this.level = guildInfo.get("level").getAsInt();
        this.side = guildInfo.get("side").getAsInt();

        if(guildInfo.has("id")) { // load from DB
            this.id = guildInfo.get("id").getAsInt();
            if (guildInfo.has("realmSlug") && !guildInfo.get("realmSlug").isJsonNull())
                this.realmSlug = guildInfo.get("realmSlug").getAsString();
        } else { // load from blizz
            loadAchievementsFromBlizz(guildInfo.get("achievements").getAsJsonObject());
        }

        this.isData = true;

    }

    private void loadAchievementsFromBlizz(JsonObject respond)
    {
        JsonArray achivs = respond.get("achievementsCompleted").getAsJsonArray();
        JsonArray achivTimes = respond.get("achievementsCompletedTimestamp").getAsJsonArray();
        for(int i = 0; i < achivs.size(); i++)
        {
            int idAchiv = achivs.get(i).getAsInt();
            //Save achivement
            GuildAchievement gAHDB = new GuildAchievement(idAchiv);
            if(!gAHDB.isInternalData())
            {
                //Create achivement
                String achivTime = parseUnixTime( achivTimes.get(i).getAsString() );
                JsonObject infoAchiv = new JsonObject();
                infoAchiv.addProperty("achievement_id", idAchiv);
                infoAchiv.addProperty("time_completed", achivTime);

                gAHDB = new GuildAchievement(infoAchiv);
            }
            this.achievements.add(gAHDB);
        }
    }

    private void loadAchievementsFromDB()
    {
        try {
            JsonArray dbAchiv = dbConnect.select(GuildAchievement.TABLE_NAME,
                                                new String[] {GuildAchievement.TABLE_KEY},
                                                "1=? ORDER BY time_completed DESC",
                                                new String[] {"1"});
            for(int i = 0; i < dbAchiv.size(); i++)
            {
                int idAchiv = dbAchiv.get(i).getAsJsonObject().get(GuildAchievement.TABLE_KEY).getAsInt();
                GuildAchievement gAh = new GuildAchievement(idAchiv);
                this.achievements.add(gAh);
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(Guild.class, "Fail to load guild Achievements "+ ex);
        }
    }

    private void loadNews(int cant)
    {
        this.news = new ArrayList<>();
        try {
            JsonArray dbAchiv = dbConnect.select(Activity.TABLE_NAME,
                                                new String[] {Activity.TABLE_KEY},
                                                "1=? ORDER BY timestamp DESC LIMIT "+ cant,
                                                new String[] {"1"});
            for(int i = 0; i < dbAchiv.size(); i++)
            {
                int idAchiv = dbAchiv.get(i).getAsJsonObject().get(Activity.TABLE_KEY).getAsInt();
                Activity gAh = new Activity(idAchiv);
                this.news.add(gAh);
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(Guild.class, "Fail to load guild news "+ ex);
        }
        this.lastNewsUpdate = new Date();
    }

    @Override
    public boolean saveInDB()
    {
        /* {"name", "realm","lastModified", "battlegroup",
         * "level", "side", "achievementPoints"};
         */
        setTableStructur(DBStructure.outKey(TABLE_STRUCTURE));
        String[] values = { this.name,
                            this.realm,
                            this.realmSlug,
                            this.lastModified +"",
                            this.battleGroup,
                            this.level +"",
                            this.side +"",
                            this.achievementPoints +"" };
        switch (saveInDBObj(values))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                this.achievements.forEach(aH -> {
                    if(!aH.isInternalData())
                        aH.saveInDB();
                });
                return true;
        }
        return false;
    }

    //GETTERS
    @Override
    public void setId(int id) { this.id = id; }
    public void setRealmSlug(String realmSlug) { this.realmSlug = realmSlug; }

    @Override
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public String getRealm() { return this.realm; }
    public String getRealmSlug() { return this.realmSlug; }
    public String getBattleGroup() { return this.battleGroup; }
    public long getLastModified() { return this.lastModified; }
    public long getAchievementPoints() { return this.achievementPoints; }
    public List<GuildAchievement> getAchievements() { loadAchievementsFromDB(); return this.achievements; }
    public List<Activity> getNews(int cant)
    {
        if(this.news.isEmpty())
        {
            loadNews(cant);
        }
        else
        {
            //Only reload if least 10 min ago
            Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, -10);
            Date tenMinuteAgo = cal.getTime();
            if(this.lastNewsUpdate.compareTo(tenMinuteAgo) < 0)
            {
                loadNews(cant);
            }
        }
        return this.news;
    }
    public int getLevel() { return this.level; }
    public int getSide() { return this.side; }

    //two guild equals method
    @Override
    public boolean equals(Object o)
    {
        if(o == this) return true;
        if(o == null || (this.getClass() != o.getClass())) return false;

        String oName = ((Guild) o).getName();
        long oLastModified = ((Guild) o).getLastModified();
        return (
                oName.equals(this.name)
                &&
                (Long.compare(oLastModified, this.lastModified) == 0)
                );
    }

}
