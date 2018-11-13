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
                <div class="row">
                    <div class="col">
                        <img src="/assets/img/artofwar_logo.png"/>
                    </div>
                    <div class="col-6">
                        <p class='home_name warcraft_font'><%= guild_info.getName() %></p>
                    </div>
                    <div class="col">
                        <img class='flipImg' src="/assets/img/artofwar_logo.png"/>
                    </div>
                </div>
                
                
            </div>
        </div>
    </body>
</html>