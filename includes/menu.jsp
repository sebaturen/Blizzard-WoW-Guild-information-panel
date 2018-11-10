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
            <% if(user != null && user.getGuildRank() != -1) { %>
                <li class="nav-item <% out.write((request.getRequestURI().equals("/alters.jsp"))? "active":""); %>">
                    <a class="nav-link" href="alters.jsp">Alters</a>
                </li>
            <% } %>
            <li class="nav-item">
                <a class="nav-link disabled" href="#">Last Update[<%= gameInfo.getLastDynamicUpdate() %>]</a>
            </li>
        </ul>            
        <div class="form-inline my-2 my-lg-0">
            WoW Token:&nbsp;
            <% int[] tokenPrice = gameInfo.getTokenWow(); %>
            <% if (tokenPrice[0] > 0) { %><span class="moneygold"><%= String.format("%,d", tokenPrice[0]) %></span><% } %>
            <% if (tokenPrice[1] > 0) { %><span class="moneysilver"><%= String.format("%,d", tokenPrice[1]) %></span><% } %>
            <% if (tokenPrice[2] > 0) { %><span class="moneycopper"><%= String.format("%,d", tokenPrice[2]) %></span><% } %>
            &nbsp;<a href="login.jsp"><button class="btn btn-outline-success" type="button"><%= (user == null || !user.checkUser())? "Login":"Account Info" %></button></a>
        </div>
    </div>
</nav>