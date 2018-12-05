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
        boolean saveNewPoll = false;
        int trySavePoll = 0;
        if(request.getParameter("save_apply") != null && request.getParameter("save_apply").equals("true"))
        { %><%@ page import ="java.util.ArrayList" %>
            <%@ page import ="java.util.List" %>
            <%@ page import = "java.util.Map" %>
            <jsp:useBean id="pollControl" class="com.blizzardPanel.viewController.PollController"/><%
               
            request.setCharacterEncoding("UTF-8");
            String pollQuest = "";
            String limitDateSet = "";
            boolean moreOptions = false;
            boolean multiOptions = false;
            boolean limitDate = false;
            if(request.getParameter("poll_quest") != null)
                pollQuest = request.getParameter("poll_quest");
            int minGuildLevel = Integer.parseInt(request.getParameter("guild_level"));
            if(request.getParameter("more_options") != null)
                moreOptions = (request.getParameter("more_options").equals("on"));
            if(request.getParameter("multi_options") != null)
                multiOptions = (request.getParameter("multi_options").equals("on"));
            if(request.getParameter("limit_date") != null)
                limitDate = (request.getParameter("limit_date").equals("on"));
            if(request.getParameter("set_date_limit") != null)
                limitDateSet = request.getParameter( "set_date_limit" );
            
            System.out.println(">> "+ pollQuest);
            List<String> options = new ArrayList<>();
            //Get all options!
            Map<String, String[]> parameters = request.getParameterMap();
            for(String parameter : parameters.keySet())
            {
                if(parameter.toLowerCase().startsWith("option_")) 
                {
                    String[] values = parameters.get(parameter);
                    if(values[0].length() > 0)
                    {
                        System.out.println(values[0]);
                        options.add(values[0]);
                    }
                }
            }
            
            trySavePoll++;
            if(pollQuest.length() > 0 && options.size() > 0)
            {
                saveNewPoll = pollControl.newPoll(
                            user, pollQuest, minGuildLevel, moreOptions,
                            multiOptions, limitDate, 
                            limitDateSet, options);
            }
        }
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
        <script src="../assets/js/create_poll.js"></script>
    </head>
    <body>
        <%@include file="../includes/menu.jsp" %>
        <div class="container fill">
            <% if(trySavePoll > 0) { 
                if(saveNewPoll)
                    out.write("<div class='alert alert-primary' role='alert'><a href='../polls.jsp' class='alert-link'>New poll</a> is create!</div>");
                else
                    out.write("<div class='alert alert-danger' role='alert'>Fail to save a new poll</div>");
            }%>
            <form method="POST" accept-charset="UTF-8">
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
                           <%  if(ranks.getRanks() != null) {
                                for(Rank r : ranks.getRanks(false)){ %>
                                    <option value="<%= r.getId() %>"><%= r.getTitle() %></option>
                          <%}/*end foreach ranks*/ } /*End if is getRanks null*/%>
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
                    <div class="col-10">
                        <input class="form-control" type="datetime-local" id="dateLimitSelect" value="<%= (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(new Date()) %>" id="example-datetime-local-input" name="set_date_limit" disabled>
                    </div>
                </div>
                <input type="hidden" value="true" name="save_apply" />
                <button type="submit" class="btn btn-primary">Save</button>
            </form>
        </div>
    </body>
</html>
<%}/*if is guild leader or officer */}/*if is guild member*/%>