<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@include file="includes/globalObject.jsp" %>
<% if (!guildMember) {%><%@ page import ="java.net.URLEncoder" %><%
        response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("polls.jsp", "UTF-8"));
    }%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title>${guild.name} - <fmt:message key="label.polls" /></title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/index.css">
        <link type="text/css" rel="stylesheet" href="assets/css/polls.css">
        <script src="assets/js/polls.js"></script>
    </head>
    <body> <!--  style="background-color: #030317;" -->
        <%@include file="includes/menu.jsp" %>
        <div id="poll_mold" class="poll" style="display:none;">
            <div class="loader ajaxLoad" style="display: none;"></div>
            <h3 class="question"></h3>
            <span class="detail"><f:message key="label.asked_by"/></span>
            <div class="options">
                <div id="opt_mold" class="option_det">
                    <span class="progress_percent"></span>
                    <h5 class="opt_text"></h5>
                    <span class="progress">
                        <div class="progress-bar" role="progressbar" style="" aria-valuemin="0" aria-valuemax="100"></div>
                    </span>
                    <div class="result"></div>
                </div>
            </div>
        </div>
        <div class="container fill">
            <div id="enabled_polls" style="display: none">
                <div class="key_title">
                    <h1 class="key_divide_title"><fmt:message key="label.enabled_polls" /></h1>
                </div>
            </div>
            <div id="disabled_polls" style="display: none">
                <div class="key_title">
                    <h1 class="key_divide_title"><fmt:message key="label.disabled_polls" /></h1>
                </div>
            </div>
            <div id="loading" class="justify-content-md-center"><div class="loader"></div></div>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>