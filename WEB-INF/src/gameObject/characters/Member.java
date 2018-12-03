/**
 * File : Member.java
 * Desc : Character Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.blizzardAPI.APIInfo;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
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

public class Member extends GameObject
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
    private int userID;
    private Rank gRank;
    private List<Spec> specs = new ArrayList<>();
    private List<ItemMember> items = new ArrayList<>();
    private CharacterStats stats;
    private boolean isMain = false;
    
    //Constant
    private static final String COMBIEN_TABLE_NAME = CHARACTER_INFO_TABLE_NAME +" c, "+ GMEMBER_ID_NAME_TABLE_NAME +" gm";
    private static final String COMBIEN_TABLE_KEY = "c.internal_id";
    private static final String[] COMBIEN_TABLE_STRUCTURE = {"c.internal_id", "gm.realm", "c.lastModified", "c.battlegroup", "c.class", 
                                                            "c.race", "c.gender", "c.level", "c.achievementPoints", "c.thumbnail", "c.calcClass", 
                                                            "c.faction", "c.totalHonorableKills", "c.guild_name", "gm.member_name", "gm.in_guild",
                                                            "gm.user_id", "gm.rank"};
    //Constructor load from DB if have a ID
    public Member(int internalID)
    {
        super(COMBIEN_TABLE_NAME, COMBIEN_TABLE_KEY, COMBIEN_TABLE_STRUCTURE);
        //Load Character from DB
        loadFromDB(internalID+"", "gm.internal_id = c.internal_id", true);
    }
    
    public Member(String name, String realm)
    {
        super(COMBIEN_TABLE_NAME, COMBIEN_TABLE_KEY, COMBIEN_TABLE_STRUCTURE);
        loadFromDBUniqued(new String[] {"gm.member_name", "gm.realm"}, new String[] {name, realm}, "gm.internal_id = c.internal_id", true);
    }

    //Load to JSON
    public Member(JSONObject playerInfo)
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
            if( this.guildName.length() > 0 && this.guildName.equals(GeneralConfig.GUILD_NAME)) this.isGuildMember = true;
            //Spec
            loadSpecFromBlizz((JSONArray) playerInfo.get("talents"));
            loadItemsFromBlizz((JSONObject) playerInfo.get("items"));
            this.stats = new CharacterStats((JSONObject) playerInfo.get("stats"));
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
            classID = (Integer) playerInfo.get("class");
            raceID = (Integer) playerInfo.get("race");
            this.isGuildMember = (Boolean) playerInfo.get("in_guild");
            loadSpecFromDB();
            loadItemsFromDB();
            this.stats = new CharacterStats(this.internalID);
            this.gRank = new Rank((Integer) playerInfo.get("rank"));
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
                Spec spec = new Spec(this.internalID, specInfoBlizz, spellTalents);
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
                this.items.add(new ItemMember(item));
            }
        }
    }
    
    public void checkSpeckIDInDb()
    {
        for(int i = 0; i < this.specs.size(); i++)
        {
           try 
            {
                //UNIQUE (member_id,name,role)   
                JSONArray dbSpec = dbConnect.select(Spec.SPECS_TABLE_NAME,
                                                    new String[] {"id"},
                                                    "member_id=? AND name=? AND role=?",
                                                    new String[] { this.internalID +"", this.specs.get(i).getName(), this.specs.get(i).getRole() });
                if(dbSpec.size() > 0)
                {
                    this.specs.get(i).setId( (Integer) ((JSONObject) dbSpec.get(0)).get("id"));
                    this.specs.get(i).setIsInternalData(true);
                }
            } catch (SQLException | DataException ex) {
                Logs.saveLogln("Fail to get old spec info in DB "+ ex);
            } 
        }
    }

    private void loadSpecFromDB()
    {
        try {
            JSONArray memberSpec = dbConnect.select(Spec.SPECS_TABLE_NAME,
                                                    new String[] { "id" },
                                                    "member_id=?",
                                                    new String[] { this.internalID +""});
            if(memberSpec.size() > 0)
            {
                for(int i = 0; i < memberSpec.size(); i++)
                {
                    Spec sp = new Spec( (Integer) ((JSONObject) memberSpec.get(i)).get("id") ); 
                    this.specs.add(i, sp);   
                }
            }
            else //No have a specs in DB!!!!
            {
                Logs.saveLogln("Fallo al cargar las spec porque el size era menor o igual a cero "+ this.name + " - "+ this.internalID);
                loadSpecFromUpdate();
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to get a 'Specs' from Member "+ this.name +" e: "+ ex);
        }        
    }
    
    private void loadItemsFromDB()
    {
        try {
            JSONArray itemDB = dbConnect.select(ItemMember.ITEMS_MEMBER_TABLE_NAME,
                                                    new String[] { "id" },
                                                    "member_id=? AND item_id != 0",
                                                    new String[] { this.internalID +""});            
            for(int i = 0; i < itemDB.size(); i++)
            {
                ItemMember sp = new ItemMember( (Integer) ((JSONObject) itemDB.get(i)).get("id") ); 
                this.items.add(sp);
            }
        } catch (SQLException | DataException ex) {
            Logs.saveLogln("Fail to get a 'Specs' from Member "+ this.name +" e: "+ ex);
        } 
    }
    
    private void loadSpecFromUpdate()
    {
        try
        {
            Update up = new Update();
            specs = up.getMemberFromBlizz(this.name, this.realm).getSpecs();
        }
        catch (IOException|ParseException|DataException ex)
        {
            Logs.saveLogln("Fail to get a spec info in member "+ this.name);
        }
    }
    
    public static Member loadMember(String name, String realm)
    {
        Member mb = new Member(name, realm);
        if(!mb.isInternalData())
        {
            try {
                Update up = new Update();
                mb = up.getMemberFromBlizz(name, realm);
                if(mb != null && mb.isData)
                {
                    mb.generateMemberID();
                    mb.saveInDB();   
                    Logs.saveLogln("New Member in DB "+ mb.getId() +" - "+ mb.getName());                 
                }
            } catch (IOException | ParseException | DataException ex) {
                Logs.saveLogln("Fail to get member info from blizzard. - "+ ex);
            }
        }
        return mb;
    }
    
    private void generateMemberID()
    {
        //check if have and id
        if(this.isData && this.internalID == 0)
        {
            try {
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
        String[] val = new String[] {this.internalID +"", this.battleGroup, this.memberClass.getId() +"",
                                    this.race.getId() +"", this.gender +"", this.level +"", this.achievementPoints +"", 
                                    this.thumbnail, this.calcClass +"", this.faction +"", this.totalHonorableKills +"",
                                    this.guildName, this.lastModified +""};
        //Valid if have a data this object, and guild is null (if we try update, and put null in query, the DB not update this column, for this use this IF)
        if(this.isData)
        {
            if (this.isGuildMember && !this.guildName.equals(GeneralConfig.GUILD_NAME)) deleteFromDB(); //prevent save in guild/internalID members table if not is a guild member
            int vSave = saveInDBObj(val);
            if ((vSave == SAVE_MSG_INSERT_OK) || (vSave == SAVE_MSG_UPDATE_OK))
            {
                //Save specs...
                this.specs.forEach((spc) -> {
                    spc.setMemberId(this.internalID);
                    //valide if this member have a this spec in DB (set Update or Insert)
                    try
                    {
                        JSONArray specMember = dbConnect.select(Spec.SPECS_TABLE_NAME, 
                                                                new String[] { Spec.SPECS_TABLE_KEY},
                                                                "member_id=? AND name=? AND role=?",
                                                                new String[] { spc.getMemberId() +"", spc.getName(), spc.getRole()});
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
                try {
                    dbConnect.update(ItemMember.ITEMS_MEMBER_TABLE_NAME,
                                    ItemMember.ITEMS_MEMBER_TABLE_CLEAR_STRUCTURE,
                                    ItemMember.ITEMS_MEMBER_TABLE_CLEAR_STRUCTURE_VALUES,
                                    "member_id=?",
                                    new String[] {this.internalID +""});                    
                } catch (DataException | ClassNotFoundException | SQLException ex) {
                    Logs.saveLogln("Fail to update remove old items "+ this.internalID +" - "+ ex);
                }
                //Update or insert a new items
                this.items.forEach((itm) -> {
                    itm.setMemberId(this.internalID);
                    ItemMember iMemberDB = new ItemMember(itm.getPosition(), this.internalID);
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
	
    //GETTERS
    public boolean isGuildMember() { return this.isGuildMember; }
    public String getName() { return this.name; }
    public String getRealm() { return this.realm; }
    public String getBattleGroup() { return this.battleGroup; }
    public PlayableClass getMemberClass() { return this.memberClass; }
    public Race getRace() { return this.race; }
    public int getGender() { return this.gender; }
    public int getLevel() { return this.level; }
    public long getAchievementPoints() { return this.achievementPoints; }
    public String getThumbnail() { return this.thumbnail; }
    public CharacterStats getStats() { return this.stats; }
    public boolean isMain() { return this.isMain; }
    public Rank getRank() { return this.gRank; }
    public String getThumbnailURL() 
    {
        return String.format(APIInfo.API_CHARACTER_RENDER_URL, GeneralConfig.SERVER_LOCATION, getThumbnail());
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
    public List<Spec> getSpecs() { return this.specs; }
    public Spec getActiveSpec() 
    {
        for(Spec sp: this.specs) {            
            if(sp.isEnable()) {
            
            return sp;
        }}
        //if is null!! we have a problem! the spec we need setters, mybe the data
        //no is real correct save, try again... only 1 time
        Logs.saveLogln("No se encontre la spec activa de "+ this.name +" cargnadola de blizz...");
        loadSpecFromUpdate();
        for(Spec sp: this.specs) if(sp.isEnable()) {
            return sp;
        }
        return null;
    }
    public double getItemLevel()
    {
        int sumItemLevl = 0;
        int count = 0;
        for(ItemMember item : this.items)
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
    public ItemMember getItemByPost(String post) 
    {
        for(ItemMember im : this.items) 
        {
            if(im.getPosition().equals(post))
                return im;
        }
        return null;
    }

    //Setters
    public void setIsMain(boolean v) { this.isMain = v; }
    public void setSpec(int id) { setSpec(id, null, null); }
    public void setSpec(String sName, String sRole) { setSpec(-1, sName, sRole); }
    public void setSpec(int id, String sName, String sRole)
    {
        for(int i = 0; i < specs.size(); i++)
        {
            Spec sp = specs.get(i);
            boolean isEnable = false; 
            if(id != -1 && id == sp.getId()) isEnable = true;
            if(sName != null && sp.isThisSpec(sName, sRole)) isEnable = true;
            specs.get(i).setEnable(isEnable);
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

        int oId = ((Member) o).getId();
        long oLastModified = ((Member) o).getLastModified();
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
        for(Spec sp : specs)
        {
            out += "\nSpec> "+ sp.getName() +"-"+ sp.getRole() +" (Enable "+ sp.isEnable() +" "+ sp.getId() +")";
        }
        return out;
    }
}