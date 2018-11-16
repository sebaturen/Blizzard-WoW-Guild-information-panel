/**
 * File : DBStructure.java
 * Desc : Data base structure
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.dbConnect;

public interface DBStructure 
{
    //Player Achivements category
    public static final String PLAYER_ACHIVEMENTS_CATEGORY_TABLE_NAME = "player_achivement_category";
    public static final String PLAYER_ACHIVEMENTS_CATEGORY_TABLE_KEY = "id";
    public static final String[] PLAYER_ACHIVEMENTS_CATEGORY_TABLE_STRUCTURE = {"id", "name", "father_id"};
    
    //Player Achivements list
    public static final String PLAYER_ACHIVEMENTS_LIST_TABLE_NAME = "player_achivement_list";
    public static final String PLAYER_ACHIVEMENTS_LIST_TABLE_KEY = "id";
    public static final String[] PLAYER_ACHIVEMENTS_LIST_TABLE_STRUCTURE = {"id", "category_id", "title", 
                                                                            "points", "description", "icon"};
    
    //Wow Token
    public static final String WOW_TOKEN_TABLE_NAME = "wow_token";
    public static final String WOW_TOKEN_TABLE_KEY = "last_updated_timestamp";
    public static final String[] WOW_TOKEN_TABLE_STRUCTURE = {"last_updated_timestamp", "price"};
        
    //In many time, we need insert out to id, because the id is auto update.
    public static String[] outKey(String[] array)
    {
        String[] outArray = new String[array.length-1];
        for(int i = 1; i < array.length; i++)
        {
            outArray[i-1] = array[i];
        }
        return outArray;
    }
               
}
