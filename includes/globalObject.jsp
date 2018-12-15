<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="gameInfo" class="com.blizzardPanel.viewController.GameInfo" scope="application"/>
<jsp:useBean id="guild_info" class="com.blizzardPanel.gameObject.guild.Guild" scope="application"/>
<jsp:useBean id="general_config" class="com.blizzardPanel.GeneralConfig" scope="application"/>
<jsp:useBean id="user" class="com.blizzardPanel.User" scope="session" />
<%@ page import ="com.blizzardPanel.exceptions.ConfigurationException" %>
<% 
    //Setting if user is a guild member, or blizzardPanel is setting all information is public.
    boolean guildMember = false;
    try 
    {        
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
        if(!gameInfo.getDBStatus() || guild_info.getName() == null)
        {
            String errorMsg = "Fail to get guild information";
            session.setAttribute("errorMsg", errorMsg);
            %><jsp:forward page="${contextPath}/internal_error.jsp">
                <jsp:param name="db_error_msg" value="${errorMsg}" />
            </jsp:forward><%
        }
    } catch (ConfigurationException ex) {
        String errorMsg = "CONFIGURATION FAIL ERROR!";
        %><jsp:forward page="${contextPath}/internal_error.jsp">
            <jsp:param name="db_error_msg" value="${errorMsg}" />
        </jsp:forward><%
    }    
%>