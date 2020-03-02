/**
 * File : EventAsistCharacter.java
 * Desc : Char detail from user in event
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.events;

import com.blizzardPanel.dbConnect.DBStructure;
import com.blizzardPanel.gameObject.GameObject;
import com.blizzardPanel.gameObject.characters.CharacterMember;
import com.google.gson.JsonObject;

public class EventAsistCharacter extends GameObject
{
    //DB Structure
    public static final String EVENTS_ASIST_CHAR_TABLE_NAME = "events_asist_char";
    public static final String EVENTS_ASIST_CHAR_TABLE_KEY = "id_asis_char";
    public static final String[] EVENTS_ASIST_CHAR_TABLE_STRUCTURE = {"id_asis_char", "id_asis", "char_id", "spec_id", "is_main" };
    
    //Atribute
    private int idAsisChar = -1;
    private int idAsis;
    private CharacterMember charM;
    private boolean isMain;
    
    public EventAsistCharacter()
    {
        super(EVENTS_ASIST_CHAR_TABLE_NAME, EVENTS_ASIST_CHAR_TABLE_KEY, EVENTS_ASIST_CHAR_TABLE_STRUCTURE);
    }
    
    public EventAsistCharacter(int id)
    {
        super(EVENTS_ASIST_CHAR_TABLE_NAME, EVENTS_ASIST_CHAR_TABLE_KEY, EVENTS_ASIST_CHAR_TABLE_STRUCTURE);
        loadFromDB(id);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        this.idAsisChar = objInfo.get("id_asis_char").getAsInt();;
        this.idAsis = objInfo.get("id_asis").getAsInt();;
        this.charM = new CharacterMember(objInfo.get("char_id").getAsInt());
        this.charM.setActiveSpec(objInfo.get("spec_id").getAsInt());
        this.isMain = objInfo.get("is_main").getAsBoolean();
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {
        setTableStructur(DBStructure.outKey(EVENTS_ASIST_CHAR_TABLE_STRUCTURE));
        /* {"id_asis", "char_id", "spec_id" }; */
        switch (saveInDBObj(new String[] {this.idAsis+"", this.charM.getId()+"", this.charM.getActiveSpec()+""}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                //return true;
        }
        return false;
    }

    @Override
    public void setId(int id) { this.idAsisChar = id; }
    public void setAsisID(int aId) { this.idAsis = aId; }
    public void setCharacterMember(CharacterMember cm) { this.charM = cm; }
    public void setCharSpec(int idSpec) { this.charM.setActiveSpec(idSpec); }
    public void setIsMain(boolean isMain) { this.isMain = isMain; }
    
    @Override
    public int getId() { return this.idAsisChar; }
    public int getIdAsis() { return this.idAsis; }
    public CharacterMember getCharMember() { return this.charM; }
    public boolean isMain() { return this.isMain; }
    
}
