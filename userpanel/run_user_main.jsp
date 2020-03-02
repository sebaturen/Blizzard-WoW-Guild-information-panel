<%@include file="../includes/globalObject.jsp" %>
<%@page import="com.google.gson.JsonObject"%>
<% 
    JsonObject json = new JsonObject();
    if(guildMember) 
    {
        int memberID = Integer.parseInt(request.getParameter("id"));
        if(user.setMainCharacter(memberID))
        {
            json.put("status", "ok");
        }
        else
        {        
            json.put("status", "fail");
            json.put("error", "Character not is a guild member - Error 001");
        }
    }
    else 
    {
        json.put("status", "fail");
        json.put("error", "User not login - Error 002");
    }
    out.print(json);
    out.flush();
%>