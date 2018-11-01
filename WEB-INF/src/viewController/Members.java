/**
 * File : Members.java
 * Desc : members.jsp view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;
import com.artOfWar.gameObject.Member;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Members
{
	//Variable
	private DBConnect dbConnect;
	
	public Members()
	{
		dbConnect = new DBConnect();
	}
	
	public Member[] getMembersList()
	{
		Member[] members = null;
		try
		{
			//Prepare list members
			List<Member> membersList = new ArrayList<>();
			//Get members to DB			
			JSONArray dbList = dbConnect.select("gMembers_id_name", 
								new String[] {"internal_id", "member_name"});	
			for(int i = 0; i < dbList.size(); i++)
			{
				int idMember = (int) ((JSONObject) dbList.get(i)).get("internal_id");
				Member member = new Member(idMember);
				//If data is successful load, save a member
				if(member.isData()) membersList.add(member);
			}
			//Convert LIST to simple Member Array
			if(membersList.size() > 0) members = membersList.toArray(new Member[membersList.size()]);
		}
		catch (SQLException|DataException e)
		{
		}
		
		return members;
	}
}