/**
 * File : Member.java
 * Desc : Character Object
 *
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.update.blizzard.Update;
import com.blizzardPanel.DataException;
import com.blizzardPanel.GeneralConfig;
import com.blizzardPanel.Logs;
import com.blizzardPanel.update.blizzard.WoWAPIService;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.ServerTime;
import com.blizzardPanel.gameObject.guild.Rank;
import com.blizzardPanel.gameObject.mythicKeystone.KeystoneDungeonRun;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CharacterMember extends GameObject {
    //Members - id - name DB
    public static final String GMEMBER_ID_NAME_TABLE_NAME = "gMembers_id_name";
    public static final String GMEMBER_ID_NAME_TABLE_KEY = "internal_id";
    public static final String[] GMEMBER_ID_NAME_TABLE_STRUCTURE = {"internal_id", "member_name", "realm",
            "rank", "in_guild", "user_id", "isDelete"};

    //Character information DB
    public static final String CHARACTER_INFO_TABLE_NAME = "character_info";
    public static final String CHARACTER_INFO_TABLE_KEY = "internal_id";
    public static final String[] CHARACTER_INFO_TABLE_STRUCTURE = {"internal_id", "battlegroup", "class",
            "race", "gender", "level", "achievementPoints",
            "thumbnail", "calcClass", "faction", "totalHonorableKills",
            "bestMythicPlusScore", "mythicPlusScores",
            "guild_name", "lastModified"};
    //Constant
    private static final String COMBIEN_TABLE_NAME = CHARACTER_INFO_TABLE_NAME + " c, " + GMEMBER_ID_NAME_TABLE_NAME + " gm";
    private static final String COMBIEN_TABLE_KEY = "c.internal_id";
    private static final String[] COMBIEN_TABLE_STRUCTURE = {"c.internal_id", "gm.realm", "c.lastModified", "c.battlegroup", "c.class",
            "c.race", "c.gender", "c.level", "c.achievementPoints", "c.thumbnail", "c.calcClass",
            "c.faction", "c.totalHonorableKills", "c.guild_name", "gm.member_name", "gm.in_guild",
            "gm.user_id", "gm.rank", "c.bestMythicPlusScore", "c.mythicPlusScores", "gm.isDelete"};
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
    //Mythic plus source
    private JsonObject bestMythicPlusScore = new JsonObject();
    private JsonObject mythicPlusScores = new JsonObject();
    private List<CharacterSpec> specs = new ArrayList<>();
    private List<CharacterItem> items = new ArrayList<>();
    private double itemLevel;
    private CharacterStats stats;
    private boolean isMain = false;
    private boolean isDelete = false;
    //specs fails
    private boolean tryLoadFailSpecs = false;

    //Constructor load from DB if have a ID
    public CharacterMember(int internalID) {
        super(COMBIEN_TABLE_NAME, COMBIEN_TABLE_KEY, COMBIEN_TABLE_STRUCTURE);
        //Load Character from DB
        loadFromDB(internalID, "gm.internal_id = c.internal_id", true);
    }

    public CharacterMember(String name, String realm) {
        super(COMBIEN_TABLE_NAME, COMBIEN_TABLE_KEY, COMBIEN_TABLE_STRUCTURE);
        loadFromDBUniqued(new String[]{"gm.member_name", "gm.realm"}, new String[]{name, realm}, "gm.internal_id = c.internal_id", true);
        if (!this.isInternalData && !this.isDelete) {
            CharacterMember upCharacter = Update.shared.getMemberFromBlizz(name, realm);
            if (!upCharacter.isDelete()) {
                cloneMember(upCharacter);
            } else {
                this.name = upCharacter.getName();
                this.realm = upCharacter.getRealm();
                this.isDelete = true;
            }
        }
    }

    //Disable character (CHARACTER NOT FOUND)
    public CharacterMember(String name, String realm, boolean isEnable) {
        super(GMEMBER_ID_NAME_TABLE_NAME, GMEMBER_ID_NAME_TABLE_KEY, GMEMBER_ID_NAME_TABLE_STRUCTURE);
        this.name = name;
        this.realm = realm;
        this.isDelete = true;
        this.isData = true;
    }

    //Load to JSON
    public CharacterMember(JsonObject playerInfo) {
        super(CHARACTER_INFO_TABLE_NAME, CHARACTER_INFO_TABLE_KEY, CHARACTER_INFO_TABLE_STRUCTURE);
        saveInternalInfoObject(playerInfo);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject playerInfo) {
        this.realm = playerInfo.get("realm").getAsString();
        this.battleGroup = playerInfo.get("battlegroup").getAsString();
        this.achievementPoints = playerInfo.get("achievementPoints").getAsLong();
        this.thumbnail = playerInfo.get("thumbnail").getAsString();
        this.calcClass = (playerInfo.get("calcClass").getAsString()).charAt(0);
        this.lastModified = playerInfo.get("lastModified").getAsLong();
        this.totalHonorableKills = playerInfo.get("totalHonorableKills").getAsLong();

        if (playerInfo.has("internal_id")) { // load DB
            this.internalID = playerInfo.get("internal_id").getAsInt();
            this.name = playerInfo.get("member_name").getAsString();
            this.isGuildMember = playerInfo.get("in_guild").getAsBoolean();
            this.isDelete = playerInfo.get("isDelete").getAsBoolean();
            this.gRank = new Rank(playerInfo.get("rank").getAsInt());
            // if is delete, not all members have all information, so try load if have, and not break if not exist info.
            try {
                this.userID = playerInfo.get("user_id").getAsInt();
                this.gender = playerInfo.get("gender").getAsInt();
                this.level = playerInfo.get("level").getAsInt();
                this.faction = playerInfo.get("faction").getAsInt();
                this.guildName = playerInfo.get("guild_name").getAsString();
                this.memberClass = new PlayableClass(playerInfo.get("class").getAsInt());
                this.race = new PlayableRace(playerInfo.get("race").getAsInt());
                this.stats = new CharacterStats(this.internalID);
                String bMyhScore = "{}", mythScores = "{}";
                if (!playerInfo.get("bestMythicPlusScore").isJsonNull())
                    bMyhScore = playerInfo.get("bestMythicPlusScore").getAsString();
                if (!playerInfo.get("mythicPlusScores").isJsonNull())
                    mythScores = playerInfo.get("mythicPlusScores").getAsString();
                loadMythicPlusScoreDB(bMyhScore, mythScores);
            } catch (NullPointerException e) {
                Logs.errorLog(CharacterMember.class, "Fail in load member info - " + this.internalID + " isDelete? " + this.isDelete + " - " + e);
            }

        } else { // load blizzard
            this.name = playerInfo.get("name").getAsString();
            this.gender = playerInfo.get("gender").getAsInt();
            this.level = playerInfo.get("level").getAsInt();
            this.faction = playerInfo.get("faction").getAsInt();
            this.memberClass = new PlayableClass(playerInfo.get("class").getAsInt());
            this.race = new PlayableRace(playerInfo.get("race").getAsInt());
            //If have a guild...
            this.guildName = "";
            this.isGuildMember = false;
            if (playerInfo.has("guild"))
                this.guildName = playerInfo.get("guild").getAsJsonObject().get("name").getAsString();
            if (this.guildName.length() > 0 && this.guildName.equals(GeneralConfig.getStringConfig("GUILD_NAME")))
                this.isGuildMember = true;
            //Generate member id:
            generateMemberID();
            //Spec
            loadSpecFromBlizz(playerInfo.get("talents").getAsJsonArray());
            loadItemsFromBlizz(playerInfo.get("items").getAsJsonObject());
            //Status
            this.stats = new CharacterStats(playerInfo.get("stats").getAsJsonObject());
        }

        this.isData = true;
    }

    private void loadMythicPlusScoreDB(String bestMythicPlusScore, String mythicPlusScores) {
        //Best Score
        if (bestMythicPlusScore.length() > 2) {
            this.bestMythicPlusScore = JsonParser.parseString(bestMythicPlusScore).getAsJsonObject();
        }
        if (mythicPlusScores.length() > 2) {
            this.mythicPlusScores = JsonParser.parseString(mythicPlusScores).getAsJsonObject();
        }
    }

    private void loadSpecFromBlizz(JsonArray allTalents) {
        for (int i = 0; i < allTalents.size(); i++) {
            JsonObject specsAvailable = (JsonObject) allTalents.get(i);
            JsonObject specInfoBlizz = (JsonObject) specsAvailable.get("spec");
            JsonArray spellTalents = (JsonArray) specsAvailable.get("talents");
            /**
             * Todos los miembros tienen talentos dependiendo de su especialidad
             * Blizzard nos ofrece los talentos por especialidad, por lo que debemos
             * recorrer la lista de "talentos" (specs) y dentro de cada spec, encontraremos
             * los talentos que el jugador escogio
             */
            if (specsAvailable.has("spec") && !specInfoBlizz.isJsonNull())
            {
                CharacterSpec spec = new CharacterSpec(this, specInfoBlizz, spellTalents);
                if (specsAvailable.has("selected")) spec.setEnable(true);
                this.specs.add(spec);
            }
        }
    }

    private void loadItemsFromBlizz(JsonObject allItems) {
        for (Object key : allItems.keySet()) {
            String postItem = (String) key;
            //all element except average item level information
            if (!postItem.equals("averageItemLevel") && !postItem.equals("averageItemLevelEquipped")) {
                JsonObject item = allItems.get(postItem).getAsJsonObject();
                item.addProperty("post_item", postItem);
                this.items.add(new CharacterItem(item));
            }
        }
    }

    private void loadSpecFromDB() {
        loadSpecFromDB(null);
    }

    private void loadActiveSpecFromDB() {
        loadSpecFromDB("AND enable=1");
    }

    private void loadSpecFromDB(String extraWhere) {
        this.specs = new ArrayList<>();
        if (!tryLoadFailSpecs && !this.isDelete) {
            try {
                JsonArray memberSpec = dbConnect.select(CharacterSpec.SPECS_TABLE_NAME,
                        new String[]{"id"},
                        "member_id=? " + ((extraWhere != null) ? extraWhere : ""),
                        new String[]{this.internalID + ""});
                if (memberSpec.size() > 0) {
                    for (int i = 0; i < memberSpec.size(); i++) {
                        CharacterSpec sp = new CharacterSpec(memberSpec.get(i).getAsJsonObject().get("id").getAsInt());
                        this.specs.add(i, sp);
                    }
                } else { // No have a specs in DB!!!!
                    Logs.errorLog(CharacterMember.class, "Fail to load spec! (size <= 0)? " + this.name + " - " + this.internalID);
                    Logs.errorLog(CharacterMember.class, "\tTry get spec again from update...");
                    if (!tryLoadFailSpecs) {
                        tryLoadFailSpecs = true;
                        loadSpecFromBlizz();
                    } else {
                        Logs.errorLog(CharacterMember.class, "Member `" + this.name + "[" + this.internalID + "]` not have spec, change status to delete");
                        isDelete = true;
                        saveInDB();
                    }
                }
            } catch (SQLException | DataException ex) {
                Logs.errorLog(CharacterMember.class, "Fail to get a 'Specs' from DB Member " + this.name + " e: " + ex);
            }
        }
    }

    private void loadStats() {
        CharacterMember tempMember = Update.shared.getMemberFromBlizz(this.name, this.realm);
        this.stats = tempMember.getStats();
        //Save new info in DB
        saveInDB();
    }

    private void loadSpecFromBlizz() {
        CharacterMember tempMember = Update.shared.getMemberFromBlizz(this.name, this.realm);
        tempMember.tryLoadFailSpecs = this.tryLoadFailSpecs;
        tempMember.internalID = this.internalID;
        tempMember.isInternalData = true;
        this.specs = tempMember.getSpecs();
        //Save new info in DB
        saveInDB();
    }

    private void loadItemsFromDB() {
        try {
            JsonArray itemDB = dbConnect.select(CharacterItem.ITEMS_MEMBER_TABLE_NAME,
                    new String[]{"id"},
                    "member_id=? AND item_id != 0",
                    new String[]{this.internalID + ""});
            for (int i = 0; i < itemDB.size(); i++) {
                CharacterItem sp = new CharacterItem(itemDB.get(i).getAsJsonObject().get("id").getAsInt());
                this.items.add(sp);
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(CharacterMember.class, "Fail to get a 'items' from DB Member " + this.name + " e: " + ex);
        }
    }

    private void generateMemberID() {
        //check if have and id
        if (this.name != null && this.realm != null && this.internalID == 0) {
            try {
                JsonArray oldId = dbConnect.select(GMEMBER_ID_NAME_TABLE_NAME,
                        new String[]{GMEMBER_ID_NAME_TABLE_KEY},
                        "member_name=? AND realm=?",
                        new String[]{this.name, this.realm});
                if (oldId.size() > 0) {
                    this.internalID = Integer.parseInt(oldId.get(0).getAsJsonObject().get(GMEMBER_ID_NAME_TABLE_KEY).getAsString());
                } else {
                    /* {"internal_id", "member_name", "realm", "rank", "in_guild", "user_id"}; */
                    String id = dbConnect.insert(GMEMBER_ID_NAME_TABLE_NAME,
                            GMEMBER_ID_NAME_TABLE_KEY,
                            new String[]{"member_name", "realm", "in_guild"},
                            new String[]{name, realm, "0"});//asumed is 0 becouse in frist moment, we get all guilds members.
                    this.internalID = Integer.parseInt(id);
                }
            } catch (DataException | SQLException ex) {
                Logs.errorLog(CharacterMember.class, "Fail to generate a member ID " + this.name + " - " + ex);
            }
        }
    }

    @Override
    public boolean saveInDB() {
        if (this.isData && this.internalID == 0) {
            generateMemberID();
        }
        if (this.isDelete) {
            /* {"internal_id", "member_name", "realm",
             *  "rank", "in_guild", "user_id", "isDelete"};
             */
            this.isInternalData = true;
            setTableName(GMEMBER_ID_NAME_TABLE_NAME);
            setTableKey(GMEMBER_ID_NAME_TABLE_KEY);
            setTableStructur(new String[]{"internal_id", "isDelete"});
            String[] val = new String[]{this.internalID + "", this.isDelete ? "1" : "0"};
            saveInDBObj(val);
            return true;
        } else {
            /* {"internal_id", "battlegroup", "class",
             * "race", "gender", "level", "achievementPoints",
             * "thumbnail", "calcClass", "faction", "totalHonorableKills",
             * "bestMythicPlusScore", "mythicPlusScores",
             * "guild_name", "lastModified"};
             */
            setTableName(CHARACTER_INFO_TABLE_NAME);
            setTableKey(CHARACTER_INFO_TABLE_KEY);
            setTableStructur(CHARACTER_INFO_TABLE_STRUCTURE);
            /* System.out.println("-----------SAVE '"+ this.name +"'-----------------");
            System.out.println(this.internalID +" - "+ this.battleGroup +" - "+ this.memberClass.getId());
            System.out.println(this.race.getId() +" - "+ this.gender +" - "+ this.level +" - "+ this.achievementPoints);
            System.out.println(this.thumbnail +" - "+ this.calcClass +" - "+ this.faction +" - "+ this.totalHonorableKills);
            System.out.printlm(this.bestMythicPlusScore.toString() +" - "+ this.mythicPlusScores.toString());
            System.out.println(this.guildName +" - "+ this.lastModified); */
            String[] val = new String[]{this.internalID + "", this.battleGroup, this.memberClass.getId() + "",
                    this.race.getId() + "", this.gender + "", this.level + "", this.achievementPoints + "",
                    this.thumbnail, this.calcClass + "", this.faction + "", this.totalHonorableKills + "",
                    this.bestMythicPlusScore.toString(), this.mythicPlusScores.toString(),
                    this.guildName, this.lastModified + ""};
            //Valid if have a data this object, and guild is null (if we try update, and put null in query, the DB not update this column, for this use this IF)
            if (this.isData) {
                String guildName = GeneralConfig.getStringConfig("GUILD_NAME");
                if (this.isGuildMember && !this.guildName.equals(guildName))
                    deleteFromDB(); //prevent save in guild/internalID members table if not is a guild member
                int vSave = saveInDBObj(val);
                if ((vSave == SAVE_MSG_INSERT_OK) || (vSave == SAVE_MSG_UPDATE_OK)) {
                    //Save specs...
                    if (this.specs.isEmpty()) loadSpecFromDB();
                    this.specs.forEach((spc) -> {
                        spc.setMemberId(this.internalID);
                        //valid if this member have a this spec in DB (set Update or Insert)
                        try {
                            JsonArray specMember = dbConnect.select(CharacterSpec.SPECS_TABLE_NAME,
                                    new String[]{CharacterSpec.SPECS_TABLE_KEY},
                                    "member_id=? AND spec_id=?",
                                    new String[]{spc.getMemberId() + "", spc.getSpec().getId() + ""});
                            if (specMember.size() > 0) {
                                int charSpecID = specMember.get(0).getAsJsonObject().get(CharacterSpec.SPECS_TABLE_KEY).getAsInt();
                                spc.setId(charSpecID);
                                spc.setIsInternalData(true);
                            }
                            spc.saveInDB();
                        } catch (DataException | SQLException ex) {
                            Logs.errorLog(CharacterMember.class, "Fail to get specs info in DB from member " + this.name + " - " + ex);
                        }
                    });
                    //Save items...
                    //Clear all old items:
                    if (this.items.isEmpty()) loadItemsFromDB();
                    try {
                        dbConnect.update(CharacterItem.ITEMS_MEMBER_TABLE_NAME,
                                CharacterItem.ITEMS_MEMBER_TABLE_CLEAR_STRUCTURE,
                                CharacterItem.ITEMS_MEMBER_TABLE_CLEAR_STRUCTURE_VALUES,
                                "member_id=?",
                                new String[]{this.internalID + ""});
                    } catch (DataException | SQLException ex) {
                        Logs.errorLog(CharacterMember.class, "Fail to update remove old items " + this.internalID + " - " + ex);
                    }
                    //Update or insert a new items
                    this.items.forEach((itm) -> {
                        itm.setMemberId(this.internalID);
                        CharacterItem iMemberDB = new CharacterItem(itm.getPosition(), this.internalID);
                        if (iMemberDB.isInternalData()) {
                            itm.setId(iMemberDB.getId());
                            itm.setIsInternalData(true);
                        }
                        itm.saveInDB();
                    });
                    //Save stats
                    this.stats.setId(this.internalID);
                    CharacterStats sDB = new CharacterStats(this.internalID);
                    if (sDB.isInternalData()) {
                        this.stats.setIsInternalData(true);
                    }
                    if (!this.stats.isData())
                        loadStats();
                    this.stats.saveInDB();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Delete from DB
     */
    private boolean deleteFromDB() {
        try {//change player in character_info in_guild because is change
            Logs.infoLog(CharacterMember.class, "Character " + this.name + " change guild");
            dbConnect.update(GMEMBER_ID_NAME_TABLE_NAME,
                    new String[]{"in_guild", "rank"},
                    new String[]{"0", "0"},
                    "internal_id=?",
                    new String[]{this.internalID + ""});
            return true;
        } catch (DataException | SQLException ex) {
            Logs.errorLog(CharacterMember.class, "Error when try remove a user not in guild: " + ex);
            return false;
        }
    }

    public void cloneMember(CharacterMember mb) {
        if (mb != null && mb.isData()) {
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
            this.bestMythicPlusScore = mb.getBestMythicPlusScore();
            this.mythicPlusScores = mb.getMythicPlusScores();
            //Try save if not exist in DB
            if (this.isInternalData)
                this.internalID = mb.getId();
            else {
                generateMemberID();
                saveInDB();
                Logs.infoLog(CharacterMember.class, "New Member in DB " + this.internalID + " - " + this.name);
            }
        }
    }

    //GETTERS
    public boolean isGuildMember() {
        return this.isGuildMember;
    }

    public String getName() {
        return this.name;
    }

    public String getRealm() {
        return this.realm;
    }

    public String getBattleGroup() {
        return this.battleGroup;
    }

    public PlayableClass getMemberClass() {
        return this.memberClass;
    }

    public PlayableRace getRace() {
        return this.race;
    }

    public int getGender() {
        return this.gender;
    }

    public int getLevel() {
        return this.level;
    }

    public long getAchievementPoints() {
        return this.achievementPoints;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public CharacterStats getStats() {
        if (!this.stats.isData()) loadStats();
        return this.stats;
    }

    public boolean isMain() {
        return this.isMain;
    }

    public boolean isDelete() {
        return this.isDelete;
    }

    public Rank getRank() {
        return this.gRank;
    }

    public double getMythicScoreAll() {
        if (this.bestMythicPlusScore.has("season") && !this.mythicPlusScores.get("all").isJsonNull())
            return this.mythicPlusScores.get("all").getAsDouble();
        else
            return 0;
    }

    public double getBestMythicScore() {
        if (this.bestMythicPlusScore.has("season") && !this.bestMythicPlusScore.get("score").isJsonNull())
            return this.bestMythicPlusScore.get("score").getAsDouble();
        else
            return 0;
    }

    public String getBestMythicScoreSeason() {
        if (this.bestMythicPlusScore.has("season") && !this.bestMythicPlusScore.get("season").isJsonNull())
            return this.bestMythicPlusScore.get("season").getAsJsonObject().get("name").getAsString();
        else
            return "";
    }

    public String getThumbnailURL() {
        return String.format(WoWAPIService.API_CHARACTER_RENDER_URL, GeneralConfig.getStringConfig("SERVER_LOCATION"), getThumbnail());
    }

    public char getCalcClass() {
        return this.calcClass;
    }

    public int getFaction() {
        return this.faction;
    }

    public String getGuildName() {
        return this.guildName;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public int getUserID() {
        return this.userID;
    }

    public JsonObject getBestMythicPlusScore() {
        return this.bestMythicPlusScore;
    }

    public JsonObject getMythicPlusScores() {
        return this.mythicPlusScores;
    }

    public Date getLastModifiedDate() {
        Date time = new Date(Long.parseLong(this.lastModified + ""));
        return time;
    }

    public long getTotalHonorableKills() {
        return this.totalHonorableKills;
    }

    public List<CharacterSpec> getSpecs() {
        if (this.specs.isEmpty() || this.specs.size() == 1) loadSpecFromDB();
        return this.specs;
    }

    public CharacterSpec getActiveSpec() {
        if (this.specs.isEmpty()) loadActiveSpecFromDB();
        for (CharacterSpec sp : this.specs)
            if (sp.isEnable())
                return sp;
        //if is null!! we have a problem! the spec we need setters, mybe the data
        //no is real correct save, try again... only 1 time
        Logs.errorLog(CharacterMember.class, "Not active spec detected " + this.name + " try load again from blizz...");
        loadSpecFromBlizz();
        for (CharacterSpec sp : this.specs)
            if (sp.isEnable())
                return sp;
        return null;
    }

    public List<CharacterItem> getItems() {
        return this.items;
    }

    public double getItemLevel() {
        //If this object have a item level loaded
        if (this.itemLevel > 0) return this.itemLevel;
        //If not save, load
        int sumItemLevl = 0;
        int count = 0;
        if (this.items.isEmpty()) loadItemsFromDB();
        for (CharacterItem item : this.items) {
            if (!item.getPosition().equals("tabard") && !item.getPosition().equals("shirt")) {
                sumItemLevl += item.getIlevel();
                count++;
            }
        }
        if (count == 0) return 0;
        this.itemLevel = (double) sumItemLevl / (double) count;
        return this.itemLevel;
    }

    public CharacterItem getItemByPost(String post) {
        if (this.items.isEmpty())
            loadItemsFromDB();
        for (CharacterItem im : this.items)
            if (im.getPosition().equals(post))
                return im;
        return null;
    }

    /*
     * Get better mythic plus run in the week
     */
    public KeystoneDungeonRun getBestRunWeek() {
        KeystoneDungeonRun kBestRun = null;
        try {
            //select * from keystone_dungeon_run_members where character_internal_id = <this.internalId>;
            JsonArray keyRunsMembersDB = dbConnect.select(KeystoneDungeonRun.KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_NAME,
                    new String[]{"keystone_dungeon_run_id"},
                    "character_internal_id=?",
                    new String[]{this.internalID + ""});

            if (keyRunsMembersDB.size() > 0) {
                //select * from keystone_dungeon_run where (id = <keys ID> OR ...) and completed_timestamp > <time...>;
                String where = "(";
                String[] whereValues = new String[keyRunsMembersDB.size()];
                for (int i = 0; i < keyRunsMembersDB.size(); i++) {
                    if (i != 0 && i != keyRunsMembersDB.size()) where += " OR ";
                    where += "id=?";
                    whereValues[i] = keyRunsMembersDB.get(i).getAsJsonObject().get("keystone_dungeon_run_id").getAsString();
                }
                where += ")";

                JsonArray keyRunsDB = dbConnect.select(KeystoneDungeonRun.KEYSTONE_DUNGEON_RUN_TABLE_NAME,
                        new String[]{"id"},
                        where + " AND completed_timestamp > " + ServerTime.getLastResetTime() + " ORDER BY keystone_level DESC LIMIT 1",
                        whereValues);

                if (keyRunsDB.size() > 0) {
                    kBestRun = new KeystoneDungeonRun(keyRunsDB.get(0).getAsJsonObject().get("id").getAsInt());
                }
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(CharacterMember.class, "Fail to get best members runs - " + ex);
        }

        return kBestRun;
    }

    //Setters
    public void setIsMain(boolean v) {
        this.isMain = v;
    }

    public void setItemLevel(double ilvl) {
        this.itemLevel = ilvl;
    }

    public void setBestMythicPlusScore(JsonObject bScore) {
        this.bestMythicPlusScore = bScore;
    }

    public void setMythicPlusScores(JsonObject mScore) {
        this.mythicPlusScores = mScore;
    }

    public void setActiveSpec(String sName, String sRole) {
        if (this.specs.isEmpty())
            loadSpecFromDB();
        for (int i = 0; i < this.specs.size(); i++) {
            PlayableSpec pSpec = new PlayableSpec(sName, sRole, getMemberClass().getId());
            boolean isEnableSpec = (this.specs.get(i).getSpec().getId() == pSpec.getId());
            this.specs.get(i).setEnable(isEnableSpec);
        }
    }

    //internal characters spec id
    public void setActiveSpec(int specId) {
        if (this.specs.isEmpty())
            loadSpecFromDB();
        for (int i = 0; i < this.specs.size(); i++) {
            boolean isEnableSpec = (specId == this.specs.get(i).getId());
            this.specs.get(i).setEnable(isEnableSpec);
        }
    }

    //if playable spec is in character spec
    public void setActiveSpecPlayableSpec(int playableSpecId) {
        if (this.specs.isEmpty())
            loadSpecFromDB();
        for (int i = 0; i < this.specs.size(); i++) {
            boolean isEnableSpec = (playableSpecId == this.specs.get(i).getSpec().getId());
            this.specs.get(i).setEnable(isEnableSpec);
        }
    }

    @Override
    public void setId(int id) {
        this.internalID = id;
    }

    @Override
    public int getId() {
        return this.internalID;
    }

    //two members equals method
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || (this.getClass() != o.getClass())) return false;

        int oId = ((CharacterMember) o).getId();
        long oLastModified = ((CharacterMember) o).getLastModified();
        return (
                oId == this.internalID
                        &&
                        (Long.compare(oLastModified, this.lastModified) == 0)
        );
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.internalID;
        hash = 71 * hash + (int) (this.lastModified ^ (this.lastModified >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "CharacterMember{" +
                "internalID=" + internalID +
                ", name='" + name + '\'' +
                ", realm='" + realm + '\'' +
                ", battleGroup='" + battleGroup + '\'' +
                ", memberClass=" + memberClass +
                ", race=" + race +
                ", gender=" + gender +
                ", level=" + level +
                ", achievementPoints=" + achievementPoints +
                ", thumbnail='" + thumbnail + '\'' +
                ", calcClass=" + calcClass +
                ", faction=" + faction +
                ", guildName='" + guildName + '\'' +
                ", lastModified=" + lastModified +
                ", totalHonorableKills=" + totalHonorableKills +
                ", isGuildMember=" + isGuildMember +
                ", userID=" + userID +
                ", gRank=" + gRank +
                ", bestMythicPlusScore=" + bestMythicPlusScore +
                ", mythicPlusScores=" + mythicPlusScores +
                ", specs=" + specs +
                ", items=" + items +
                ", itemLevel=" + itemLevel +
                ", stats=" + stats +
                ", isMain=" + isMain +
                ", isDelete=" + isDelete +
                ", tryLoadFailSpecs=" + tryLoadFailSpecs +
                '}';
    }
}
