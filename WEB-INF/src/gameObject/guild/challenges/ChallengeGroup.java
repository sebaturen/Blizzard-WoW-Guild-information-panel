/**
 * File : ChallengeGroup.java
 * Desc : Guild Challenges object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.guild.challenges;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.characters.Member;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChallengeGroup extends GameObject
{
    //Challenge Groups DB
    public static final String CHALLENGE_GROUPS_TABLE_NAME = "guild_challenge_groups";
    public static final String CHALLENGE_GROUPS_TABLE_KEY = "group_id";
    public static final String[] CHALLENGE_GROUPS_TABLE_STRUCTURE = {"group_id", "challenge_id", "time_date",
                                                                     "time_hours", "time_minutes", "time_seconds",
                                                                     "time_milliseconds", "is_positive"};
    //Challenge group Members DB
    public static final String CHALLENGE_GROUP_MEMBERS_TABLE_NAME = "guild_challenge_group_members";
    public static final String CHALLENGE_GROUP_MEMBERS_TABLE_KEY = "member_in_group_id";
    public static final String[] CHALLENGE_GROUP_MEMBERS_TABLE_STRUCTURE = {"member_in_group_id", "internal_member_id",
                                                                            "group_id", "character_spec_id"};
    
    //Attribute
    private int id;
    private int challengeId;
    private Date timeDate;
    private int timeHours;
    private int timeMinutes;
    private int timeSeconds;
    private int timeMilliseconds;
    private boolean isPositive;
    private List<Member> members = new ArrayList<>();
        
    //Constructor
    public ChallengeGroup(int id)
    {        
        super(CHALLENGE_GROUPS_TABLE_NAME, CHALLENGE_GROUPS_TABLE_KEY, CHALLENGE_GROUPS_TABLE_STRUCTURE);
        loadFromDB(id);
        loadMembersFromDB();
    }
    
    //Load to JSON
    public ChallengeGroup(int challengeId, JSONObject challengeGroup)
    {
        super(CHALLENGE_GROUPS_TABLE_NAME, CHALLENGE_GROUPS_TABLE_KEY, CHALLENGE_GROUPS_TABLE_STRUCTURE);
        this.challengeId = challengeId;
        saveInternalInfoObject(challengeGroup);
    }
    
    private void loadMembersFromDB()
    {
        try {
            //dbConnect, select * from challenge_group_members where group_id = this.id;
            JSONArray dbMem = dbConnect.select(CHALLENGE_GROUP_MEMBERS_TABLE_NAME,
                                                DBStructure.outKey(CHALLENGE_GROUP_MEMBERS_TABLE_STRUCTURE),
                                                "group_id=?",
                                                new String[] {this.id +""});
            for(int i = 0; i < dbMem.size(); i++)
            {
                Member cMem = new Member( (Integer) ((JSONObject) dbMem.get(i)).get("internal_member_id"));
                if(cMem.isData())
                {
                    cMem.setActiveSpec( (Integer) ((JSONObject) dbMem.get(i)).get("character_spec_id") );
                    members.add(cMem);                    
                }
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to load members from challenge group id: "+ this.id);
        }
    }

    @Override
    protected void saveInternalInfoObject(JSONObject exInfo)
    {
        if(exInfo.containsKey("group_id")) this.id = ((Integer) exInfo.get("group_id"));
        if(exInfo.containsKey("time"))
        {
            //info come to blizzard
            JSONObject dataRun = (JSONObject) exInfo.get("time");
            this.timeHours = ((Long) dataRun.get("hours")).intValue();
            this.timeMinutes = ((Long) dataRun.get("minutes")).intValue();
            this.timeSeconds = ((Long) dataRun.get("seconds")).intValue();
            this.timeMilliseconds = ((Long) dataRun.get("milliseconds")).intValue();
            this.isPositive = (Boolean) dataRun.get("isPositive");
            try {
                this.timeDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").parse(exInfo.get("date").toString());
            } catch (ParseException ex) {
                Logs.saveLogln("(Blizz) Fail to convert date from challenge group! "+ this.id +" - "+ ex);
            }
        }
        else
        {
            this.challengeId = (Integer) exInfo.get("challenge_id");
            this.timeHours = (Integer) exInfo.get("time_hours");
            this.timeMinutes = (Integer) exInfo.get("time_minutes");
            this.timeSeconds = (Integer) exInfo.get("time_seconds");
            this.timeMilliseconds = (Integer) exInfo.get("time_milliseconds");
            this.isPositive = (Boolean) exInfo.get("is_positive");
            try { //2018-10-17 02:39:00
                this.timeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(exInfo.get("time_date").toString());
            } catch (ParseException ex) {
                Logs.saveLogln("(DB) Fail to convert date from challenge group! "+ this.id +" - "+ ex);
            }
        }
        this.isData = true;
    }

    @Override
    public boolean saveInDB()
    {
        String isPostSQL = (this.isPositive)? "1":"0";
        setTableStructur(DBStructure.outKey(CHALLENGE_GROUPS_TABLE_STRUCTURE));
        /* {"challenge_id", "time_date",
         * "time_hours", "time_minutes", "time_seconds",
         * "time_milliseconds", "is_positive"};
         */
        int saveValue = saveInDBObj(new String[] {this.challengeId +"", getDBDate(this.timeDate), 
                                            this.timeHours +"", this.timeMinutes +"", this.timeSeconds +"", 
                                            this.timeMilliseconds +"", isPostSQL});
        switch (saveValue)
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                //Save members
                this.members.forEach((m) -> {                    
                    try {
                        JSONArray memInGroupId = null;
                        try {
                            //Verificate if this memers is previewsly register from this group
                            memInGroupId = dbConnect.select(CHALLENGE_GROUP_MEMBERS_TABLE_NAME,
                                    new String[] { "member_in_group_id" },
                                    "internal_member_id=? AND group_id=?",
                                    new String[] { m.getId() +"", this.id +"" } );
                        } catch (SQLException ex) {
                            Logs.saveLogln("Fail to get memberInGroupID "+ ex);
                        }
                        //Insert or update... if need insert is because not is register :D
                        if ( (memInGroupId == null) || (memInGroupId.isEmpty()) )
                        {//insert
                            dbConnect.insert(CHALLENGE_GROUP_MEMBERS_TABLE_NAME,
                                            CHALLENGE_GROUP_MEMBERS_TABLE_KEY,
                                            new String[] { "internal_member_id", "group_id", "character_spec_id" },
                                            new String[] { m.getId() +"", this.id +"", m.getActiveSpec().getId() +"" });
                        }
                    } catch (DataException|ClassNotFoundException|SQLException ex) {
                        Logs.saveLogln("Fail to save members in groups: "+ ex);
                    }
                });
                return true;
        }
        return false;
    }
    
    //Getters/Setters
    @Override
    public void setId(int id) { this.id = id; }
    public void setTimeDate(Date timDate) { this.timeDate = timDate; }
    public void setTimeHours(int timeHours) { this.timeHours = timeHours; }
    public void setTimeMinutes(int timeMinutes) { this.timeMinutes = timeMinutes; }
    public void setTimeSeconds(int timeSeconds) { this.timeSeconds = timeSeconds; }
    public void setTimeMilliseconds(int timeMilliseconds) { this.timeMilliseconds = timeMilliseconds; }
    public void setPositive(boolean isPositive) { this.isPositive = isPositive; }
    public void addMember(Member mb) { members.add(mb); }
    @Override
    public int getId() { return this.id; }
    public Date getTimeDate() { return this.timeDate; }
    public int getTimeHour() { return this.timeHours; }
    public int getTimeMinutes() { return this.timeMinutes; }
    public int getTimeSeconds() { return this.timeSeconds; }
    public int getTimeMilliseconds() { return this.timeMilliseconds; }
    public boolean isPositive() { return this.isPositive; }
    public List<Member> getMembers() { return this.members; }

    @Override
    public String toString()
    {
        String out = "\t\t"+ this.id +" - "+ this.timeDate +"\n"+
                    "\t\t"+this.timeHours +":"+ this.timeMinutes +":"+ this.timeSeconds +
                    " ("+ this.timeMilliseconds +") -> "+ this.isPositive;
        out += "\n\t\tMembers!!////////////\n";
        out = members.stream().map((mb) -> "\t\t\t"+ mb.toString() +"\n").reduce(out, String::concat);
        out += "\t\tEND MEMBERS!!///////////";
        return out;
    }
}