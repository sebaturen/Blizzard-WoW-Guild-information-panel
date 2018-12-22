<%@include file="includes/globalObject.jsp" %>
<%@ page import ="com.blizzardPanel.gameObject.KeystoneDungeon.KeystoneDungeonRun" %>
<%@ page import ="com.blizzardPanel.gameObject.characters.CharacterMember" %>
<jsp:useBean id="mPlus" class="com.blizzardPanel.viewController.MythicPlusControl" scope="application"/>
<%!
    public String renderMember(CharacterMember mem)
    {
        //General info
        String className = mem.getMemberClass().getSlug();
        String specName = mem.getActiveSpec().getSpec().getSlug();
        String isMain = ((mem.isGuildMember())? "<i class='main_char artOfWar-icon'>&#xe801;</i>":"");

        //Generate render
        return 
            "<tr>"+
                "<td class='character-"+ className +"'>"+ isMain +" "+ mem.getName() +"</td>"+
                "<td>"+
                    "<img src='assets/img/icons/"+ mem.getActiveSpec().getSpec().getRole() +".png' style='width: 22px;'/>"+
                    "<img src='assets/img/classes/specs/spec_"+ className +"_"+ specName +".png' style='width: 22px;'/>"+
                "</td>"+
                "<td>"+ String.format("%.0f", mem.getItemLevel()) +"</td>"+
            "</tr>";
    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Mythic Plus</title>
        <%@include file="includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/index.css">
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container fill">
            <% int i = 0; 
                for(KeystoneDungeonRun keyRun : mPlus.getLastKeyRun()) 
                { if (i == 0) { %><div class="row "><% } else if (i%3==0) { %></div><div class="row"><% } %>
                <div id="key_run_<%= keyRun.getId() %>" class="key_run_group dungeon-challenge col">
                    <div class="key_run_dun_img dungeon-challenge-img" style='background-image: url("assets/img/dungeon/<%= keyRun.getKeystoneDungeon().getMapId() %>.jpg");'>
                        <div class="key_run_lvl"><%= keyRun.getKeystoneLevel() %></div>
                    </div>
                    <%  String classGroupTime = (keyRun.isCompleteInTime())? "downgrade":"upgrade";
                        String dateGroup = keyRun.getCompleteDate();
                        int upgradeKey = keyRun.getUpgradeKey(); 
                        int[] durationTime = keyRun.getTimeDuration();
                    %>
                    <p class='group-time key-<%= classGroupTime %>'><%= "["+ durationTime[0] +"h:"+ durationTime[1] +"m:"+ durationTime[2] +"s]" %><%= (upgradeKey == -1)? "":" (+"+upgradeKey+")" %></p>
                    <p class='key-date'><%= dateGroup %></p>
                    <table class="table table-dark character-tab">
                        <thead>
                            <tr>
                                <th scope="col">Name</th>
                                <th scope="col">Role</th>
                                <th scope="col">iLevel</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            out.write(renderMember(keyRun.getTank()));
                            out.write(renderMember(keyRun.getHealr()));
                            for(CharacterMember m : keyRun.getDPS())
                                out.write(renderMember(m));
                        %>
                        </tbody>
                    </table>                    
                </div>
            <%i++; } //end foreach key runs%>
            </div> <!-- close last 'i' div open -->
        </div>
        <%@include file="includes/footer.jsp" %>
    </body>
</html>