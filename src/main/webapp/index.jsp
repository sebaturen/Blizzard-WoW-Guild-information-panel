<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title>${guild.name}</title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/index.css">

    </head>
    <body>
        <div id="img_fPage" class="img_fondo img_fondo_pagina_old"></div>
        <div class="container fill" style="margin-top: 0px;">
            <div id="welcome">
                <div class="row guild_logoName divder">
                    <div class="col-3 log_artofwar">
                        <img src="assets/img/artofwar_logo.png"/>
                    </div>
                    <div class="col-6 align-self-center">
                        <p class='home_name warcraft_font'>Art of War</p>
                    </div>
                    <div class="col-3 log_artofwar">
                        <img class='flipImg' src="assets/img/artofwar_logo.png"/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-8 guild_achievements">
                        <p class='small_title warcraft_font'>News</p>
                        P&aacute;gina en Cuarentena por Covid-Blizzard.<br>
                        ... Nos encontramos trabajando en actualizar la informaci&oacute;n a los nuevos cambios.
                    </div>
                    <div class="col-md-4">
                        <p class='small_title warcraft_font'>Social Media</p>
                        <iframe src="https://discordapp.com/widget?id=200781976653791232&theme=dark" width="100%" height="500" allowtransparency="true" frameborder="0"></iframe>
                        <!-- Content... -->
                    </div>
                </div>
            </div>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>