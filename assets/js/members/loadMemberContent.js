/*Load members information!*/
$(document).ready(function() {
    /*Load a member details*/
    $.getScript('assets/js/members/membersList.jsp', function() {
        console.log('Members list is load!');
        /*Load render member*/
        $.getScript('assets/js/members/members.js', function() {
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
            console.log('Memer renders function is load!');
        });
    });
});