<%@include file="../includes/globalObject.jsp" %>
<%@ page import ="java.net.URLEncoder" %>
<%
if(!guildMember)
{
    response.sendRedirect("../login.jsp?rdir="+URLEncoder.encode("userpanel/event_create.jsp", "UTF-8"));
}
else
{
%>
<%@ page import ="java.text.SimpleDateFormat" %>
<%@ page import ="java.util.Date" %>
<%@ page import ="com.blizzardPanel.gameObject.guild.Rank" %>
<jsp:useBean id="ranks" class="com.blizzardPanel.viewController.GuildRanks" scope="request"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Create Event panel</title>
        <%@include file="../includes/header.jsp" %>
        <script src="../assets/js/event_create.js"></script>
        <!-- Date Picker! -->
        <script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
        <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
        <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
        <!-- END Date Picker -->
    </head>
    <body>
        <%@include file="../includes/menu.jsp" %>
        <div class="container fill">
            <div class="loader ajaxLoad" style="display: none;"></div>
            <div id="create_event_result" style="display: none;"></div>
            <form method="POST" accept-charset="UTF-8" id="event_create_form">
                <!-- Question -->
                <div class="form-group">
                    <label for="exampleTextarea">Event title*</label>
                    <input class="form-control" type="text" value="" name="event_title" id="example-text-input">
                    <label>Description*</label>
                    <textarea class="form-control" id="exampleTextarea" rows="3" name="event_desc"></textarea>
                    <label>Date*</label>
                    <input class="form-control" type="text" name="event_date" value="" />
                    <label>Min level</label>
                    <input class="form-control" type="number" name="event_lvl" value="120" />
                    <br>
                    <div class="form-group row">
                        <label class="col-2" for="exampleSelect1">Minimum guild level</label>
                        <div class="col-10">
                            <select name="guild_level" class="form-control" id="exampleSelect1">
                               <% if(ranks.getRanks() != null) {
                                    for(Rank r : ranks.getRanks(false)){ %>
                                        <option value="<%= r.getId() %>"><%= r.getTitle() %></option>
                              <%}/*end foreach ranks*/ } /*End if is getRanks null*/ %>
                            </select>
                        </div>
                    </div>
                </div>
                <input type="hidden" value="true" name="save_apply" />
                <button type="submit" class="btn btn-primary">Save</button>
            </form>
        </div>
        <%@include file="../includes/footer.jsp" %>
    </body>
</html>
<%} /*if is guild member*/%>
