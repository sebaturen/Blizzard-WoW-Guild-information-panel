<%@ page import ="com.blizzardPanel.gameObject.WoWToken" %>
<%@ page import ="com.blizzardPanel.update.blizzard.WoWOauthService" %>
<% String[] path = (request.getRequestURI()).split("/");
String currentPath = ""; if (path.length > 0) currentPath = path[path.length-1]; %>
<nav class="navbar navbar-expand-lg navbar-dark">
    <a class="navbar-brand" href="<%= request.getContextPath() %>/index.jsp">
        <img src="<%= request.getContextPath() %>/assets/img/artofwar_logo.png" height="30" alt="">
    </a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item <%= (currentPath.equals("index.jsp") || currentPath.equals(""))? "active":"" %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/index.jsp"><fmt:message key="label.home" /></a>
            </li>
            <li class="nav-item <%= (currentPath.equals("list_members.jsp"))? "active":"" %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/members.jsp"><fmt:message key="label.member_list" /></a>
            </li>
            <li class="nav-item dropdown <%= (currentPath.equals("mythic_plus.jsp") || currentPath.equals("mythic_plus_failed.jsp"))? "active":"" %>">
                <a id="navbarDropdown" class="nav-link dropdown-toggle" href="#" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><fmt:message key="label.mythic_plus" /></a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <a class="dropdown-item" href="<%= request.getContextPath() %>/mythic_plus.jsp"><fmt:message key="label.week_runs" /></a>
                    <a class="dropdown-item" href="<%= request.getContextPath() %>/mythic_plus_failed.jsp"><fmt:message key="label.failed_runs" /></a>
                </div>
            </li>
            <% if(guildMember) { %>
                <li class="nav-item <%= (currentPath.equals("polls.jsp"))? "active":"" %>">
                    <a class="nav-link" href="<%= request.getContextPath() %>/polls.jsp"><fmt:message key="label.polls" /></a>
                </li>
            <% } %>
            <% if(guildMember && user.getId() == 1) { %>
            <li class="nav-item <%= (currentPath.equals("progress.jsp"))? "active":"" %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/progress.jsp"><fmt:message key="label.guild_progress" /></a>
            </li>
                <li class="nav-item <%= (currentPath.equals("alters.jsp"))? "active":"" %>">
                    <a class="nav-link" href="<%= request.getContextPath() %>/alters.jsp"><fmt:message key="label.alters" /></a>
                </li>
                <li class="nav-item <%= (currentPath.equals("auction_house.jsp"))? "active":"" %>">
                    <a class="nav-link" href="<%= request.getContextPath() %>/auction_house.jsp"><fmt:message key="label.auction_house" /></a>
                </li>
                <li class="nav-item <%= (currentPath.equals("events.jsp"))? "active":"" %>">
                    <a class="nav-link" href="<%= request.getContextPath() %>/events.jsp"><fmt:message key="label.events" /></a>
                </li>
            <% } %>
            <li class="nav-item <%= (currentPath.equals("faqs.jsp"))? "active":"" %>">
                <a class="nav-link" href="<%= request.getContextPath() %>/faqs.jsp"><fmt:message key="label.simeo" /></a>
            </li>
            <li class="nav-item">
                <jsp:setProperty name="dateObject" property="time" value="${guild.last_modified}" />
                <a class="nav-link disabled" href="#">Last Update[<fmt:formatDate value="${dateObject}" pattern="${general_config.getDateFormat(locale)}" />]</a>
            </li>
        </ul>
        <div class="form-inline my-2 my-lg-0">
            <div class="token_price">
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

    <div id="mythic_run_mold" class="key_run_group dungeon-challenge col-12 col-sm-6 col-md-4" style="display: none">
        <div class="mythicContent">
            <div class='key_run_dun_img dungeon-challenge-img' style=''>
                <div class='key_lvl_det'>
                    <div class='key_run_lvl key_title'></div>
                </div>
                <div class="key_db_title">
                    <h2 class="key_dung_title"></h2>
                </div>
            </div>
            <div class="key_detail">
                <div class="row">
                    <p class="col key_group_time"></p>
                    <p class="col key_date"></p>
                </div>
                <div class="character-tab key_characters">
                    <div class="row pjInfo">
                        <div class="col-7"><fmt:message key="label.name" /></div>
                        <div class="col-3"><fmt:message key="label.role" /></div>
                        <div class="col-2"><fmt:message key="label.ilvl" /></div>
                    </div>
                        <div id="key_char_detail" class="row pjInfo">
                        <div class="col-7 key_char_name"></div>
                        <div class="col-3">
                            <img class="key_char_rol_img" src='' style='width: 22px;'/>
                            <img class="key_char_spec_img" rc='' style='width: 22px;'/>
                        </div>
                        <div class="col-2 key_char_ilvl"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</nav>
<div class="item-floating-desc tooltip-affix">
    <div class="itemDesc tooltipDesc">
        <p id="affix_name"></p>
        <p id="affix_desc" class="tooltip-yellow itemSpellDetail"></p>
        <div id="wow_token_graph" style="display: none">
            <div id="tokenGraph" style="height: 400px; width: 500px;"></div>
        </div>
    </div>
</div>