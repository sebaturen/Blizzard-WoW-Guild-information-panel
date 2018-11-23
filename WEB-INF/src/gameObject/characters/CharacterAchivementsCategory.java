/**
 * File : CharacterAchivementsCategory.java
 * Desc : Character Achivements Category Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.gameObject.GameObject;
import org.json.simple.JSONObject;

public class CharacterAchivementsCategory extends GameObject
{
    
    //DB TABLE STRUCTURE
    public static final String PLAYER_ACHIEVEMENT_CATEGORY_TABLE_NAME = "player_achievement_category";
    public static final String PLAYER_ACHIEVEMENT_CATEGORY_TABLE_KEY = "id";
    public static final String[] PLAYER_ACHIEVEMENT_CATEGORY_TABLE_STRUCTURE = {"id", "name", "father_id"};
    
    //Atribute
    private int id;
    private String name;
    private CharacterAchivementsCategory fatherCategory;
    
    public CharacterAchivementsCategory(int id)
    {
        super(PLAYER_ACHIEVEMENT_CATEGORY_TABLE_NAME, PLAYER_ACHIEVEMENT_CATEGORY_TABLE_KEY, PLAYER_ACHIEVEMENT_CATEGORY_TABLE_STRUCTURE);
        loadFromDB(id +"");
    }
    
    public CharacterAchivementsCategory(JSONObject info)
    {
        super(PLAYER_ACHIEVEMENT_CATEGORY_TABLE_NAME, PLAYER_ACHIEVEMENT_CATEGORY_TABLE_KEY, PLAYER_ACHIEVEMENT_CATEGORY_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }    

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        this.id = (Integer) objInfo.get("id");
        this.name = objInfo.get("name").toString();
        if(objInfo.containsKey("father_id"))
        {
            this.fatherCategory = new CharacterAchivementsCategory((Integer) objInfo.get("father_id"));
        }
        this.isData = true;
    }

    @Override
    public boolean saveInDB() 
    {            
        /* {"id", "name", "father_id"} */
        String[] saveParam;
        if(fatherCategory == null)
        {
            saveParam = new String[] {this.id +"", this.name};
            setTableStructur(new String[] { "id", "name" });
        }
        else
        {
            saveParam = new String[] {this.id +"", this.name, this.fatherCategory.getId()};
        }
        switch (saveInDBObj(saveParam))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
            return true;
        }
        return false;
    }

    //Getters and Setters
    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }

    @Override
    public String getId() { return this.id +""; }
    public String getName() { return this.name; }
    public CharacterAchivementsCategory getFatherCategory() { return this.fatherCategory; }
}
