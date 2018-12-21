<%@include file="../../includes/globalObject.jsp" %>
<% if(user.getGuildRank() == 0 || user.getGuildRank() == 1) { %>
<jsp:useBean id="upBlizz" class="com.blizzardPanel.viewController.UpdateControl" scope="request"/>
<%
    String arg = request.getParameter("arg");
    String type = request.getParameter("type");
    if(arg.equals("0"))
        upBlizz.runUpdate(new String[] {type});
    else
        upBlizz.runUpdate(new String[] {type, arg});

} %>
