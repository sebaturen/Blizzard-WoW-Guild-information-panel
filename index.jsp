<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
	<head>
		<%@include file="includes/header.jsp" %>
		<jsp:useBean id="guild_info" class="com.artOfWar.gameObject.Guild"/>
	</head>
	<body>
		<%@include file="includes/menu.jsp" %>
		<div class="container fill">
			<div id="welcome">
				Welcome to <% out.write(guild_info.getName()); %>
			</div>
		</div>
	</body>
</html>