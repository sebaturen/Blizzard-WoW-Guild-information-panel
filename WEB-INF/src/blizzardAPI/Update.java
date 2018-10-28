/**
 * File : Update.java
 * Desc : Update player and guild information
 * @author Sebastián Turen Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;
import com.artOfWar.gameObject.Guild;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;

public class Update implements APIInfo
{
	
	//TEST FUNCTION
	public void runUpdate() throws IOException, ParseException, ClassNotFoundException, SQLException, DataException
	{
		getGuildProfile();
	}
	
	//Atribute
	private String accesToken = "";
	private DBConnect dbConnect;
	
	/**
	 * Constructor. Ejecuta el metodo para obtener el token de acceso
	 */
	public Update() throws IOException, ParseException, DataException
	{
		dbConnect = new DBConnect();
		generateAccesToken();
	}
	
	/**
	 * Siempre se requiere un token de acceso que depende del ID
	 * y del clientSecret de la api de Blizzard, por lo que debe ser generada 
	 * al inicializarse este objeto
	 */
	private void generateAccesToken() throws IOException, ParseException, DataException
	{
		String urlString = String.format(API_OAUTH_TOKEN_URL, SERVER_LOCATION);
		String apiInfo = Base64.getEncoder().encodeToString((CLIENT_ID+":"+CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));
		
		//prepare info
		String boodyDate = "grant_type=client_credentials";
		byte[] postDataBytes = boodyDate.getBytes("UTF-8");
		
		//Get an Acces Token
		this.accesToken = (String) (curl(urlString,
										"POST",
										"Basic "+ apiInfo,
										postDataBytes)
									).get("access_token");
	}
	
	/**
	 * Get a guild profile
	 */
	private void getGuildProfile() throws IOException, ParseException, SQLException, ClassNotFoundException, DataException
	{
		if(this.accesToken.length() == 0) System.out.println("Acces token not found");
		else
		{
			String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_GUILD_PROFILE, 
																			URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
																			URLEncoder.encode(GUILD_NAME, "UTF-8").replace("+", "%20")));
			JSONObject respond = curl(urlString, 
									"GET",
									"Bearer "+ this.accesToken);
			
			Guild actualGuild = new Guild(); //actual guild in DB
			Guild apiGuild = new Guild(respond.get("name").toString(), //consctrut a new guild, using a get data
										(long) respond.get("lastModified"),
										respond.get("battlegroup").toString(),
										((Long) respond.get("level")).intValue(),
										((Long) respond.get("side")).intValue(),
										(long) respond.get("achievementPoints"));
										
			//If guild not exist in DB, or is not update
			if( !actualGuild.isData() ) //guild not exist
			{
				System.out.println("Guild not found");
				dbConnect.insert("guild_info",
								new String[] {"name","lastModified", "battlegroup", "level", "side", "achievementPoints"},
								new String[] { 	respond.get("name").toString(), 
												respond.get("lastModified").toString(),
												respond.get("battlegroup").toString(),
												respond.get("level").toString(),
												respond.get("side").toString(),
												respond.get("achievementPoints").toString()});
				
			}
			else if (!actualGuild.equals(apiGuild))
			{
				System.out.println("Guild Update found");
				dbConnect.update("guild_info",
								new String[] {"lastModified", "battlegroup", "level", "side", "achievementPoints"},
								new String[] { 	respond.get("lastModified").toString(),
												respond.get("battlegroup").toString(),
												respond.get("level").toString(),
												respond.get("side").toString(),
												respond.get("achievementPoints").toString()});
			}else { System.out.println("Guild not CHANGE"); }
		}
	}
	
	/**
	 * Genera la conexion URL a la appi
	 * @urlString : URl de la api completa
	 * @method : GET, POST, DELETE, etc
	 * @authorization : autorizacion de la API, Bearer, o basic, etc
	 * @bodyData : en caso de tener datos en el body
	 */
	private JSONObject curl(String urlString, String method, String authorization) throws IOException, ParseException, DataException { return curl(urlString, method, authorization, null); }
	private JSONObject curl(String urlString, String method, String authorization, byte[] bodyData) throws IOException, ParseException, DataException
	{
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		//set Connection
		conn.setRequestMethod(method);
		conn.setRequestProperty("Authorization", authorization);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		//body data
		if(bodyData != null) 
		{			
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(bodyData.length));
			conn.getOutputStream().write(bodyData);
		}
		
		//return Object
		JSONObject json;
		
		//Error Request controller
		switch(conn.getResponseCode())
		{
			case HttpURLConnection.HTTP_OK:
				//get result
				BufferedReader reader = new BufferedReader ( new InputStreamReader(conn.getInputStream()));
				String result = reader.readLine();
				reader.close();

				//Parse JSON Object
				JSONParser parser = new JSONParser();
				json = (JSONObject) parser.parse(result);

				break;
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				throw new DataException("Error: "+ conn.getResponseCode() +" - UnAuthorized request, check CLIENT_ID and CLIENT_SECRET in APIInfo.java");
			case HttpURLConnection.HTTP_BAD_REQUEST:
				throw new DataException("Error: "+ conn.getResponseCode() +" - Bad Request request, check the API URL is correct in APIInfo.java");
			case HttpURLConnection.HTTP_NOT_FOUND:
				throw new DataException("Error: "+ conn.getResponseCode() +" - Data not found, check the guild name, server location and realm in APIInfo.java");
			default:
				throw new DataException("Error: "+ conn.getResponseCode() +" - Internal Code: 0");
		}
		
		return json;

	}
}