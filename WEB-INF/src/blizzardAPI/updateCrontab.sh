#!/bin/sh
#Run this code every 1h, set in crontab
#all elements is automaticaly update in DB
#
#In centos I use:
#	0 * * * * root /opt/tomcat/artOfWar/ROOT/WEB-INF/src/blizzardAPI/updateCrontab.sh
#

java -cp "/opt/tomcat/artOfWar/ROOT/WEB-INF/classes:/opt/tomcat/artOfWar/ROOT/WEB-INF/lib/json-simple-1.1.1.jar:/opt/tomcat/lib/mariadb-java-client-2.3.0.jar" \
    com.artOfWar.blizzardAPI.UpdateRunningCrontab &>> /opt/tomcat/logs/artOfWarUpdateLog.log
