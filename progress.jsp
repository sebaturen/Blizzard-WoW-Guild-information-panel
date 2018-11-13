<%@include file="includes/globalObject.jsp" %>
<%@ page import ="com.artOfWar.gameObject.Boss" %>
<%@ page import ="com.artOfWar.gameObject.guild.raids.RaidDificultBoss" %>
<%@ page import ="com.artOfWar.gameObject.guild.raids.RaidDificult" %>
<%@ page import ="com.artOfWar.gameObject.guild.raids.Raid" %>
<%@ page import ="java.text.SimpleDateFormat" %>
<jsp:useBean id="progress" class="com.artOfWar.viewController.GuildProgress"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Guild Progress</title>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <% for(Raid r : progress.getRaids()) {%>                
                <div class="dungeon-challenge-img" style='background-image: url("assets/img/raids/<%= r.getSlug() %>.jpg");'>
                    <h2 class="dung-title"><%= r.getName() %></h2>
                </div>                
                <% for(RaidDificult rDiff : r.getDificults()) { 
                    if(rDiff.getDificultBoss().size() > 0) {%> 
                    <div>
                        <h2><%= rDiff.getName() %></h2>
                        <p class='world-rank'><%= (rDiff.getRankWorld() > 0)? "World: "+ rDiff.getRankWorld():"" %></p>
                        <p class='region-rank'><%= (rDiff.getRankRegion() > 0)? "Region: "+ rDiff.getRankRegion():"" %></p>
                        <p class='realm-rank'><%= (rDiff.getRankRealm() > 0)? "Realm: "+ rDiff.getRankRealm():"" %></p>
                        <% int progressPersent = (rDiff.getDificultBoss().size()*100)/r.getTotalBoss(); %>
                        <div class="progress">
                            <div class="progress-bar" role="progressbar" style="width: <%= progressPersent %>%" aria-valuenow="<%= progressPersent %>" aria-valuemin="0" aria-valuemax="100"></div>
                        </div>
                    </div>
                    <table class="table table-dark character-tab">
                        <thead>
                            <tr>
                                <th scope='row'>Boss</th>
                                <th scope='row'>First Defeated</th>
                                <th scope='row'>iLevel AVG</th>
                            </tr>
                        </thead>
                        <tbody>
                        <% for(RaidDificultBoss diffBoss : rDiff.getDificultBoss()) {%> 
                            <tr>
                                <th scope='row'><img src='assets/img/bosses/<%= diffBoss.getBoss().getSlug() %>.png'/><br><%= diffBoss.getBoss().getName() %></th>
                                <% String dateFirstDefeated = (new SimpleDateFormat("d MMM / yyyy")).format(diffBoss.getFirstDefeated()); %>
                                <td scope='row'><%= dateFirstDefeated %></td>
                                <td scope='row'><%= diffBoss.getItemLevelAvg() %></td>
                            </tr>
                        <%}%>
                        </tbody>
                    </table>
                <%} }%>
            <%}%>
        </div>
    </body>
</html>