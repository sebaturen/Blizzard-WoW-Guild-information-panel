/**
 * File : Guild.java
 * Desc : Guild Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild;

import com.blizzardPanel.gameObject.guild.achievement.GuildAchievement;
import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import static com.blizzardPanel.blizzardAPI.Update.parseUnixTime;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Guild extends GameObject
{
    //Guild DB
    public static final String GUILD_TABLE_NAME = "guild_info";
    public static final String GUILD_TABLE_KEY = "id";
    public static final String[] GUILD_TABLE_STRUCTURE = {"id", "name", "realm", "realm_slug", "lastModified", "battlegroup",
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
    private List<New> news = new ArrayList<>();

    //Constructor
    public Guild()
    {
        super(GUILD_TABLE_NAME, GUILD_TABLE_KEY, GUILD_TABLE_STRUCTURE);
        //Load guild from DB
        loadFromDB(1); //asumed the first guild is only this guild (this plataform)
    }

    //Load to JSON
    public Guild(JSONObject guildInfo)
    {
        super(GUILD_TABLE_NAME, GUILD_TABLE_KEY, GUILD_TABLE_STRUCTURE);
        saveInternalInfoObject(guildInfo);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject guildInfo)
    {
        this.name = guildInfo.get("name").toString();
        this.lastModified = Long.parseLong(guildInfo.get("lastModified").toString());
        this.battleGroup = guildInfo.get("battlegroup").toString();
        this.achievementPoints = Long.parseLong(guildInfo.get("achievementPoints").toString());
        this.realm = guildInfo.get("realm").toString();
        if(guildInfo.get("level").getClass() == java.lang.Long.class)
        {//if info come to blizzAPI
            this.level = ((Long) guildInfo.get("level")).intValue();
            this.side =  ((Long) guildInfo.get("side")).intValue();
            loadAchievementsFromBlizz((JSONObject) guildInfo.get("achievements"));
        }
        else
        {
            this.id = (Integer) guildInfo.get("id");
            this.level = (Integer) guildInfo.get("level");
            this.side =  (Integer) guildInfo.get("side");
            if(guildInfo.get("realmSlug") != null)
                this.realmSlug = guildInfo.get("realmSlug").toString();
        }
        this.isData = true;
    }

    private void loadAchievementsFromBlizz(JSONObject respond)
    {
        JSONArray achivs = (JSONArray)respond.get("achievementsCompleted");
        JSONArray achivTimes = (JSONArray)respond.get("achievementsCompletedTimestamp");
        for(int i = 0; i < achivs.size(); i++)
        {
            int idAchiv = ((Long) achivs.get(i)).intValue();
            //Save achivement
            GuildAchievement gAHDB = new GuildAchievement(idAchiv);
            if(!gAHDB.isInternalData())
            {
                //Create achivement
                String achivTime = parseUnixTime(((Long) achivTimes.get(i)).toString());
                JSONObject infoAchiv = new JSONObject();
                infoAchiv.put("achievement_id", idAchiv);
                infoAchiv.put("time_completed", achivTime);

                gAHDB = new GuildAchievement(infoAchiv);
            }
            this.achievements.add(gAHDB);
        }
    }

    private void loadAchievementsFromDB()
    {
        try {
            JSONArray dbAchiv = dbConnect.select(GuildAchievement.GUILD_ACHIEVEMENTS_TABLE_NAME,
                                                new String[] {GuildAchievement.GUILD_ACHIEVEMENTS_TABLE_KEY},
                                                "1=? ORDER BY time_completed DESC",
                                                new String[] {"1"});
            for(int i = 0; i < dbAchiv.size(); i++)
            {
                int idAchiv = (Integer) ((JSONObject)dbAchiv.get(i)).get(GuildAchievement.GUILD_ACHIEVEMENTS_TABLE_KEY);
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
            JSONArray dbAchiv = dbConnect.select(New.GUILD_NEWS_TABLE_NAME,
                                                new String[] {New.GUILD_NEWS_TABLE_KEY},
                                                "1=? ORDER BY timestamp DESC LIMIT "+ cant,
                                                new String[] {"1"});
            for(int i = 0; i < dbAchiv.size(); i++)
            {
                int idAchiv = (Integer) ((JSONObject)dbAchiv.get(i)).get(New.GUILD_NEWS_TABLE_KEY);
                New gAh = new New(idAchiv);
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
        setTableStructur(DBStructure.outKey(GUILD_TABLE_STRUCTURE));
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
    public List<New> getNews(int cant)
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
