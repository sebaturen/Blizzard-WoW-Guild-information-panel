<% if (!guildMember) {%><%@ page import ="java.net.URLEncoder" %><%
    response.sendRedirect("login.jsp?rdir="+URLEncoder.encode("events.jsp", "UTF-8"));
} else { %>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<%@ page import ="com.blizzardPanel.User" %>
<%@ page import ="com.blizzardPanel.events.Event" %>
<%@ page import ="com.blizzardPanel.events.EventAsist" %>
<%@ page import ="com.blizzardPanel.events.EventAsistCharacter" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterMember" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterSpec" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.PlayableSpec" %>
<% 
Event ev = new Event(Integer.parseInt(request.getParameter("ev"))); 
EventAsist evAsist = ev.getAssistDetail(user); 
if (!ev.isEnable() || ev.isHide()) {
    response.sendRedirect("events.jsp");
} else { %>
    <div class="loader ajaxLoad" style="display: none;"></div>
    <div id="event_add_result" style="display: none;"></div>
    <span class="ev_owner character-<%= (user.getMainCharacter() != null)? user.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"><%=  (user.getMainCharacter() != null)? user.getMainCharacter().getName():user.getBattleTag().split("#")[0] %></span>
    <div id="eventDetail" data-id="<%= ev.getId() %>" class="key_title key_divide_title"><h1><%= ev.getTitle() %></h1></div>
    <span class="right_small_date"><%= ev.getDate() %></span>
    <p><%= ev.getDesc() %></p>
    <!-- OTHER PLAYERS... -->
    <table class="table table-dark character-tab">
        <thead>
            <tr>
                <th scope="col">Member</th>
                <th scope="col">Main Select</th>
                <th scope="col">Alters select</th>
            </tr>
        </thead>
        <tbody>
            <% for(EventAsist assistens : ev.getEventAssist()) { 
                User asistUser = assistens.getUser(); 
                CharacterMember mainChar = assistens.getMainEventAssistCharacter().getCharMember();
                String className = mainChar.getMemberClass().getSlug(); %>
                <tr>
                    <td><span class="character-<%= (asistUser.getMainCharacter() != null)? asistUser.getMainCharacter().getMemberClass().getSlug():"BATTLE_TAG"%>"><%=  (asistUser.getMainCharacter() != null)? asistUser.getMainCharacter().getName():asistUser.getBattleTag().split("#")[0] %></span></td>
                    <td>
                        <img src="assets/img/classes/specs/spec_<%= className %>_<%= mainChar.getActiveSpec().getSpec().getSlug() %>.png" style="width: 22px;" >
                        <span class="character-<%= className %>"><%= mainChar.getName() %></span>
                    </td>
                    <td>
                        <% for(EventAsistCharacter alters : assistens.getAlterEventAssistCharacter() ) { 
                            CharacterMember alt = alters.getCharMember();
                            String altClassName = alt.getMemberClass().getSlug(); %>
                                <img src="assets/img/classes/specs/spec_<%= altClassName %>_<%= alt.getActiveSpec().getSpec().getSlug() %>.png" style="width: 22px;" >
                                <span class="character-<%= altClassName %>"><%= alt.getName() %></span>
                                <br>
                        <% } %>
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
    <!-- Character select area: -->
    <div id="participate_switch_container" class="custom-control custom-switch">
        <input type="checkbox" class="custom-control-input" id="participate_switch" <%= (evAsist != null)? "checked":"" %>>
        <label class="custom-control-label" for="customSwitch1">participate</label>
    </div>
    <div class="main_alter_drop_zone row">
        <div class="col-md-4">
            <h3>Main select</h3>
            <% if(evAsist != null) {
                CharacterMember main = evAsist.getMainEventAssistCharacter().getCharMember(); %>
                <div id="main_zone" 
                    class="drop_zone" 
                    data-char-id="<%= main.getId() %>" 
                    data-spec-id="<%= main.getActiveSpec().getId() %>"
                >
                    <div id="delete_main"><i class='fa fa-trash'></i></div>
                    <div id="main_name">
                        <div class="character-<%= main.getMemberClass().getSlug() %>">
                            <%= main.getName() %>
                        </div>
                    </div>
                    <div id="main_lvl"><%= main.getLevel() %></div>
                    <div id="main_specs">
                        <div class="specs">
                        <% for(CharacterSpec spec : main.getSpecs()) { %>
                            <img 
                                class="<%= (main.getActiveSpec().getId() != spec.getId())? "black_white":"" %> spec_select" 
                                onclick="specSelecMain(this)" 
                                data-spec_id="<%= spec.getId() %>" 
                                data-char_id="<%= main.getId() %>" 
                                src="assets/img/classes/specs/spec_<%= main.getMemberClass().getSlug() %>_<%= spec.getSpec().getSlug() %>.png"
                                style="width: 40px;"
                            >
                        <%}%>
                        </div>
                    </div>
                </div>
            <% } else { %>
                <div id="main_zone" class="drop_zone">
                    <div id="delete_main" style="display: none;"><i class='fa fa-trash'></i></div>
                    <div id="main_name"></div>
                    <div id="main_lvl"></div>
                    <div id="main_specs"></div>
                </div>
            <% } %>
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
                        <% if(evAsist != null) {
                            for(EventAsistCharacter charAsis : evAsist.getAlterEventAssistCharacter() ) {
                                CharacterMember alter = charAsis.getCharMember();
                                String className = alter.getMemberClass().getSlug(); %>
                                <tr id="alter_selec_spec_<%= alter.getId() %>"
                                    data-char-id="<%= alter.getId() %>" 
                                    data-spec-id="<%= alter.getActiveSpec().getId() %>"
                                >
                                    <td class="character-<%= className %>"><%= alter.getName() %></td>
                                    <td><%= alter.getLevel() %></td>
                                    <td class="specs">
                                        <div class="specs">
                                            <% for(CharacterSpec spec : alter.getSpecs()) { %>
                                                <img 
                                                    class="<%= (alter.getActiveSpec().getId() != spec.getId())? "black_white":"" %> spec_select" 
                                                    onclick="specSelecAlter(this)" 
                                                    data-spec_id="<%= spec.getId() %>" 
                                                    data-char_id="<%= alter.getId() %>" 
                                                    src="assets/img/classes/specs/spec_<%= alter.getMemberClass().getSlug() %>_<%= spec.getSpec().getSlug() %>.png"
                                                    style="width: 22px;"
                                                >
                                            <%}%>
                                        </div>
                                    </td>
                                    <td class="removeCharAlter" data-char_id="<%= alter.getId() %>" onclick="removeChar(this)">
                                        <i class="fa fa-trash"></i>
                                    </td>
                                </tr>
                            <% } %>
                        <% } %>
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
                if ( m.getFaction() == 0 && 
                        m.getRealm().equals("Ragnaros") && 
                        m.isGuildMember() &&
                        m.getLevel() >= ev.getMinLevel() )
                {
                        //Get img from speck
                        String className = m.getMemberClass().getSlug();
                        String specName = m.getActiveSpec().getSpec().getSlug();
                        List<CharacterSpec> specs = m.getSpecs(); 
                        String divClass = "user_char";
                        if(evAsist != null && evAsist.isAssistCharacter(m)) {
                            divClass +="_disable";
                        }%>
                        <tr class="<%= divClass %>"
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
    <button id="btn_save_inf" type="submit" class="btn btn-primary" disabled>Save</button>
<%} /* is enable/disable event */ } /* first if */%>