/*Load members information!*/
$(document).ready(function() {
    /*Load a member details*/
    $.get("/rest/member/list?locale="+ Cookies.get('locale'), function(data) {
        console.log("members is load complete", data);
        addMember(data);
    });
});


function addMember(members){
    $("#charContent").html("");
    jQuery.each(members, function(i, val) {
        var outForm =
                '<tr class="pjInfo pointer" data-id="'+ i +'" data-internal_id="'+ val.id +'">'+
                    '<td scope="row"><img class="img_profile" src="'+ val.media.avatar +'?alt=/shadow/avatar/2-1.jpg" /></td>'+
                    '<td scope="row" class="character-'+ val.info.class.id +'">'+ val.name +'</td>'+
                    '<td scope="row"><img src="assets/img/classes/class_'+ val.info.class.id +'.png" style="width: 22px;"/></td>'+
                    '<td scope="row">'+ val.info.lvl +'</td>'+
                    '<td scope="row"><img src="assets/img/classes/specs/spec_'+ val.info.class.id +'_'+ val.spec.id +'.png" style="width: 22px;"/> <img src="assets/img/icons/'+ val.spec.rol +'.png" style="width: 22px;"/></td>';
        if(val.info.equip_ilvl > 0) {
            outForm += '<td scope="col">'+ val.info.equip_ilvl +'</td>';
            outForm += '<td scope="col">+ val.hoalvl +</td>';
            outForm += '<td scope="col">+ val.mythicScore +<br>';
            //if(val.bestMythicScore.score != val.mythicScore) {
            //    outForm += '<div class="bestScoreMythc">'+ val.bestMythicScore.score +' - '+ val.bestMythicScore.season +'</div>';
            //}
            outForm += '</td>';
        }
        outForm += '</tr>';
        $("#charContent").append(outForm);
    });
}