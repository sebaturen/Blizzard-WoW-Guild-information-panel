/**
 * File : AuctionItem.java
 * Desc : Auction house items
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import org.json.simple.JSONObject;

public class AuctionItem extends GameObject
{
    public static final String AUCTION_ITEMS_TABLE_NAME = "auction_items";
    public static final String AUCTION_ITEMS_KEY = "auc";
    public static final String[] AUCTION_ITEMS_TABLE_STRUCTURE = {"auc", "item", "buyout", "bid", "quantity", "timeLeft",
                                                                    "owner", "ownerRealm", "context", "rand", "status", "auc_date"}; 
    //Atributes
    private int auc;
    private Item item;
    private long buyout;
    private long bid;
    private int quantity;
    private String timeLeft;
    private String owner;
    private String ownerRealm;
    private int context;
    private int rand;
    private String aucDate;
    private boolean status = true; //if exist or not, the idea is save in DB and use query to delete that information if is old
    
    public AuctionItem(int auc)
    {
        super(AUCTION_ITEMS_TABLE_NAME, AUCTION_ITEMS_KEY, AUCTION_ITEMS_TABLE_STRUCTURE);
        loadFromDB(auc +"");
    }
    
    public AuctionItem(JSONObject info)
    {
        super(AUCTION_ITEMS_TABLE_NAME, AUCTION_ITEMS_KEY, AUCTION_ITEMS_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JSONObject objInfo) 
    {
        if(objInfo.get("auc").getClass() == java.lang.Long.class)
        {//Info come to blizzard
            this.auc = ((Long) objInfo.get("auc")).intValue();
            this.quantity = ((Long) objInfo.get("quantity")).intValue();
            this.context = ((Long) objInfo.get("context")).intValue();
            this.rand = ((Long) objInfo.get("rand")).intValue();
            this.item = Item.loadItem(((Long) objInfo.get("item")).intValue());
        }
        else
        {//info come to DB
            this.auc = (Integer) objInfo.get("auc");
            this.quantity = (Integer) objInfo.get("quantity");
            this.context = (Integer) objInfo.get("context");
            this.rand = (Integer) objInfo.get("rand");
            this.item = Item.loadItem((Integer) objInfo.get("item"));
            this.aucDate = objInfo.get("auc_date").toString();
        }
        this.buyout = (long) objInfo.get("buyout");
        this.bid = (long) objInfo.get("bid");
        this.timeLeft = objInfo.get("timeLeft").toString();
        this.owner = objInfo.get("owner").toString();
        this.ownerRealm = objInfo.get("ownerRealm").toString();
        this.isData = true;        
    }

    @Override
    public boolean saveInDB() {
        /* {"auc", "item", "buyout", "bid", "quantity", "timeLeft",
         * "owner", "ownerRealm", "context", "rand", "auc_date"}; 
`        */
        switch(saveInDBObj(new String[] { this.auc+"", this.item.getId()+"", this.buyout +"", this.bid +"", this.quantity +"", this.timeLeft,
                                        this.owner, this.ownerRealm, this.context +"", this.rand +"", (this.status)? "1":"0", this.aucDate}))
        {
            case SAVE_MSG_INSERT_OK: case SAVE_MSG_UPDATE_OK:
                return true;            
        }
        return false;
    }
    
    //Getters and Setters

    @Override
    public void setId(int id) { this.auc = id; }
    public void setAucDate(String date) { this.aucDate = date; }

    @Override
    public int getId() { return this.auc; }
    public Item getItem() { return this.item; }
    public int getQuantity() { return this.quantity; }
    public String getTimeLeft() { return this.timeLeft; }
    public String getOwner() { return this.owner; }
    public String getOwnerRealm() { return this.ownerRealm; }
    public int getContext() { return this.context; }
    public int getRand() { return this.rand; }  
    public String getAucDate() { return this.aucDate; }
    public long getBuyout() { return this.buyout; } 
    public int[] getBuyoutDividePrice() { return dividePrice(this.buyout); }
    public long getUniqueBuyoutPrice() { return this.buyout/this.quantity; }
    public int[] getUniqueBuyoutDividePrice() { return dividePrice(getUniqueBuyoutPrice()); }
    public long getBid() { return this.bid; }
    public int[] getBidDividePrice() { return dividePrice(this.bid); }  
    
    public static int[] dividePrice(long itemPrice) 
    {        
        String price = ((Long) itemPrice).toString();
        int[] out = {0,0,0}; //[0-gold][1-silver][2-copper]        
        if(price.length() > 4)
            out[0] = Integer.parseInt(price.substring(0,price.length()-4));
        if(price.length() >= 4)
            out[1] = Integer.parseInt(price.substring(price.length()-4,price.length()-2));
        if(price.length() >= 2)
            out[2] = Integer.parseInt(price.substring(price.length()-2,price.length()));
        else
            out[2] = Integer.parseInt(price);
        return out;
    }
    
}
