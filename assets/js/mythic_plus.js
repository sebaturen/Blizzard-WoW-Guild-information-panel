//Show key affixes detail
$(document).ready(function() {
   $(".key_affix_img")
    .mouseover(function() {
        $("#afix_name").text($(this).data("name"));
        $("#afix_desc").text($(this).data("desc"));
        $(".tooltip-affix").show();        
    })
    .mouseleave(function() {
        $(".tooltip-affix").hide();
    });
});