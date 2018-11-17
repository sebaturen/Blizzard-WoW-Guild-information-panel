<%@include file="includes/globalObject.jsp" %>
<%@ page import ="com.artOfWar.gameObject.characters.Member" %>
<%@ page import ="java.util.ArrayList" %>
<%@ page import ="java.util.List" %>
<jsp:useBean id="members" class="com.artOfWar.viewController.Members"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Guild members</title>
        <%@include file="includes/header.jsp" %> 
        <link type="text/css" rel="stylesheet" href="/assets/css/members.css"> 
        <script>
            var members = [
                <% 
                List<Integer> guildRank = new ArrayList<>();
                List<String> mClass = new ArrayList<>();
                List<String> spec   = new ArrayList<>();
                for(Member member : members.getMembersList())
                { 
                    String className = ((member.getmemberClass().getEnName()).replaceAll("\\s+","-")).toLowerCase(); 
                    String specName = ((member.getActiveSpec().getName()).replaceAll("\\s+","-")).toLowerCase(); 
                    if (!guildRank.contains(member.getRank())) guildRank.add(member.getRank());
                    if (!mClass.contains(className)) mClass.add(className);
                    if (!spec.contains(specName)) spec.add(specName);
                    String iLevel = "0";
                    String healt = "";
                    if(user != null && user.getGuildRank() != -1)
                    {
                        iLevel = String.format("%.2f", member.getItemLevel());
                        healt = String.format("%,d", member.getStats().getHealth());
                    }
                %>
                { name: '<%= member.getName() %>', class: '<%= className %>', spec: '<%= specName %>', level: <%= member.getLevel() %>, 
                  img: '<%= member.getThumbnailURL() %>', rol: '<%= member.getActiveSpec().getRole() %>', member_id: <%= member.getId() %>, gRank: <%= member.getRank() %>,
                  iLevel: '<%= iLevel %>', health: '<%= healt %>'},
              <%}%>
            ];
        </script>
        <script src="/assets/js/members.js"></script>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container">
            <div id="character-content">
                <% if(user != null && user.getGuildRank() != -1) { %>
                <button type="button" class="btn btn-info" id='membersFilters'>Filters</button>
                <form style="display: none;" id='formFilter'>
                    <br>
                    <div class="form-group">
                        <label>Name</label>
                        <input class="form-control" type="text" value="" id="nameInput"/>
                    </div>
                    <div class="row">
                        <div class="col">
                            <div class="form-group">
                                <label>Guild Rank</label>
                                <select class="form-control" id='guildRankSelect'>
                                    <option>All</option>
                                    <% for(Integer b : guildRank) {out.write("<option>"+b+"</option>");} %>
                                </select>
                            </div>                                
                            <div class="form-group">
                                <label>Class</label>
                                <select class="form-control" id='classSelect'>
                                    <option>All</option>
                                    <% for(String b : mClass) {out.write("<option>"+b+"</option>");} %>
                                </select>
                            </div>
                        </div>
                        <div class='col'>
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
                        <% if(user != null && user.getGuildRank() != -1) { %>
                            <th scope="col" id="iLevelColum" class='pointer'>iLevel</th>
                        <% } %>
                        </tr>
                    </thead>
                    <tbody id="charContent">
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>
