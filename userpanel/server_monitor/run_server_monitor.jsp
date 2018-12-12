<%@include file="../../includes/globalObject.jsp" %>
<% if(user.getGuildRank() == 0 || user.getGuildRank() == 1) { %>
<jsp:useBean id="sMonitor" class="com.blizzardPanel.viewController.ServerMonitor" scope="request"/>
<% sMonitor.runSee(); } %>