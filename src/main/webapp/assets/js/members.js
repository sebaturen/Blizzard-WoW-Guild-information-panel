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
    let fullLoad = ($("#fullLoad").length != 0);
    jQuery.each(members, function(i, val) {
        let outForm = (fullLoad)? '<a href="?id='+ val.id +'">':'';
         outForm +=
                '<div class="row pjInfo pointer" data-id="'+ val.id +'">'+
                    '<div class="col"><img class="img_profile" src="'+ val.media.avatar +'?alt=/shadow/avatar/2-1.jpg" /></div>'+
                    '<div class="col character-'+ val.info.class.id +'">'+ val.name +'</div>'+
                    '<div class="col"><img src="assets/img/classes/class_'+ val.info.class.id +'.png" style="width: 22px;"/></div>'+
                    '<div class="col">'+ val.info.lvl +'</div>'+
                    '<div class="col"><img src="assets/img/classes/specs/spec_'+ val.info.class.id +'_'+ val.spec.id +'.png" style="width: 22px;"/> <img src="assets/img/icons/'+ val.spec.rol +'.png" style="width: 22px;"/></div>';
        if(typeof val.info.equip_lvl != 'undefined') {

            // Equip LVL
            outForm += '<div class="col">'+ val.info.avg_lvl +'<br>';
            if (val.info.equip_lvl != val.info.avg_lvl) {
                outForm += '<div class="bestScoreMythc">'+ val.info.equip_lvl +'</div></div>';
            } else {
                outForm += '</div>';
            }

            // HOA lvl
            outForm += '<div class="col">';
            if (typeof val.info.hoa_lvl != 'undefined') {
                outForm += val.info.hoa_lvl;
            }
            outForm += '</div>';

            // Raider IO
            outForm += '<div class="col">';
            if (typeof val.info.mythicScore != 'undefined' && val.info.mythicScore.all > 0) {
                outForm += parseInt(val.info.mythicScore.all) +'<br>';
            }
            if (typeof val.info.bestMythicScore != 'undefined' && val.info.bestMythicScore.score != val.info.mythicScore.all) {
                outForm += '<div class="bestScoreMythc" style="color: '+ val.info.bestMythicScore.scoreColor +'">'+ parseInt(val.info.bestMythicScore.score) +' - '+ val.info.bestMythicScore.season.name +'</div>';
            }
            outForm += '</div>';
        }
        outForm += '</div>';
        outForm += (fullLoad)? '</a>':'';
        $("#charContent").append(outForm);
    });
}

function memberDetail(memberId) {

}