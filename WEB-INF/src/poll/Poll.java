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
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.guild.Rank;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collections;

public class Poll extends GameObject
{
    //DB Structure
    public static final String POLLS_TABLE_NAME = "polls";
    public static final String POLLS_TABLE_KEY = "id";
    public static final String[] POLLS_TABLE_STRUCTURE = {"id", "user_id", "poll_question", 
                                                        "min_rank", "multi_select", "can_add_more_option", 
                                                        "start_date", "is_limit_date", "end_date", "isEnable", "isHide"};
    
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
    private boolean isHide;
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
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.id = objInfo.get("id").getAsInt();
        this.user = new User(objInfo.get("user_id").getAsInt());
        this.pollQuestion = objInfo.get("poll_question").getAsString();
        this.minRank = new Rank(objInfo.get("min_rank").getAsInt());
        this.multiSelect = objInfo.get("multi_select").getAsBoolean();
        this.canAddMoreOptions = objInfo.get("can_add_more_option").getAsBoolean();
        this.startDate = objInfo.get("start_date").getAsString();
        this.isLimitDate = objInfo.get("is_limit_date").getAsBoolean();
        if(this.isLimitDate)
            this.endDate = objInfo.get("end_date").getAsString();
        this.isEnable = objInfo.get("isEnable").getAsBoolean();
        this.isHide = objInfo.get("isHide").getAsBoolean();
        this.isData = true;
    }
    
    private void loadOptions()
    {
        this.options = new ArrayList<>();
        try {
            JsonArray opDB = dbConnect.select(PollOption.POLL_OPTION_TABLE_NAME,
                    new String[] { PollOption.POLL_OPTION_TABLE_KEY },
                    "poll_id=?",
                    new String[] {this.id+""});
            for(int i = 0; i < opDB.size(); i++)
            {
                this.options.add(new PollOption( opDB.get(i).getAsJsonObject().get(PollOption.POLL_OPTION_TABLE_KEY).getAsInt() ));
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(Poll.class, "Fail to get options in poll - "+ this.id +" - "+ ex);
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
                    this.startDate, (this.isLimitDate)? "1":"0", this.endDate, (this.isEnable)? "1":"0", (this.isHide)? "1":"0"};
        }
        else
        {
            strucInfo = new String[] {"user_id", "poll_question", 
                                    "min_rank", "multi_select", "can_add_more_option", 
                                    "start_date", "is_limit_date", "isEnable","isHide"};
            info = new String[] {this.user.getId()+"", this.pollQuestion, 
                    this.minRank.getId()+"", (this.multiSelect)? "1":"0", (this.canAddMoreOptions)? "1":"0",
                    this.startDate, (this.isLimitDate)? "1":"0", (this.isEnable)? "1":"0", (this.isHide)? "1":"0"};
        }
        /* {"user_id", "poll_question", 
         * "min_rank", "multi_select", "can_add_more_option", 
         * "start_date", is_limit_date, "end_date", "isEnable"} */
        setTableStructur(strucInfo);
        switch (saveInDBObj(info))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                if(this.options.isEmpty()) loadOptions();
                this.options.forEach((op) -> {
                    op.setPollId(this.id);
                    op.saveInDB();
                });
                return true;
        }
        return false;
    }
    
    public boolean removeOptionDB(int id)
    {
        if(this.options.isEmpty()) loadOptions();
        for(PollOption opt : this.options)
        {
            if(opt.getId() == id)
            {
                this.options.remove(opt);
                return opt.removeOption();
            }
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
    public Date getEndDateObj() {
        if (this.endDate != null) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.endDate);
            } catch (ParseException e) {
                Logs.errorLog(this.getClass(), "FAILED - to parse date "+ e);
            }
        }
        return null;
    }
    public List<PollOption> getOptions() 
    { 
        loadOptions();        
        Collections.sort(this.options);
        return this.options; 
    }
    public PollOption getOption(int id) 
    {
        if(this.options.isEmpty()) loadOptions();
        for(PollOption opt : this.options)
        {
            if(opt.getId() == id) return opt;
        }
        return null;
    }
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
                Logs.errorLog(Poll.class, "Fail to convert end date in poll - "+ this.id +" - "+ ex);
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
    public void setEnable(boolean isEnable) { this.isEnable = isEnable; this.saveInDB(); }
    public void setHide() { this.isHide = true; this.saveInDB(); }
    public void addOption(PollOption op) { this.options.add(op); }
    public int addOption(String s, User u) 
    {
        if(this.isEnable)
        {            
            PollOption addOption = new PollOption();
            addOption.setPollId(this.id);
            addOption.setOptionText(s);
            addOption.setOwner(u);
            addOption.setDate(Update.getCurrentTimeStamp());
            addOption.setIsData(true);
            if(!this.multiSelect)
            {
                for(PollOption opt : this.options)
                {
                    opt.removeResult(u);
                }
            }
            addOption.saveInDB();
            addOption.addResult(u);
            this.options.add(addOption);
            return addOption.getId();
        }
        else
        {
            return -1;
        }
    }
    
    public boolean addResult(int optId, User u)
    {
        if(this.isEnable)
        {            
            if(this.options.isEmpty()) loadOptions();  
            boolean r = false;
            for(PollOption opt : this.options)
            {
                if(!this.multiSelect)
                {
                    opt.removeResult(u);
                }
                if(opt.getId() == optId)
                {
                    r = opt.addResult(u);
                }
            }
            return r;
        }
        return false;
    }
    
    public boolean removeResult(int optId, User u)
    {
        if(this.isEnable)
        {
            if(this.options.isEmpty()) loadOptions();
            for(PollOption opt : this.options)
            {
                if(opt.getId() == optId)
                {
                    return opt.removeResult(u);
                }
            }            
        }
        return false;
    }

}