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
            //Only show members who logged in at least 1 month ago
            Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MONTH, -1);
            Date oneMotheAgo = cal.getTime();
            
            //Prepare list members
            List<CharacterMember> mList = new ArrayList<>();
            //Get members to DB		
            
            //select gm.internal_id from gMembers_id_name gm, character_info c where in_guild=1 AND gm.internal_id = c.internal_id AND c.lastModified > 1539003688424;
            //Convert LIST to simple Member Array
            if(mList.size() > 0) this.membersList = mList.toArray(new CharacterMember[mList.size()]);

    }
    
    public CharacterMember getMember(int id) 
    {
        //if(this.membersList == null) return new CharacterMember(id);
        for(CharacterMember m : this.membersList)
        {
            //if(m.getId() == id) return m;
        }
        //return new CharacterMember(id);
        return null;
    }
    
    public CharacterMember[] getMembersList() 
    { 
        if(this.membersList == null) 
            generateMembersList(); 
        return this.membersList; 
    }
}