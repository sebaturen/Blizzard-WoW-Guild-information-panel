<h1 id="blizzard-wow-guild-information-panel">Blizzard WoW Guild Information Panel</h1>
<p>This panel was created to center all information of the guild in only one page, guild members, guild challenges, and raid progression.<br>
User system is included, for set polls or more information about the members. (Member details like item level, equipment, etc...).</p>
<h1 id="requirements">Requirements</h1>
<ul>
<li>Tomcat 8+</li>
<li>MariaDB 10+</li>
<li>MariaDBConnector lib in tomcat lib folder.</li>
<li>Blizzard API Client</li>
</ul>
<h1 id="installation">Installation</h1>
<h2 id="prepare-general-configuration">Prepare General Configuration</h2>
<p>Before compile and run this panel, you need edit a <code>WEB-INF/src/GeneralConfig.java</code></p>
<h3 id="first-set-your-guild-information">First, set your guild information:</h3>
<blockquote>
<p>GUILD_NAME<br>
GUILD_REALM<br>
SERVER_LOCATION<br>
LENGUAJE_API_LOCALE<br>
MAIN_URL<br>
BLIZZAR_LINK<br>
(is important the information is exactly!, if have a spaces or is in other lenguaje, use the correct information)</p>
</blockquote>
<h3 id="second-tomcat-information">Second, Tomcat information</h3>
<blockquote>
<p>LOGS_FILE_PATH<br>
LOGS_FILE_USER_OWNER<br>
(if you installed tomcat with a specific user, use this user here, in this way the logs will be saved correctly and tomcat will not have problems to edit the file)</p>
</blockquote>
<h3 id="third-you-need-a-blizzard-api-client">Third, you need a Blizzard API Client</h3>
<p>get your <code>Client ID</code> and <code>Client Secret</code>, can create in: <a href="https://develop.battle.net/access/clients">Blizzard developer portal</a></p>
<blockquote>
<p>CLIENT_ID<br>
CLIENT_SECRET</p>
</blockquote>
<h3 id="fourth-prepare-data-base">Fourth, prepare data base</h3>
<p>is important create user, database and install the DB file, (SQL folder)</p>
<blockquote>
<p>WEB-INF/src/sql/update_timeline.sql<br>
WEB-INF/src/sql/static_content.sql<br>
WEB-INF/src/sql/guild_members_info.sql<br>
<strong>Is important run in this order!</strong></p>
</blockquote>
<p>continue, set a DB information in <code>GeneralConfig</code></p>
<blockquote>
<p>DB_NAME<br>
DB_USER<br>
DB_PASSWORD</p>
</blockquote>
<h3 id="is-time-to-compile">is time to compile!</h3>
<h2 id="get-a-static-content-information">Get a static content information</h2>
<p>Before use, you need a static information in your DB (like playable class, playable roles, achievements list, etc)<br>
for this, you can run <code>WEB-INF/classes/blizzardAPI/UpdateRunningCrontab.class</code> file, or if you Linux use the prepared script <code>WEB-INF/classes/blizzardAPI/updateCrontab.sh</code> first set tomcat folder and blizzardPanel folder</p>
<blockquote>
<p>TOMCAT_WEBAPPS_PATH<br>
TOMCAT_LIB_PATH<br>
BLIZZARD_PANEL_FOLDER<br>
(remember get a run permissions <code>$ chmod +x updateCrontab.sh</code>)</p>
</blockquote>
<p>when the script is ready (or class) use parameter <code>1</code> (get a static information)<br>
<code>$ ./updateCrontab.sh 0</code><br>
the system will start to get the information from blizzard</p>
<blockquote>
<p>or if you want, can run one by one, use parameter:<br>
<code>$ ./updateCrontab.sh 0 [&lt;param&gt;]</code><br>
#Static:<br>
PlayableClass<br>
PlayableSpec<br>
PlayableRaces<br>
GuildAchievementsLists<br>
CharacterAchievementsLists<br>
BossInformation<br>
updateSpellInformation<br>
updateItemInformation</p>
</blockquote>
<h2 id="get-a-dynamic-content-information">Get a Dynamic content information</h2>
<p>Is the same from previews step, only use the parameter <code>0</code> (get a dynamic information)<br>
<code>$ ./updateCrontab.sh 1</code></p>
<blockquote>
<p>or if you want, can run one by one, use parameter:<br>
<code>$ ./updateCrontab.sh 1 [&lt;param&gt;]</code><br>
#Dynamic</p>
<blockquote>
<p>GuildProfile<br>
GuildMembers<br>
CharacterInfo<br>
GuildChallenges<br>
GuildNews<br>
WowToken<br>
UsersCharacters<br>
GuildProgression</p>
</blockquote>
</blockquote>
<p>When all information is correct load, you can see your panel!.<br>
Edit the page style and enjoy :D</p>
<h2 id="recommendation">Recommendation</h2>
<p>Use the last step script and put this in Crontab, from every hour, or how many you want update.<br>
Sample:<br>
<code>#Every hour run dynamic content update</code><br>
<code>0 * * * * root &lt;folder&gt;/WEB-INF/classes/com/blizzardPanel/blizzardAPI/updateCrontab.sh 0</code><br>
<code>#One day in moth update statyc content</code><br>
<code>30 0 * * 1 root &lt;folder&gt;/WEB-INF/classes/com/blizzardPanel/blizzardAPI/updateCrontab.sh 1</code><br>
<code>#Every 10 min search AH update</code><br>
<code>*/10 * * * * root &lt;folder&gt;/WEB-INF/classes/com/blizzardPanel/blizzardAPI/updateCrontab.sh 2</code><br>
<code>#In 12am move AH history</code><br>
<code>2 12 * * * root &lt;folder&gt;/WEB-INF/classes/com/blizzardPanel/blizzardAPI/updateCrontab.sh 3</code></p>

