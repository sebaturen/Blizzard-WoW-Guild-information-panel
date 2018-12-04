<%@include file="includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.guild.New" %>
<%@ page import ="com.blizzardPanel.gameObject.guild.achievement.GuildAchievement" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.achievement.CharacterAchivementsList" %>
<%@ page import ="java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %></title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/index.css">
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div id="welcome">
                <div class="row guild_logoName divder">
                    <div class="col-3 log_artofwar">
                        <img src="/assets/img/artofwar_logo.png"/>
                    </div>
                    <div class="col-6 align-self-center">
                        <p class='home_name warcraft_font'><%= guild_info.getName() %></p>
                    </div>
                    <div class="col-3 log_artofwar">
                        <img class='flipImg' src="/assets/img/artofwar_logo.png"/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-8">
                        <p class='small_title warcraft_font'>Social Media</p>
                        <iframe src="https://discordapp.com/widget?id=200781976653791232&theme=dark" width="350" height="500" allowtransparency="true" frameborder="0"></iframe>
                        <!-- Content... -->
                    </div>
                    <div class="col-md-4 guild_achievements">
                        <p class='small_title warcraft_font'>News</p>
                        <%
                        List<New> news = guild_info.getNews();
                        for(int i = 0; i < news.size() && i <= 6; i++) 
                        {
                            New inf = news.get(i);
                            String img = "";
                            String desc = "";
                            String iaDetail = "";
                            switch(inf.getType())
                            {
                                case "itemLoot":
                                    img = inf.getItem().getIconRenderURL();
                                    desc = "Item Loot";
                                    iaDetail = inf.getItem().getName();
                                    break;
                                case "playerAchievement":
                                    img = inf.getCharacterAchievement().getIconRenderURL();
                                    desc = "Character Achievement";
                                    iaDetail = inf.getCharacterAchievement().getTitle();
                                    break;
                                case "guildAchievement":
                                    img = inf.getGuildAchievement().getIconRenderURL();
                                    desc = "Guild Achievement";
                                    iaDetail = inf.getGuildAchievement().getTitle();
                                    break;
                            }//end swithc%> 
                            <div class="new divder row">
                                <div class="col-2"><img src="<%= img %>"/></div>
                                <div class="newDetail col-10">
                                    <p><%= inf.getMember().getName() %> <span  class="right_small_date"><%= inf.getTimeStampString() %></span></p>
                                    <p class="desc"><%= desc %> <%= inf.getContext() %></p>
                                    <p class="desc"><%= iaDetail %></p>
                                </div>
                            </div>
                      <%}//end for news%>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>