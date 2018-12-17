<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="gameInfo" class="com.blizzardPanel.viewController.GameInfo" scope="application"/>
<jsp:useBean id="guild_info" class="com.blizzardPanel.gameObject.guild.Guild" scope="application"/>
<jsp:useBean id="general_config" class="com.blizzardPanel.GeneralConfig" scope="application"/>
<jsp:useBean id="user" class="com.blizzardPanel.User" scope="session" />
<% 
    //Setting if user is a guild member, or blizzardPanel is setting all information is public.
    boolean guildMember = false; 
    if(general_config.getBooleanConfig("REQUERID_LOGIN_TO_INFO"))
    {
        if (user != null && user.getGuildRank() != -1)
            guildMember = true;
    }
    else
    {
        guildMember = true;
    } 
    //Verify if DB is correct (like configuration is correct)
    if(!gameInfo.getSistemStatus() || guild_info.getName() == null)
    {
        %><jsp:forward page="${contextPath}/internal_error.jsp" /><%
    }  
%>