<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import ="com.artOfWar.gameObject.Member" %>
<%@ page import ="com.artOfWar.gameObject.challenge.Challenge" %>
<%@ page import ="com.artOfWar.gameObject.challenge.ChallengeGroup" %>
<jsp:useBean id="challenges" class="com.artOfWar.viewController.GuildChallenges"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <%@include file="includes/header.jsp" %>
    </head>
    <body>
        <%@include file="includes/menu.jsp" %>
        <div class="container">
            <div id="challenges-content">			
                <%  
                for(Challenge ch : challenges.getChallengesList())
                {
                    out.write("<b>"+ ch.getMapName() +"</b></br>");	
                    for(ChallengeGroup groupCh : ch.getChallengeGroups())
                    {
                        out.write("--> "+ groupCh.getTimeDate() +"</br>");
                        out.write("["+ groupCh.getTimeHour() +":"+ groupCh.getTimeMinutes() +":"+ groupCh.getTimeSeconds() +":"+ groupCh.getTimeMilliseconds() +"] (Finish in Time? "+ groupCh.isPositive() +")</br>");
                        for(Member m : groupCh.getMembers())
                        {
                            out.write("-"+ m.getName() +" ("+ m.getActiveSpec().getRole() +")</br>");
                        }
                    }
                    out.write("</br>");
                }
                %>
            </div>
        </div>
    </body>
</html>
