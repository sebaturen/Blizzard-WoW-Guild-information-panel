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

    //Delte main select
    $("#delete_main").click(function() { clearMain(); });

    //Switch participate
    $("#participate_switch_container").click(function()
    {
        if($("#participate_switch").is(':checked'))
        {
            var confClear = confirm("You want remove all selected?");
            if(confClear)
            {
                //Clear all selected
                $("#alter_table_zone").html("");
                clearMain();
                //Enable all characters
                jQuery.each( $(".character-tab > tbody tr"), function( i, val ) {
                    $(this).attr("class", "user_char");
                });
                $("#participate_switch").prop('checked', false);
                $("#btn_save_inf").attr("disabled", true);
            }
        }
        else
        {
            alert("Drag your character to a section...");
            /*$("#participate_switch").prop('checked', true);
            $("#btn_save_inf").attr("disabled", false);*/
        }
    });

    //Send save info
    $("#btn_save_inf").click(function() {
        if($("#main_zone").data("char-id") > 0 && $("#main_zone").data("spec-id") > 0)
        {
            //Get al alters select detail
            var altersDetail = [];
            jQuery.each( $("#alter_table_zone tr"), function( i, val ) {
                altersDetail[i] = {id: $(this).data("char-id"), spec: $(this).data("spec-id")};
            });
            //Save memver info
            $(".ajaxLoad").show();
            $("#btn_save_inf").attr("disabled", true);
            $.ajax({
                method: "POST",
                url: "userpanel/event_controller.jsp",
                data: { event_action: "addMember",
                        val: {
                            event_id: $("#eventDetail").data("id"),
                            main_id: $("#main_zone").data("char-id"),
                            main_spec: $("#main_zone").data("spec-id"),
                            alters: altersDetail
                        } },
                dataType: "json"
            })
            .done(function(mData) {
                if(mData.status == "ok")
                {
                    console.log("ok recargar (?)");
                }
                else
                {
                    console.log(mData);
                    $("#event_add_result").html("<div class='alert alert-danger' role='alert'>"+ mData.msg +"</div>");
                }
            })
            .fail(function() {
                $("#event_add_result").html("<div class='alert alert-danger' role='alert'>Failed to save a new member information</div>");
            })
            .always(function() {
                $("#event_add_result").show();
                $(".ajaxLoad").hide();
            });
        }
        else
        {
            alert("Set Main character and spec first");
        }
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
        $("#btn_save_inf").attr("disabled", false);

        //----- Enable delete main
        $("#delete_main").show();
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

function clearMain()
{
    $("#char_info_"+ $("#main_zone").data("char-id")).attr("class", "user_char");
    $("#main_zone").attr("data-char-id", 0);
    $("#main_zone").attr("data-spec-id", 0);
    $("#main_name").html("");
    $("#main_lvl").html("");
    $("#main_specs").html("");
    $("#delete_main").hide();
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
                        "<td class='removeCharAlter' data-char_id='"+ charSelectId +"' onclick='removeChar(this)'><i class='fa fa-trash'></i></td>"+
                    "</tr>";
        $("#alter_table_zone").append(charHtmlTable);

        //Disable new alter char
        $("#char_info_"+ charSelectId).attr("class", "user_char_disable");

        //------ Enable participate switch
        $("#participate_switch").prop('checked', true);
        $("#btn_save_inf").attr("disabled", false);
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
