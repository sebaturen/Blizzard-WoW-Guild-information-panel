/**
 * File : AuctionHouse.java
 * Desc : Auction house view controller
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.viewController;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.blizzardAPI.Update;
import com.blizzardPanel.dbConnect.DBConnect;
import com.blizzardPanel.gameObject.AuctionItem;
import com.blizzardPanel.gameObject.Item;
import com.google.gson.JsonArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuctionHouse 
{
    private final DBConnect dbConnect = new DBConnect();
    
    public String getLastAHUpdate()
    {
        String out = "";
        try
        {		
            JsonArray dateUpdate = dbConnect.select(Update.UPDATE_INTERVAL_TABLE_NAME,
                                                    new String[] {"update_time"},
                                                    "type=? order by id desc limit 1",
                                                    new String[] {Update.UPDATE_TYPE_AUCTION +""});
            if (dateUpdate.size() > 0)
            {
                out += dateUpdate.get(0).getAsJsonObject().get("update_time").getAsString();
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.errorLog(AuctionHouse.class, "Fail to get a last dynamic update");
        }
        return out;
    }
    
    public List<AuctionItem> getAucItem(int itemId)
    {
        List<AuctionItem> auItem = new ArrayList<>();
        try
        {
            JsonArray aucItem = dbConnect.select(AuctionItem.AUCTION_ITEMS_TABLE_NAME,
                                                new String[] {AuctionItem.AUCTION_ITEMS_KEY}, 
                                                "item = ? AND status = 1 ORDER BY buyout/quantity ASC",
                                                new String[] { itemId +""});
            for(int j = 0; j < aucItem.size(); j++)
            {
                int aucId = aucItem.get(j).getAsJsonObject().get(AuctionItem.AUCTION_ITEMS_KEY).getAsInt();
                auItem.add(new AuctionItem(aucId));
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.errorLog(AuctionHouse.class, "Fail to get a auction item id "+ itemId +" - "+ e);
        }
        return auItem;
    }
    
    public List<Item> getItems(String name)
    {
        List<Item> items = new ArrayList<>();
        if(name.length() < 3) return null;
        try
        {		
            JsonArray dataItems = dbConnect.select(Item.ITEM_TABLE_NAME,
                                                    new String[] {Item.ITEM_TABLE_KEY},
                                                    "name LIKE ? AND id !=0",
                                                    new String[] { "%"+name+"%"});
            for(int i = 0; i < dataItems.size(); i++)
            {
                int itemId = dataItems.get(i).getAsJsonObject().get(Item.ITEM_TABLE_KEY).getAsInt();
                items.add(new Item(itemId));
            }
        }
        catch (SQLException|DataException e)
        {
            Logs.errorLog(AuctionHouse.class, "Fail to get items name like '"+ name +"' - "+ e);
        }
        return items;
    }
}
