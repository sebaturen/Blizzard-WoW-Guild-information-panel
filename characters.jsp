<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
  <head>
    <%@include file="includes/header.jsp" %>
  </head>
  <body>
    <%@include file="includes/menu.jsp" %>
    <div class="container">
		<jsp:useBean id="addr" class="com.artOfWar.blizzardAPI.Update"/>
		<!-- addr.getCharacterInfo(); --><%  %>	
      <div id="player-content">
        <table class="player-tab">
          <thead>
            <tr>
              <th>Name</th>
              <th>Nivel</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <th>Alphascan</th>
              <td>120</td>
            </tr>
            <tr>
              <th>Papadaman</th>
              <td>120</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </body>
</html>
