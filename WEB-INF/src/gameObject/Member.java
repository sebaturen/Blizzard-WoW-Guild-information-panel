/**
 * File : Member.java
 * Desc : Character Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.gameObject;

import com.artOfWar.blizzardAPI.APIInfo;
import com.artOfWar.blizzardAPI.Update;
import com.artOfWar.DataException;
import java.io.IOException;

import org.json.simple.JSONObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

public class Member extends GameObject
{
    //Attribute
    private int internalID;
    private String name;
    private String realm;
    private String battleGroup;
    private PlayableClass memberClass;
    private Race race;
    private int gender;
    private int level;
    private long achievementPoints;
    private String thumbnail;
    private char calcClass;
    private int faction;
    private String guildName;
    private long lastModified;
    private long totalHonorableKills;
    private boolean isGuildMember;
    private List<Spec> specs;

    //Constant
    private static final String TABLE_NAME = "character_info";
    private static final String[] TABLE_STRUCTURE = {"internal_id", "lastModified", "battlegroup", "class", 
                                                    "race", "gender", "level", "achievementPoints", "thumbnail", "calcClass", 
                                                    "faction", "totalHonorableKills", "guild_name"};

    private static final String COMBIEN_TABLE_NAME = TABLE_NAME +" c, "+Update.GMEMBERS_ID_TABLE +" gm";
    private static final String[] COMBIEN_TABLE_STRUCTURE = {"c.internal_id", "gm.realm", "c.lastModified", "c.battlegroup", "c.class", 
                                                            "c.race", "c.gender", "c.level", "c.achievementPoints", "c.thumbnail", "c.calcClass", 
                                                            "c.faction", "c.totalHonorableKills", "c.guild_name", "gm.member_name", "gm.in_guild"};
	
    //Constructor load from DB if have a ID
    public Member(int internalID)
    {
        super(COMBIEN_TABLE_NAME,COMBIEN_TABLE_STRUCTURE);
        specs = new ArrayList<>();
        //Load Character from DB
        loadFromDB(internalID+"", "gm.internal_id = c.internal_id", true);
    }

    //Load to JSON
    public Member(JSONObject playerInfo)
    {
        super(TABLE_NAME,TABLE_STRUCTURE);
        specs = new ArrayList<>();
        saveInternalInfoObject(playerInfo);
    }
	
    @Override
    protected void saveInternalInfoObject(JSONObject playerInfo)
    {		
        this.internalID = (Integer) playerInfo.get("internal_id");
        this.realm = playerInfo.get("realm").toString();
        this.battleGroup = playerInfo.get("battlegroup").toString();
        this.achievementPoints = (long) playerInfo.get("achievementPoints");
        this.thumbnail = playerInfo.get("thumbnail").toString();
        this.calcClass = (playerInfo.get("calcClass").toString()).charAt(0);
        this.lastModified = (long) playerInfo.get("lastModified");
        this.totalHonorableKills = (long) playerInfo.get("totalHonorableKills");

        int classID, raceID;
        if(playerInfo.get("gender").getClass() == java.lang.Long.class)
        {//if info come to blizzAPI
            this.name = playerInfo.get("name").toString();
            this.gender = ((Long) playerInfo.get("gender")).intValue();
            this.level = ((Long) playerInfo.get("level")).intValue();
            this.faction = ((Long) playerInfo.get("faction")).intValue();
            classID = ((Long) playerInfo.get("class")).intValue();
            raceID = ((Long) playerInfo.get("race")).intValue();
            //If have a guild...
            this.guildName = "";
            this.isGuildMember = false;
            if(playerInfo.containsKey("guild"))	this.guildName = ((JSONObject) playerInfo.get("guild")).get("name").toString();
            if( this.guildName.length() > 0 && this.guildName.equals(APIInfo.GUILD_NAME)) this.isGuildMember = true;
            //Spec
            loadSpecFromBlizz((JSONArray) playerInfo.get("talents"));
        }
        else
        {//if come to DB
            this.name = playerInfo.get("member_name").toString();
            this.gender = (Integer) playerInfo.get("gender");
            this.level = (Integer) playerInfo.get("level");
            this.faction = (Integer) playerInfo.get("faction");
            this.guildName = playerInfo.get("guild_name").toString();
            classID = (Integer) playerInfo.get("class");
            raceID = (Integer) playerInfo.get("race");
            this.isGuildMember = (Boolean) playerInfo.get("in_guild");
            loadSpecFromDB();
        }

        this.memberClass = new PlayableClass(classID);
        this.race = new Race(raceID);

        this.isData = true;	
    }
    
    private void loadSpecFromBlizz(JSONArray allTalents)
    {
        for(int i = 0; i < allTalents.size(); i++)
        {   
            JSONObject specsAvailable = (JSONObject) allTalents.get(i);
            JSONArray spellTalents = (JSONArray) specsAvailable.get("talents");  
            /**
             * Todos los miembros tienen talentos dependiendo de su especialidad
             * Blizzard nos ofrece los talentos por especialidad, por lo que debemos
             * recorrer la lista de "talentos" (specs) y dentro de cada spec, encontraremos
             * los talentos que el jugador escogio
             */
            if(spellTalents.size() > 0) //Blizzard codio la API para retornar muchas posibles especializaciones
            {                           //Aunque el usuario solo tenga a escojer 3, por lo que si tiene datos, trabajaremos
                JSONObject specInfoBlizz = (JSONObject) specsAvailable.get("spec");
                Spec spec = new Spec(this.internalID, specInfoBlizz, spellTalents);
                if(specsAvailable.containsKey("selected")) spec.setEnable(true);
                this.specs.add(spec);
            }
        }
    }

    private void loadSpecFromDB()
    {
        try {
            JSONArray memberSpec = dbConnect.select(Spec.TABLE_NAME,
                    new String[] { "id" },
                    "member_id=?",
                    new String[] { this.internalID +""});
            
            if(memberSpec.size() > 0)
            {
                for(int i = 0; i < memberSpec.size(); i++)
                {
                    Spec sp = new Spec( (Integer) ((JSONObject) memberSpec.get(i)).get("id") );
                    this.specs.add(sp);
                }
            }
            else //No have a specs in DB!!!!
            {
                loadSpecFromUpdate();
            }
        } catch (SQLException | DataException ex) {
            System.out.println("Fail to get a 'Specs' from Member "+ this.name +" e: "+ ex);
        }        
    }
    
    private void loadSpecFromUpdate()
    {
        try
        {
            Update up = new Update();
            specs = up.getMemberFromBlizz(this.internalID, this.name, this.realm).getSpecs();
        }
        catch (IOException|ParseException|DataException ex)
        {
            System.out.println("Fail to get a spec info in member "+ this.name);
        }
    }
    
    @Override
    public boolean saveInDB()
    {
        String[] val = new String[] {this.internalID +"", this.lastModified +"", this.battleGroup, this.memberClass.getId() +"",
                                    this.race.getId() +"", this.gender +"", this.level +"", this.achievementPoints +"", this.thumbnail, this.calcClass +"", 
                                    this.faction +"", this.totalHonorableKills +"", this.guildName };
        //Valid if have a data this object, and guild is null (if we try update, and put null in query, the DB not update this column, for this use this IF)
        if(this.isData)
        {
            if (!this.guildName.equals(APIInfo.GUILD_NAME)) deleteFromDB(); //prevent save in guild/internalID members table if not is a guild member
            int vSave = saveInDBObj(val);
            if ((vSave == SAVE_MSG_INSERT_OK) || (vSave == SAVE_MSG_UPDATE_OK))
            {
                //Save specs...
                for(int i = 0; i < specs.size(); i++)
                {
                    specs.get(i).saveInDB();
                }
                return true;
            }
        }
        return false;
    }

    /**
    * Delete from DB
    */
    private boolean deleteFromDB()
    {
        try
        {//change player in character_info in_guild because is change
            System.out.println("Character "+ this.name +" change guild");
            dbConnect.update(com.artOfWar.blizzardAPI.Update.GMEMBERS_ID_TABLE,
                            new String[] {"in_guild", "rank"},
                            new String[] {"0", "0"},
                            "internal_id=?",
                            new String[] { this.internalID +"" });
            return true;
        }
        catch (SQLException|DataException|ClassNotFoundException ex)
        {
            System.out.println("Error when try remove a user not in guild: "+ ex);
            return false;
        }				
    }
	
    //GETTERS	
    public int getInternalID() { return this.internalID; }
    public String getName() { return this.name; }
    public String getRealm() { return this.realm; }
    public String getBattleGroup() { return this.battleGroup; }
    public PlayableClass getmemberClass() { return this.memberClass; }
    public Race getRace() { return this.race; }
    public int getGender() { return this.gender; }
    public int getLevel() { return this.level; }
    public long getAchievementPoints() { return this.achievementPoints; }
    public String getThumbnail() { return this.thumbnail; }
    public char getCalcClass() { return this.calcClass; }
    public int getFaction() { return this.faction; }
    public String getGuildName() { return this.guildName; }
    public long getLastModified() { return this.lastModified; }
    public Date getLastModifiedDate() {
        //All lastModified in blizzard API is added 3 cero more...
        String val = this.lastModified+"";
        val = val.substring(0, val.length()-3);
        return new Date((Long.parseLong(val))*1000); 
    }
    public long getTotalHonorableKills() { return this.totalHonorableKills; }
    public List<Spec> getSpecs() { return this.specs; }
    public Spec getActiveSpec() 
    {
        for(Spec sp: this.specs) if(sp.isEnable()) return sp;
        //if is null!! we have a problem! the spec we need setters, mybe the data
        //no is real correct save, try again... only 1 time
        loadSpecFromUpdate();
        for(Spec sp: this.specs) if(sp.isEnable()) return sp;
        return null;
    }

    //Setters
    public void setSpec(int id) { setSpec(id, null, null); }
    public void setSpec(String sName, String sRole) { setSpec(-1, sName, sRole); }
    public void setSpec(int id, String sName, String sRole)
    {
        for(int i = 0; i < specs.size(); i++)
        {
            boolean isEnable = false; 
            if(id != -1 && id == specs.get(i).getId()) isEnable = true;
            if(sName != null && specs.get(i).isThisSpec(sName, sRole)) isEnable = true;
            specs.get(i).setEnable(isEnable);
        }
    }
    
    @Override
    public void setId(String id) { this.internalID = Integer.parseInt(id); }

    //two members equals method
    @Override
    public boolean equals(Object o) 
    {
        if(o == this) return true;
        if(o == null || (this.getClass() != o.getClass())) return false;

        int oId = ((Member) o).getInternalID();
        long oLastModified = ((Member) o).getLastModified();
        return (  
                oId == this.internalID
                &&
                (Long.compare(oLastModified, this.lastModified) == 0)
                );
    }
}