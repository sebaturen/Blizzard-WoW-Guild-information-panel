<%@include file="includes/globalObject.jsp" %>
<% if (!guildMember) {%><%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("polls.jsp", "UTF-8"));
} else {%>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<%@ page import = "com.blizzardPanel.poll.Poll" %>
<%@ page import = "com.blizzardPanel.poll.PollOption" %>
<%@ page import = "com.blizzardPanel.poll.PollOptionResult" %>
<jsp:useBean id="pollControl" class="com.blizzardPanel.viewController.PollController"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Poll</title>
        <%@include file="includes/header.jsp" %>
        <script src="assets/js/polls.js"></script>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div id="currentUserInfo" style="display: none;"
             data-user_id="<%= user.getId() %>"
             data-user_show="<%= (user.getMainCharacter() != null)? user.getMainCharacter().getName():user.getBattleTag().split("#")[0] %>"
             data-user_class="<%= (user.getMainCharacter() != null)? ((user.getMainCharacter().getMemberClass().getEnName()).replaceAll("\\s+","-")).toLowerCase():"BATTLE_TAG"%>"
        ></div>
        <div class="container fill">
            <div class="loader ajaxLoad" style="display: none;"></div><%= (user.getGuildRank() == 0 || user.getGuildRank() == 1)? "<a href='userpanel/poll_create.jsp' class='right'><button type='submit' class='btn btn-outline-warning btn-sm'>Create Poll</button></a><br><br>":"" %>
            <% for(Poll p : pollControl.getPolls()) {%>
                <div id="poll_<%= p.getId() %>" class="divder">
                    <p>
                        <% if(p.getUser().getMainCharacter() != null) 
                        {
                            String className = ((p.getUser().getMainCharacter().getMemberClass().getEnName()).replaceAll("\\s+","-")).toLowerCase();
                            out.write("<span class='character-"+ className +" char-name'>"+ p.getUser().getMainCharacter().getName() +"</span>");
                        }
                        else
                        {
                            out.write("<span><img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 40px'>"+ p.getUser().getBattleTag().split("#")[0] +"</span>");   
                        }%>
                        <span class="right_small_date">(<%= p.getStartDate() %>)</span>
                    </p>
                    <div class="returnCode"><%= p.getPollQuestion() %></div>
                    <div class="poll_options" 
                         data-is_multi="<%= p.isMultiSelect() %>" 
                         data-poll_id="<%= p.getId() %>">
                        <% for(PollOption pOpt : p.getOptions()) 
                        {
                            String userSelected = "";
                            boolean currentUserSelected = false;
                            for(PollOptionResult pOptResult : pOpt.getResult()) 
                            {
                                if(pOptResult.getOwner().getMainCharacter() != null) 
                                {
                                    String className = ((pOptResult.getOwner().getMainCharacter().getMemberClass().getEnName()).replaceAll("\\s+","-")).toLowerCase();
                                    userSelected += "<span id='poll_"+ p.getId() +"_opt_"+ pOpt.getId() +"_user_"+ pOptResult.getOwner().getId() +"' class='mem-name character-"+ className +" char-name'>&nbsp;"+ pOptResult.getOwner().getMainCharacter().getName() +",</span>";
                                }
                                else
                                {
                                    userSelected += "<span id='poll_"+ p.getId() +"_opt_"+ pOpt.getId() +"_user_"+ pOptResult.getOwner().getId() +"' class='mem-name'><img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 20px'>"+ pOptResult.getOwner().getBattleTag().split("#")[0] +",</span>";
                                }
                                //if have a owner
                                if(pOptResult.getOwner().equals(user))
                                {
                                    currentUserSelected = true;
                                }
                            }//end foreach poll options result %>
                            <div id="poll_<%= p.getId() %>_opt_<%= pOpt.getId() %>" class="divder row justify-content-between poll_<%= p.getId() %>_opts" data-opt_id="<%= pOpt.getId() %>">
                                <div class="poll_option pointer col-10">
                                    <button onclick='addOptionButton(this)' type="button" class="btn_poll_option btn <%= (currentUserSelected)? "btn-success":"btn-outline-success" %>"><%= (currentUserSelected)? "<i class='artOfWar-icon'>&#xe802;</i>":"" %></button>
                                    <span class="poll_opt_span"><%= pOpt.getOptionText() %></span>
                                </div>
                                <%//Only GUILD_LEADER and OFFICER or (Option owner if not have more 1 user select) can delete this option%>
                                <div class="col-2"><%= (user.getGuildRank() == 0 || user.getGuildRank() == 1 || (pOpt.getOwner().equals(user) && pOpt.getResult().size() <= 1))? "<button onclick='removeOption(this)' type='button' class='btn_poll_option_delete btn btn-outline-danger deleteOption' data-poll_id='"+ p.getId() +"' data-opt_id='poll_"+ p.getId() +"_opt_"+ pOpt.getId() +"' data-opt_id_db='"+ pOpt.getId() +"'><i class='artOfWar-icon'>&#xe803;</i></button>":"" %></div>
                                <div class="userSelect"><%= userSelected %></div>
                            </div>
                      <%} //End foreach poll options%>
                        <%= (p.isCanAddMoreOptions())? "<button id='addOption_poll_"+ p.getId() +"' type='button' class='btn btn-success addOption'>+ Option</button>":"" %>
                    </div>
                </div>
          <%}//End for polls%>
        </div>
    </body>
</html>
<%}%>