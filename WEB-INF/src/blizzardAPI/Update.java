/**
 * File : Update.java
 * Desc : Update guild and character in guild information
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.blizzardAPI;

import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.DataException;
import com.artOfWar.gameObject.Guild;
import com.artOfWar.gameObject.Member;
import com.artOfWar.gameObject.PlayableClass;
import com.artOfWar.gameObject.Race;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Update implements APIInfo
{

	//Constant	
	public static final int DYNAMIC_UPDATE = 0;
	public static final int STATIC_UPDATE = 1;
	
	//Attribute
	private String accesToken = "";
	private static DBConnect dbConnect;
	
	/**
	 * Constructor. Ejecuta el metodo para obtener el token de acceso
	 */
	public Update() throws IOException, ParseException, DataException
	{
		dbConnect = new DBConnect();
		generateAccesToken();
	}
	
	/**
	 * Run Dynamic element update method and save the register the last update.
	 */
	public void updateDynamicAll()
	{
		System.out.println("-------Update proces is START! (Dynamic)------");
		//Guild information update!
		System.out.println("Guild Information update!");
		try { getGuildProfile(); } 
		catch (IOException|ParseException|SQLException|ClassNotFoundException|DataException ex) { System.out.println("Fail update Guild Info: "+ ex); }
		//Guild members information update!
		System.out.println("Guild members information update!");
		try { getGuildMembers(); } 
		catch (IOException|ParseException|SQLException|ClassNotFoundException|DataException ex) { System.out.println("Fail update Guild Members Info: "+ ex); }
		//Character inforotmation update!						
		System.out.println("Character information update!");
		try { getCharacterInfo(); } 
		catch (IOException|ParseException|SQLException|DataException ex) { System.out.println("Fail get a CharacterS Info: "+ ex); }
		System.out.println("-------Update proces is COMPLATE! (Dynamic)------");
		
		//Save log update in DB
		try 
		{			
			Calendar cal = Calendar.getInstance();  
			Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
			
			dbConnect.insert("update_timeline",
							new String[] {"type", "update_time"},
							new String[] {DYNAMIC_UPDATE +"", timestamp.toString()});
		} 
		catch(DataException|SQLException|ClassNotFoundException e)
		{
			System.out.println("Fail to save update time: "+ e);
		}
	}
	
	/**
	 * Run Static element update
	 */
	public void updateStaticAll()
	{
		System.out.println("-------Update proces is START! (Static)------");
		//Playeble Class
		System.out.println("Playable class Information update!");
		try { getPlayableClass(); } 
		catch (IOException|ParseException|SQLException|DataException ex) { System.out.println("Fail update Playable class Info: "+ ex); }
		System.out.println("Races Information update!");
		try { getRaces(); } 
		catch (IOException|ParseException|SQLException|DataException ex) { System.out.println("Fail update Races Info: "+ ex); }		
		System.out.println("-------Update proces is COMPLATE! (Static)------");
		
		//Save log update in DB
		try 
		{
			Calendar cal = Calendar.getInstance();  
			Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
			
			dbConnect.insert("update_timeline",
							new String[] {"type", "update_time"},
							new String[] {STATIC_UPDATE +"", timestamp.toString()});
		} 
		catch(DataException|SQLException|ClassNotFoundException e)
		{
			System.out.println("Fail to save update time: "+ e);
		}		
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
		
		//Get an Access Token
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
			Guild apiGuild = new Guild(respond);
			apiGuild.saveInDB();
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
				
				//Check if have a guild and if set guild, (Blizzard not update a guilds members list) 
				if(info.containsKey("guild") && (info.get("guild").toString()).equals(GUILD_NAME))
				{
					dbConnect.insert(	"gMembers_id_name",
										new String[] {"member_name","rank"},
										new String[] {info.get("name").toString(), ((JSONObject) members.get(i)).get("rank").toString()},
										"ON DUPLICATE KEY UPDATE member_name='"+  info.get("name").toString() +"'");
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
						
			int iProgres = 1;
			System.out.print("0%");
			for(int i = 0; i < members.size(); i++)
			{
				JSONObject member = (JSONObject) members.get(i); //internal DB Members [internal_id, name, rank]				
				//Generate an API URL
				String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, String.format(API_CHARACTER_PROFILE, 
																				URLEncoder.encode(GUILD_REALM, "UTF-8").replace("+", "%20"), 
																				URLEncoder.encode(member.get("member_name").toString(), "UTF-8").replace("+", "%20")));
				try 
				{
					//Call Blizzard API
					JSONObject blizzPlayerInfo = curl(urlString, //DataException possible trigger
										"GET",
										"Bearer "+ this.accesToken,
										new String[] {"fields=guild"});
					blizzPlayerInfo.put("internal_id", (int) member.get("internal_id"));
					Member blizzPlayer = new Member(blizzPlayerInfo);
					blizzPlayer.saveInDB();
				} 
				catch (DataException e) //Error in blizzard API, like player not found
				{
					System.out.println("BlizzAPI haven a error to "+ member.get("member_name") +"\n\t"+ e);
				}
				
				//Show update logress...
				if ( (((iProgres*2)*10)*members.size())/100 < i )
				{
					System.out.print("..."+ ((iProgres*2)*10) +"%");
					iProgres++;
				}
			}
			System.out.println("...100%");
		}
	}
	
	/**
	 * get a playable class information 
	 */
	public void getPlayableClass() throws SQLException, DataException, IOException, ParseException
	{
		if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
		else
		{
			//Generate an API URL
			String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, API_PLAYABLE_CLASS);
			//Call Blizzard API
			JSONObject blizzPlayableClass = curl(urlString, //DataException posible trigger
								"GET",
								"Bearer "+ this.accesToken,
								new String[] {"namespace=static-us"});
			
			JSONArray playClass = (JSONArray) blizzPlayableClass.get("classes");
			for(int i = 0; i < playClass.size(); i++)
			{
				JSONObject info = (JSONObject) playClass.get(i);
				
				PlayableClass pClass = new PlayableClass(info);
				pClass.saveInDB();
			}
		}
	}
	
	/**
	 * get a Characters races information 
	 */
	public void getRaces() throws SQLException, DataException, IOException, ParseException
	{
		if(this.accesToken.length() == 0) throw new DataException("Acces Token Not Found");
		else
		{
			//Generate an API URL
			String urlString = String.format(API_ROOT_URL, SERVER_LOCATION, API_CHARACTER_RACES);
			//Call Blizzard API
			JSONObject blizzRaces = curl(urlString, //DataException posible trigger
								"GET",
								"Bearer "+ this.accesToken);

			JSONArray races = (JSONArray) blizzRaces.get("races");
			for(int i = 0; i < races.size(); i++)
			{
				JSONObject info = (JSONObject) races.get(i);
				Race race = new Race(info);
				race.saveInDB();		
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