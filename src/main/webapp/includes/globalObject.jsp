<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import ="java.util.Date" %>
<!-- GUILD CONTROLLER-->
<%@ page import ="com.blizzardPanel.viewController.GuildController" %>
<%@ page import ="com.blizzardPanel.gameObject.guilds.Guild" %>
<%@ page import ="com.blizzardPanel.User" %>
<!-- GENERAL CONTROLLER -->
<jsp:useBean id="user" class="com.blizzardPanel.User" scope="session" />
<jsp:useBean id="general_config" class="com.blizzardPanel.GeneralConfig" scope="application"/>
<jsp:useBean id="dateObject" class="java.util.Date" />
<!-- Language locale -->
<c:if test="${empty locale}">
    <c:set var="locale" value="es_MX" />
    <c:if test="${cookie['locale'].value != null}">
        <c:set var="locale" value="${cookie['locale'].value}"/>
    </c:if>
    <c:if test="${cookie['locale'] == null}">
        <%
            Cookie newLocale = new Cookie("locale", "es_MX");
            response.addCookie(newLocale);
        %>
    </c:if>
</c:if>
<fmt:setLocale value="${locale}" />
<fmt:setBundle basename="messages" />
<%
    // Setting if user is a guild member, or blizzardPanel is setting all information is public.
    boolean guildMember = false;
    if(general_config.getBooleanConfig("REQUIRED_LOGIN_TO_INFO")) {
        if (user != null && user.getIs_guild_member())
            guildMember = true;
    } else {
        guildMember = true;
    }

%>
<c:if test="${empty guild}">
    <jsp:forward page="${contextPath}/internal_error.jsp" />
</c:if>