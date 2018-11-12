<%@include file="includes/globalObject.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %></title>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <div id="welcome">
                Welcome to <%= guild_info.getName() %><br/>
                <img src="/assets/img/artofwar_logo.png"/><br/>
            </div>
        </div>
    </body>
</html>