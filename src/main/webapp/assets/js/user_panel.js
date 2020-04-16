$(document).ready(function() {
    $(document).on("click", ".main_char", function() {
        $(".ajaxLoad").show();
        let currentChar = $(this);
        event.preventDefault();
        console.log("main!", currentChar.data("member_id"));
        $.post("rest/user/"+ $(this).data("member_id"), function() {
           $(".main_char").html("&#xe800;");
           currentChar.html("&#xe801;");
        }).always(function() {
            $(".ajaxLoad").hide();
        });
    });
});