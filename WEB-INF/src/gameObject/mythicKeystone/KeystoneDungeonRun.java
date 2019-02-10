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
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    private long complatedTimeStamp;
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

    public KeystoneDungeonRun(JSONObject info)
    {
        super(KEYSTONE_DUNGEON_RUN_TABLE_NAME, KEYSTONE_DUNGEON_RUN_TABLE_KEY, KEYSTONE_DUNGEON_RUN_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    protected void saveInternalInfoObject(JSONObject objInfo)
    {
        this.complatedTimeStamp = (long) objInfo.get("completed_timestamp");
        this.duration = (long) objInfo.get("duration");
        if(objInfo.get("keystone_level").getClass() == Long.class)
        {//info come from blizzard
            this.keystoneLevel = ((Long) objInfo.get("keystone_level")).intValue();
            this.ksDun = new KeystoneDungeon( ((Long)((JSONObject) objInfo.get("dungeon")).get("id")).intValue());
            if(!this.ksDun.isInternalData())
            {
                String urlDunDetail = (((JSONObject) ((JSONObject)objInfo.get("dungeon")).get("key")).get("href")).toString();
                try { this.ksDun = (new Update()).getKeyStoneDungeonDetail(urlDunDetail); } 
                catch (IOException | ParseException | DataException ex) { Logs.errorLog(KeystoneDungeonRun.class, "Fail to get Dugneon details from update - "+ ex); }
            }
            this.isCompleteInTime = (Boolean) objInfo.get("is_completed_within_time");
            loadMembersFromBlizz((JSONArray) objInfo.get("members"));
            loadKeystoneAffixesFromBlizz((JSONArray) objInfo.get("keystone_affixes"));
        }
        else
        {
            this.id = (int) objInfo.get("id");
            this.keystoneLevel = (int) objInfo.get("keystone_level"); 
            this.ksDun = new KeystoneDungeon((Integer) objInfo.get("keystone_dungeon_id"));
            this.isCompleteInTime = (Boolean) objInfo.get("is_complete_in_time");
            loadMembersFromDB();
            loadKeystoneAffixesDB(objInfo.get("key_affixes").toString());
        }
        this.isData = true;
    }
    
    private void loadKeystoneAffixesDB(String keyAffixString)
    {
        try 
        {
            JSONParser parser = new JSONParser();
            JSONObject keyAffix;
            if(keyAffixString.length() > 2)
            {
                keyAffix = (JSONObject) parser.parse(keyAffixString);
                for(int i = 0; i < keyAffix.size(); i++)
                {
                    int affixId = ((Long) keyAffix.get(i+"")).intValue();
                    KeystoneAffix kAf = new KeystoneAffix(affixId);
                    this.keyAffixes.add(kAf);
                }
            }
        } catch (ParseException ex) {
            Logs.errorLog(KeystoneDungeonRun.class, "Fail to parse stats keystone afixes "+ this.id +" - "+ ex);
        }
    }
    
    private void loadKeystoneAffixesFromBlizz(JSONArray keyAffix)
    {
        for(int i = 0; i < keyAffix.size(); i++)
        {
            JSONObject kBlizzDetail = (JSONObject) keyAffix.get(i);
            KeystoneAffix kAffix = new KeystoneAffix( ((Long) kBlizzDetail.get("id")).intValue() );
            if(!kAffix.isInternalData())
            {
                kAffix = new KeystoneAffix(kBlizzDetail);
                kAffix.saveInDB();
            }
            this.keyAffixes.add(kAffix);
        }
    }
    
    private void loadMembersFromBlizz(JSONArray runMemsInfo)
    {
        for(int i = 0; i < runMemsInfo.size(); i++)
        {
            JSONObject memI = (JSONObject) runMemsInfo.get(i);
            JSONObject charInfo = (JSONObject) memI.get("character");
            String charName = charInfo.get("name").toString();
            System.out.println("Load member> "+ charName);
            Realm charRealm = new Realm( ((Long) ((JSONObject)charInfo.get("realm")).get("id") ).intValue());
            //New character
            CharacterMember newMember = new CharacterMember(charName, charRealm.getName());
            if(!newMember.isDelete())
            {
                newMember.setItemLevel( ((Long) ((JSONObject)runMemsInfo.get(i)).get("equipped_item_level") ).intValue() );
                JSONObject specDetail = (JSONObject) ((JSONObject) runMemsInfo.get(i)).get("specialization");
                newMember.setActiveSpecPlayableSpec( ((Long) specDetail.get("id")).intValue() );
                this.members.add(newMember);                
            }
        }
    }
    
    private void loadMembersFromDB()
    {
        try 
        {
            JSONArray memsInfo = dbConnect.select(KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_NAME,
                                        KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_STRUCTURE,
                                        "keystone_dungeon_run_id=?",
                                        new String[] {this.id+""});
            for(int i = 0; i < memsInfo.size(); i++)
            {
                JSONObject memDetail = (JSONObject) memsInfo.get(i);
                CharacterMember mem = new CharacterMember( (Integer) memDetail.get("character_internal_id") );
                mem.setItemLevel( (Integer) memDetail.get("character_item_level"));
                mem.setActiveSpec( (Integer) memDetail.get("character_spec_id"));
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
        JSONObject keyAffix = new JSONObject();
        for(int i = 0; i < this.keyAffixes.size(); i++)
        {
            keyAffix.put(i, this.keyAffixes.get(i).getId());
        }
        //{"id", "completed_timestamp", "duration", "keystone_level", "keystone_dungeon_id", "is_complete_in_time"} //
        String[] saveData;
        if(this.isInternalData)
        {
            saveData = new String[] {this.id+"", this.complatedTimeStamp +"", this.duration +"", this.keystoneLevel +"", 
                                        this.ksDun.getId() +"", (this.isCompleteInTime)? "1":"0", keyAffix.toString()};
        }
        else
        {            
            setTableStructur(DBStructure.outKey(KEYSTONE_DUNGEON_RUN_TABLE_STRUCTURE));
            saveData = new String[] {this.complatedTimeStamp +"", this.duration +"", this.keystoneLevel +"", 
                                        this.ksDun.getId() +"", (this.isCompleteInTime)? "1":"0", keyAffix.toString()};
        }
        switch (saveInDBObj(saveData))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                this.members.forEach( (m) -> 
                {
                    try 
                    {
                        JSONArray memInGroupId = null;
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
                        if ( (memInGroupId == null) || (memInGroupId.isEmpty()) )
                        {//insert
                            dbConnect.insert(
                                KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_NAME,
                                KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_KEY,
                                DBStructure.outKey(KEYSTONE_DUNGEON_RUN_MEMBERS_TABLE_STRUCTURE),
                                //{"keystone_dungeon_run_id", "character_internal_id", "character_spec_id", "character_item_level"};
                                new String[] { this.id+"", m.getId()+"", m.getActiveSpec().getId()+"", m.getItemLevel()+"" });
                        }
                    } catch (DataException|ClassNotFoundException|SQLException ex) {
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
    public long getComplatedTimeStamp() { return this.complatedTimeStamp; }
    public List<KeystoneAffix> getAffixes() { return this.keyAffixes; }
    public String getCompleteDate() 
    {        
        Date time = new Date(this.complatedTimeStamp);
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
                return cm;
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

}
