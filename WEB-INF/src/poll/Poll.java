/**
 * File : Poll.java
 * Desc : Poll Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.poll;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBStructure;
import static com.blizzardPanel.gameObject.GameObject.SAVE_MSG_INSERT_OK;
import static com.blizzardPanel.gameObject.GameObject.SAVE_MSG_UPDATE_OK;
import com.blizzardPanel.gameObject.guild.Rank;
import static com.blizzardPanel.poll.Poll.POLLS_TABLE_STRUCTURE;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Poll extends GameObject
{
    //DB Structure
    public static final String POLLS_TABLE_NAME = "polls";
    public static final String POLLS_TABLE_KEY = "id";
    public static final String[] POLLS_TABLE_STRUCTURE = {"id", "user_id", "poll_question", 
                                                        "min_rank", "multi_select", "can_add_more_option", 
                                                        "start_date", "is_limit_date", "end_date", "isEnable"};
    
    //Atribute
    private int id = -1;
    private User user;
    private String pollQuestion;
    private Rank minRank;
    private boolean multiSelect;
    private boolean canAddMoreOptions;
    private String startDate;
    private boolean isLimitDate;
    private String endDate;
    private boolean isEnable;
    private List<PollOption> options = new ArrayList<>();
    
    public Poll()
    {
        super(POLLS_TABLE_NAME, POLLS_TABLE_KEY, POLLS_TABLE_STRUCTURE);        
    }
    
    public Poll(int id)
    {
        super(POLLS_TABLE_NAME, POLLS_TABLE_KEY, POLLS_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        this.id = (Integer) objInfo.get("id");
        this.user = new User((Integer) objInfo.get("user_id"));
        this.pollQuestion = objInfo.get("poll_question").toString();
        this.minRank = new Rank((Integer) objInfo.get("min_rank"));
        this.multiSelect = (Boolean) objInfo.get("multi_select");
        this.canAddMoreOptions = (Boolean) objInfo.get("can_add_more_option");
        this.startDate = objInfo.get("start_date").toString();
        this.isLimitDate = (Boolean) objInfo.get("is_limit_date");
        if(objInfo.containsKey("end_data"))
            this.endDate = objInfo.get("end_date").toString();
        this.isEnable = (Boolean) objInfo.get("isEnable");
        loadOptions();
        this.isData = true;
    }
    
    private void loadOptions()
    {
        try {
            JSONArray opDB = dbConnect.select(PollOption.POLL_OPTION_TABLE_NAME,
                    new String[] { PollOption.POLL_OPTION_TABLE_KEY },
                    "poll_id=?",
                    new String[] {this.id+""});
            for(int i = 0; i < opDB.size(); i++)
            {
                options.add(new PollOption((Integer) ((JSONObject)opDB.get(i)).get(PollOption.POLL_OPTION_TABLE_KEY) ));
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to get options in poll - "+ this.id +" - "+ ex);
        }
    }

    @Override
    public boolean saveInDB() 
    {
        String[] strucInfo;
        String[] info;
        if(this.endDate != null)
        {
            strucInfo = DBStructure.outKey(POLLS_TABLE_STRUCTURE);
            info = new String[] {this.user.getId()+"", this.pollQuestion, 
                    this.minRank.getId()+"", (this.multiSelect)? "1":"0", (this.canAddMoreOptions)? "1":"0",
                    this.startDate, (this.isLimitDate)? "1":"0", this.endDate, (this.isEnable)? "1":"0"};
        }
        else
        {
            strucInfo = new String[] {"user_id", "poll_question", 
                                    "min_rank", "multi_select", "can_add_more_option", 
                                    "start_date", "is_limit_date", "isEnable"};
            info = new String[] {this.user.getId()+"", this.pollQuestion, 
                    this.minRank.getId()+"", (this.multiSelect)? "1":"0", (this.canAddMoreOptions)? "1":"0",
                    this.startDate, (this.isLimitDate)? "1":"0", (this.isEnable)? "1":"0"};
        }
        /* {"user_id", "poll_question", 
         * "min_rank", "multi_select", "can_add_more_option", 
         * "start_date", is_limit_date, "end_date", "isEnable"} */
        setTableStructur(strucInfo);
        switch (saveInDBObj(info))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                this.options.forEach((op) -> {
                    op.setPollId(this.id);
                    op.saveInDB();
                });
                return true;
        }
        return false;
    }
    
    //Getters and Setters
    @Override
    public int getId() { return this.id; }
    public User getUser() { return this.user; }
    public String getPollQuestion() { return this.pollQuestion; }
    public Rank getMinRank() { return this.minRank; }
    public String getStartDate() { return this.startDate; }
    public String getEndDate() { return this.endDate; }
    public List<PollOption> getOptions() { return this.options; }
    public boolean isMultiSelect() { return this.multiSelect; }
    public boolean isCanAddMoreOptions() { return this.canAddMoreOptions; }
    public boolean isEnable() 
    {
        //Valid if not end date.
        if (this.isLimitDate && this.isEnable)
        {
            try {
                Date newDate = new Date();
                Date enDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(this.endDate);
                if(newDate.compareTo(enDate) > 0)
                {
                    this.isEnable = false;
                    saveInDB();
                }
            } catch (ParseException ex) {
                Logs.saveLogln("Fail to convert end date in poll - "+ this.id +" - "+ ex);
                this.isEnable = false;
                saveInDB(); //save a new false
            }
        }
        return this.isEnable;
    }
    
    @Override
    public void setId(int id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setPollQuestion(String pollQuestion) { this.pollQuestion = pollQuestion; } 
    public void setMinRank(Rank minRank) { this.minRank = minRank; }
    public void setMultiSelect(boolean multiSelect) { this.multiSelect = multiSelect; }
    public void setCanAddMoreOptions(boolean canAddMoreOptions) { this.canAddMoreOptions = canAddMoreOptions; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setIsLimitDate(boolean isLimitDate) { this.isLimitDate = isLimitDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setIsEnable(boolean isEnable) { this.isEnable = isEnable; }
    public void addOption(PollOption op) { this.options.add(op); }
    
}