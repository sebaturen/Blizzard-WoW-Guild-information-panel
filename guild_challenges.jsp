<%@include file="includes/globalObject.jsp" %>
<%@ page import ="com.artOfWar.gameObject.characters.Member" %>
<%@ page import ="com.artOfWar.gameObject.guild.challenges.Challenge" %>
<%@ page import ="com.artOfWar.gameObject.guild.challenges.ChallengeGroup" %>
<%@ page import ="java.text.SimpleDateFormat" %>
<jsp:useBean id="challenges" class="com.artOfWar.viewController.GuildChallenges"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Guild Challenges</title>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container">
            <div id="challenges-content">
                <%  
                for(Challenge ch : challenges.getChallengesList())
                {%>
                <div id='dung-<%= ch.getMapId() %>' class='dungeon-challenge'>                        
                    <div class="dungeon-challenge-img" style='background-image: url("assets/img/dungeon/<%= ch.getMapId() %>.jpg");'>
                        <h2 class="dung-title"><%= ch.getMapName()%></h2>
                    </div>
                    <div class="row">
                    <% for(ChallengeGroup groupCh : ch.getChallengeGroups())
                    {%>
                    <div class="col group-info">
                        <%  int upgradeKey = ch.isUpdateKey(groupCh);
                            String classGroupTime = (upgradeKey == -1)? "downgrade":"upgrade";
                            String dateGroup = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(groupCh.getTimeDate());
                            dateGroup = "<p class='key-date'>"+ dateGroup +"</p>";
                        %>
                        <p class='group-time key-<%= classGroupTime %>'><%= "["+ groupCh.getTimeHour() +"h:"+ groupCh.getTimeMinutes() +"m:"+ groupCh.getTimeSeconds() +"s]" %><%= (upgradeKey == -1)? "":" (+"+upgradeKey+")" %></p>
                        <%= dateGroup %>
                        <table class="table table-dark character-tab">
                            <thead>
                                <tr>
                                    <th scope="col">Name</th>
                                    <th scope="col">Role</th>
                                    <th scope="col">(Spec)</th>
                                </tr>
                            </thead>
                            <tbody>
                            <% for(Member m : groupCh.getMembers())
                            {%>
                                <tr>
                                    <td class="character-<%= (m.getmemberClass().getEnName()).replaceAll("\\s+","") %>"><%= m.getName() %></td>
                                    <td><img src="assets/img/icons/<%= m.getActiveSpec().getRole() %>.png" style="width: 22px;"/></td>
                                    <% //Get img from speck
                                        String className = ((m.getmemberClass().getEnName()).replaceAll("\\s+","-")).toLowerCase();
                                        String specName = ((m.getActiveSpec().getName()).replaceAll("\\s+","-")).toLowerCase();
                                    %>
                                    <td><img src="assets/img/classes/specs/spec_<%= className %>_<%= specName %>.png" style="width: 22px;"/></td>
                                </tr>
                          <%}%>
                            </tbody>
                        </table>
                    </div>
                  <%}%>
                    </div>
                </div>
                <br>
              <%}%>                        
            </div>
        </div>
    </body>
</html>
