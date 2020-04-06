<%@ page import ="com.blizzardPanel.gameObject.WoWToken" %>
<%@ page import ="com.blizzardPanel.update.blizzard.WoWOauthService" %>
<% String[] path = (request.getRequestURI()).split("/");
String currentPath = ""; if (path.length > 0) currentPath = path[path.length-1];%>
<nav class="navbar navbar-expand-lg navbar-dark">
    <a class="navbar-brand" href="<%= request.getContextPath() %>/index.jsp">
        <img src="<%= request.getContextPath() %>/assets/img/artofwar_logo.png" height="30" alt="">
    </a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="item-floating-desc tooltip-wow_token">
        <div class="itemDesc tooltipDesc">
            <div id="graph">
                <div id="tokenGraph" style="height: 400px; width: 500px;"></div>
            </div>
        </div>
    </div>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item <% out.write((currentPath.equals("index.jsp"))? "active":""); %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/index.jsp"><fmt:message key="label.home" /></a>
            </li>
            <li class="nav-item <% out.write((currentPath.equals("members.jsp"))? "active":""); %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/members.jsp"><fmt:message key="label.member_list" /></a>
            </li>
            <li class="nav-item <% out.write((currentPath.equals("mythic_plus.jsp"))? "active":""); %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/mythic_plus.jsp"><fmt:message key="label.mythic_plus" /></a>
            </li>
            <li class="nav-item <% out.write((currentPath.equals("progress.jsp"))? "active":""); %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/progress.jsp"><fmt:message key="label.guild_progress" /></a>
            </li>
            <% if(guildMember) { %>
                <li class="nav-item <% out.write((currentPath.equals("alters.jsp"))? "active":""); %>">
                    <a class="nav-link" href="<%= request.getContextPath() %>/alters.jsp"><fmt:message key="label.alters" /></a>
                </li>
                <li class="nav-item <% out.write((currentPath.equals("auction_house.jsp"))? "active":""); %>">
                    <a class="nav-link" href="<%= request.getContextPath() %>/auction_house.jsp"><fmt:message key="label.auction_house" /></a>
                </li>
                <li class="nav-item <% out.write((currentPath.equals("polls.jsp"))? "active":""); %>">
                    <a class="nav-link" href="<%= request.getContextPath() %>/polls.jsp"><fmt:message key="label.polls" /></a>
                </li>
                <li class="nav-item <% out.write((currentPath.equals("events.jsp"))? "active":""); %>">
                    <a class="nav-link" href="<%= request.getContextPath() %>/events.jsp"><fmt:message key="label.events" /></a>
                </li>
            <% } %>
            <li class="nav-item <% out.write((currentPath.equals("faqs.jsp"))? "active":""); %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/faqs.jsp"><fmt:message key="label.simeo" /></a>
            </li>
            <li class="nav-item">
                <jsp:setProperty name="dateObject" property="time" value="${guild.last_modified}" />
                <a class="nav-link disabled" href="#">Last Update[<fmt:formatDate value="${dateObject}" pattern="${general_config.getDateFormat(cookie['locale'].getValue())}" />]</a>
            </li>
        </ul>
        <div class="form-inline my-2 my-lg-0">
            <div id="token_price">
                <p class="quality-wow-token"><fmt:message key="label.wow_token" /></p>:&nbsp;
                <% WoWToken tokenPrice = new WoWToken.Builder().build(); %>
                <% if (tokenPrice.getPrice().getGold() > 0) { %><span class="moneygold"><%= String.format("%,d", tokenPrice.getPrice().getGold()) %></span><% } %>
                <% if (tokenPrice.getPrice().getSilver() > 0) { %><span class="moneysilver"><%= String.format("%,d", tokenPrice.getPrice().getSilver()) %></span><% } %>
                <% if (tokenPrice.getPrice().getCopper() > 0) { %><span class="moneycopper"><%= String.format("%,d", tokenPrice.getPrice().getCopper()) %></span><% } %>
            </div>
            <% // Link Blizzard account
                String redirectUri = request.getContextPath() +"/login.jsp";
                if(!user.isLogin())
                {
                    redirectUri = String.format(WoWOauthService.API_OAUTH_URL,
                                                general_config.getStringConfig("SERVER_LOCATION"));
                    redirectUri += WoWOauthService.API_OAUTH_AUTHORIZE;
                    String urlRedirectGenerator = general_config.getStringConfig("MAIN_URL")+general_config.getStringConfig("BLIZZAR_LINK");
                    if (request.getParameter("rdir") != null) { session.setAttribute("internal_redirect", request.getParameter("rdir")); }
                    redirectUri += "?redirect_uri="+ java.net.URLEncoder.encode(urlRedirectGenerator, "UTF-8");
                    redirectUri += "&scope=wow.profile";
                    redirectUri += "&state=%7B%22region%22%3A%22"+ general_config.getStringConfig("SERVER_LOCATION") +"%22%7D";
                    redirectUri += "&response_type=code";
                    redirectUri += "&client_id=" + general_config.getStringConfig("CLIENT_ID");
                }
            %>
            &nbsp;<a href="<%= redirectUri %>">
                <button class="btn btn-outline-success" type="button">
                    <c:if test="${user.login}">
                        <fmt:message key="label.account_info" />
                    </c:if>
                    <c:if test="${!user.login}">
                        <fmt:message key="label.login" />
                    </c:if>
                </button>
        </a>
        </div>
    </div>
</nav>