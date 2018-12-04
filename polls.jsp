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
        <div class="container fill">
            <%= (user.getGuildRank() == 0 || user.getGuildRank() == 1)? "<a href='userpanel/create_poll.jsp' class='right'><button type='submit' class='btn btn-outline-warning btn-sm'>Create Poll</button></a><br><br>":"" %>
            <% for(Poll p : pollControl.getPolls()) {%>
                <div class="divder">
                    <p>
                        <% if(p.getUser().getMainCharacter() != null) {
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
                    <div class="poll_options">
                        <% for(PollOption pOpt : p.getOptions()) {
                            String userSelected = "";
                            boolean currentUserSelected = false;
                            for(PollOptionResult pOptResult : pOpt.getResult()) {
                                if(pOptResult.getOwner().getMainCharacter() != null) {
                                    String className = ((pOptResult.getOwner().getMainCharacter().getMemberClass().getEnName()).replaceAll("\\s+","-")).toLowerCase();
                                    userSelected += "<span class='mem-name character-"+ className +" char-name'>"+ pOptResult.getOwner().getMainCharacter().getName() +",</span>";
                                }
                                else
                                {
                                    userSelected += "<span class='mem-name'><img src='assets/img/icons/Battlenet_icon_flat.svg' style='width: 20px'>"+ pOptResult.getOwner().getBattleTag().split("#")[0] +",</span>";
                                }
                                if(pOptResult.getOwner().equals(user))
                                {
                                    currentUserSelected = true;
                                }
                            }//end foreach poll options result %>
                            <div id="poll_opt_<%= pOpt.getId() %>">
                                <p class="poll_option pointer">
                                    <button type="button" class="btn_poll_option btn <%= (currentUserSelected)? "btn-success":"btn-outline-success" %>"><%= (currentUserSelected)? "<i class='artOfWar-icon'>&#xe802;</i>":"" %></button>
                                    <%= pOpt.getOptionText() %>
                                </p>
                                <%= userSelected %>
                            </div>
                      <%} //End foreach poll options%>
                    </div>
                </div>
          <%}//End for polls%>
        </div>
    </body>
</html>
<%}%>