<%@include file="../includes/globalObject.jsp" %>
<%
if(guildMember)
{%><%@ page import = "com.blizzardPanel.poll.Poll" %><%@page import="org.json.simple.JSONObject"%><%
    
    JSONObject json = new JSONObject();
    
    //Load poll edit
    String pollAction = request.getParameter("poll_action");
    json.put("action", pollAction);
    int pollId = Integer.parseInt(request.getParameter("poll_id"));
    Poll editPoll = new Poll(pollId);
    boolean canEdit = false;
    int optId = -1;
    
    //Try action
    switch(pollAction)
    {
        case "removeOption":
            optId = Integer.parseInt(request.getParameter("poll_opt_id"));
            if(user.getGuildRank() == 0 || user.getGuildRank() == 1) canEdit = true;
            if(editPoll.getOption(optId) != null)
            {
                if(editPoll.getOption(optId).getOwner().equals(user) &&
                   editPoll.getOption(optId).getResult().size() <= 1)
                {
                    canEdit = true;
                }
            }
            if(canEdit)
            {
                json.put("status", editPoll.removeOptionDB(optId));
            }   
            break;
        case "addOption":
            String textOption = request.getParameter("poll_opt_add_text");
            if(user.getGuildRank() == 0 || user.getGuildRank() == 1) canEdit = true;
            if(editPoll.isCanAddMoreOptions())
            {
                canEdit = true;
            }
            if(canEdit)
            {                
                int newIdOption = editPoll.addOption(textOption, user);
                json.put("status", "ok");
                json.put("option_id", newIdOption);
            }
            break;
        case "addResult":
            optId = Integer.parseInt(request.getParameter("poll_opt_id"));
            json.put("status", editPoll.addResult(optId, user));
            break;
        case "removeResult":
            optId = Integer.parseInt(request.getParameter("poll_opt_id"));
            json.put("status", editPoll.removeResult(optId, user));            
            break;
        default:
            json.put("status", "fail");
            break;
            
    }    
    out.print(json);
    out.flush();
}
%>