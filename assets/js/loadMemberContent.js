/*Load members information!*/
$(document).ready(function() {
    /*Load a member details*/
    $.getScript('/assets/js/memberDetail.jsp', function() {
        console.log('Member details is load!');
        /*Load render member*/
        $.getScript('/assets/js/members.js', function() {
            if(moreDetail)
            {
                /*Load filters options~*/
                loadFilterOptions();
                /*Run a clear member contents*/
                runFilter();                
            }
            else
            {
                putMembers(members);
            }
            console.log('Memer renders is load!');
        });
    });
});