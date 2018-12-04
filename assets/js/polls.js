//Send a ajax request to server...
$(document).ready(function() {
    $('.poll_option')
    .mouseover(function() {
        $(this).children(".btn_poll_option").html('<i class="artOfWar-icon">&#xe802;</i>');
    })
    .mouseleave(function() {
        if($(this).children(".btn_poll_option").hasClass("btn-outline-success"))
        {
            $(this).children(".btn_poll_option").html('');
        }
    })
    .click(function() {
        console.log("click!");
        
    });
});