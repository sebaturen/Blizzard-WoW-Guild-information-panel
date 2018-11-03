<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*"%>
<%@ page import ="com.artOfWar.gameObject.Member" %>
<jsp:useBean id="members" class="com.artOfWar.viewController.Members"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
	<head>
		<%@include file="includes/header.jsp" %>
	</head>
	<body>
		<%@include file="includes/menu.jsp" %>
		<div class="container">
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
							//Only show members who logged in at least 1 month ago
							java.util.Calendar cal = java.util.Calendar.getInstance();
							cal.add(java.util.Calendar.MONTH, -1);
							java.util.Date oneMotheAgo = cal.getTime();
							
							for(Member member : members.getMembersList())
							{								
								if( (member.getLastModifiedDate()).compareTo(oneMotheAgo) > 0)
								{
									out.write	("<tr>									"+
												"	<th>"+ member.getName()  +"</th>	"+
												"	<td>"+ member.getLevel() +"</td>	"+
												"</tr>");									
								}
							}
						%>
					</tbody>
				</table>
			</div>
		</div>
	</body>
</html>
