<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
  <head>
    <%@include file="includes/header.jsp" %>
  </head>
  <body>
    <%@include file="includes/menu.jsp" %>
    <div class="container">
		<div id="welcome">
			<jsp:useBean id="addr" class="com.artOfWar.blizzardAPI.Update"/>
			<jsp:setProperty name="addr" property="*"/>
			
			<% addr.runUpdate(); %>
			Welcome to Art of War
		</div>
    </div>
  </body>
</html>