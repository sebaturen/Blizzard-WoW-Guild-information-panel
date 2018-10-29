/**
 * File : Update.java
 * Desc : Update guild and character in guild information
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;
import com.artOfWar.gameObject.Guild;
import com.artOfWar.gameObject.Character;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;

public class Update implements APIInfo
{

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
										null,
										postDataBytes)
									).get("access_token");
	}
	
	/**
	 * Get a guild profile
	 */
	public void getGuildProfile() throws IOException, ParseException, SQLException, ClassNotFoundException, DataException
	{
		if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
		else
		{
			//Generate an API URL
			String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_GUILD_PROFILE, 
																			URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
																			URLEncoder.encode(GUILD_NAME, "UTF-8").replace("+", "%20")));
			//Call Blizzard API
			JSONObject respond = curl(urlString, 
									"GET",
									"Bearer "+ this.accesToken);
			
			Guild actualGuild = new Guild(); //actual guild in DB
			Guild apiGuild = new Guild(respond);
										
			//If guild not exist in DB, or is not update
			if( !actualGuild.isData() ) //guild not exist
			{
				System.out.println("Guild not found");
				apiGuild.insertInDB();
				
			}
			else if (!actualGuild.equals(apiGuild))
			{
				System.out.println("Guild Update found");
				apiGuild.updateInDB();
			}
		}
	}
	
	/**
	 * get a guilds members
	 */
	public void getGuildMembers() throws DataException, IOException, ParseException, SQLException, ClassNotFoundException
	{
		if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
		else
		{
			//Generate an API URL
			String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_GUILD_PROFILE, 
																			URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
																			URLEncoder.encode(GUILD_NAME, "UTF-8").replace("+", "%20")));
			//Call Blizzard API
			JSONObject respond = curl(urlString, 
								"GET",
								"Bearer "+ this.accesToken,
								new String[] {"fields=members"});
			
			JSONArray members = (JSONArray) respond.get("members");
			
			for(int i = 0; i < members.size(); i++)
			{
				JSONObject info = (JSONObject) ((JSONObject) members.get(i)).get("character");
				JSONArray playerDB = dbConnect.select("gMembers_id_name",
													new String[] {"internal_id","member_name"},
													"member_name=\""+info.get("name")+"\"");
				//If not exist in table, right a name 
				if(playerDB.size() == 0) //not exist
				{//save member
					System.out.println("Player "+ info.get("name") +" SAVE");
					dbConnect.insert("gMembers_id_name",
								new String[] {"member_name","rank"},
								new String[] {info.get("name").toString(),
											((JSONObject) members.get(i)).get("rank").toString()});
				}
			}
		}
	}
	
	/**
	 * get a player information
	 */
	public void getCharacterInfo() throws SQLException, DataException, IOException, ParseException
	{
		if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
		else
		{
			JSONArray members = dbConnect.select("gMembers_id_name", 
									new String[] {"internal_id", "member_name"});
									
			if(members.size() > 0) //we have a player in DB
			{
				for(int i = 0; i < members.size(); i++)
				{
					JSONObject member = (JSONObject) members.get(i); //internal DB Members					
					//Generate an API URL
					String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_CHARACTER_PROFILE, 
																					URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
																					URLEncoder.encode(member.get("member_name").toString(), "UTF-8").replace("+", "%20")));
					try 
					{
						//Call Blizzard API
						JSONObject blizzPlayerInfo = curl(urlString, //DataException posible trigger
											"GET",
											"Bearer "+ this.accesToken,
											new String[] {"fields=guild"});
						//is Outdated?... if is>
						Character blizzPlayer = new Character((int) member.get("internal_id"), blizzPlayerInfo);
						System.out.println(blizzPlayer.getName());
						//else NOTHIN!												
					} 
					catch (DataException e) //Error in blizz api, like player not found
					{
						System.out.println("Blizz lanzo error para el amigo "+ member.get("member_name") +"\n\t"+ e);
					}
				}
			}
		}
	}
	
	/**
	 * Genera la conexion URL a la appi
	 * @urlString : URl de la api completa
	 * @method : GET, POST, DELETE, etc
	 * @authorization : autorizacion de la API, Bearer, o basic, etc
	 * @parameters : parametros URL ("field=member","acctrion=move"....)
	 * @bodyData : en caso de tener datos en el body
	 */
	private JSONObject curl(String urlString, String method, String authorization) throws IOException, ParseException, DataException { return curl(urlString, method, authorization, null, null); }
	private JSONObject curl(String urlString, String method, String authorization, String[] parameters) throws IOException, ParseException, DataException { return curl(urlString, method, authorization, parameters, null); }
	private JSONObject curl(String urlString, String method, String authorization, String[] parameters, byte[] bodyData) throws IOException, ParseException, DataException
	{
		//Add paramethers
		if(parameters != null)
		{
			String url = urlString +"?";
			for(String param : parameters) { url += param +"&"; }
			urlString = url.substring(0,url.length()-1);
		}
		
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