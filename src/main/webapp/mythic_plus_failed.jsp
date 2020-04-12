<%@include file="includes/globalObject.jsp" %>
<% if (!guildMember) {%><%@ page import ="java.net.URLEncoder" %><%
        response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("mythic_plus_failed.jsp", "UTF-8"));
} %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
<head>
    <title>${guild.name} - <fmt:message key="label.failed_runs" /></title>
    <%@include file="includes/header.jsp" %>
    <link type="text/css" rel="stylesheet" href="assets/css/index.css">
    <script src="assets/js/mythic_plus_failed.js"></script>
</head>
<body style="background-color: #3e0505;">
<%@include file="includes/menu.jsp" %>
<div class="container fill">
    <div id="topFailedRun" style="display: none;">
        <div class="key_title">
            <h1><fmt:message key="label.top_failed_runs" /></h1><br>
        </div>
    </div>
    <div id="failedRunsWeekList" style="display: none;">
        <div class="key_title">
            <h1 class="key_divide_title"><fmt:message key="label.failed_runs_of_the_week" /></h1>
        </div>
    </div>
    <div id="loading" class="justify-content-md-center"><div class="loader"></div></div>
    <div class="item-floating-desc tooltip-affix">
        <div class="itemDesc tooltipDesc">
            <p id="affix_name"></p>
            <p id="affix_desc" class="tooltip-yellow itemSpellDetail"></p>
        </div>
    </div>
</div>
<%@include file="includes/footer.jsp" %>
</body>
</html>