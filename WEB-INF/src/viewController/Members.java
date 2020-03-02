/**
 * File : Members.java
 * Desc : members.jsp view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class Members
{
    //Variable
    private final DBConnect dbConnect;
    private CharacterMember[] membersList;

    public Members()
    {
        dbConnect = new DBConnect();
    }
    
    private void generateMembersList()
    {
        try
        {            
            //Only show members who logged in at least 1 month ago
            Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MONTH, -1);
            Date oneMotheAgo = cal.getTime();
            
            //Prepare list members
            List<CharacterMember> mList = new ArrayList<>();
            //Get members to DB		
            
            //select gm.internal_id from gMembers_id_name gm, character_info c where in_guild=1 AND gm.internal_id = c.internal_id AND c.lastModified > 1539003688424;
            JsonArray dbList = dbConnect.select(CharacterMember.GMEMBER_ID_NAME_TABLE_NAME +" gm, "+ CharacterMember.CHARACTER_INFO_TABLE_NAME +" c",
                                                new String[] {"gm.internal_id"},
                                                "in_guild=? AND gm.internal_id = c.internal_id AND c.lastModified > ? AND gm.isDelete=?"+
                                                " ORDER BY gm.rank ASC, c.level DESC, gm.member_name ASC", 
                                                new String[] {"1", oneMotheAgo.getTime() +"", "0"}, true);	
            for(int i = 0; i < dbList.size(); i++)
            {
                int idMember = dbList.get(i).getAsJsonObject().get("internal_id").getAsInt() ;
                CharacterMember member = new CharacterMember(idMember);
                //If data is successful load, save a member
                if(member.isData())
                {
                    member.getItemLevel();
                    mList.add(member);
                }
            }
            //Convert LIST to simple Member Array
            if(mList.size() > 0) this.membersList = mList.toArray(new CharacterMember[mList.size()]);
        }
        catch (SQLException|DataException e)
        {
            Logs.errorLog(Members.class, "Fail to load members lists - Members View Controller "+ e);
        }
    }
    
    public CharacterMember getMember(int id) 
    {
        if(this.membersList == null) return new CharacterMember(id);
        for(CharacterMember m : this.membersList)
        {
            if(m.getId() == id) return m;
        }
        return new CharacterMember(id);
    }
    
    public CharacterMember[] getMembersList() 
    { 
        if(this.membersList == null) 
            generateMembersList(); 
        return this.membersList; 
    }
}