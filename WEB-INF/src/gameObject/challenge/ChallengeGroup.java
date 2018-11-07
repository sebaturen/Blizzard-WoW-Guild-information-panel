/**
 * File : ChallengeGroup.java
 * Desc : Guild Challenges object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject.challenge;

import com.artOfWar.DataException;
import com.artOfWar.gameObject.DBStructure;
import com.artOfWar.gameObject.GameObject;
import com.artOfWar.gameObject.Member;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChallengeGroup extends GameObject
{
    //Attribute
    private int id;
    private int challengeId;
    private Date timeDate;
    private int timeHours;
    private int timeMinutes;
    private int timeSeconds;
    private int timeMilliseconds;
    private boolean isPositive;
    private List<Member> members;
        
    //Constructor
    public ChallengeGroup(int id)
    {        
        super(CHALLENGE_GROUPS_TABLE_NAME, CHALLENGE_GROUPS_TABLE_KEY, CHALLENGE_GROUPS_TABLE_STRUCTURE);
        members = new ArrayList<>();
        loadFromDB(id +"");
        loadMembersFromDB();
    }
    
    //Load to JSON
    public ChallengeGroup(int challengeId, JSONObject challengeGroup)
    {
        super(CHALLENGE_GROUPS_TABLE_NAME, CHALLENGE_GROUPS_TABLE_KEY, CHALLENGE_GROUPS_TABLE_STRUCTURE);
        this.challengeId = challengeId;
        members = new ArrayList<>();
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
                    cMem.setSpec( (Integer) ((JSONObject) dbMem.get(i)).get("spec_id"));
                    members.add(cMem);                    
                }
            }
        } catch (SQLException | DataException ex) {
            System.out.println("Fail to load members from challenge group id: "+ this.id);
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
                System.out.println("(Blizz) Fail to convert date from challenge group! "+ this.id);
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
                System.out.println("(DB) Fail to convert date from challenge group! "+ this.id);
            }
        }
        this.isData = true;
    }

    @Override
    public boolean saveInDB()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(timeDate); 
        String isPostSQL = (this.isPositive)? "1":"0";
        setTableStructur(DBStructure.outKey(CHALLENGE_GROUPS_TABLE_STRUCTURE));
        /* {"challenge_id", "time_date",
         * "time_hours", "time_minutes", "time_seconds",
         * "time_milliseconds", "is_positive"};
         */
        int saveValue = saveInDBObj(new String[] {this.challengeId +"", strDate, 
                                            this.timeHours +"", this.timeMinutes +"", this.timeSeconds +"", 
                                            this.timeMilliseconds +"", isPostSQL});
        switch (saveValue)
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                //Save members
                members.forEach((m) -> {                    
                    try {
                        System.out.println("("+ m.getInternalID() +") "+ m.getName());
                        /*{"internal_member_id", "group_id", "spec_id"};*/
                        dbConnect.insert(CHALLENGE_GROUP_MEMBERS_TABLE_NAME,
                                        CHALLENGE_GROUP_MEMBERS_TABLE_KEY,
                                        DBStructure.outKey(CHALLENGE_GROUP_MEMBERS_TABLE_STRUCTURE),
                                        new String[] {m.getInternalID() +"", this.id +"", m.getActiveSpec().getId() +""},
                                        "ON DUPLICATE KEY UPDATE spec_id=?",
                                        new String[] { m.getActiveSpec().getId() +"" });
                    } catch (DataException|ClassNotFoundException ex) {
                        System.out.println("Fail to save members in groups: "+ ex);
                    }
                });
                return true;
        }
        return false;
    }
    
    //Getters/Setters
    public void setTimeDate(Date timDate) { this.timeDate = timDate; }
    public void setTimeHours(int timeHours) { this.timeHours = timeHours; }
    public void setTimeMinutes(int timeMinutes) { this.timeMinutes = timeMinutes; }
    public void setTimeSeconds(int timeSeconds) { this.timeSeconds = timeSeconds; }
    public void setTimeMilliseconds(int timeMilliseconds) { this.timeMilliseconds = timeMilliseconds; }
    public void setPositive(boolean isPositive) { this.isPositive = isPositive; }
    public void addMember(Member mb) { members.add(mb); }
    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }
	
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