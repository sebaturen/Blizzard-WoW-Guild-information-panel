/*Auction house*/
var aucListLoad; //use in ajax server request - item detail
var itemListLoad; //use in ajax server request - item list
$(document).ready(function() {    
    $('#itemName').keyup(function () { searchItem($(this).val()); });
});

function searchItem(itemName) 
{
    if(itemName.length >= 3)
    {
        if(itemListLoad !== undefined)
        {
            itemListLoad.abort();
        }
        $('#itemsSuggested').show();
        $('#itemsSuggested').html('<div class="row justify-content-md-center"><div class="loader itemSuggestLoad"></div></div>');
        itemListLoad = $.getScript('assets/js/auctionHouse/itemsList.jsp?name='+ encodeURIComponent(itemName), function() {
            renderItemList();
        });        
    }
    if(itemName.length === 0)
    {
        if(itemListLoad !== undefined)
        {
            itemListLoad.abort();
        }
        $('#itemsSuggested').html("");
        $('#itemsSuggested').hide();        
    }
}

function renderItemList()
{
    $('#itemsSuggested').html("");
    $('#itemsSuggested').show();
    jQuery.each( items, function(i, item) 
    {
        $('#itemsSuggested').append("<div class='suggest_item pointer' data-img='"+ item.itemImg +"' data-id='"+ item.itemID +"' data-name='"+ item.itemName +"'>"+
                                        "<img class='suggest_item_icon' src='"+ item.itemImg +"'>"+ item.itemName +
                                    "</div>");
    });
    $('.suggest_item').click(function() {
        //Hide other suggestion...
        $('#itemsSuggested').hide();
        //Set actuali suggestion clicked...
        $('#itemName').val($(this).data("name"));
        $('#itemSearchImag').html("<img src='"+ $(this).data("img") +"'>");
        //Clear content...
        $("#items_ah_content").html("");
        //Search a auc info :D
        $("#items_ah_content").append('<tr><td colspan="6"><div class="row justify-content-md-center"><div class="loader"></div></div></td></tr>');
        var itemId = $(this).data("id");
        aucListLoad = $.getScript('assets/js/auctionHouse/autionItem.jsp?id='+itemId, function() {
            var auList = window['auctions_'+ itemId];
            renderAuctions(auList);
        });
    });
}

function renderAuctions(auctionsList)
{
    $("#items_ah_content").html("");
    if(auctionsList.length === 0)
    {
        $("#items_ah_content").append('<tr><td colspan="6">Data not found</td></tr>');
    }
    jQuery.each( auctionsList, function(i, auc) 
    {
        $("#items_ah_content").append
        (
            '<tr>'+
                '<td scope="row">'+
                    ((auc.uniqueGold > 0)? '<span class="moneygold">'+ auc.uniqueGold +'</span>':'')+
                    ((auc.uniqueSilver > 0)? '<span class="moneysilver">'+ auc.uniqueSilver +'</span>':'')+
                    ((auc.uniqueCopper > 0)? '<span class="moneycopper">'+ auc.uniqueCopper +'</span>':'')+
                '</td>'+
                '<td scope="row">'+ auc.stacks +" stack of "+ auc.quantity +"</td>"+
                '<td scope="row">'+
                    ((auc.stackGold > 0)? '<span class="moneygold">'+ auc.stackGold +'</span>':'')+
                    ((auc.stackSilver > 0)? '<span class="moneysilver">'+ auc.stackSilver +'</span>':'')+
                    ((auc.stackCopper > 0)? '<span class="moneycopper">'+ auc.stackCopper +'</span>':'')+
                '</td>'+
            '</tr>'
        );
    });
}