//Show key affixes detail
$(document).ready(function() {
    
    /*Load a keystone runs */
    $.getScript('assets/js/mythicPlus/mythic_plus_list.jsp', function() {
        console.log('Keys run list is load!');
        weekRun(keystone_run);
        bestRun(keystone_best_run);
        $("#afixLoad").hide();
    });
    
    /*Mose over and leave in affix detail*/
    $('#runList')
    .on('mouseover', '.key_affix_img', function() {
        $("#afix_name").text($(this).data("name"));
        $("#afix_desc").text($(this).data("desc"));
        $(".tooltip-affix").show();          
    })
    .on('mouseleave', '.key_affix_img', function() {
        $(".tooltip-affix").hide();     
    });
    
    /*
     * keystone_run.forEach(function(run) {
            if(run.upgrade_key == -1 && run.key_lvl < 10) {
                    tempRun.push(run);
            }
            });
            renderRuns(tempRun);
     */
});

function weekRun(keyRuns)
{    
    $("#runList").html("<h1 class='key_divide_title'>Runs of the week</h1>");
    $("#runList").append(renderRuns(keyRuns));
    $("#runList").show();
}

function bestRun(keyRuns)
{    
    $("#bestRun").html("<h1 class='key_divide_title'>Best Runs</h1>");
    $("#bestRun").append(renderRuns(keyRuns));
    $("#bestRun").show();
}

function renderRuns(keyRuns)
{
    var out = '';
    jQuery.each( keyRuns, function(i, keyRun) 
    {    
        i = parseInt(i);
        if(keyRun !== undefined )
        {
            if (i === 0) { out += '<div class="row ">'; } else if (i%3 === 0) { out += '</div><div class="row">'; }
            out +=
              "<div id='key_run_"+ keyRun.run_id +"' class='key_run_group dungeon-challenge col'>"+
                  "<div class='key_run_dun_img dungeon-challenge-img' style='background-image: url(\"assets/img/dungeon/"+ keyRun.map_id +".jpg\");'>"+
                      "<div class='key_run_lvl'>"+ keyRun.key_lvl +"</div>"+
                      "<h2 class='dung-title'>"+ keyRun.map_name +"</h2>"+
                  "</div>"+            
                  "<p class='group-time key-"+ keyRun.up_down +"'>["+ keyRun.duration_h +"h:"+ keyRun.duration_m +"m:"+ keyRun.duration_s +"s]"+ ((keyRun.upgrade_key > 0)? " (+"+ keyRun.upgrade_key +")":"") +"</p>"+
                  "<p class='key-date'>"+ keyRun.complete_date +"</p>"+
                  "<table class='table table-dark character-tab'>"+
                      "<thead>"+
                          "<tr>"+
                              "<th scope='col'>Name</th>"+
                              "<th scope='col'>Role</th>"+
                              "<th scope='col'>iLevel</th>"+
                          "</tr>"+
                      "</thead>"+
                      "<tbody>";
            jQuery.each( keyRun.mem, function(j, mem) 
            {            
                var isMain = ((mem.is_main == 'true')? "<i class='main_char artOfWar-icon'>&#xe801;</i>":"");
                out +=
                            "<tr>"+
                                "<td class='character-"+ mem.class_name +"'>"+ isMain +" "+ mem.name +"</td>"+
                                "<td>"+
                                    "<img src='assets/img/icons/"+ mem.rol +".png' style='width: 22px;'/>"+
                                    "<img src='assets/img/classes/specs/spec_"+ mem.class_name +"_"+ mem.spec_name +".png' style='width: 22px;'/>"+
                                "</td>"+
                                "<td>"+ mem.i_level +"</td>"+
                            "</tr>";
            });            
            out +=          "<tr>"+
                                "<td colspan='3' class='key_affixes'>";                                
            jQuery.each( keyRun.affix, function(k, affix)
            {
                out +=              "<img class='key_affix_img' src='"+ keystone_affixes[affix].icon_url +"' data-name='"+ keystone_affixes[affix].name +"' data-desc=\""+ keystone_affixes[affix].desc +"\">";
            });
            out +=              "</td>"+
                            "</tr>"+
                        "</tbody>"+
                    "</table>"+
                "</div>";  
        }
    });
    out += "</div>"; //<!-- close last 'i' div open -->
    
    return out;
}