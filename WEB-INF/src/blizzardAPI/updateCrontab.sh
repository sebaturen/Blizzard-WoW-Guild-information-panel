#!/bin/sh
#Run this code every 1h, set in crontab
#all elements is automaticaly update in DB
#
#In centos I use:
#	0 * * * * root /opt/tomcat/artOfWar/ROOT/WEB-INF/src/blizzardAPI/updateCrontab.sh 0
#                                                                                         ^
#                                                                      0: Dynamic; 1: Static
#

TOMCAT_WEBAPPS_PATH="/opt/tomcat/artOfWar/"
TOMCAT_LIB_PATH="/opt/tomcat/lib/"
BLIZZARD_PANEL_FOLDER="ROOT"


java -cp $TOMCAT_WEBAPPS_PATH$BLIZZARD_PANEL_FOLDER"/WEB-INF/classes:"\
$TOMCAT_WEBAPPS_PATH$BLIZZARD_PANEL_FOLDER"/WEB-INF/lib/json-simple-1.1.1.jar:"\
$TOMCAT_LIB_PATH"mariadb-java-client-2.3.0.jar"\
    com.blizzardPanel.blizzardAPI.UpdateRunningCrontab $@