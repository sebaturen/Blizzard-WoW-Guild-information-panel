<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*"%>
<jsp:useBean id="gameInfo" class="com.blizzardPanel.viewController.GameInfo" scope="application"/>
<jsp:useBean id="guild_info" class="com.blizzardPanel.gameObject.guild.Guild" scope="request"/>
<jsp:useBean id="user" class="com.blizzardPanel.viewController.User" scope="session" />
<% 
    //Setting if user is a guild member, or blizzardPanel is setting all information is public.
    boolean guildMember = false;
    if(com.blizzardPanel.GeneralConfig.REQUERID_LOGIN_TO_INFO)
    {
        if (user != null && user.getGuildRank() != -1)
            guildMember = true;
    }
    else
    {
        guildMember = true;
    }

    //Verify if DB is correct (like configuration is correct)
    if(!gameInfo.getDBStatus())
    {
        session.setAttribute("errorMsg", gameInfo.getDBErrorMsg());
        %><jsp:forward page="${contextPath}/internal_error.jsp">
            <jsp:param name="db_error_msg" value="${errorMsg}" />
        </jsp:forward><%
    }
%>