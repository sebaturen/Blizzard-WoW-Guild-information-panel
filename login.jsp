<%@include file="includes/globalObject.jsp" %>
<% 
//Log-out    
if(request.getParameter("logOut") != null && request.getParameter("logOut").equals("true")) 
{
    session.invalidate();
    response.sendRedirect("login.jsp");
}
else
{
   //login-form
    if (user == null || !user.checkUser()) 
    {
        %>
        <jsp:forward page="userpanel/login_form.jsp"> 
            <jsp:param name="login_redirect" value="true" />
        </jsp:forward> 
        <%
    }
    //User panel
    else
    {
        %>
        <jsp:forward page="userpanel/user_panel.jsp"> 
            <jsp:param name="login_redirect" value="true" />
        </jsp:forward> 
        <%
    } 
}



%>