/**
 * File : AuctionHouse.java
 * Desc : Auction house view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.artOfWar.viewController;

import com.artOfWar.DataException;
import com.artOfWar.Logs;
import com.artOfWar.blizzardAPI.Update;
import com.artOfWar.dbConnect.DBConnect;
import com.artOfWar.gameObject.AuctionItem;
import com.artOfWar.gameObject.Item;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AuctionHouse 
{
    private final DBConnect dbConnect = new DBConnect();
    
    public String getLastAHUpdate()
    {
        String out = "";
        try
        {		
            JSONArray dateUpdate = dbConnect.select(Update.UPDATE_INTERVAL_TABLE_NAME,
                                                    new String[] {"update_time"},
                                                    "type=? order by id desc limit 1",
                                                    new String[] {Update.UPDATE_AUCTION +""});
            if (dateUpdate.size() > 0)
            {
                out += (((JSONObject)dateUpdate.get(0)).get("update_time")).toString();
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.saveLog("Fail to get a last dynamic update");
        }
        return out;
    }
    
    public List<AuctionItem> getAucItem(String itemName)
    {
        List<AuctionItem> auItem = new ArrayList<>();
        try
        {		
            JSONArray dataItems = dbConnect.select(Item.ITEM_TABLE_NAME,
                                                    new String[] {Item.ITEM_TABLE_KEY},
                                                    "name LIKE ? AND id !=0 LIMIT 5",
                                                    new String[] { "%"+itemName+"%"});
            for(int i = 0; i < dataItems.size(); i++)
            {
                int itemId = (Integer) ((JSONObject) dataItems.get(i)).get(Item.ITEM_TABLE_KEY);
                JSONArray aucItem = dbConnect.select(AuctionItem.AUCTION_ITEMS_TABLE_NAME,
                                                    new String[] {AuctionItem.AUCTION_ITEMS_KEY}, 
                                                    "item = ? AND status = 1",
                                                    new String[] { itemId +""});
                for(int j = 0; j < aucItem.size(); j++)
                {
                    int aucId = (Integer) ((JSONObject) aucItem.get(j)).get(AuctionItem.AUCTION_ITEMS_KEY);
                    auItem.add(new AuctionItem(aucId));
                }
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.saveLog("Fail to get a items like... "+ e);
        }
        return auItem;
    }
}
