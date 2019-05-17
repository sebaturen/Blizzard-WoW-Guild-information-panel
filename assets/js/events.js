$(document).ready(function() {

    //User character drag...
    $(".character-tab").on('mousedown', ".user_char", function() { dragChar(this); });
    $(".user_char").bind('touchstart', function(){ dragChar(this); })

    //User drop in Main zone...
    $("#main_zone").mouseup(function() { dropCharMain(this) });
    //Mose drop in alter zone
    $("#alters_zone").mouseup(function() { dropCharAlter(this) });

    //Mouse release
    $("#ev_container").mouseup(function() {
        dragStar = false;
        $(".item-floting-desc").html();
        $(".item-floting-desc").hide();
    });

});

var dragStar = false;
var charSelectId = 0;
function dragChar(charInfo)
{
    dragStar = true;
    charSelectId = $(charInfo).data("id");
    var nameChar = $(charInfo).data("name");
    var classChar = $(charInfo).data("class");
    var lvlChar = $(charInfo).data("lvl");
    var sepcsChar = [];
    var specCharHtml = "<div class='specs'>";
    var i = 0;
    while($(charInfo).data("spec-"+ i +"-id") != null)
    {
        sepcsChar[i] = {id: $(charInfo).data("spec-"+ i +"-id"), name: ($(charInfo).data("spec-"+ i +"-slug")) };
        specCharHtml += '<img src="assets/img/classes/specs/spec_'+ classChar +'_'+ sepcsChar[i].name +'.png" style="width: 40px;">&nbsp';
        i++;
    }
    specCharHtml += "</div>";
    var htmlPj = "<div>" +
                    "<div class='character-"+ classChar +"'>"+ nameChar +"</div>"+
                    lvlChar +
                    specCharHtml+
                "</div>";
    $(".item-floting-desc").html(htmlPj);
    $(".item-floting-desc").show();
}

function dropCharMain(charInfo)
{
    if(dragStar)
    {
        //change elements in "Main select zone"
        var nameChar = $("#char_info_"+ charSelectId).data("name");
        var classChar = $("#char_info_"+ charSelectId).data("class");
        var lvlChar = $("#char_info_"+ charSelectId).data("lvl");
        $("#main_name").html("<div class='character-"+ classChar +"'>"+ nameChar +"</div>");
        $("#main_lvl").html(lvlChar);
        //Specs:
        var sepcsChar = [];
        var specCharHtml = "<div class='specs'>";
        var i = 0;
        while($("#char_info_"+ charSelectId).data("spec-"+ i +"-id") != null)
        {
            sepcsChar[i] = {id: $("#char_info_"+ charSelectId).data("spec-"+ i +"-id"), name: ($("#char_info_"+ charSelectId).data("spec-"+ i +"-slug")) };
            specCharHtml += '<img class="black_white spec_select" onclick="specSelecMain(this)" data-spec_id="'+ sepcsChar[i].id +'" data-char_id="'+ charSelectId +'" src="assets/img/classes/specs/spec_'+ classChar +'_'+ sepcsChar[i].name +'.png" style="width: 40px;">&nbsp';
            i++;
        }
        specCharHtml += "</div>";
        $("#main_specs").html(specCharHtml);

        //------ Enable old Main Char
        var oldCharSelect = $("#main_zone").data("char-id");
        if(oldCharSelect > 0)
        {
            console.log("old Select> "+ oldCharSelect);
            $("#char_info_"+ $("#main_zone").data("char-id")).attr("class", "user_char");
        }
        $("#main_zone").attr("data-char-id", charSelectId);
        $("#main_zone").attr("data-spec-id", 0);
        $("#main_zone").data("char-id", charSelectId);
        //------ Disable new Main char
        $("#char_info_"+ charSelectId).attr("class", "user_char_disable");

        //------ Enable participate switch
        $("#participate_switch").prop('checked', true);
    }
}

function specSelecMain(imgSpec)
{
    var idSelectedSpec = $(imgSpec).data("spec_id");
    $("#main_zone").attr("data-spec-id", idSelectedSpec);
    jQuery.each( $("#main_specs > .specs img"), function( i, val ) {
        $(this).attr("class", "black_white spec_select");
    });
    $(imgSpec).attr("class", "spec_select");
}

function dropCharAlter(charInfo)
{
    if(dragStar)
    {
        //change elements in "Main select zone"
        var nameChar = $("#char_info_"+ charSelectId).data("name");
        var classChar = $("#char_info_"+ charSelectId).data("class");
        var lvlChar = $("#char_info_"+ charSelectId).data("lvl");
        var charHtmlTable = "<tr id='alter_selec_spec_"+ charSelectId +"' data-char-id='"+ charSelectId +"' data-spec-id='0'>"+
                                "<td class='character-"+ classChar +"'>"+ nameChar +"</td>"+
                                "<td>"+ lvlChar +"</td>"+
                                "<td class='specs'>"; //..continue specs
        //Specs:
        var sepcsChar = [];
        var specCharHtml = "<div class='specs'>";
        var i = 0;
        while($("#char_info_"+ charSelectId).data("spec-"+ i +"-id") != null)
        {
            sepcsChar[i] = {id: $("#char_info_"+ charSelectId).data("spec-"+ i +"-id"), name: ($("#char_info_"+ charSelectId).data("spec-"+ i +"-slug")) };
            specCharHtml += '<img class="black_white spec_select" onclick="specSelecAlter(this)" data-spec_id="'+ sepcsChar[i].id +'" data-char_id="'+ charSelectId +'" src="assets/img/classes/specs/spec_'+ classChar +'_'+ sepcsChar[i].name +'.png" style="width: 22px;">&nbsp';
            i++;
        }
        charHtmlTable += specCharHtml +"</td>"+
                        "<td data-char_id='"+ charSelectId +"' onclick='removeChar(this)'>X</td>"+
                    "</tr>";
        $("#alter_table_zone").append(charHtmlTable);

        //Disable new alter char
        $("#char_info_"+ charSelectId).attr("class", "user_char_disable");

        //------ Enable participate switch
        $("#participate_switch").prop('checked', true);
    }
}

function specSelecAlter(imgSpec)
{
    var idSeledChar = $(imgSpec).data("char_id");
    var idSelectedSpec = $(imgSpec).data("spec_id");
    $("#alter_selec_spec_"+ idSeledChar).attr("data-spec-id", idSelectedSpec);
    jQuery.each( $("#alter_selec_spec_"+ idSeledChar +" > .specs > .specs img"), function( i, val ) {
        $(this).attr("class", "black_white spec_select");
    });
    $(imgSpec).attr("class", "spec_select");
}

function removeChar(charRemove)
{
    var idSeledChar = $(charRemove).data("char_id");
    $("#char_info_"+ idSeledChar).attr("class", "user_char");
    $("#alter_selec_spec_"+ idSeledChar).remove();
}
