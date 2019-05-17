<%@include file="includes/globalObject.jsp" %>
<% if (!guildMember) {%><%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("events.jsp", "UTF-8"));
} else {%>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<%@ page import ="com.blizzardPanel.events.Event" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterMember" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterSpec" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.PlayableSpec" %>
<jsp:useBean id="eventsControl" class="com.blizzardPanel.viewController.EventsController" scope="session"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Events</title>
        <%@include file="includes/header.jsp" %>
        <script src="assets/js/events.js"></script>
    </head>
    <body>
        <div id="drag_char" class="item-floting-desc" style="display: none;"></div>
        <%@include file="includes/menu.jsp" %>
        <div id="currentUserInfo" style="display: none;"
             data-user_id="<%= user.getId() %>"
             data-user_show="<%= (user.getMainCharacter() != null)? user.getMainCharacter().getName():user.getBattleTag().split("#")[0] %>"
             data-user_class="<%= (user.getMainCharacter() != null)? user.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"
        ></div>
        <div id="ev_container" class="container fill">
            <% if( request.getParameter("ev") != null && (Integer.parseInt(request.getParameter("ev"))) > 0) { /*event detal: */
                    Event ev = new Event(Integer.parseInt(request.getParameter("ev"))); %>
                    <span class="ev_owner character-<%= (user.getMainCharacter() != null)? user.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"><%=  (user.getMainCharacter() != null)? user.getMainCharacter().getName():user.getBattleTag().split("#")[0] %></span>
                    <div class="ev_title"><h1><%= ev.getTitle() %></h1></div>
                    <span class="right_small_date"><%= ev.getDate() %></span>
                    <p><%= ev.getDesc() %></p>
                    <!-- OTHER PLAYERS... -->
                    <div class="custom-control custom-switch">
                        <input type="checkbox" class="custom-control-input" id="participate_switch">
                        <label class="custom-control-label" for="customSwitch1">participate</label>
                    </div>
                    <!-- Character select area: -->
                    <div class="main_alter_drop_zone row">
                        <div class="col-md-4">
                            <h3>Main select</h3>
                            <div id="main_zone" class="drop_zone">
                                <div id="main_name"></div>
                                <div id="main_lvl"></div>
                                <div id="main_specs"></div>
                            </div>
                        </div>
                        <div class="col-md-8">
                            <h3>Alters select</h3>
                            <div id="alters_zone" class="drop_zone">
                                <table class="table table-dark character-alter-tab">
                                    <thead>
                                        <tr>
                                            <th scope="col"></th> <!-- NAME -->
                                            <th scope="col"></th> <!-- LVL -->
                                            <th scope="col"></th> <!-- SPECS -->
                                            <th scope="col"></th> <!-- DELETE -->
                                        </tr>
                                    </thead>
                                    <tbody id="alter_table_zone">
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <% //User character info~
                    List<CharacterMember> memberChars = user.getCharacters();
                    if(memberChars.size() > 0)
                    {%>
                        <table class="table table-dark character-tab">
                            <thead>
                                <tr>
                                    <th scope="col">Name</th>
                                    <th scope="col">Spec</th>
                                    <th scope="col">Level</th>
                                </tr>
                            </thead>
                            <tbody>
                              <% for(CharacterMember m : memberChars)
                                 {
                                   if (m.getFaction() == 0 && m.getRealm().equals("Ragnaros"))
                                   {
                                       //Get img from speck
                                       String className = m.getMemberClass().getSlug();
                                       String specName = m.getActiveSpec().getSpec().getSlug();
                                       List<CharacterSpec> specs = m.getSpecs(); %>
                                        <tr class="user_char"
                                            id="char_info_<%= m.getId() %>"
                                            data-id="<%= m.getId() %>"
                                            data-name="<%= m.getName() %>"
                                            data-class="<%= className %>"
                                            data-lvl="<%= m.getLevel() %>"
                                            <%  int i = 0;
                                                for(CharacterSpec cs : specs) {
                                                    out.write("data-spec-"+ i +"-id='"+ cs.getId() +"' data-spec-"+ i +"-slug='"+ cs.getSpec().getSlug() +"'");
                                                    i++;
                                                } %>
                                        >
                                            <td class="character-<%= className %>"><%= m.getName() %></td>
                                            <td><img src="assets/img/classes/specs/spec_<%= className %>_<%= specName %>.png" style="width: 22px;"/></td>
                                            <td><%= m.getLevel() %></td>
                                        </tr>
                                    <% } /*end 'if' is correct faction and realm */ %>
                              <%} /* end foreach member m */ %>
                            </tbody>
                        </table>
                  <% } /* close if members CharacterMember more 0 */%>
            <% } else { /*if detail event... continue event list: */
                    List<Event> events = eventsControl.getEvents();
                    if(events.size() > 0) {
                        for(Event e : events) {%>
                            <a href="?ev=<%= e.getId() %>"><div><%= e.getTitle() %></div></a>
                    <%} /*end foreach events*/ } /*end iff events size > 0*/ %>

            <% } /*close event list*/ %>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>
<%}%>
