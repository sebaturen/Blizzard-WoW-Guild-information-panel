<% String[] path = (request.getRequestURI()).split("/");
String currentPath = ""; if (path.length > 0) currentPath = path[path.length-1];%>
<div id="img_fPage" class="img_fondo img_fondo_pagina"></div>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="<%= request.getContextPath() %>/index.jsp">
        <img src="<%= request.getContextPath() %>/assets/img/artofwar_logo.png" height="30" alt="">
    </a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item <% out.write((currentPath.equals("index.jsp"))? "active":""); %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/index.jsp">Home</a>
            </li>
            <li class="nav-item <% out.write((currentPath.equals("members.jsp"))? "active":""); %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/members.jsp">Members List</a>
            </li>
            <li class="nav-item <% out.write((currentPath.equals("guild_challenges.jsp"))? "active":""); %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/guild_challenges.jsp">Guild Challenges</a>
            </li>
            <li class="nav-item <% out.write((currentPath.equals("progress.jsp"))? "active":""); %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/progress.jsp">Guild Progress</a>
            </li>
            <% if(guildMember) { %>
                <li class="nav-item <% out.write((currentPath.equals("alters.jsp"))? "active":""); %>">
                    <a class="nav-link" href="<%= request.getContextPath() %>/alters.jsp">Alters</a>
                </li>
                <li class="nav-item <% out.write((currentPath.equals("auction_house.jsp"))? "active":""); %>">
                    <a class="nav-link" href="<%= request.getContextPath() %>/auction_house.jsp">Auction House</a>
                </li>
            <% } %>
            <li class="nav-item">
                <a class="nav-link disabled" href="#">Last Update[<%= gameInfo.getLastDynamicUpdate() %>]</a>
            </li>
        </ul>            
        <div class="form-inline my-2 my-lg-0">
            <p class="quality-wow-token">WoW Token</p>:&nbsp;
            <% int[] tokenPrice = gameInfo.getTokenWow(); %>
            <% if (tokenPrice[0] > 0) { %><span class="moneygold"><%= String.format("%,d", tokenPrice[0]) %></span><% } %>
            <% if (tokenPrice[1] > 0) { %><span class="moneysilver"><%= String.format("%,d", tokenPrice[1]) %></span><% } %>
            <% if (tokenPrice[2] > 0) { %><span class="moneycopper"><%= String.format("%,d", tokenPrice[2]) %></span><% } %>
            <%//Blizzard account vinculation                                    
                String redirectUri = request.getContextPath() +"/login.jsp";
                if(!user.checkUser())
                {
                    redirectUri = String.format(com.blizzardPanel.blizzardAPI.APIInfo.API_OAUTH_URL, 
                                                com.blizzardPanel.GeneralConfig.SERVER_LOCATION,
                                                com.blizzardPanel.blizzardAPI.APIInfo.API_OAUTH_AUTHORIZE);
                    String urlRedirectGenerator = com.blizzardPanel.GeneralConfig.MAIN_URL+com.blizzardPanel.GeneralConfig.BLIZZAR_LINK;
                    if (request.getParameter("rdir") != null) { session.setAttribute("internal_redirect", request.getParameter("rdir")); }
                    redirectUri += "?redirect_uri="+ java.net.URLEncoder.encode(urlRedirectGenerator, "UTF-8");
                    redirectUri += "&scope=wow.profile";
                    redirectUri += "&state=%7B%22region%22%3A%22us%22%7D";
                    redirectUri += "&response_type=code";
                    redirectUri += "&client_id=" + com.blizzardPanel.blizzardAPI.APIInfo.CLIENT_ID;                    
                }
            %>
            &nbsp;<a href="<%= redirectUri %>"><button class="btn btn-outline-success" type="button"><%= (!user.checkUser())? "Login":"Account Info" %></button></a>
        </div>
    </div>
</nav>