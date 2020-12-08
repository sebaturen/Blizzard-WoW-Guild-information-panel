-- MySQL dump 10.17  Distrib 10.3.22-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: artofdb
-- ------------------------------------------------------
-- Server version	10.3.22-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `achievement_categories`
--

DROP TABLE IF EXISTS `achievement_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `achievement_categories` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `is_guild_category` tinyint(1) DEFAULT NULL,
  `display_order` int(11) DEFAULT NULL,
  `parent_category_id` bigint(20) unsigned DEFAULT NULL,
  `last_modified` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `parent_category_id` (`parent_category_id`),
  CONSTRAINT `achievement_categories_ibfk_1` FOREIGN KEY (`parent_category_id`) REFERENCES `achievement_categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15428 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `achievement_media`
--

DROP TABLE IF EXISTS `achievement_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `achievement_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) COLLATE utf8_bin NOT NULL,
  `value` text COLLATE utf8_bin NOT NULL,
  `last_modified` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14069 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `achievements`
--

DROP TABLE IF EXISTS `achievements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `achievements` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci NOT NULL,
  `category_id` bigint(20) unsigned NOT NULL,
  `reward_description` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `faction_type` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `points` int(11) DEFAULT NULL,
  `media_id` bigint(20) unsigned DEFAULT NULL,
  `display_order` int(11) DEFAULT NULL,
  `is_account_wide` tinyint(1) DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `category_id` (`category_id`),
  KEY `faction_type` (`faction_type`),
  KEY `media_id_2` (`media_id`),
  CONSTRAINT `achievements_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `achievement_categories` (`id`) ON DELETE CASCADE,
  CONSTRAINT `achievements_ibfk_3` FOREIGN KEY (`faction_type`) REFERENCES `static_info` (`type`),
  CONSTRAINT `achievements_ibfk_4` FOREIGN KEY (`media_id`) REFERENCES `achievement_media` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14197 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `boss_list`
--

