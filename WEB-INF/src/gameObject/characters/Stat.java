/**
 * File : Stat.java
 * Desc : ItemMember Object
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject.characters;

import com.blizzardPanel.gameObject.GameObject;
import org.json.simple.JSONObject;

public class Stat extends GameObject
{    
    //Stats table DB
    public static final String STATS_TABLE_NAME = "stats";
    public static final String STATS_TABLE_KEY = "id";
    public static final String[] STATS_TABLE_STRUCTURE = {"id", "en_US"};
    
    //Atribute
    private int id;
    private String enUs;
    private int amount;
    
    public Stat(int id)
    {
        super(STATS_TABLE_NAME, STATS_TABLE_KEY, STATS_TABLE_STRUCTURE);
        loadFromDB(id +"");
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) {
        this.id = (Integer) objInfo.get("id");
        this.enUs = objInfo.get("en_US").toString();
    }

    @Override
    public boolean saveInDB() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setId(String id) { this.id = Integer.parseInt(id); }
    public void setAmount(int i) { this.amount = i; }

    @Override
    public String getId() { return this.id +""; }
    public String getEnUs() { return this.enUs; }
    public int getAmount() { return this.amount; }
    
    
}
