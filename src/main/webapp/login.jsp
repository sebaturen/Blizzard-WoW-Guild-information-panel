<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Log-out
    if(request.getParameter("logOut") != null && request.getParameter("logOut").equals("true")) {
        session.invalidate();
        response.sendRedirect("index.jsp");
    } else {
        // Locale
        if (request.getParameter("locale") != null) {
            Cookie newLocale = new Cookie("locale", request.getParameter("locale"));
            response.addCookie(newLocale);
            request.setAttribute("locale", request.getParameter("locale"));
        }
        %>
        <%@include file="includes/globalObject.jsp" %>
        <c:if test="${user == null || !user.login}">
            <jsp:forward page="user_panel/login_form.jsp">
                <jsp:param name="login_redirect" value="true" />
            </jsp:forward>
        </c:if>
        <c:if test="${user != null && user.login}">
            <jsp:forward page="user_panel/user_panel.jsp">
                <jsp:param name="login_redirect" value="true" />
            </jsp:forward>
        </c:if>
    <%}%>
