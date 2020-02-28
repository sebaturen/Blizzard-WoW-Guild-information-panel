<%@include file="../includes/globalObject.jsp" %>
<%@page import="org.json.simple.JSONObject"%>
<%
    request.setCharacterEncoding("UTF-8");
    JSONObject json = new JSONObject();
    if(guildMember)
    {%>
        <%@ page import ="java.util.ArrayList" %>
        <%@ page import ="java.util.List" %>
        <%@ page import ="java.util.Date" %>
        <%@ page import ="java.text.SimpleDateFormat" %>
        <%@ page import = "com.blizzardPanel.events.Event" %>
        <%@ page import = "com.blizzardPanel.gameObject.characters.CharacterMember" %>
        <jsp:useBean id="eventsControl" class="com.blizzardPanel.viewController.EventsController" scope="session"/><%

        //Load event edit
        String eventAction = request.getParameter("event_action");
        json.put("action", eventAction);

        //Controll event:
        int eventId = -1;
        Event editEvent = null;
        boolean canEdit = false;

        if(eventAction.equals("addMember") ||
            eventAction.equals("event_add_result2") ||
            eventAction.equals("event_add_result2") ||
            eventAction.equals("event_add_result2"))
        {
            eventId = Integer.parseInt(request.getParameter("val[event_id]"));
            System.out.println("ev ID> "+ eventId);
            editEvent = eventsControl.getEvent(eventId);
        }

        //Try action
        switch(eventAction)
        {
            case "addMember":
                int mainCharID = Integer.parseInt(request.getParameter("val[main_id]"));
                int mainSpecID = Integer.parseInt(request.getParameter("val[main_spec]"));
                if(mainCharID > 0 && mainSpecID > 0)
                {
                    //Main char detail
                    CharacterMember mainChar = new CharacterMember(mainCharID);
                    //set main spec
                    mainChar.setActiveSpec(mainSpecID);
                    if(mainChar.getUserID() == user.getId())
                    {
                        //while alters...
                        List<CharacterMember> altersChar = new ArrayList<>();
                        int j = 0;
                        boolean failAlter = false;
                        while(request.getParameter("val[alters]["+ j +"][id]") != null)
                        {
                            int altCharID = Integer.parseInt(request.getParameter("val[alters]["+ j +"][id]"));
                            int altCharSpec = Integer.parseInt(request.getParameter("val[alters]["+ j +"][spec]"));
                            CharacterMember altChar = new CharacterMember(altCharID);
                            altChar.setActiveSpec(altCharSpec);
                            if(altChar.getUserID() != user.getId())
                            {
                                failAlter = true;
                                break;
                            }
                            else
                            {
                                altersChar.add(altChar);
                            }
                            j++;
                        }
                        if(!failAlter)
                        {
                            //Set in event...
                            if(editEvent.addCharactersFormUser(user, mainChar, altersChar))
                            {
                                json.put("status", "ok");
                            }
                            else
                            {
                                json.put("status", "fail");
                                json.put("msg", "Failed to save in DB");
                            }
                        }
                        else
                        {
                            json.put("status", "fail");
                            json.put("msg", "Error, alters character not is your character");
                        }
                    }
                    else
                    {
                        json.put("status", "fail");
                        json.put("msg", "Error, main character not is your character");
                    }
                }
                else
                {
                    json.put("status", "fail");
                    json.put("msg", "Error, main character or main spect character not is correct");
                }
                break;
            /*case "removeOption":
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
                break;*/
            case "createEvent":
                //Create event values
                String eventTitle = null;
                String eventDesc = null;
                String eventDate = null;

                //search values
                int i = 0;
                while(request.getParameter("val["+i+"][name]") != null)
                {
                    String valName = request.getParameter("val["+i+"][name]");
                    String valValue = request.getParameter("val["+i+"][value]");

                    //general settings
                    switch(valName)
                    {
                        case "event_title": eventTitle = valValue; break;
                        case "event_desc": eventDesc = valValue; break;
                        case "event_date": eventDate = valValue; break;
                    }
                    i++;
                }

                if(eventTitle != null && eventDesc != null && eventDate != null)
                {
                    //Try parse date time:
                    try
                    {
                        eventDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(eventDate));
                        Date nowDate = new Date();
                        Date evDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(eventDate);
                        if(nowDate.compareTo(evDate) < 0)
                        {
                            //Try save in DB
                            if(eventsControl.newEvent(user, eventTitle, eventDesc, eventDate))
                            {
                                json.put("status", "ok");
                            }
                            else
                            {
                                json.put("status", "fail");
                                json.put("msg", "Failed to save in DB");
                            }
                        }
                        else
                        {
                            json.put("status", "fail");
                            json.put("msg", "Error, the date must be later than the current one");
                        }
                    } catch (java.text.ParseException ex) {
                        json.put("status", "fail");
                        json.put("msg", "Error, `date` not correct");
                    }
                }
                else
                {
                    json.put("status", "fail");
                    json.put("msg", "Error, complete all information");
                }
                break;
            default:
                json.put("status", "fail");
                json.put("msg", "Acction not found");
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
