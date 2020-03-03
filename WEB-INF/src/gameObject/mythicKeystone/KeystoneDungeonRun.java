/**
 * File : KeystoneDungeonRun.java
 * Desc : KeystoneDungeonRun Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */

package com.blizzardPanel.gameObject.mythicKeystone;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.Realm;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class KeystoneDungeonRun extends GameObject
{
    //DBStructure
    public static final String KEYSTONE_DUNGEON_RUN_TABLE_NAME = "keystone_dungeon_run";
    public static final String KEYSTONE_DUNGEON_RUN_TABLE_KEY = "id";
    public static final String[] KEYSTONE_DUNGEON_RUN_TABLE_STRUCTURE = {"id", "completed_timestamp", "duration", "keystone_level", "keystone_dungeon_id", "is_complete_in_time", "key_affixes"};
    //DBstructure Members
    public static final String KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_NAME = "keystone_dungeon_run_members";
    public static final String KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_KEY = "id";
    public static final String[] KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_STRUCTURE = {"id", "keystone_dungeon_run_id", "character_internal_id", "character_spec_id", "character_item_level"};

    //Atributes
    private int id;
    private long completedTimeStamp;
    private long duration;
    private int keystoneLevel;
    private KeystoneDungeon ksDun;
    private boolean isCompleteInTime;
    private List<CharacterMember> members = new ArrayList<>();
    private List<KeystoneAffix> keyAffixes = new ArrayList<>();

    public KeystoneDungeonRun(int id)
    {
        super(KEYSTONE_DUNGEON_RUN_TABLE_NAME, KEYSTONE_DUNGEON_RUN_TABLE_KEY, KEYSTONE_DUNGEON_RUN_TABLE_STRUCTURE);
        loadFromDB(id);
    }
    
    public KeystoneDungeonRun(long complateTimeStamp, long duration, int keyLevel, int keyDunId, boolean isCompletInTime)
    {
        super(KEYSTONE_DUNGEON_RUN_TABLE_NAME, KEYSTONE_DUNGEON_RUN_TABLE_KEY, KEYSTONE_DUNGEON_RUN_TABLE_STRUCTURE);
        loadFromDBUniqued(
                new String[] {"completed_timestamp", "duration", "keystone_level", 
                            "keystone_dungeon_id", "is_complete_in_time"}, 
                new String[] {complateTimeStamp+"", duration+"", keyLevel+"", 
                            keyDunId+"", (isCompletInTime? "1":"0")});
    }

    public KeystoneDungeonRun(JsonObject info)
    {
        super(KEYSTONE_DUNGEON_RUN_TABLE_NAME, KEYSTONE_DUNGEON_RUN_TABLE_KEY, KEYSTONE_DUNGEON_RUN_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.completedTimeStamp = objInfo.get("completed_timestamp").getAsLong();
        this.duration = objInfo.get("duration").getAsLong();
        this.keystoneLevel = objInfo.get("keystone_level").getAsInt();
        if (objInfo.has("id")) { // from DB
            this.id = objInfo.get("id").getAsInt();
            this.ksDun = new KeystoneDungeon(objInfo.get("keystone_dungeon_id").getAsInt());
            this.isCompleteInTime = objInfo.get("is_complete_in_time").getAsBoolean();
            loadMembersFromDB();
            loadKeystoneAffixesDB(objInfo.get("key_affixes").getAsString());
        } else { // from blizzard
            this.ksDun = new KeystoneDungeon( objInfo.get("dungeon").getAsJsonObject().get("id").getAsInt() );
            if(!this.ksDun.isInternalData())
            {
                String urlDunDetail = objInfo.get("dungeon").getAsJsonObject().get("key").getAsJsonObject().get("href").getAsString();
                this.ksDun = Update.shared.getKeyStoneDungeonDetail(urlDunDetail);
            }
            this.isCompleteInTime = objInfo.get("is_completed_within_time").getAsBoolean();
            loadMembersFromBlizz(objInfo.get("members").getAsJsonArray());
            loadKeystoneAffixesFromBlizz(objInfo.get("keystone_affixes").getAsJsonArray());

        }
        this.isData = true;
    }
    
    private void loadKeystoneAffixesDB(String keyAffixString)
    {
        JsonObject keyAffix;
        if(keyAffixString.length() > 2)
        {
            keyAffix = JsonParser.parseString(keyAffixString).getAsJsonObject();
            for(int i = 0; i < keyAffix.size(); i++)
            {
                int affixId = keyAffix.get(i+"").getAsInt();
                KeystoneAffix kAf = new KeystoneAffix(affixId);
                this.keyAffixes.add(kAf);
            }
        }
    }
    
    private void loadKeystoneAffixesFromBlizz(JsonArray keyAffix)
    {
        for(int i = 0; i < keyAffix.size(); i++)
        {
            JsonObject kBlizzDetail = keyAffix.get(i).getAsJsonObject();
            KeystoneAffix kAffix = new KeystoneAffix( kBlizzDetail.get("id").getAsInt() );
            if(!kAffix.isInternalData())
            {
                kAffix = new KeystoneAffix(kBlizzDetail);
                kAffix.saveInDB();
            }
            this.keyAffixes.add(kAffix);
        }
    }
    
    private void loadMembersFromBlizz(JsonArray runMemsInfo)
    {
        for(int i = 0; i < runMemsInfo.size(); i++)
        {
            JsonObject memI = runMemsInfo.get(i).getAsJsonObject();
            JsonObject charInfo = memI.get("character").getAsJsonObject();
            String charName = charInfo.get("name").getAsString();
            Realm charRealm = new Realm( charInfo.get("realm").getAsJsonObject().get("id").getAsInt() );
            //New character
            CharacterMember newMember = new CharacterMember(charName, charRealm.getName());
            if(!newMember.isDelete())
            {
                newMember.setItemLevel( runMemsInfo.get(i).getAsJsonObject().get("equipped_item_level").getAsInt() );
                JsonObject specDetail = runMemsInfo.get(i).getAsJsonObject().get("specialization").getAsJsonObject();
                newMember.setActiveSpecPlayableSpec( specDetail.get("id").getAsInt() );
                this.members.add(newMember);                
            }
        }
    }
    
    private void loadMembersFromDB()
    {
        try 
        {
            JsonArray memsInfo = dbConnect.select(KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_NAME,
                                        KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_STRUCTURE,
                                        "keystone_dungeon_run_id=?",
                                        new String[] {this.id+""});
            for(int i = 0; i < memsInfo.size(); i++)
            {
                JsonObject memDetail = memsInfo.get(i).getAsJsonObject();
                CharacterMember mem = new CharacterMember( memDetail.get("character_internal_id").getAsInt() );
                mem.setItemLevel( memDetail.get("character_item_level").getAsInt() );
                mem.setActiveSpec( memDetail.get("character_spec_id").getAsInt());
                members.add(mem);
            }
        } catch (SQLException | DataException ex) {
            Logs.errorLog(KeystoneDungeonRun.class, "Fail get members from this run id "+ this.id +" - "+ ex);
        }
    }

    @Override
    public boolean saveInDB()
    {
        //Keystone affixes:
        JsonObject keyAffix = new JsonObject();
        for(int i = 0; i < this.keyAffixes.size(); i++)
        {
            keyAffix.addProperty(""+ i, this.keyAffixes.get(i).getId());
        }
        //{"id", "completed_timestamp", "duration", "keystone_level", "keystone_dungeon_id", "is_complete_in_time"} //
        String[] saveData;
        if(this.isInternalData)
        {
            saveData = new String[] {this.id+"", this.completedTimeStamp +"", this.duration +"", this.keystoneLevel +"",
                                        this.ksDun.getId() +"", (this.isCompleteInTime)? "1":"0", keyAffix.toString()};
        }
        else
        {            
            setTableStructur(DBStructure.outKey(KEYSTONE_DUNGEON_RUN_TABLE_STRUCTURE));
            saveData = new String[] {this.completedTimeStamp +"", this.duration +"", this.keystoneLevel +"",
                                        this.ksDun.getId() +"", (this.isCompleteInTime)? "1":"0", keyAffix.toString()};
        }
        switch (saveInDBObj(saveData))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                this.members.forEach( (m) -> 
                {
                    try 
                    {
                        JsonArray memInGroupId = null;
                        try 
                        {
                            //Verificate if this memers is previewsly register from this group
                            memInGroupId = dbConnect.select(KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_NAME,
                                    new String[] { "id" },
                                    "keystone_dungeon_run_id=? AND character_internal_id=?",
                                    new String[] { this.id +"", m.getId() +"" } );
                        } catch (SQLException ex) {
                            Logs.errorLog(KeystoneDungeonRun.class, "Fail to get memberInGroupID "+ ex);
                        }
                        //Insert or update... if need insert is because not is register :D
                        if ( (memInGroupId == null) || (memInGroupId.size() == 0) )
                        {//insert
                            dbConnect.insert(
                                KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_NAME,
                                KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_KEY,
                                DBStructure.outKey(KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_STRUCTURE),
                                //{"keystone_dungeon_run_id", "character_internal_id", "character_spec_id", "character_item_level"};
                                new String[] { this.id+"", m.getId()+"", m.getActiveSpec().getId()+"", m.getItemLevel()+"" });
                        }
                    } catch (DataException | SQLException ex) {
                        Logs.errorLog(KeystoneDungeonRun.class, "Fail to save members in groups: "+ ex);
                    }
                } );
                return true;
        }
        return false;
    }

    //Getters and Setters
    @Override
    public int getId() { return this.id; }
    public long getCompletedTimeStamp() { return this.completedTimeStamp; }
    public List<KeystoneAffix> getAffixes() { return this.keyAffixes; }
    public String getCompleteDate() 
    {        
        Date time = new Date(this.completedTimeStamp);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }
    public long getDuration() { return this.duration; }
    //Return [hour][minute][seccond]
    public int[] getTimeDuration()
    {
        Date timeCero= new Date(0); 
        Date time = new Date(this.duration); 
        long diff = time.getTime() - timeCero.getTime();   
        int[] times = new int[3];
        times[0] = (int) (diff / (60 * 60 * 1000));
        times[1] = (int) (diff / (60 * 1000) % 60); 
        times[2] = (int) (diff / 1000 % 60);
        return times;
    }
    public int getUpgradeKey()
    {
        if(this.ksDun.getKeystoneUpgrades3() > this.duration) return 3;
        if(this.ksDun.getKeystoneUpgrades2() > this.duration) return 2;
        if(this.ksDun.getKeystoneUpgrades1() > this.duration) return 1;
        return -1;
    }
    public int getKeystoneLevel() { return this.keystoneLevel; }
    public KeystoneDungeon getKeystoneDungeon() { return this.ksDun; }
    public List<CharacterMember> getMembers() { return this.members; }
    public CharacterMember getTank() 
    {
        for(CharacterMember cm : this.members)
            if(cm.getActiveSpec().getSpec().getRole().equals("TANK"))
                return cm;
        return null;
    }
    public CharacterMember getHealr() 
    {
        for(CharacterMember cm : this.members)
            if(cm.getActiveSpec().getSpec().getRole().equals("HEALING"))
            {
                //System.out.println(cm);
                return cm;                
            }
        return null;
    }
    public List<CharacterMember> getDPS() 
    {
        List<CharacterMember> mDps = new ArrayList<>();
        for(CharacterMember cm : this.members)
            if(cm.getActiveSpec().getSpec().getRole().equals("DPS"))
                mDps.add(cm);
        return mDps;
    }
    
    
    public boolean isCompleteInTime() { return this.isCompleteInTime; }

    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return "KeystoneDungeonRun{" +
                "id=" + id +
                ", complatedTimeStamp=" + completedTimeStamp +
                ", duration=" + duration +
                ", keystoneLevel=" + keystoneLevel +
                ", ksDun=" + ksDun +
                ", isCompleteInTime=" + isCompleteInTime +
                ", members=" + members +
                ", keyAffixes=" + keyAffixes +
                '}';
    }
}
