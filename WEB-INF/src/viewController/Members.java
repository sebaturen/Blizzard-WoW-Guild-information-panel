/**
 * File : Members.java
 * Desc : members.jsp view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;
import com.artOfWar.gameObject.Member;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.sql.SQLException;

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
		Member[] membersList = null;
		try
		{
			JSONArray dbList = dbConnect.select("gMembers_id_name", 
								new String[] {"internal_id", "member_name"});	
			if(dbList.size() > 0) membersList = new Member[dbList.size()];
			for(int i = 0; i < dbList.size(); i++)
			{
				membersList[i] = new Member( (int) ((JSONObject) dbList.get(i)).get("internal_id") );
			}
		}
		catch (SQLException|DataException e)
		{
		}
		
		return membersList;
	}
}