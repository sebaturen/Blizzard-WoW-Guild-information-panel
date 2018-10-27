/**
 * File : Update.java
 * Desc : Update player and guild information
 * @author Sebastián Turen Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.dbConnect.DBConnect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.DataFormatException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;

public class Update implements APIInfo
{
	
	//TEST FUNCTION
	public void runUpdate() throws IOException, ParseException, DataFormatException, ClassNotFoundException, SQLException
	{
		getGuildProfile();
	}
	
	private String accesToken = "";
	private DBConnect dbConnect;
	
	/**
	 * Constructor. Ejecuta el metodo para obtener el token de acceso
	 */
	public Update() throws IOException, ParseException
	{
		dbConnect = new DBConnect();
		generateAccesToken();
	}
	
	/**
	 * Siempre se requiere un token de acceso que depende del ID
	 * y del clientSecret de la api de Blizzard, por lo que debe ser generada 
	 * al inicializarse este objeto
	 */
	private void generateAccesToken() throws IOException, ParseException
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
	private void getGuildProfile() throws IOException, ParseException, DataFormatException, SQLException, ClassNotFoundException
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
			
			//IF NOT EXISTE NAME GUILD {
			dbConnect.insert("guild_info",
							new String[] {"name","lastModified", "battlegroup", "level", "side", "achievementPoints"},
							new String[] { 	respond.get("name").toString(), 
											respond.get("lastModified").toString(),
											respond.get("battlegroup").toString(),
											respond.get("level").toString(),
											respond.get("side").toString(),
											respond.get("achievementPoints").toString()});
			//} ELSE IF (NEW LASTMODIFIED != OLD LASTMODIFIED) {
				//UPDATE
			//}
		}
	}
	
	/**
	 * Genera la conexion URL a la appi
	 * @urlString : URl de la api completa
	 * @method : GET, POST, DELETE, etc
	 * @authorization : autorizacion de la API, Bearer, o basic, etc
	 * @bodyData : en caso de tener datos en el body
	 */
	private JSONObject curl(String urlString, String method, String authorization) throws IOException, ParseException { return curl(urlString, method, authorization, null); }
	private JSONObject curl(String urlString, String method, String authorization, byte[] bodyData) throws IOException, ParseException
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
		
		//!!!!!!!CONTROLAR LOS ERRORES!!!!!!!!
		
		//get result
		BufferedReader reader = new BufferedReader ( new InputStreamReader(conn.getInputStream()));
		String result = reader.readLine();
		reader.close();
		
		//Parse JSON Object
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(result);
		
		return json;
	}
}