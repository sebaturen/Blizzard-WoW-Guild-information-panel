/*Auction house*/
$(document).ready(function() {    
    $('#itemName').keyup(function () { searchItem($(this).val()); });
});

function searchItem(itemName) 
{
    if(itemName.length > 3)
    {
        console.log(itemName);
        $.getScript('/assets/js/autionItem.jsp?name='+ encodeURIComponent(itemName), function() {
            renderItemList();
        });        
    }
}

function renderItemList()
{
    $("#items_ah_content").html("");
    jQuery.each( items, function(auc, itemInfo) 
    {
        $("#items_ah_content").append(
                '<tr id="'+ auc +'">'+
                    '<td scope="row"><img src="'+ itemInfo.itemImg +'" class="AH_item_icon"></td>'+
                    '<td scope="row">'+ itemInfo.itemName +'</td>'+
                    '<td scope="row">'+ itemInfo.quantity +'</td>'+
                    '<td scope="row">'+ 
                        ((itemInfo.buyGold > 0)? '<span class="moneygold">'+ itemInfo.buyGold +'</span>':'')+
                        ((itemInfo.buySilver > 0)? '<span class="moneysilver">'+ itemInfo.buySilver +'</span>':'')+
                        ((itemInfo.buyCopper > 0)? '<span class="moneycopper">'+ itemInfo.buyCopper +'</span>':'')+
                    '</td>'+
                    '<td scope="row">'+ 
                        ((itemInfo.pushGold > 0)? '<span class="moneygold">'+ itemInfo.pushGold +'</span>':'')+
                        ((itemInfo.pushSilver > 0)? '<span class="moneysilver">'+ itemInfo.pushSilver +'</span>':'')+
                        ((itemInfo.pushCopper > 0)? '<span class="moneycopper">'+ itemInfo.pushCopper +'</span>':'')+                        
                    '</td>'+
                    '<td scope="row">'+ itemInfo.owner +'</td>'+
                    '<td scope="row">'+ itemInfo.timeLef +'</td>'+
                '</tr>');
    });
}