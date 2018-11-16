<%@include file="../../includes/globalObject.jsp" %>
<% if(user.getGuildRank() == 0 || user.getGuildRank() == 1) { %>
<jsp:useBean id="upBlizz" class="com.artOfWar.viewController.UpdateControl" scope="request"/>
<% upBlizz.runUpdate(); } %>