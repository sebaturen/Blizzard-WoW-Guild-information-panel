/**
 * File : AuctionItem.java
 * Desc : Auction house items
 * @author Sebastián Turén Croquevielle(seba@turensoft.com)
 */
package com.blizzardPanel.gameObject;

import com.google.gson.JsonObject;

public class AuctionItem extends GameObject
{
    public static final String AUCTION_ITEMS_TABLE_NAME = "auction_items";
    public static final String AUCTION_ITEMS_KEY = "auc";
    public static final String[] AUCTION_ITEMS_TABLE_STRUCTURE = {"auc", "item", "buyout", "bid", "quantity", "timeLeft",
                                                                    "ownerRealm", "context", "rand", "status", "auc_date"};
    //Atributes
    private int auc;
    private Item item;
    private long buyout;
    private long bid;
    private int quantity;
    private String timeLeft;
    private String ownerRealm;
    private int context;
    private int rand;
    private String aucDate;
    private boolean status = true; //if exist or not, the idea is save in DB and use query to delete that information if is old
    
    public AuctionItem(int auc)
    {
        super(AUCTION_ITEMS_TABLE_NAME, AUCTION_ITEMS_KEY, AUCTION_ITEMS_TABLE_STRUCTURE);
        loadFromDB(auc);
    }
    
    public AuctionItem(JsonObject info)
    {
        super(AUCTION_ITEMS_TABLE_NAME, AUCTION_ITEMS_KEY, AUCTION_ITEMS_TABLE_STRUCTURE);
        saveInternalInfoObject(info);
    }

    @Override
    protected void saveInternalInfoObject(JsonObject objInfo)
    {
        if (objInfo.has("auc_date")) {
            this.aucDate = objInfo.get("auc_date").getAsString();
        }
        this.auc = objInfo.get("auc").getAsInt();
        //this.item = new Item(objInfo.get("item").getAsInt());
        this.bid = objInfo.get("bid").getAsLong();
        this.buyout = objInfo.get("buyout").getAsLong();
        this.quantity = objInfo.get("quantity").getAsInt();
        this.timeLeft = objInfo.get("timeLeft").getAsString();
        this.rand = objInfo.get("rand").getAsInt();
        this.context = objInfo.get("context").getAsInt();
        if (objInfo.has("ownerRealm"))
            this.ownerRealm = objInfo.get("ownerRealm").getAsString();
        this.isData = true;
    }

    @Override
    public boolean saveInDB() {
        /* {"auc", "item", "buyout", "bid", "quantity", "timeLeft",
         * "ownerRealm", "context", "rand", "auc_date"};
`        */
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
