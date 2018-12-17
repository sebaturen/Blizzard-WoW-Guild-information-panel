---


---

<h1 id="blizzard-wow-guild-information-panel">Blizzard WoW Guild Information Panel</h1>
<p>This panel is create for center all information of the guild in only one page, guild members, guild challenges, and raid progression.<br>
User system is include, for set polls or more information about the members. (member details as item level, equip, etc).</p>
<h1 id="requirements">Requirements</h1>
<ul>
<li>Tomcat 8+</li>
<li>MariaDB 10+</li>
<li>MariaDBConnector lib in tomcat lib folder.</li>
<li>Blizzard API Client</li>
</ul>
<h1 id="installation">Installation</h1>
<h2 id="prepare-general-configuration">Prepare General Configuration</h2>
<p>Before run this panel, you need edit a <code>META-INF/context.xml</code></p>
<h3 id="first-db-configuration">First, DB Configuration</h3>
<p>set a DB information</p>
<blockquote>
<p>url=“jdbc:mysql://127.0.0.1/<code>DATABASE</code>”<br>
username="<code>userDB</code>"<br>
password="<code>passwordDB</code>"<br>
driverClassName="<code>org.mariadb.jdbc.Driver</code>"</p>
</blockquote>
<p>install the DB file, (SQL folder)</p>
<blockquote>
<p>WEB-INF/src/sql/update_timeline.sql<br>
WEB-INF/src/sql/static_content.sql<br>
WEB-INF/src/sql/guild_members_info.sql<br>
<strong>Is important run in this order!</strong></p>
</blockquote>
<h3 id="second-guild-information">Second, Guild information:</h3>
<blockquote>
<p>GUILD_NAME<br>
GUILD_REALM<br>
SERVER_LOCATION<br>
LENGUAJE_API_LOCALE<br>
MAIN_URL<br>
BLIZZAR_LINK<br>
(is important the information is exactly!, if have a spaces or is in other lenguaje, use the correct information)</p>
</blockquote>
<h3 id="third-blizzard-api-client">Third, Blizzard API Client</h3>
<p>get your <code>Client ID</code> and <code>Client Secret</code>, can create in: <a href="https://develop.battle.net/access/clients">Blizzard developer portal</a></p>
<blockquote>
<p>CLIENT_ID<br>
CLIENT_SECRET</p>
</blockquote>
<h3 id="other-options">Other options:</h3>
<p>Interval update. (Automatically the panel update the game and guild information, you can change the time in <code>GeneralConfig.java</code>)</p>
<blockquote>
<p><code>TIME_INTERVAL_DYNAMIC_UPDATE</code> (60 min)<br>
<code>TIME_INTERVAL_STATIC_UPDATE</code> (30 days)<br>
<code>TIME_INTERVAL_GUILD_NEW_UPDATE</code> (10 min)<br>
<code>TIME_INTERVAL_AUCTION_HOUSE_UPDATE</code> (10 min)<br>
Requerid more 0*</p>
</blockquote>
<h2 id="first-run">First run</h2>
<p>In first run, the system get all first need information, this may take a time, see the log file to check the progress:<br>
<code>${catalina.base}/logs/BlizzardPanel.log</code></p>