DROP TABLE IF EXISTS `boss_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `boss_list` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `slug` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `character_info`
--

DROP TABLE IF EXISTS `character_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_info` (
  `character_id` bigint(20) unsigned NOT NULL,
  `class_id` bigint(20) unsigned NOT NULL,
  `race_id` bigint(20) unsigned NOT NULL,
  `gender_type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `level` int(11) NOT NULL,
  `achievement_points` bigint(20) NOT NULL,
  `faction_type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `active_title_id` bigint(20) unsigned DEFAULT NULL,
  `bestMythicPlusScore` text COLLATE utf8_unicode_ci DEFAULT '',
  `mythicPlusScores` text COLLATE utf8_unicode_ci DEFAULT '',
  `guild_id` bigint(20) unsigned DEFAULT NULL,
  `last_login` bigint(20) unsigned DEFAULT NULL,
  `average_item_level` int(11) DEFAULT NULL,
  `equipped_item_level` int(11) DEFAULT NULL,
  PRIMARY KEY (`character_id`),
  KEY `class` (`class_id`),
  KEY `race` (`race_id`),
  KEY `guild_id` (`guild_id`),
  KEY `faction_type` (`faction_type`),
  KEY `active_title` (`active_title_id`),
  CONSTRAINT `character_info_ibfk_10` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `character_info_ibfk_11` FOREIGN KEY (`active_title_id`) REFERENCES `character_titles` (`id`),
  CONSTRAINT `character_info_ibfk_4` FOREIGN KEY (`guild_id`) REFERENCES `guild_info` (`id`),
  CONSTRAINT `character_info_ibfk_6` FOREIGN KEY (`faction_type`) REFERENCES `static_info` (`type`),
  CONSTRAINT `character_info_ibfk_8` FOREIGN KEY (`class_id`) REFERENCES `playable_class` (`id`),
  CONSTRAINT `character_info_ibfk_9` FOREIGN KEY (`race_id`) REFERENCES `playable_races` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `character_items`
--

DROP TABLE IF EXISTS `character_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `character_id` bigint(20) unsigned NOT NULL,
  `slot_type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `item_id` bigint(20) unsigned NOT NULL,
  `quality_type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `level` int(11) DEFAULT NULL,
  `stats` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `armor` int(11) DEFAULT NULL,
  `azerite_level` int(11) DEFAULT NULL,
  `media_id` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `member_id` (`character_id`,`item_id`,`slot_type`),
  KEY `item_id` (`item_id`),
  KEY `slot_type` (`slot_type`),
  KEY `quality_type` (`quality_type`),
  KEY `media_id_2` (`media_id`),
  CONSTRAINT `character_items_ibfk_4` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`),
  CONSTRAINT `character_items_ibfk_6` FOREIGN KEY (`slot_type`) REFERENCES `static_info` (`type`),
  CONSTRAINT `character_items_ibfk_7` FOREIGN KEY (`quality_type`) REFERENCES `static_info` (`type`),
  CONSTRAINT `character_items_ibfk_8` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `character_items_ibfk_9` FOREIGN KEY (`media_id`) REFERENCES `item_media` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=692152 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `character_media`
--

DROP TABLE IF EXISTS `character_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_media` (
  `character_id` bigint(20) unsigned NOT NULL,
  `avatar_url` text COLLATE utf8_bin DEFAULT NULL,
  `bust_url` text COLLATE utf8_bin DEFAULT NULL,
  `render_url` text COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`character_id`),
  UNIQUE KEY `character_id` (`character_id`),
  CONSTRAINT `character_media_ibfk_1` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `character_specs`
--

DROP TABLE IF EXISTS `character_specs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_specs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `character_id` bigint(20) unsigned NOT NULL,
  `specialization_id` bigint(20) unsigned NOT NULL,
  `enable` tinyint(1) NOT NULL,
  `tier_0` bigint(20) unsigned DEFAULT NULL,
  `tier_1` bigint(20) unsigned DEFAULT NULL,
  `tier_2` bigint(20) unsigned DEFAULT NULL,
  `tier_3` bigint(20) unsigned DEFAULT NULL,
  `tier_4` bigint(20) unsigned DEFAULT NULL,
  `tier_5` bigint(20) unsigned DEFAULT NULL,
  `tier_6` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `member_id` (`character_id`,`specialization_id`),
  KEY `spec_id` (`specialization_id`),
  KEY `tier_0` (`tier_0`),
  KEY `tier_1` (`tier_1`),
  KEY `tier_2` (`tier_2`),
  KEY `tier_3` (`tier_3`),
  KEY `tier_4` (`tier_4`),
  KEY `tier_5` (`tier_5`),
  KEY `tier_6` (`tier_6`),
  CONSTRAINT `character_specs_ibfk_11` FOREIGN KEY (`specialization_id`) REFERENCES `playable_spec` (`id`),
  CONSTRAINT `character_specs_ibfk_12` FOREIGN KEY (`tier_0`) REFERENCES `spells` (`id`),
  CONSTRAINT `character_specs_ibfk_15` FOREIGN KEY (`tier_1`) REFERENCES `spells` (`id`),
  CONSTRAINT `character_specs_ibfk_16` FOREIGN KEY (`tier_2`) REFERENCES `spells` (`id`),
  CONSTRAINT `character_specs_ibfk_17` FOREIGN KEY (`tier_3`) REFERENCES `spells` (`id`),
  CONSTRAINT `character_specs_ibfk_18` FOREIGN KEY (`tier_4`) REFERENCES `spells` (`id`),
  CONSTRAINT `character_specs_ibfk_19` FOREIGN KEY (`tier_5`) REFERENCES `spells` (`id`),
  CONSTRAINT `character_specs_ibfk_20` FOREIGN KEY (`tier_6`) REFERENCES `spells` (`id`),
  CONSTRAINT `character_specs_ibfk_21` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=243145 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `character_stats`
--

