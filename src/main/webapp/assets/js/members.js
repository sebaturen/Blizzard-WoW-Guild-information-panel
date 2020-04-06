/*Load members information!*/
$(document).ready(function() {
    /*Load a member details*/
    $.get("rest/member/list?locale="+ Cookies.get('locale'), function(data) {
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
        if(typeof val.info.equip_lvl != 'undefined') {

            // Equip LVL
            outForm += '<td scope="col">'+ val.info.avg_lvl +'<br>';
            if (val.info.equip_lvl != val.info.avg_lvl) {
                outForm += '<div class="bestScoreMythc">'+ val.info.equip_lvl +'</div></td>';
            }

            // HOA lvl
            outForm += '<td scope="col">';
            if (typeof val.info.hoa_lvl != 'undefined') {
                outForm += val.info.hoa_lvl;
            }
            outForm += '</td>';

            // Raider IO
            outForm += '<td scope="col">';
            if (typeof val.info.mythicScore != 'undefined' && val.info.mythicScore.all > 0) {
                outForm += parseInt(val.info.mythicScore.all) +'<br>';
            }
            if (typeof val.info.bestMythicScore != 'undefined' && val.info.bestMythicScore.score != val.info.mythicScore.all) {
                outForm += '<div class="bestScoreMythc" style="color: '+ val.info.bestMythicScore.scoreColor +'">'+ parseInt(val.info.bestMythicScore.score) +' - '+ val.info.bestMythicScore.season.name +'</div>';
            }
            outForm += '</td>';
        }
        outForm += '</tr>';
        $("#charContent").append(outForm);
    });
}