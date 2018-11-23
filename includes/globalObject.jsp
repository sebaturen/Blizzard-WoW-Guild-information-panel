<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*"%>
<jsp:useBean id="gameInfo" class="com.blizzardPanel.viewController.GameInfo" scope="request"/>
<jsp:useBean id="guild_info" class="com.blizzardPanel.gameObject.guild.Guild" scope="request"/>
<jsp:useBean id="user" class="com.blizzardPanel.viewController.User" scope="session" />