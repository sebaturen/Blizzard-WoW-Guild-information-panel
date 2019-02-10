<%@include file="../includes/globalObject.jsp" %>
<%@ page import ="java.net.URLEncoder" %>
<%
if(!guildMember)
{  
    response.sendRedirect("../login.jsp?rdir="+URLEncoder.encode("userpanel/poll_create.jsp", "UTF-8"));
}
else
{
    if(user.getGuildRank() != 0 && user.getGuildRank() != 1)
    {//Validate user is Guild Lider or Officer
        out.write("Only Guild Leader or Officers can access from this page.");
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
        <title><%= guild_info.getName() %> - Create poll panel</title>
        <%@include file="../includes/header.jsp" %>
        <script src="../assets/js/poll_create.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.15.2/moment.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.8.0/js/bootstrap-datepicker.min.js"></script>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.8.0/css/bootstrap-datepicker.min.css" rel="stylesheet"/>
    </head>
    <body>
        <%@include file="../includes/menu.jsp" %>
        <div class="container fill">
            <div class="loader ajaxLoad" style="display: none;"></div>
            <div id="create_poll_result" style="display: none;"></div>
            <form method="POST" accept-charset="UTF-8" id="poll_create_form">
                <!-- Question -->
                <div class="form-group">
                    <label for="exampleTextarea">Poll question</label>
                    <textarea class="form-control" id="exampleTextarea" rows="3" name="poll_quest"></textarea>
                </div>
                <!-- Options -->
                <div id="options" class="divder">
                    <!-- Option 1-->
                    <div id="option_1" class="form-group row">
                        <label for="example-text-input" class="col-2 col-form-label">Option 1</label>
                        <div class="col-10">
                            <input class="form-control" type="text" value="" name="option_1" id="example-text-input">
                        </div>
                    </div>
                    <div class="row form-group justify-content-end">
                        <button type="button" class="col-3 btn btn-success addOption">+ Option</button>
                    </div>
                </div>
                <!-- Settings -->
                <div class="form-group row">
                    <label class="col-2" for="exampleSelect1">Minimu guild level</label>
                    <div class="col-10">
                        <select name="guild_level" class="form-control" id="exampleSelect1">                        
                           <% if(ranks.getRanks() != null) {
                                for(Rank r : ranks.getRanks(false)){ %>
                                    <option value="<%= r.getId() %>"><%= r.getTitle() %></option>
                          <%}/*end foreach ranks*/ } /*End if is getRanks null*/ %>
                        </select>
                    </div>
                </div>                    
                <div class="form-check">                    
                    <label class="form-check-label">
                        <input type="checkbox" class="form-check-input" name="more_options"> 
                        Other member can add more options?
                    </label>
                </div>
                <div class="form-check">
                    <label class="form-check-label">
                        <input type="checkbox" class="form-check-input" name="multi_options"> 
                        Members can select multiple options?
                    </label>
                </div>  
                <div class="form-group row">
                    <div class="form-check col-2">
                        <label class="form-check-label">
                            <input type="checkbox" class="form-check-input" name="limit_date" id="limitDate"> 
                            Limit Date?
                        </label>  
                    </div> 
                    <div class="col-10" id="divDataPicker" style="display: none;">                        
                        <div class="input-group date" data-provide="datepicker">
                            <input id="dateLimitSelect" type="text" class="form-control" name="set_date_limit" disable>
                            <span class="input-group-addon"><i class="glyphicon glyphicon-th"></i></span>
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
<%}/*if is guild leader or officer */}/*if is guild member*/%>