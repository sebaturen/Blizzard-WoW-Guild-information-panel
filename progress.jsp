<%@include file="includes/globalObject.jsp" %>
<%@ page import ="com.artOfWar.gameObject.Boss" %>
<%@ page import ="com.artOfWar.gameObject.guild.raids.RaidDificultBoss" %>
<%@ page import ="com.artOfWar.gameObject.guild.raids.RaidDificult" %>
<%@ page import ="com.artOfWar.gameObject.guild.raids.Raid" %>
<jsp:useBean id="progress" class="com.artOfWar.viewController.GuildProgress"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %></title>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <% for(Raid r : progress.getRaids()) {%>                
                <div class="dungeon-challenge-img" style='background-image: url("assets/img/raids/<%= r.getSlug() %>.jpg");'>
                    <h2 class="dung-title"><%= r.getName() %></h2>
                </div>                
                <% for(RaidDificult rDiff : r.getDificults()) {%>                
                    <table class="table table-dark character-tab">
                        <thead>
                            <tr>
                                <th scope='row'><%= rDiff.getName() %></th>
                                <th scope='row' class='world-rank'><%= (rDiff.getRankWorld() > 0)? "World: "+ rDiff.getRankWorld():"" %></th>
                                <th scope='row' class='region-rank'><%= (rDiff.getRankRegion() > 0)? "Ragion: "+ rDiff.getRankRegion():"" %></th>
                                <th scope='row' class='realm-rank'><%= (rDiff.getRankRealm() > 0)? "Realm: "+ rDiff.getRankRealm():"" %></th>
                            </tr>
                        </thead>
                        <tbody>
                        <% for(RaidDificultBoss diffBoss : rDiff.getDificultBoss()) {%> 
                            <tr>
                                <th scope='row'><img src='assets/img/bosses/<%= diffBoss.getBoss().getSlug() %>.png'/><br><%= diffBoss.getBoss().getName() %></th>
                                <td scope='row'><%= diffBoss.getFirstDefeated() %></td>
                                <td scope='row'><%= diffBoss.getItemLevelAvg() %></td>
                                <td scope='row'></td>
                            </tr>
                        <%}%>
                        </tbody>
                    </table>
                <%}%>
            <%}%>
        </div>
    </body>
</html>