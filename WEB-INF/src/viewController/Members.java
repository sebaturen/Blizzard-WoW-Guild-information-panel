/**
 * File : Members.java
 * Desc : members.jsp view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;
import com.artOfWar.blizzardAPI.APIInfo;
import com.artOfWar.blizzardAPI.Update;
import com.artOfWar.gameObject.Member;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Members
{
    //Variable
    private final DBConnect dbConnect;
    private Member[] membersList;

    public Members()
    {
        dbConnect = new DBConnect();
        generateMembersList();
    }
    
    private void generateMembersList()
    {
        try
        {
            //Prepare list members
            List<Member> mList = new ArrayList<>();
            //Get members to DB			
            JSONArray dbList = dbConnect.select(Update.GMEMBERS_ID_TABLE, 
                                                new String[] {"internal_id", "member_name"},
                                                "in_guild=? AND realm=?", 
                                                new String[] {"1", APIInfo.GUILD_REALM});	
            for(int i = 0; i < dbList.size(); i++)
            {
                int idMember = (int) ((JSONObject) dbList.get(i)).get("internal_id");
                Member member = new Member(idMember);
                //If data is successful load, save a member
                if(member.isData()) mList.add(member);
            }
            //Convert LIST to simple Member Array
            if(mList.size() > 0) this.membersList = mList.toArray(new Member[mList.size()]);
        }
        catch (SQLException|DataException e)
        {
            System.out.println("Fail to load members lists - Members View Controller");
        }
    }
	
    public Member[] getMembersList() { return this.membersList; }
}