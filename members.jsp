<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import ="com.artOfWar.gameObject.Member" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
	<head>
		<%@include file="includes/header.jsp" %>
	</head>
	<body>
		<%@include file="includes/menu.jsp" %>
		<div class="container">
			<jsp:useBean id="members" class="com.artOfWar.viewController.Members"/>
			<div id="character-content">
				<table class="character-tab">
					<thead>
						<tr>
						<th>Name</th>
						<th>Level</th>
						</tr>
					</thead>
					<tbody>
						<%  
							for(Member member : members.getMembersList())
							{
								out.println("<tr>									"+
											"	<th>"+ member.getName()  +"</th>	"+
											"	<td>"+ member.getLevel() +"</td>	"+
											"</tr>");
							}
						%>
					</tbody>
				</table>
			</div>
		</div>
	</body>
</html>
