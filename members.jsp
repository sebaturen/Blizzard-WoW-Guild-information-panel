<%@include file="includes/globalObject.jsp" %>
<jsp:useBean id="members" class="com.blizzardPanel.viewController.Members"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Guild members</title>
        <%@include file="includes/header.jsp" %> 
        <link type="text/css" rel="stylesheet" href="assets/css/members.css">
        <script src="assets/js/members/loadMemberContent.js"></script>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container">
            <div id="character-content">
                <% if(guildMember) { %>
                <button type="button" class="btn btn-info" id='membersFilters'>Filters</button>
                <form style="display: none;" id='formFilter'>
                    <br>
                    <div class="row">
                        <div class="col">
                            <div class="form-group">
                                <label>Name</label>
                                <input class="form-control" type="text" value="" id="nameInput"/>
                            </div>
                            <div class="form-group">
                                <label>Guild Rank</label>
                                <select class="form-control" id='guildRankSelect'>
                                    <option>All</option>
                                </select>
                            </div>                                
                            <div class="form-group">
                                <label>Class</label>
                                <select class="form-control" id='classSelect'>
                                    <option>All</option>
                                </select>
                            </div>
                        </div>
                        <div class='col'>
                            <div class="form-group">
                                <label>Races</label>
                                <select class="form-control" id='racesSelect'>
                                    <option>All</option>
                                </select>
                            </div>
                            <div class="form-group">  
                                <label>Level</label>                               
                                <div class="row">
                                    <div class='col'> 
                                        <select class="form-control" id="levelSelect">
                                            <option>All</option>
                                            <option>Greater than</option>
                                            <option>Less than</option>
                                        </select>                                      
                                    </div>
                                    <div class='col'>
                                        <input class="form-control" type="number" value="" id="levelInput" disabled/>
                                    </div>
                                </div>                                
                            </div>
                            <div class="form-group">
                                <label>Item Level</label>
                                <div class='row'>
                                    <div class="col">
                                        <select class="form-control" id='ilevelSelect'>
                                            <option>All</option>
                                            <option>Greater than</option>
                                            <option>Less than</option>
                                        </select>
                                    </div>
                                    <div class="col">
                                        <input class="form-control" type="number" value="" id="ilevelInput" disabled/>
                                    </div>                                    
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
                <% } %>
                <table class="table table-dark character-tab">
                    <thead>
                        <tr>
                            <th scope="col" id="rankColum" class='pointer'>#</th>
                            <th scope="col" id="nameColum" class='pointer'>Name</th>
                            <th scope="col" id="classColum" class='pointer'>Class</th>
                            <th scope="col" id="levelColum" class='pointer'>Level</th>
                            <th scope="col" id="specColum" class='pointer'>Current Spec</th>
                        <% if(guildMember) { %>
                            <th scope="col" id="iLevelColum" class='pointer'>iLevel</th>
                        <% } %>
                        </tr>
                    </thead>
                    <tbody id="charContent">
                        <tr><td colspan='6'><div class="row justify-content-md-center"><div class="loader"></div></div></td></tr>
                    </tbody>
                </table>
            </div>
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>