DROP TABLE IF EXISTS `character_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_stats` (
  `character_id` bigint(20) unsigned NOT NULL,
  `health` int(11) DEFAULT NULL,
  `power` int(11) DEFAULT NULL,
  `power_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `speed` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `strength` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `agility` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `intellect` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `stamina` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `melee` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `mastery` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `bonus_armor` int(11) DEFAULT NULL,
  `lifesteal` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `versatility` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `avoidance` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `attack_power` int(11) DEFAULT NULL,
  `hand` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `spell` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `mana` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `armor` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `dodge` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `parry` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `block` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `ranged` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `corruption` text COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`character_id`),
  KEY `power_type` (`power_type`),
  CONSTRAINT `character_stats_ibfk_2` FOREIGN KEY (`power_type`) REFERENCES `static_info` (`type`),
  CONSTRAINT `character_stats_ibfk_3` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `character_titles`
--

DROP TABLE IF EXISTS `character_titles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `character_titles` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin NOT NULL,
  `gender_name_male` text COLLATE utf8_bin NOT NULL,
  `gender_name_female` text COLLATE utf8_bin NOT NULL,
  `last_modified` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=420 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `characters`
--

DROP TABLE IF EXISTS `characters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `characters` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin NOT NULL,
  `realm_slug` varchar(50) COLLATE utf8_bin NOT NULL,
  `is_valid` tinyint(1) NOT NULL,
  `blizzard_id` bigint(20) unsigned DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  `specializations_last_modified` bigint(20) unsigned DEFAULT NULL,
  `equipment_last_modified` bigint(20) unsigned DEFAULT NULL,
  `statistics_last_modified` bigint(20) unsigned DEFAULT NULL,
  `media_last_modified` bigint(20) unsigned DEFAULT NULL,
  `mythic_plus_last_modified` bigint(20) unsigned DEFAULT NULL,
  `mythic_plus_seasons_last_modified` text COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `realm_slug` (`realm_slug`),
  CONSTRAINT `characters_ibfk_1` FOREIGN KEY (`realm_slug`) REFERENCES `realms` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=223542568 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `creature_media`
--

DROP TABLE IF EXISTS `creature_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creature_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) COLLATE utf8_bin NOT NULL,
  `value` text COLLATE utf8_bin NOT NULL,
  `last_modified` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=94789 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `creatures`
--

