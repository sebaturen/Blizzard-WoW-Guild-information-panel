/**
 * File : Member.java
 * Desc : Character Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.blizzardAPI.APIInfo;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.exceptions.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.exceptions.ConfigurationException;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.guild.Rank;
import java.io.IOException;

import org.json.simple.JSONObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

public class Character extends GameObject
{
    //Members - id - name DB
    public static final String GMEMBER_ID_NAME_TABLE_NAME = "gMembers_id_name";
    public static final String GMEMBER_ID_NAME_TABLE_KEY = "internal_id";
    public static final String[] GMEMBER_ID_NAME_TABLE_STRUCTURE = {"internal_id", "member_name", "realm", 
                                                                    "rank", "in_guild", "user_id"};
    
    //Character information DB
    public static final String CHARACTER_INFO_TABLE_NAME = "character_info";
    public static final String CHARACTER_INFO_TABLE_KEY = "internal_id";
    public static final String[] CHARACTER_INFO_TABLE_STRUCTURE = {"internal_id", "battlegroup", "class",
                                                                    "race", "gender", "level", "achievementPoints",
                                                                    "thumbnail", "calcClass", "faction", "totalHonorableKills",
                                                                    "guild_name", "lastModified"};
    //Constant
    private static final String COMBIEN_TABLE_NAME = CHARACTER_INFO_TABLE_NAME +" c, "+ GMEMBER_ID_NAME_TABLE_NAME +" gm";
    private static final String COMBIEN_TABLE_KEY = "c.internal_id";
    private static final String[] COMBIEN_TABLE_STRUCTURE = {"c.internal_id", "gm.realm", "c.lastModified", "c.battlegroup", "c.class", 
                                                            "c.race", "c.gender", "c.level", "c.achievementPoints", "c.thumbnail", "c.calcClass", 
                                                            "c.faction", "c.totalHonorableKills", "c.guild_name", "gm.member_name", "gm.in_guild",
                                                            "gm.user_id", "gm.rank"};
    //Attribute
    private int internalID;
    private String name;
    private String realm;
    private String battleGroup;
    private PlayableClass memberClass;
    private PlayableRace race;
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
    private int userID;
    private Rank gRank;
    private List<CharacterSpec> specs = new ArrayList<>();
    private List<CharacterItems> items = new ArrayList<>();
    private CharacterStats stats;
    private boolean isMain = false;
    
    //Constructor load from DB if have a ID
    public Character(int internalID)
    {
        super(COMBIEN_TABLE_NAME, COMBIEN_TABLE_KEY, COMBIEN_TABLE_STRUCTURE);
        //Load Character from DB
        loadFromDB(internalID, "gm.internal_id = c.internal_id", true);
    }
    
    public Character(String name, String realm)
    {
        super(COMBIEN_TABLE_NAME, COMBIEN_TABLE_KEY, COMBIEN_TABLE_STRUCTURE);
        loadFromDBUniqued(new String[] {"gm.member_name", "gm.realm"}, new String[] {name, realm}, "gm.internal_id = c.internal_id", true);
        if(!this.isInternalData)
        {
            try {
                Update up = new Update();
                cloneMember(up.getMemberFromBlizz(name, realm));
            } catch (IOException | ParseException | DataException ex) {
                Logs.saveLogln("Fail to get member info from blizzard. - "+ ex);
            } catch (ConfigurationException e) {
                Logs.saveLogln("FAIL IN CONFIGURATION! "+ e);
                System.exit(-1);
            }
        }
    }

    //Load to JSON
    public Character(JSONObject playerInfo)
    {
        super(CHARACTER_INFO_TABLE_NAME, CHARACTER_INFO_TABLE_KEY, CHARACTER_INFO_TABLE_STRUCTURE);
        saveInternalInfoObject(playerInfo);
    }
	
    @Override
    protected void saveInternalInfoObject(JSONObject playerInfo)
    {		
        this.realm = playerInfo.get("realm").toString();
        this.battleGroup = playerInfo.get("battlegroup").toString();
        this.achievementPoints = (long) playerInfo.get("achievementPoints");
        this.thumbnail = playerInfo.get("thumbnail").toString();
        this.calcClass = (playerInfo.get("calcClass").toString()).charAt(0);
        this.lastModified = (long) playerInfo.get("lastModified");
        this.totalHonorableKills = (long) playerInfo.get("totalHonorableKills");

        int raceID;
        if(playerInfo.get("gender").getClass() == java.lang.Long.class)
        {   
            try {
                //if info come to blizzAPI
                this.name = playerInfo.get("name").toString();
                this.gender = ((Long) playerInfo.get("gender")).intValue();
                this.level = ((Long) playerInfo.get("level")).intValue();
                this.faction = ((Long) playerInfo.get("faction")).intValue();
                this.memberClass = new PlayableClass(((Long) playerInfo.get("class")).intValue());
                this.race = new PlayableRace(((Long) playerInfo.get("race")).intValue());
                //If have a guild...
                this.guildName = "";
                this.isGuildMember = false;
                if(playerInfo.containsKey("guild"))	this.guildName = ((JSONObject) playerInfo.get("guild")).get("name").toString();
                if( this.guildName.length() > 0 && this.guildName.equals(GeneralConfig.getStringConfig("GUILD_NAME"))) this.isGuildMember = true;
                //Generate member id:            
                generateMemberID();
                //Spec
                loadSpecFromBlizz((JSONArray) playerInfo.get("talents"));
                loadItemsFromBlizz((JSONObject) playerInfo.get("items"));
                //Status
                this.stats = new CharacterStats((JSONObject) playerInfo.get("stats"));
            } catch (ConfigurationException ex) {
                Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
                System.exit(-1);
            }
        }
        else
        {//if come to DB
            this.internalID = (Integer) playerInfo.get("internal_id");
            this.userID = (Integer) playerInfo.get("user_id");
            this.name = playerInfo.get("member_name").toString();
            this.gender = (Integer) playerInfo.get("gender");
            this.level = (Integer) playerInfo.get("level");
            this.faction = (Integer) playerInfo.get("faction");
            this.guildName = playerInfo.get("guild_name").toString();
            this.memberClass = new PlayableClass((Integer) playerInfo.get("class"));
            this.race = new PlayableRace((Integer) playerInfo.get("race"));
            this.isGuildMember = (Boolean) playerInfo.get("in_guild");
            this.stats = new CharacterStats(this.internalID);
            this.gRank = new Rank((Integer) playerInfo.get("rank"));
        }


        this.isData = true;	
    }
    
    private void loadSpecFromBlizz(JSONArray allTalents)
    {
        for(int i = 0; i < allTalents.size(); i++)
        {   
            JSONObject specsAvailable = (JSONObject) allTalents.get(i);
            JSONObject specInfoBlizz = (JSONObject) specsAvailable.get("spec");
            JSONArray spellTalents = (JSONArray) specsAvailable.get("talents"); 
            /**
             * Todos los miembros tienen talentos dependiendo de su especialidad
             * Blizzard nos ofrece los talentos por especialidad, por lo que debemos
             * recorrer la lista de "talentos" (specs) y dentro de cada spec, encontraremos
             * los talentos que el jugador escogio
             */
            if(specInfoBlizz != null) //Blizzard codio la API para retornar muchas posibles especializaciones
            {
                //Aunque el usuario solo tenga a escojer 3, por lo que si tiene datos, trabajaremos
                CharacterSpec spec = new CharacterSpec(this, specInfoBlizz, spellTalents);
                if(specsAvailable.containsKey("selected")) spec.setEnable(true);
                this.specs.add(spec);
            }
        }
    }
    
    private void loadItemsFromBlizz(JSONObject allItems)
    {        
        for(Object key : allItems.keySet()) 
        {
            String postItem = (String)key;
            //all element exept average item level information
            if(!postItem.equals("averageItemLevel") && !postItem.equals("averageItemLevelEquipped"))
            {
                JSONObject item = (JSONObject) allItems.get(postItem);
                item.put("post_item", postItem);
                this.items.add(new CharacterItems(item));
            }
        }
    }

    private void loadSpecFromDB() { loadSpecFromDB(null); }
    private void loadActiveSpecFromDB() {loadSpecFromDB("AND enable=1");}
    private void loadSpecFromDB(String extraWhere)
    {
        try 
        {
            JSONArray memberSpec = dbConnect.select(CharacterSpec.SPECS_TABLE_NAME,
                                                    new String[] { "id" },
                                                    "member_id=? "+ ((extraWhere != null)? extraWhere:""),
                                                    new String[] { this.internalID +""});
            if(memberSpec.size() > 0)
            {
                for(int i = 0; i < memberSpec.size(); i++)
                {
                    CharacterSpec sp = new CharacterSpec( (Integer) ((JSONObject) memberSpec.get(i)).get("id") ); 
                    this.specs.add(i, sp);   
                }
            }
            else //No have a specs in DB!!!!
            {
                Logs.saveLogln("Fail to load spec! (size <= 0)? "+ this.name + " - "+ this.internalID);
                Logs.saveLogln("\tTry get spec again from update...");
                loadSpecFromBlizz();
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to get a 'Specs' from DB Member "+ this.name +" e: "+ ex);
        }
    }
    
    private void loadStats()
    {
        try
        {
            Update up = new Update();
            Character tempMember = up.getMemberFromBlizz(this.name, this.realm);
            this.stats = tempMember.getStats();
            //Save new info in DB
            saveInDB();
        }
        catch (IOException|ParseException|DataException ex)
        {
            Logs.saveLogln("Fail to get a spec info in member "+ this.name);
        }
        catch (ConfigurationException e)
        {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ e);
            System.exit(-1);
        }
    }
    
    private void loadSpecFromBlizz()
    {
        try
        {
            Update up = new Update();
            Character tempMember = up.getMemberFromBlizz(this.name, this.realm);
            this.specs = tempMember.getSpecs();
            //Save new info in DB
            saveInDB();
        }
        catch (IOException|ParseException|DataException ex)
        {
            Logs.saveLogln("Fail to get a spec info in member "+ this.name);
        }
        catch (ConfigurationException e)
        {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ e);
            System.exit(-1);
        }
    }
    
    private void loadItemsFromDB()
    {
        try {
            JSONArray itemDB = dbConnect.select(CharacterItems.ITEMS_MEMBER_TABLE_NAME,
                                                    new String[] { "id" },
                                                    "member_id=? AND item_id != 0",
                                                    new String[] { this.internalID +""});            
            for(int i = 0; i < itemDB.size(); i++)
            {
                CharacterItems sp = new CharacterItems( (Integer) ((JSONObject) itemDB.get(i)).get("id") ); 
                this.items.add(sp);
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to get a 'items' from DB Member "+ this.name +" e: "+ ex);
        } 
    }
    
    private void generateMemberID()
    {
        //check if have and id
        if(this.name != null && this.realm != null && this.internalID == 0)
        {
            try 
            {
                JSONArray oldId = dbConnect.select(GMEMBER_ID_NAME_TABLE_NAME, 
                                                new String[] { GMEMBER_ID_NAME_TABLE_KEY },
                                                "member_name=? AND realm=?",
                                                new String[] { this.name, this.realm });
                if(oldId.size() > 0)
                {
                    this.internalID = Integer.parseInt( ((JSONObject)oldId.get(0)).get(GMEMBER_ID_NAME_TABLE_KEY).toString());
                }
                else
                {
                    /* {"internal_id", "member_name", "realm", "rank", "in_guild", "user_id"}; */
                    String id = dbConnect.insert(GMEMBER_ID_NAME_TABLE_NAME,
                            GMEMBER_ID_NAME_TABLE_KEY,
                            new String[] { "member_name", "realm", "in_guild" },
                            new String[] { name, realm, "0" });//asumed is 0 becouse in frist moment, we get all guilds members.
                    this.internalID = Integer.parseInt(id);
                }
            } catch (DataException | ClassNotFoundException | SQLException ex) {
                Logs.saveLogln("Fail to generate a member ID "+ this.name +" - "+ ex);
            }
        } 
    }
    
    @Override
    public boolean saveInDB()
    {
        if(this.isData && this.internalID == 0)
        {
            generateMemberID();
        }
        /* {"internal_id", "battlegroup", "class",
         * "race", "gender", "level", "achievementPoints",
         * "thumbnail", "calcClass", "faction", "totalHonorableKills",
         * "guild_name", "lastModified"};
         */
        setTableName(CHARACTER_INFO_TABLE_NAME);
        setTableKey(CHARACTER_INFO_TABLE_KEY);
        setTableStructur(CHARACTER_INFO_TABLE_STRUCTURE);
        String[] val = new String[] {this.internalID +"", this.battleGroup, this.memberClass.getId() +"",
                                    this.race.getId() +"", this.gender +"", this.level +"", this.achievementPoints +"", 
                                    this.thumbnail, this.calcClass +"", this.faction +"", this.totalHonorableKills +"",
                                    this.guildName, this.lastModified +""};
        //Valid if have a data this object, and guild is null (if we try update, and put null in query, the DB not update this column, for this use this IF)
        if(this.isData)
        {
            try {
                String guildName = GeneralConfig.getStringConfig("GUILD_NAME");
            } catch (ConfigurationException ex) {
                Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
                System.exit(-1);
            }
            if (this.isGuildMember && !this.guildName.equals(guildName)) deleteFromDB(); //prevent save in guild/internalID members table if not is a guild member
            int vSave = saveInDBObj(val);
            if ((vSave == SAVE_MSG_INSERT_OK) || (vSave == SAVE_MSG_UPDATE_OK))
            {
                //Save specs...
                if(this.specs.isEmpty()) loadSpecFromDB();
                this.specs.forEach((spc) -> {
                    spc.setMemberId(this.internalID);
                    //valide if this member have a this spec in DB (set Update or Insert)
                    try
                    {
                        JSONArray specMember = dbConnect.select(CharacterSpec.SPECS_TABLE_NAME, 
                                                                new String[] { CharacterSpec.SPECS_TABLE_KEY},
                                                                "member_id=? AND spec_id=?",
                                                                new String[] { spc.getMemberId() +"", spc.getSpec().getId() +""});
                        if(specMember.size()>0)
                        {
                            spc.setIsInternalData(true);
                        }
                        spc.saveInDB();
                    } catch (DataException | SQLException ex) {
                        Logs.saveLogln("Fail to get specs info in DB from member "+ this.name +" - "+ ex);
                    }
                });
                //Save items...
                //Clear all old items:   
                if(this.items.isEmpty()) loadItemsFromDB();
                try {
                    dbConnect.update(CharacterItems.ITEMS_MEMBER_TABLE_NAME,
                                    CharacterItems.ITEMS_MEMBER_TABLE_CLEAR_STRUCTURE,
                                    CharacterItems.ITEMS_MEMBER_TABLE_CLEAR_STRUCTURE_VALUES,
                                    "member_id=?",
                                    new String[] {this.internalID +""});                    
                } catch (DataException | ClassNotFoundException | SQLException ex) {
                    Logs.saveLogln("Fail to update remove old items "+ this.internalID +" - "+ ex);
                }
                //Update or insert a new items
                this.items.forEach((itm) -> {
                    itm.setMemberId(this.internalID);
                    CharacterItems iMemberDB = new CharacterItems(itm.getPosition(), this.internalID);
                    if(iMemberDB.isInternalData())
                    {
                        itm.setId(iMemberDB.getId());
                        itm.setIsInternalData(true);
                    }
                    itm.saveInDB();
                });
                //Save stats
                this.stats.setId(this.internalID);
                CharacterStats sDB = new CharacterStats(this.internalID);
                if(sDB.isInternalData())
                {
                    this.stats.setIsInternalData(true);
                }
                if(!this.stats.isData())
                    loadStats();
                this.stats.saveInDB();
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
            Logs.saveLogln("Character "+ this.name +" change guild");
            dbConnect.update(GMEMBER_ID_NAME_TABLE_NAME,
                            new String[] {"in_guild", "rank"},
                            new String[] {"0", "0"},
                            "internal_id=?",
                            new String[] { this.internalID +"" });
            return true;
        }
        catch (DataException|ClassNotFoundException|SQLException ex)
        {
            Logs.saveLogln("Error when try remove a user not in guild: "+ ex);
            return false;
        }				
    }
    
    public void cloneMember(Character mb)
    {
        if(mb != null && mb.isData())
        {          
            //Attribute    
            this.name = mb.getName();
            this.realm = mb.getRealm();
            this.battleGroup = mb.getBattleGroup();
            this.memberClass = mb.getMemberClass();
            this.race = mb.getRace();
            this.gender = mb.getGender();
            this.level = mb.getLevel();
            this.achievementPoints = mb.getAchievementPoints();
            this.thumbnail = mb.getThumbnail();
            this.calcClass = mb.getCalcClass();
            this.faction = mb.getFaction();
            this.guildName = mb.getGuildName();
            this.lastModified = mb.getLastModified();
            this.totalHonorableKills = mb.getTotalHonorableKills();
            this.isGuildMember = mb.isGuildMember();
            this.userID = mb.getUserID();
            this.gRank = mb.getRank();
            this.specs = mb.getSpecs();
            this.items = mb.getItems();
            this.stats = mb.getStats();
            this.isMain = mb.isMain();
            this.isInternalData = mb.isInternalData();
            this.isData = mb.isData(); 
            //Try save if not exist in DB
            if(this.isInternalData)
                this.internalID = mb.getId();
            else
            {
                generateMemberID();
                saveInDB();
                Logs.saveLogln("New Member in DB "+ this.internalID +" - "+ this.name);                 
            }
        }
    }
	
    //GETTERS
    public boolean isGuildMember() { return this.isGuildMember; }
    public String getName() { return this.name; }
    public String getRealm() { return this.realm; }
    public String getBattleGroup() { return this.battleGroup; }
    public PlayableClass getMemberClass() { return this.memberClass; }
    public PlayableRace getRace() { return this.race; }
    public int getGender() { return this.gender; }
    public int getLevel() { return this.level; }
    public long getAchievementPoints() { return this.achievementPoints; }
    public String getThumbnail() { return this.thumbnail; }
    public CharacterStats getStats() { if(!this.stats.isData()) loadStats(); return this.stats; }
    public boolean isMain() { return this.isMain; }
    public Rank getRank() { return this.gRank; }
    public String getThumbnailURL() 
    {
        try {
            return String.format(APIInfo.API_CHARACTER_RENDER_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), getThumbnail());
        } catch (ConfigurationException ex) {
            Logs.saveLogln("FAIL IN CONFIGURATION! "+ ex);
            System.exit(-1);
            return null;
        }
    }
    public char getCalcClass() { return this.calcClass; }
    public int getFaction() { return this.faction; }
    public String getGuildName() { return this.guildName; }
    public long getLastModified() { return this.lastModified; }
    public int getUserID() { return this.userID; }
    public Date getLastModifiedDate() {
        //All lastModified in blizzard API is added 3 cero more...
        String val = this.lastModified+"";
        val = val.substring(0, val.length()-3);
        return new Date((Long.parseLong(val))*1000); 
    }
    public long getTotalHonorableKills() { return this.totalHonorableKills; }
    public List<CharacterSpec> getSpecs() { if(this.specs.isEmpty()) loadSpecFromDB(); return this.specs; }
    public CharacterSpec getActiveSpec() 
    {
        if(this.specs.isEmpty()) loadActiveSpecFromDB();
        for(CharacterSpec sp: this.specs) 
            if(sp.isEnable())            
                return sp;
        //if is null!! we have a problem! the spec we need setters, mybe the data
        //no is real correct save, try again... only 1 time
        Logs.saveLogln("Not active spec detected "+ this.name +" try load again from blizz...");
        loadSpecFromBlizz();
        for(CharacterSpec sp: this.specs) 
            if(sp.isEnable())
                return sp;
        return null;
    }
    public List<CharacterItems> getItems()
    {
        return this.items;
    }
    
    public double getItemLevel()
    {
        int sumItemLevl = 0;
        int count = 0;
        if(this.items.isEmpty()) loadItemsFromDB();
        for(CharacterItems item : this.items)
        {
            if(!item.getPosition().equals("tabard") && !item.getPosition().equals("shirt"))
            {
                sumItemLevl += item.getIlevel(); 
                count++;
            }
        }
        if(count == 0) return 0;
        double iLeve = (double)sumItemLevl/(double)count;
        return iLeve;
    }
    
    public CharacterItems getItemByPost(String post) 
    {
        if(this.items.isEmpty())
            loadItemsFromDB();
        for(CharacterItems im : this.items)
            if(im.getPosition().equals(post))
                return im;
        return null;
    }

    //Setters
    public void setIsMain(boolean v) { this.isMain = v; }    
    public void setActiveSpec(String sName, String sRole)
    {
        if(this.specs.isEmpty())
            loadSpecFromDB();
        for(int i = 0; i < this.specs.size(); i++)
        {
            PlayableSpec pSpec = new PlayableSpec(sName, sRole, getMemberClass().getId());
            boolean isEnableSpec = (this.specs.get(i).getSpec().getId() == pSpec.getId());
            this.specs.get(i).setEnable(isEnableSpec);
        }
    }
    public void setActiveSpec(int specId)
    {
        if(this.specs.isEmpty())
            loadSpecFromDB();
        for(int i = 0; i < this.specs.size(); i++)
        {            
            boolean isEnableSpec = (specId == this.specs.get(i).getId());
            this.specs.get(i).setEnable(isEnableSpec);
        }
    }
    
    @Override
    public void setId(int id) { this.internalID = id; }
    @Override
    public int getId() { return this.internalID; }

    //two members equals method
    @Override
    public boolean equals(Object o) 
    {
        if(o == this) return true;
        if(o == null || (this.getClass() != o.getClass())) return false;

        int oId = ((Character) o).getId();
        long oLastModified = ((Character) o).getLastModified();
        return (  
                oId == this.internalID
                &&
                (Long.compare(oLastModified, this.lastModified) == 0)
                );
    }
    
    @Override
    public String toString()
    {
        String out = "Member: "+ this.name +" ("+ this.internalID +" - "+ this.realm +")";
        for(CharacterSpec sp : specs)
        {
           // out += "\nSpec> "+ sp.getName() +"-"+ sp.getRole() +" (Enable "+ sp.isEnable() +" "+ sp.getId() +")";
        }
        return out;
    }
}