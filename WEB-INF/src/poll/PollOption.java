/**
 * File : PollOption.java
 * Desc : PollOption Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.poll;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import static com.blizzardPanel.gameObject.GameObject.SAVE_MSG_INSERT_OK;
import static com.blizzardPanel.gameObject.GameObject.SAVE_MSG_UPDATE_OK;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PollOption extends GameObject
{
    //DB Structure
    public static final String POLL_OPTION_TABLE_NAME = "poll_options";
    public static final String POLL_OPTION_TABLE_KEY = "id";
    public static final String[] POLL_OPTION_TABLE_STRUCTURE = {"id", "poll_id", "option_txt", "owner_id", "date"};
    
    //Atributes
    private int id = -1;
    private int pollId;
    private String optionText;
    private User owner;
    private String date;
    private List<PollOptionResult> results = new ArrayList<>();
    
    public PollOption()
    {
        super(POLL_OPTION_TABLE_NAME, POLL_OPTION_TABLE_KEY, POLL_OPTION_TABLE_STRUCTURE);        
    }
    
    public PollOption(int id)
    {
        super(POLL_OPTION_TABLE_NAME, POLL_OPTION_TABLE_KEY, POLL_OPTION_TABLE_STRUCTURE);
        loadFromDB(id);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        this.id = (Integer) objInfo.get("id");
        this.pollId = (Integer) objInfo.get("poll_id");
        this.optionText = objInfo.get("option_txt").toString();
        this.owner = new User((Integer) objInfo.get("owner_id"));
        this.date = objInfo.get("date").toString();
        loadSelected();
        this.isData = true;
    }
    
    private void loadSelected()
    {
        try {
            JSONArray rDB = dbConnect.select(PollOptionResult.POLL_OPTION_RESULTS_TABLE_NAME,
                                            new String[] { PollOptionResult.POLL_OPTION_RESULTS_TABLE_KEY },
                                            "poll_option_id=?",
                                            new String[] { this.id+""});
            for(int i = 0; i < rDB.size(); i++)
            {
                results.add(new PollOptionResult((Integer) ((JSONObject)rDB.get(i)).get(PollOptionResult.POLL_OPTION_RESULTS_TABLE_KEY) ));
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to get options result in poll - "+ this.id +" - "+ ex);
        }
    }

    @Override
    public boolean saveInDB() 
    {
        setTableStructur(DBStructure.outKey(POLL_OPTION_TABLE_STRUCTURE));
        /* {"poll_id", "option_txt", "owner_id", "date"} */
        switch (saveInDBObj(new String[] {this.pollId+"", this.optionText, this.owner.getId()+"", this.date}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;
        }
        return false;
    }
    
    //Getters and Setters
    @Override
    public int getId() { return this.id; }
    public int getPollId() { return this.pollId; }
    public String getOptionText() { return this.optionText; }
    public User getOwner() { return this.owner; }
    public String getDate() { return this.date; }
    public List<PollOptionResult> getResult() { return this.results; }

    @Override
    public void setId(int id) { this.id = id; }
    public void setPollId(int id) { this.pollId = id; }
    public void setOptionText(String text) { this.optionText = text; }
    public void setOwner(User u) { this.owner = u; }
    public void setDate(String date) { this.date = date; }
    
}