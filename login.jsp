<%@include file="includes/globalObject.jsp" %>
<jsp:setProperty name="user" property="*" />
<% 
//Log-out    
if(request.getParameter("logOut") != null && request.getParameter("logOut").equals("true")) 
{
    session.invalidate();
    response.sendRedirect("login.jsp");
}
//login-form
if (user == null || !user.checkUser()) 
{
    %>
    <jsp:forward page="userpanel/login_form.jsp"> 
        <jsp:param name="login_redirect" value="true" />
    </jsp:forward> 
    <%
}
else
{
    %>
    <jsp:forward page="userpanel/user_panel.jsp"> 
        <jsp:param name="login_redirect" value="true" />
    </jsp:forward> 
    <%
}


%>