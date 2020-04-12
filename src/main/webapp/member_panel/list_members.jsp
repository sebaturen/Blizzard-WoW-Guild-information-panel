<%@include file="../includes/globalObject.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="es">
    <head>
        <title>${guild.name} - <fmt:message key="label.member_list" /></title>
        <%@include file="../includes/header.jsp" %>
        <link type="text/css" rel="stylesheet" href="assets/css/members.css">
        <script src="assets/js/members.js"></script>
    </head>
    <body style="background-color: #010424;">
        <%@include file="../includes/menu.jsp" %>
        <div class="container">
            <div id="character-content">
                <% if(guildMember) { %>
                <div id="fullLoad"></div>
                <button type="button" class="btn btn-info" id='membersFilters' style="display: none;"><fmt:message key="label.filters" /></button>
                <div style="display: none;" id='formFilter'>
                    <br>
                    <div class="row">
                        <div class="col">
                            <div class="form-group">
                                <label><fmt:message key="label.name" /></label>
                                <input class="form-control" type="text" value="" id="nameInput"/>
                            </div>
                            <div class="form-group">
                                <label><fmt:message key="label.guild_rank" /></label>
                                <select class="form-control" id='guildRankSelect'>
                                    <option value="-1"><fmt:message key="label.all" /></option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label><fmt:message key="label.class" /></label>
                                <select class="form-control" id='classSelect'>
                                    <option value="-1"><fmt:message key="label.all" /></option>
                                </select>
                            </div>
                        </div>
                        <div class='col'>
                            <div class="form-group">
                                <label><fmt:message key="label.race" /></label>
                                <select class="form-control" id='racesSelect'>
                                    <option value="-1"><fmt:message key="label.all" /></option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label><fmt:message key="label.level" /></label>
                                <div class="row">
                                    <div class='col'>
                                        <select class="form-control" id="levelSelect">
                                            <option value="-1"><fmt:message key="label.all" /></option>
                                            <option value="1"><fmt:message key="label.greater_then" /></option>
                                            <option value="2"><fmt:message key="label.less_then" /></option>
                                        </select>
                                    </div>
                                    <div class='col'>
                                        <input class="vInput form-control" type="number" value="" id="levelInput" disabled/>
                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label><fmt:message key="label.item_level" /></label>
                                <div class='row'>
                                    <div class="col">
                                        <select class="form-control" id='ilevelSelect'>
                                            <option value="-1"><fmt:message key="label.all" /></option>
                                            <option value="1"><fmt:message key="label.greater_then" /></option>
                                            <option value="2"><fmt:message key="label.less_then" /></option>
                                        </select>
                                    </div>
                                    <div class="col">
                                        <input class="vInput form-control" type="number" value="" id="ilevelInput" disabled/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
                <div class="table table-dark character-tab">
                    <div class="row pjInfo">
                        <div class="col pointer" id="rankColum">#</div>
                        <div class="col pointer" id="nameColum"><fmt:message key="label.name" /></div>
                        <div class="col pointer" id="classColum"><fmt:message key="label.class" /></div>
                        <div class="col pointer" id="levelColum"><fmt:message key="label.level" /></div>
                        <div class="col pointer d-none d-md-block" id="specColum"><fmt:message key="label.current_spec" /></div>
                        <% if(guildMember) { %>
                            <div class="col d-none d-md-block pointer" id="iLevelColum"><fmt:message key="label.ilvl" /></div>
                            <div class="col d-none d-md-block pointer" id="hoalvl">HoA Lvl</div>
                            <div class="col d-none d-md-block pointer" id="ioScore">Raider.IO</div>
                        <% } %>
                    </div>
                    <div id="charContent">
                        <div class="row justify-content-md-center"><div class="loader"></div></div>
                    </div>
                </div>
            </div>
        </div>
        <%@include file="../includes/footer.jsp" %>
    </body>
</html>
