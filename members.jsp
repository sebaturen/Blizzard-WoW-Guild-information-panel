<%@include file="includes/globalObject.jsp" %>
<%@ page import ="com.artOfWar.gameObject.Member" %>
<jsp:useBean id="members" class="com.artOfWar.viewController.Members"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title><%= guild_info.getName() %> - Guild members</title>
        <%@include file="includes/header.jsp" %>        
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container">
            <div id="character-content">
                <table class="table table-dark character-tab">
                    <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Name</th>
                            <th scope="col">Class</th>
                            <th scope="col">Level</th>
                            <th scope="col">Role (Spec)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%  
                        int i = 1;
                        for(Member member : members.getMembersList())
                        {%>
                            <tr>
                                <th scope="row"><%= i %></th>
                                <td class="character-<%= (member.getmemberClass().getEnName()).replaceAll("\\s+","") %>"><%= member.getName() %></td>
                                <td><%= member.getmemberClass().getEnName() %></td>
                                <td><%= member.getLevel() %></td>
                                <td><%= member.getActiveSpec().getRole() %> (<%= member.getActiveSpec().getName() %>)</td>
                            </tr>
                      <%i++;}%>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>