DROP TABLE IF EXISTS `creatures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creatures` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin NOT NULL,
  `media_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `media_id` (`media_id`),
  CONSTRAINT `creatures_ibfk_1` FOREIGN KEY (`media_id`) REFERENCES `creature_media` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5134 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `encounters`
--

DROP TABLE IF EXISTS `encounters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `encounters` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin NOT NULL,
  `description` text COLLATE utf8_bin DEFAULT NULL,
  `creatures` text COLLATE utf8_bin DEFAULT NULL,
  `instance_id` bigint(20) unsigned DEFAULT NULL,
  `category` varchar(50) COLLATE utf8_bin NOT NULL,
  `modes` text COLLATE utf8_bin DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `category_type` (`category`),
  KEY `instance_id` (`instance_id`),
  CONSTRAINT `encounters_ibfk_2` FOREIGN KEY (`instance_id`) REFERENCES `instances` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2382 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `desc` text COLLATE utf8_unicode_ci NOT NULL,
  `date` datetime NOT NULL,
  `owner_id` int(11) NOT NULL,
  `min_rank` int(11) DEFAULT NULL,
  `min_level` int(11) DEFAULT NULL,
  `isEnable` tinyint(1) NOT NULL DEFAULT 1,
  `isHide` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `owner_id` (`owner_id`),
  CONSTRAINT `events_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `events_asist`
--

DROP TABLE IF EXISTS `events_asist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events_asist` (
  `id_asis` int(11) NOT NULL AUTO_INCREMENT,
  `id_event` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id_asis`),
  UNIQUE KEY `id_event` (`id_event`,`user_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `events_asist_char`
--

DROP TABLE IF EXISTS `events_asist_char`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events_asist_char` (
  `id_asis_char` int(11) NOT NULL AUTO_INCREMENT,
  `id_asis` int(11) NOT NULL,
  `char_id` int(11) NOT NULL,
  `spec_id` int(11) NOT NULL,
  `is_main` tinyint(1) NOT NULL,
  PRIMARY KEY (`id_asis_char`),
  KEY `id_asis` (`id_asis`),
  KEY `char_id` (`char_id`),
  KEY `spec_id` (`spec_id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `expansion`
--

DROP TABLE IF EXISTS `expansion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `expansion` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin NOT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=397 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guild_achievements`
--

DROP TABLE IF EXISTS `guild_achievements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guild_achievements` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `guild_id` bigint(20) unsigned NOT NULL,
  `achievement_id` bigint(20) unsigned NOT NULL,
  `time_completed` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `guild_id` (`guild_id`),
  KEY `achievement_id` (`achievement_id`),
  CONSTRAINT `guild_achievements_ibfk_2` FOREIGN KEY (`guild_id`) REFERENCES `guild_info` (`id`),
  CONSTRAINT `guild_achievements_ibfk_3` FOREIGN KEY (`achievement_id`) REFERENCES `achievements` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14233 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guild_activities`
--

DROP TABLE IF EXISTS `guild_activities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guild_activities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `guild_id` bigint(20) unsigned NOT NULL,
  `type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `timestamp` bigint(20) unsigned NOT NULL,
  `detail` text COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `guild_id` (`guild_id`),
  CONSTRAINT `guild_activities_ibfk_8` FOREIGN KEY (`guild_id`) REFERENCES `guild_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=167835 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guild_info`
--

DROP TABLE IF EXISTS `guild_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guild_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `realm_id` bigint(20) NOT NULL,
  `faction_type` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `achievement_points` bigint(20) NOT NULL,
  `created_timestamp` bigint(20) NOT NULL,
  `member_count` int(11) NOT NULL,
  `last_modified` bigint(20) NOT NULL,
  `achievement_last_modified` bigint(20) DEFAULT NULL,
  `roster_last_modified` bigint(20) DEFAULT NULL,
  `activities_last_modified` bigint(20) DEFAULT NULL,
  `full_sync` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`,`realm_id`),
  KEY `realm_id` (`realm_id`),
  KEY `faction_type` (`faction_type`),
  CONSTRAINT `guild_info_ibfk_1` FOREIGN KEY (`realm_id`) REFERENCES `realms` (`id`),
  CONSTRAINT `guild_info_ibfk_2` FOREIGN KEY (`faction_type`) REFERENCES `static_info` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=95539215 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guild_raid_dificult_bosses`
--

DROP TABLE IF EXISTS `guild_raid_dificult_bosses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guild_raid_dificult_bosses` (
  `r_d_boss_id` int(11) NOT NULL AUTO_INCREMENT,
  `boss_id` int(11) NOT NULL,
  `difi_id` int(11) NOT NULL,
  `firstDefeated` datetime NOT NULL,
  `itemLevelAvg` double NOT NULL,
  `artifactPowerAvg` double DEFAULT NULL,
  PRIMARY KEY (`r_d_boss_id`),
  UNIQUE KEY `boss_id` (`boss_id`,`difi_id`),
  KEY `difi_id` (`difi_id`),
  CONSTRAINT `guild_raid_dificult_bosses_ibfk_1` FOREIGN KEY (`boss_id`) REFERENCES `boss_list` (`id`),
  CONSTRAINT `guild_raid_dificult_bosses_ibfk_2` FOREIGN KEY (`difi_id`) REFERENCES `guild_raid_dificults` (`difi_id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guild_raid_dificults`
--

DROP TABLE IF EXISTS `guild_raid_dificults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guild_raid_dificults` (
  `difi_id` int(11) NOT NULL AUTO_INCREMENT,
  `raid_id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `rank_world` int(11) DEFAULT NULL,
  `rank_region` int(11) DEFAULT NULL,
  `rank_realm` int(11) DEFAULT NULL,
  PRIMARY KEY (`difi_id`),
  UNIQUE KEY `raid_id` (`raid_id`,`name`),
  CONSTRAINT `guild_raid_dificults_ibfk_1` FOREIGN KEY (`raid_id`) REFERENCES `raids` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guild_rank`
--

DROP TABLE IF EXISTS `guild_rank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guild_rank` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `guild_id` bigint(20) unsigned NOT NULL,
  `rank_lvl` int(11) NOT NULL,
  `title` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `guild_id` (`guild_id`),
  CONSTRAINT `guild_rank_ibfk_1` FOREIGN KEY (`guild_id`) REFERENCES `guild_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4551 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guild_roster`
--

DROP TABLE IF EXISTS `guild_roster`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guild_roster` (
  `character_id` bigint(20) unsigned NOT NULL,
  `guild_id` bigint(20) unsigned NOT NULL,
  `rank_id` bigint(20) unsigned NOT NULL,
  `add_regist` bigint(20) unsigned NOT NULL,
  `current_status` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`character_id`),
  UNIQUE KEY `id` (`character_id`),
  KEY `guild_id` (`guild_id`),
  KEY `rank_id` (`rank_id`),
  CONSTRAINT `guild_roster_ibfk_2` FOREIGN KEY (`guild_id`) REFERENCES `guild_info` (`id`),
  CONSTRAINT `guild_roster_ibfk_3` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `guild_roster_ibfk_4` FOREIGN KEY (`rank_id`) REFERENCES `guild_rank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instance_area`
--

DROP TABLE IF EXISTS `instance_area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instance_area` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5730 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instance_location`
--

DROP TABLE IF EXISTS `instance_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instance_location` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10053 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instance_maps`
--

DROP TABLE IF EXISTS `instance_maps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instance_maps` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2218 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instance_media`
--

DROP TABLE IF EXISTS `instance_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instance_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `value` text COLLATE utf8_bin DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1181 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instances`
--

DROP TABLE IF EXISTS `instances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instances` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text COLLATE utf8_bin NOT NULL,
  `map_id` bigint(20) unsigned DEFAULT NULL,
  `area_id` bigint(20) unsigned DEFAULT NULL,
  `description` text COLLATE utf8_bin DEFAULT NULL,
  `expansion_id` bigint(20) unsigned NOT NULL,
  `location_id` bigint(20) unsigned DEFAULT NULL,
  `modes` text COLLATE utf8_bin NOT NULL,
  `media_id` bigint(20) unsigned NOT NULL,
  `minimum_level` int(11) NOT NULL,
  `category_type` varchar(50) COLLATE utf8_bin NOT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `map_id` (`map_id`),
  KEY `area_id` (`area_id`),
  KEY `expansion_id` (`expansion_id`),
  KEY `location_id` (`location_id`),
  KEY `media_id` (`media_id`),
  CONSTRAINT `instances_ibfk_1` FOREIGN KEY (`map_id`) REFERENCES `instance_maps` (`id`),
  CONSTRAINT `instances_ibfk_2` FOREIGN KEY (`area_id`) REFERENCES `instance_area` (`id`),
  CONSTRAINT `instances_ibfk_3` FOREIGN KEY (`expansion_id`) REFERENCES `expansion` (`id`),
  CONSTRAINT `instances_ibfk_4` FOREIGN KEY (`location_id`) REFERENCES `instance_location` (`id`),
  CONSTRAINT `instances_ibfk_5` FOREIGN KEY (`media_id`) REFERENCES `instance_media` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1181 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item_media`
--

DROP TABLE IF EXISTS `item_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) COLLATE utf8_bin NOT NULL,
  `value` text COLLATE utf8_bin NOT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=175012 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `items` (
  `id` bigint(20) unsigned NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `is_stackable` tinyint(1) DEFAULT NULL,
  `quality_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `required_level` int(11) DEFAULT NULL,
  `media_id` bigint(20) unsigned DEFAULT NULL,
  `inventory_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_equippable` tinyint(1) DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `quality_type` (`quality_type`),
  KEY `inventory_type` (`inventory_type`),
  KEY `media_id_2` (`media_id`),
  CONSTRAINT `items_ibfk_2` FOREIGN KEY (`quality_type`) REFERENCES `static_info` (`type`),
  CONSTRAINT `items_ibfk_3` FOREIGN KEY (`inventory_type`) REFERENCES `static_info` (`type`),
  CONSTRAINT `items_ibfk_4` FOREIGN KEY (`media_id`) REFERENCES `item_media` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keystone_affix_media`
--

DROP TABLE IF EXISTS `keystone_affix_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keystone_affix_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) COLLATE utf8_bin NOT NULL,
  `value` text COLLATE utf8_bin NOT NULL,
  `last_modified` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keystone_affixes`
--

DROP TABLE IF EXISTS `keystone_affixes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keystone_affixes` (
  `id` bigint(20) unsigned NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci NOT NULL,
  `media_id` bigint(20) unsigned DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `media_id_2` (`media_id`),
  CONSTRAINT `keystone_affixes_ibfk_2` FOREIGN KEY (`media_id`) REFERENCES `keystone_affix_media` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keystone_dungeon`
--

DROP TABLE IF EXISTS `keystone_dungeon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keystone_dungeon` (
  `id` bigint(20) unsigned NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `slug` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `keystone_upgrades_1` bigint(20) NOT NULL,
  `keystone_upgrades_2` bigint(20) NOT NULL,
  `keystone_upgrades_3` bigint(20) NOT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keystone_dungeon_run`
--

DROP TABLE IF EXISTS `keystone_dungeon_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keystone_dungeon_run` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `completed_timestamp` bigint(20) NOT NULL,
  `duration` bigint(20) NOT NULL,
  `keystone_level` int(11) NOT NULL,
  `keystone_dungeon_id` bigint(20) unsigned NOT NULL,
  `is_completed_within_time` tinyint(1) NOT NULL,
  `key_affixes` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `completed_timestamp` (`completed_timestamp`,`duration`,`keystone_level`,`keystone_dungeon_id`,`is_completed_within_time`),
  KEY `keystone_dungeon_id` (`keystone_dungeon_id`),
  CONSTRAINT `keystone_dungeon_run_ibfk_1` FOREIGN KEY (`keystone_dungeon_id`) REFERENCES `keystone_dungeon` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10721 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keystone_dungeon_run_members`
--

DROP TABLE IF EXISTS `keystone_dungeon_run_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keystone_dungeon_run_members` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keystone_dungeon_run_id` int(11) NOT NULL,
  `character_id` bigint(20) unsigned NOT NULL,
  `character_spec_id` bigint(20) unsigned NOT NULL,
  `character_item_level` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `character_spec` (`character_spec_id`),
  KEY `character_id` (`character_id`),
  KEY `keystone_dungeon_run_id` (`keystone_dungeon_run_id`),
  CONSTRAINT `keystone_dungeon_run_members_ibfk_7` FOREIGN KEY (`character_spec_id`) REFERENCES `playable_spec` (`id`),
  CONSTRAINT `keystone_dungeon_run_members_ibfk_8` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `keystone_dungeon_run_members_ibfk_9` FOREIGN KEY (`keystone_dungeon_run_id`) REFERENCES `keystone_dungeon_run` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=49329 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keystone_seasons`
--

DROP TABLE IF EXISTS `keystone_seasons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keystone_seasons` (
  `id` bigint(20) unsigned NOT NULL,
  `start_timestamp` bigint(20) unsigned NOT NULL,
  `end_timestamp` bigint(20) unsigned DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `media_assets`
--

DROP TABLE IF EXISTS `media_assets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `media_assets` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `value` text COLLATE utf8_unicode_ci NOT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=288734 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `playable_class`
--

DROP TABLE IF EXISTS `playable_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playable_class` (
  `id` bigint(20) unsigned NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `gender_name_male` text COLLATE utf8_unicode_ci NOT NULL,
  `gender_name_female` text COLLATE utf8_unicode_ci NOT NULL,
  `media_id` bigint(20) unsigned DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `media_id_2` (`media_id`),
  CONSTRAINT `playable_class_ibfk_1` FOREIGN KEY (`media_id`) REFERENCES `playable_class_media` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `playable_class_media`
--

DROP TABLE IF EXISTS `playable_class_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playable_class_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) COLLATE utf8_bin NOT NULL,
  `value` text COLLATE utf8_bin NOT NULL,
  `last_modified` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `playable_races`
--

DROP TABLE IF EXISTS `playable_races`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playable_races` (
  `id` bigint(20) unsigned NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `gender_name_male` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `gender_name_female` text COLLATE utf8_unicode_ci DEFAULT NULL,
  `faction_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_selectable` tinyint(1) DEFAULT NULL,
  `is_allied_race` tinyint(1) DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `faction_type` (`faction_type`),
  CONSTRAINT `playable_races_ibfk_1` FOREIGN KEY (`faction_type`) REFERENCES `static_info` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `playable_spec`
--

DROP TABLE IF EXISTS `playable_spec`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playable_spec` (
  `id` bigint(20) unsigned NOT NULL,
  `playable_class_id` bigint(20) unsigned NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `role_type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `desc_male` text COLLATE utf8_unicode_ci NOT NULL,
  `desc_female` text COLLATE utf8_unicode_ci NOT NULL,
  `media_id` bigint(20) unsigned DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `class` (`playable_class_id`),
  KEY `role_type` (`role_type`),
  KEY `media_id_2` (`media_id`),
  CONSTRAINT `playable_spec_ibfk_1` FOREIGN KEY (`playable_class_id`) REFERENCES `playable_class` (`id`),
  CONSTRAINT `playable_spec_ibfk_3` FOREIGN KEY (`role_type`) REFERENCES `static_info` (`type`),
  CONSTRAINT `playable_spec_ibfk_4` FOREIGN KEY (`media_id`) REFERENCES `playable_spec_media` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `playable_spec_media`
--

DROP TABLE IF EXISTS `playable_spec_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playable_spec_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) COLLATE utf8_bin NOT NULL,
  `value` text COLLATE utf8_bin NOT NULL,
  `last_modified` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=582 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `poll_option_result`
--

DROP TABLE IF EXISTS `poll_option_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `poll_option_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `poll_id` int(11) NOT NULL,
  `poll_option_id` int(11) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `owner_id` (`owner_id`),
  KEY `poll_option_id` (`poll_option_id`),
  KEY `poll_id` (`poll_id`),
  CONSTRAINT `poll_option_result_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `poll_option_result_ibfk_2` FOREIGN KEY (`poll_option_id`) REFERENCES `poll_options` (`id`) ON DELETE CASCADE,
  CONSTRAINT `poll_option_result_ibfk_3` FOREIGN KEY (`poll_id`) REFERENCES `polls` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=846 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `poll_options`
--

DROP TABLE IF EXISTS `poll_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `poll_options` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `poll_id` int(11) NOT NULL,
  `option` tinytext COLLATE utf8_unicode_ci NOT NULL,
  `owner_id` int(11) NOT NULL,
  `create_timestamp` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `poll_id` (`poll_id`),
  KEY `owner_id` (`owner_id`),
  CONSTRAINT `poll_options_ibfk_2` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `poll_options_ibfk_3` FOREIGN KEY (`poll_id`) REFERENCES `polls` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `polls`
--

DROP TABLE IF EXISTS `polls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `polls` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_user_id` int(11) NOT NULL,
  `question` text COLLATE utf8_bin NOT NULL,
  `min_rank` bigint(20) unsigned DEFAULT NULL,
  `multi_select` tinyint(1) NOT NULL,
  `can_add_more_option` tinyint(1) NOT NULL,
  `start_date` bigint(20) NOT NULL,
  `end_date` bigint(20) DEFAULT NULL,
  `is_enabled` tinyint(1) NOT NULL DEFAULT 1,
  `is_hide` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `user_id` (`owner_user_id`),
  KEY `min_rank` (`min_rank`),
  CONSTRAINT `polls_ibfk_1` FOREIGN KEY (`owner_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `polls_ibfk_2` FOREIGN KEY (`min_rank`) REFERENCES `guild_rank` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `raids`
--

DROP TABLE IF EXISTS `raids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `raids` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `slug` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `total_boss` tinyint(4) DEFAULT -1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `realms`
--

DROP TABLE IF EXISTS `realms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `realms` (
  `id` bigint(20) NOT NULL,
  `slug` varchar(50) COLLATE utf8_bin NOT NULL,
  `name` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `locale` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `timezone` varchar(50) COLLATE utf8_bin NOT NULL,
  `type_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_modified` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`),
  KEY `type_type` (`type_type`),
  CONSTRAINT `realms_ibfk_1` FOREIGN KEY (`type_type`) REFERENCES `static_info` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spell_media`
--

DROP TABLE IF EXISTS `spell_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spell_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) COLLATE utf8_bin NOT NULL,
  `value` text COLLATE utf8_bin NOT NULL,
  `last_modified` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=288734 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `spells`
--

DROP TABLE IF EXISTS `spells`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spells` (
  `id` bigint(20) unsigned NOT NULL,
  `name` text COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci NOT NULL,
  `media_id` bigint(20) unsigned DEFAULT NULL,
  `last_modified` bigint(20) unsigned DEFAULT NULL,
  `is_valid` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `media_id_2` (`media_id`),
  CONSTRAINT `spells_ibfk_1` FOREIGN KEY (`media_id`) REFERENCES `spell_media` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `static_info`
--

DROP TABLE IF EXISTS `static_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `static_info` (
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `name` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats`
--

DROP TABLE IF EXISTS `stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stats` (
  `id` int(11) NOT NULL,
  `en_US` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `update_timeline`
--

DROP TABLE IF EXISTS `update_timeline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `update_timeline` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `update_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=108651 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_character`
--

DROP TABLE IF EXISTS `user_character`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_character` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `character_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `character_id` (`character_id`),
  CONSTRAINT `user_character_ibfk_1` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1005 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_vpn`
--

DROP TABLE IF EXISTS `user_vpn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_vpn` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `timestamp` bigint(20) DEFAULT NULL,
  `timestamp_dc` bigint(20) DEFAULT NULL,
  `t_bytes_sent` bigint(20) DEFAULT NULL,
  `t_bytes_recv` bigint(20) DEFAULT NULL,
  `t_packets_sent` bigint(20) DEFAULT NULL,
  `t_packets_recv` bigint(20) DEFAULT NULL,
  `t_errin` bigint(20) DEFAULT NULL,
  `t_errout` bigint(20) DEFAULT NULL,
  `t_dropin` bigint(20) DEFAULT NULL,
  `t_dropout` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `user` (`user_id`),
  CONSTRAINT `user_vpn_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `battle_tag` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `access_token` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `discord_user_id` bigint(20) unsigned DEFAULT NULL,
  `main_character_id` bigint(20) unsigned DEFAULT NULL,
  `vpn_ip` varchar(11) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_guild_member` tinyint(1) NOT NULL DEFAULT 0,
  `guild_rank` int(11) NOT NULL DEFAULT -1,
  `last_login` bigint(20) unsigned DEFAULT NULL,
  `last_alters_update` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `battle_tag_UNIQUE` (`battle_tag`),
  KEY `main_character` (`main_character_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`main_character_id`) REFERENCES `characters` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wow_token`
--

DROP TABLE IF EXISTS `wow_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wow_token` (
  `last_updated_timestamp` bigint(20) NOT NULL,
  `price` bigint(20) NOT NULL,
  PRIMARY KEY (`last_updated_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-08-16 18:01:24
