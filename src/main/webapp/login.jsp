<%@include file="includes/globalObject.jsp" %>
<%
    //Log-out
    if(request.getParameter("logOut") != null && request.getParameter("logOut").equals("true")) {
        session.invalidate();
        response.sendRedirect("login.jsp");
    } else {
        //login-form
        if (user == null || !user.isLogin()) {
        %>
            <jsp:forward page="user_panel/login_form.jsp">
                <jsp:param name="login_redirect" value="true" />
            </jsp:forward>
        <%
        } else { //User panel
        %>
            <jsp:forward page="user_panel/user_panel.jsp">
                <jsp:param name="login_redirect" value="true" />
            </jsp:forward>
        <%
        }
    }
%>