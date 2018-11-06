<jsp:useBean id="lUpdate" class="com.artOfWar.viewController.LastUpdate"/>
<div id="img_fPage" class="img_fondo img_fondo_pagina"></div>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="index.jsp">
        <img src="/assets/img/artofwar_logo.png" height="30" alt="">
    </a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item <% out.write((request.getRequestURI().equals("/index.jsp"))? "active":""); %>">
                <a class="nav-link" href="index.jsp">Home</a>
            </li>
            <li class="nav-item <% out.write((request.getRequestURI().equals("/members.jsp"))? "active":""); %>">
                <a class="nav-link" href="members.jsp">Members List</a>
            </li>
            <li class="nav-item <% out.write((request.getRequestURI().equals("/guild_challenges.jsp"))? "active":""); %>">
                <a class="nav-link" href="guild_challenges.jsp">Guild Challenges</a>
            </li>
            <li class="nav-item">
                <a class="nav-link disabled" href="#">Last Update[<%= lUpdate.getLastDynamicUpdate() %>]</a>
            </li>
        </ul>
    </div>
</nav>