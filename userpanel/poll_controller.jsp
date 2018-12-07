<%@include file="../includes/globalObject.jsp" %>
<%@page import="org.json.simple.JSONObject"%>
<%
    JSONObject json = new JSONObject();
    if(guildMember)
    {%> 
        <%@ page import = "com.blizzardPanel.poll.Poll" %>
        <%@ page import ="java.util.ArrayList" %>
        <%@ page import ="java.util.List" %>
        <%@ page import ="java.text.SimpleDateFormat" %>
        <jsp:useBean id="pollControl" class="com.blizzardPanel.viewController.PollController" scope="session"/><%


        //Load poll edit
        String pollAction = request.getParameter("poll_action");
        json.put("action", pollAction);

        //Controll poll:
        int pollId = -1;
        Poll editPoll = null;
        boolean canEdit = false;
        int optId = -1;    

        if(pollAction.equals("removeOption") ||
            pollAction.equals("addOption") ||
            pollAction.equals("addResult") ||
            pollAction.equals("removeResult"))
        {
            pollId = Integer.parseInt(request.getParameter("poll_id"));
            editPoll = pollControl.getPoll(pollId);    
        }

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
            case "createPoll":
                //Create poll values
                String pollQuest = null;
                String limitDateSet = null;
                boolean moreOptions = false;
                boolean multiOptions = false;
                boolean limitDate = false;
                int minGuildLevel = -1;
                List<String> options = new ArrayList<>();
                //search values
                int i = 0;
                while(request.getParameter("val["+i+"][name]") != null)
                {
                    String valName = request.getParameter("val["+i+"][name]");
                    String valValue = request.getParameter("val["+i+"][value]");
                    //general settings
                    switch(valName)
                    {
                        case "poll_quest": pollQuest = valValue; break;
                        case "guild_level": minGuildLevel = Integer.parseInt(valValue); break;
                        case "more_options": moreOptions = (valValue.equals("on")); break;
                        case "multi_options": multiOptions = (valValue.equals("on")); break;
                        case "limit_date": limitDate = (valValue.equals("on")); break;
                        case "set_date_limit": limitDateSet = valValue; break;
                    }
                    //Save Options
                    if(valName.toLowerCase().startsWith("option_"))
                    {
                        options.add(valValue);
                    }
                    i++;
                }
                if(pollQuest != null && pollQuest.length() > 0 && options.size() > 0)
                {
                    //Try parse date time:
                    try 
                    {
                        if(limitDateSet.length() == 10)
                            limitDateSet = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(limitDateSet +" 00:00:00"));
                        else
                            limitDateSet = null;
                        //Try save in DB
                        if(pollControl.newPoll(
                            user, pollQuest, minGuildLevel, moreOptions,
                            multiOptions, limitDate, 
                            limitDateSet, options))
                        {                    
                            json.put("status", "ok");
                        }
                        else
                        {                    
                            json.put("status", "fail");
                            json.put("msg", "Fail to save in DB");
                        }
                    } catch (java.text.ParseException ex) {
                        json.put("status", "fail");
                        json.put("msg", "Fail limit date not correct");                        
                    }
                }
                else
                {
                    json.put("status", "fail");
                    json.put("msg", "Fail, complete all information");
                }
                break;
            default:
                json.put("status", "fail");        
                break;

        }
    }
    else
    {
        json.put("status", "fail");   
        json.put("msg", "Not user login detected");
    }
    out.print(json);
    out.flush();
%>