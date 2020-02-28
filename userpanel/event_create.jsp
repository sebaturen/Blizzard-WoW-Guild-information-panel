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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Create Event panel</title>
        <%@include file="../includes/header.jsp" %>
        <script src="../assets/js/event_create.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
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
                    <div class="form-group">
                        <div class="input-group date" id="datetimepicker1" data-target-input="nearest">
                            <input type="text" name="event_date" class="form-control datetimepicker-input" data-target="#datetimepicker1"/>
                            <div class="input-group-append" data-target="#datetimepicker1" data-toggle="datetimepicker">
                                <div class="input-group-text"><i class="fa fa-calendar"></i></div>
                            </div>
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
