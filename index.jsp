<%@include file="includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.guild.GuildAchievement" %>
<%@ page import ="java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %></title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="/assets/css/index.css">
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div id="welcome">
                <div class="row guild_logoName divder">
                    <div class="col log_artofwar">
                        <img src="/assets/img/artofwar_logo.png"/>
                    </div>
                    <div class="col-6 align-self-center">
                        <p class='home_name warcraft_font'><%= guild_info.getName() %></p>
                    </div>
                    <div class="col log_artofwar">
                        <img class='flipImg' src="/assets/img/artofwar_logo.png"/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-8">
                        <p class='small_title warcraft_font'>Social Media</p>
                        <iframe src="https://discordapp.com/widget?id=200781976653791232&theme=dark" width="350" height="500" allowtransparency="true" frameborder="0"></iframe>
                        <!-- Content... -->
                    </div>
                    <div class="col-4 guild_achievements">
                        <p class='small_title warcraft_font'>Last Achievements</p>
                        <%
                        List<GuildAchievement> ahivs = guild_info.getAchievements();
                        for(int i = 0; i < ahivs.size() && i < 6; i++) { %>
                            <div class="achievement divder row">
                                <div class="col-1 achievementImg" style="background-image: url('<%= ahivs.get(i).getAchievement().getIconRenderURL() %>')"></div>
                                <div class="achievementDetail col-11">
                                    <p><%= ahivs.get(i).getAchievement().getTitle()%></p>
                                    <p class="desc"><%= ahivs.get(i).getAchievement().getDescription()%></p>
                                </div>
                            </div>
                      <%}%>
                        <!-- Content... -->
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>