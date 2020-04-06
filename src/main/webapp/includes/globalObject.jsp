<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import ="java.util.Date" %>
<!-- GUILD CONTROLLER-->
<%@ page import ="com.blizzardPanel.viewController.GuildController" %>
<%@ page import ="com.blizzardPanel.gameObject.guilds.Guild" %>
<%@ page import ="com.blizzardPanel.User" %>
<!-- GENERAL CONTROLLER -->
<jsp:useBean id="user" class="com.blizzardPanel.User" scope="session" />
<jsp:useBean id="general_config" class="com.blizzardPanel.GeneralConfig" scope="application"/>
<jsp:useBean id="dateObject" class="java.util.Date" />
<fmt:setBundle basename="messages" />
<%    
    // Setting if user is a guild member, or blizzardPanel is setting all information is public.
    boolean guildMember = false;
    if(general_config.getBooleanConfig("REQUERID_LOGIN_TO_INFO")) {
        if (user != null && user.isGuildMember())
            guildMember = true;
    } else {
        guildMember = true;
    }

    String locale = "es_MX";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("locale")) {
                locale = cookie.getValue();
            }
        }
    }
    response.addCookie(new Cookie("locale", "es_MX"));

%>
<c:if test="${empty guild}">
    <jsp:forward page="${contextPath}/internal_error.jsp" />
</c:if>