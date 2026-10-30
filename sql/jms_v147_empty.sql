-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- ホスト: 127.0.0.1:3306
-- 生成日時: 2026-06-16 16:27:59
-- サーバのバージョン： 8.4.7
-- PHP のバージョン: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- データベース: `jms_v147`
--

-- --------------------------------------------------------

--
-- テーブルの構造 `accounts`
--

DROP TABLE IF EXISTS `accounts`;
CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(13) NOT NULL DEFAULT '',
  `password` varchar(128) NOT NULL DEFAULT '',
  `salt` varchar(32) DEFAULT NULL,
  `2ndpassword` varchar(134) DEFAULT NULL,
  `salt2` varchar(32) DEFAULT NULL,
  `loggedin` tinyint UNSIGNED NOT NULL DEFAULT '0',
  `lastlogin` timestamp NULL DEFAULT NULL,
  `createdat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `birthday` date NOT NULL DEFAULT '0000-00-00',
  `banned` tinyint(1) NOT NULL DEFAULT '0',
  `banreason` text,
  `gm` tinyint(1) NOT NULL DEFAULT '0',
  `email` tinytext,
  `macs` tinytext,
  `tempban` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `greason` tinyint UNSIGNED DEFAULT NULL,
  `ACash` int DEFAULT NULL,
  `mPoints` int DEFAULT NULL,
  `gender` tinyint UNSIGNED NOT NULL DEFAULT '0',
  `SessionIP` varchar(64) DEFAULT NULL,
  `points` int NOT NULL DEFAULT '0',
  `vpoints` int NOT NULL DEFAULT '0',
  `monthvotes` int NOT NULL DEFAULT '0',
  `totalvotes` int NOT NULL DEFAULT '0',
  `lastvote` int NOT NULL DEFAULT '0',
  `lastvote2` int NOT NULL DEFAULT '0',
  `lastlogon` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `ranking1` (`id`,`banned`,`gm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `achievements`
--

DROP TABLE IF EXISTS `achievements`;
CREATE TABLE IF NOT EXISTS `achievements` (
  `achievementid` int NOT NULL DEFAULT '0',
  `charid` int NOT NULL DEFAULT '0',
  `accountid` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`achievementid`,`charid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `alliances`
--

DROP TABLE IF EXISTS `alliances`;
CREATE TABLE IF NOT EXISTS `alliances` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(13) NOT NULL,
  `leaderid` int NOT NULL,
  `guild1` int NOT NULL,
  `guild2` int NOT NULL,
  `guild3` int NOT NULL DEFAULT '0',
  `guild4` int NOT NULL DEFAULT '0',
  `guild5` int NOT NULL DEFAULT '0',
  `rank1` varchar(13) NOT NULL DEFAULT 'Master',
  `rank2` varchar(13) NOT NULL DEFAULT 'Jr.Master',
  `rank3` varchar(13) NOT NULL DEFAULT 'Member',
  `rank4` varchar(13) NOT NULL DEFAULT 'Member',
  `rank5` varchar(13) NOT NULL DEFAULT 'Member',
  `capacity` int NOT NULL DEFAULT '2',
  `notice` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `bbs_replies`
--

DROP TABLE IF EXISTS `bbs_replies`;
CREATE TABLE IF NOT EXISTS `bbs_replies` (
  `replyid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `threadid` int UNSIGNED NOT NULL,
  `postercid` int UNSIGNED NOT NULL,
  `timestamp` bigint UNSIGNED NOT NULL,
  `content` varchar(26) NOT NULL DEFAULT '',
  `guildid` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`replyid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `bbs_threads`
--

DROP TABLE IF EXISTS `bbs_threads`;
CREATE TABLE IF NOT EXISTS `bbs_threads` (
  `threadid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `postercid` int UNSIGNED NOT NULL,
  `name` varchar(26) NOT NULL DEFAULT '',
  `timestamp` bigint UNSIGNED NOT NULL,
  `icon` smallint UNSIGNED NOT NULL,
  `startpost` text NOT NULL,
  `guildid` int UNSIGNED NOT NULL,
  `localthreadid` int UNSIGNED NOT NULL,
  PRIMARY KEY (`threadid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `buddies`
--

DROP TABLE IF EXISTS `buddies`;
CREATE TABLE IF NOT EXISTS `buddies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `characterid` int NOT NULL,
  `buddyid` int NOT NULL,
  `pending` tinyint NOT NULL DEFAULT '0',
  `groupname` varchar(16) NOT NULL DEFAULT 'ETC',
  PRIMARY KEY (`id`),
  KEY `buddies_ibfk_1` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `cashshop_modified_items`
--

DROP TABLE IF EXISTS `cashshop_modified_items`;
CREATE TABLE IF NOT EXISTS `cashshop_modified_items` (
  `serial` int NOT NULL,
  `discount_price` int NOT NULL DEFAULT '-1',
  `mark` tinyint(1) NOT NULL DEFAULT '-1',
  `showup` tinyint(1) NOT NULL DEFAULT '0',
  `itemid` int NOT NULL DEFAULT '0',
  `priority` tinyint NOT NULL DEFAULT '0',
  `package` tinyint(1) NOT NULL DEFAULT '0',
  `period` tinyint NOT NULL DEFAULT '0',
  `gender` tinyint(1) NOT NULL DEFAULT '0',
  `count` tinyint NOT NULL DEFAULT '0',
  `meso` int NOT NULL DEFAULT '0',
  `unk_1` tinyint(1) NOT NULL DEFAULT '0',
  `unk_2` tinyint(1) NOT NULL DEFAULT '0',
  `unk_3` tinyint(1) NOT NULL DEFAULT '0',
  `extra_flags` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`serial`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `characters`
--

DROP TABLE IF EXISTS `characters`;
CREATE TABLE IF NOT EXISTS `characters` (
  `id` int NOT NULL AUTO_INCREMENT,
  `accountid` int NOT NULL DEFAULT '0',
  `world` tinyint(1) NOT NULL DEFAULT '0',
  `name` varchar(13) NOT NULL DEFAULT '',
  `level` int NOT NULL DEFAULT '0',
  `exp` int NOT NULL DEFAULT '0',
  `str` int NOT NULL DEFAULT '0',
  `dex` int NOT NULL DEFAULT '0',
  `luk` int NOT NULL DEFAULT '0',
  `int` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `mp` int NOT NULL DEFAULT '0',
  `maxhp` int NOT NULL DEFAULT '0',
  `maxmp` int NOT NULL DEFAULT '0',
  `meso` int NOT NULL DEFAULT '0',
  `tama` int NOT NULL DEFAULT '0',
  `hpApUsed` int NOT NULL DEFAULT '0',
  `job` int NOT NULL DEFAULT '0',
  `skincolor` tinyint(1) NOT NULL DEFAULT '0',
  `gender` tinyint(1) NOT NULL DEFAULT '0',
  `fame` int NOT NULL DEFAULT '0',
  `hair` int NOT NULL DEFAULT '0',
  `face` int NOT NULL DEFAULT '0',
  `ap` int NOT NULL DEFAULT '0',
  `map` int NOT NULL DEFAULT '0',
  `spawnpoint` int NOT NULL DEFAULT '0',
  `gm` int NOT NULL DEFAULT '0',
  `party` int NOT NULL DEFAULT '0',
  `buddyCapacity` int NOT NULL DEFAULT '25',
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `guildid` int UNSIGNED NOT NULL DEFAULT '0',
  `guildrank` tinyint UNSIGNED NOT NULL DEFAULT '5',
  `allianceRank` tinyint UNSIGNED NOT NULL DEFAULT '5',
  `monsterbookcover` int UNSIGNED NOT NULL DEFAULT '0',
  `dojo_pts` int UNSIGNED NOT NULL DEFAULT '0',
  `dojoRecord` tinyint UNSIGNED NOT NULL DEFAULT '0',
  `pets` varchar(13) NOT NULL DEFAULT '-1,-1,-1',
  `sp` varchar(255) NOT NULL DEFAULT '0,0,0,0,0,0,0,0,0,0',
  `subcategory` int NOT NULL DEFAULT '0',
  `Jaguar` int NOT NULL DEFAULT '0',
  `rank` int NOT NULL DEFAULT '1',
  `rankMove` int NOT NULL DEFAULT '0',
  `jobRank` int NOT NULL DEFAULT '1',
  `jobRankMove` int NOT NULL DEFAULT '0',
  `marriageId` int NOT NULL DEFAULT '0',
  `familyid` int NOT NULL DEFAULT '0',
  `seniorid` int NOT NULL DEFAULT '0',
  `junior1` int NOT NULL DEFAULT '0',
  `junior2` int NOT NULL DEFAULT '0',
  `currentrep` int NOT NULL DEFAULT '0',
  `totalrep` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `accountid` (`accountid`),
  KEY `party` (`party`),
  KEY `ranking1` (`level`,`exp`),
  KEY `ranking2` (`gm`,`job`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `character_slots`
--

DROP TABLE IF EXISTS `character_slots`;
CREATE TABLE IF NOT EXISTS `character_slots` (
  `id` int NOT NULL AUTO_INCREMENT,
  `accid` int NOT NULL DEFAULT '0',
  `worldid` int NOT NULL DEFAULT '0',
  `charslots` int NOT NULL DEFAULT '6',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `csequipment`
--

DROP TABLE IF EXISTS `csequipment`;
CREATE TABLE IF NOT EXISTS `csequipment` (
  `inventoryequipmentid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int UNSIGNED NOT NULL DEFAULT '0',
  `upgradeslots` int NOT NULL DEFAULT '0',
  `level` int NOT NULL DEFAULT '0',
  `str` int NOT NULL DEFAULT '0',
  `dex` int NOT NULL DEFAULT '0',
  `int` int NOT NULL DEFAULT '0',
  `luk` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `mp` int NOT NULL DEFAULT '0',
  `watk` int NOT NULL DEFAULT '0',
  `matk` int NOT NULL DEFAULT '0',
  `wdef` int NOT NULL DEFAULT '0',
  `mdef` int NOT NULL DEFAULT '0',
  `acc` int NOT NULL DEFAULT '0',
  `avoid` int NOT NULL DEFAULT '0',
  `hands` int NOT NULL DEFAULT '0',
  `speed` int NOT NULL DEFAULT '0',
  `jump` int NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint NOT NULL DEFAULT '0',
  `growthLevelUpType` int NOT NULL DEFAULT '0',
  `growthLevel` int NOT NULL DEFAULT '0',
  `growthEXP` int NOT NULL DEFAULT '0',
  `durability` int NOT NULL DEFAULT '-1',
  `enhance` tinyint NOT NULL DEFAULT '0',
  `rank` int NOT NULL DEFAULT '0',
  `hidden` int NOT NULL DEFAULT '0',
  `potential1` smallint NOT NULL DEFAULT '0',
  `potential2` smallint NOT NULL DEFAULT '0',
  `potential3` smallint NOT NULL DEFAULT '0',
  `socket1` smallint NOT NULL DEFAULT '0',
  `socket2` smallint NOT NULL DEFAULT '0',
  `hpR` smallint NOT NULL DEFAULT '0',
  `mpR` smallint NOT NULL DEFAULT '0',
  `incattackSpeed` int NOT NULL DEFAULT '0',
  `tuc` int NOT NULL DEFAULT '0',
  `ViciousHammerMax` int NOT NULL DEFAULT '0',
  `potentialCount` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `csitems`
--

DROP TABLE IF EXISTS `csitems`;
CREATE TABLE IF NOT EXISTS `csitems` (
  `inventoryitemid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int DEFAULT NULL,
  `accountid` int DEFAULT NULL,
  `packageid` int DEFAULT NULL,
  `itemid` int NOT NULL DEFAULT '0',
  `inventorytype` int NOT NULL DEFAULT '0',
  `position` int NOT NULL DEFAULT '0',
  `quantity` int NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int NOT NULL DEFAULT '-1',
  `flag` int NOT NULL DEFAULT '0',
  `expiredate` bigint NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `drop_data`
--

DROP TABLE IF EXISTS `drop_data`;
CREATE TABLE IF NOT EXISTS `drop_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dropperid` int NOT NULL,
  `itemid` int NOT NULL DEFAULT '0',
  `minimum_quantity` int NOT NULL DEFAULT '1',
  `maximum_quantity` int NOT NULL DEFAULT '1',
  `questid` int NOT NULL DEFAULT '0',
  `chance` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `mobid` (`dropperid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `dueyequipment`
--

DROP TABLE IF EXISTS `dueyequipment`;
CREATE TABLE IF NOT EXISTS `dueyequipment` (
  `inventoryequipmentid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int UNSIGNED NOT NULL DEFAULT '0',
  `upgradeslots` int NOT NULL DEFAULT '0',
  `level` int NOT NULL DEFAULT '0',
  `str` int NOT NULL DEFAULT '0',
  `dex` int NOT NULL DEFAULT '0',
  `int` int NOT NULL DEFAULT '0',
  `luk` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `mp` int NOT NULL DEFAULT '0',
  `watk` int NOT NULL DEFAULT '0',
  `matk` int NOT NULL DEFAULT '0',
  `wdef` int NOT NULL DEFAULT '0',
  `mdef` int NOT NULL DEFAULT '0',
  `acc` int NOT NULL DEFAULT '0',
  `avoid` int NOT NULL DEFAULT '0',
  `hands` int NOT NULL DEFAULT '0',
  `speed` int NOT NULL DEFAULT '0',
  `jump` int NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint NOT NULL DEFAULT '0',
  `growthLevelUpType` int NOT NULL DEFAULT '0',
  `growthLevel` int NOT NULL DEFAULT '0',
  `growthEXP` int NOT NULL DEFAULT '0',
  `durability` int NOT NULL DEFAULT '-1',
  `enhance` tinyint NOT NULL DEFAULT '0',
  `rank` int NOT NULL DEFAULT '0',
  `hidden` int NOT NULL DEFAULT '0',
  `potential1` smallint NOT NULL DEFAULT '0',
  `potential2` smallint NOT NULL DEFAULT '0',
  `potential3` smallint NOT NULL DEFAULT '0',
  `socket1` smallint NOT NULL DEFAULT '0',
  `socket2` smallint NOT NULL DEFAULT '0',
  `hpR` smallint NOT NULL DEFAULT '0',
  `mpR` smallint NOT NULL DEFAULT '0',
  `incattackSpeed` int NOT NULL DEFAULT '0',
  `tuc` int NOT NULL DEFAULT '0',
  `ViciousHammerMax` int NOT NULL DEFAULT '0',
  `potentialCount` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `dueyitems`
--

DROP TABLE IF EXISTS `dueyitems`;
CREATE TABLE IF NOT EXISTS `dueyitems` (
  `inventoryitemid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int DEFAULT NULL,
  `accountid` int DEFAULT NULL,
  `packageid` int DEFAULT NULL,
  `itemid` int NOT NULL DEFAULT '0',
  `inventorytype` int NOT NULL DEFAULT '0',
  `position` int NOT NULL DEFAULT '0',
  `quantity` int NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int NOT NULL DEFAULT '-1',
  `flag` int NOT NULL DEFAULT '0',
  `expiredate` bigint NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `dueypackages`
--

DROP TABLE IF EXISTS `dueypackages`;
CREATE TABLE IF NOT EXISTS `dueypackages` (
  `PackageId` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `RecieverId` int NOT NULL,
  `SenderName` varchar(13) NOT NULL,
  `Mesos` int UNSIGNED DEFAULT '0',
  `TimeStamp` bigint UNSIGNED DEFAULT NULL,
  `Checked` tinyint UNSIGNED DEFAULT '1',
  `Type` tinyint UNSIGNED NOT NULL,
  PRIMARY KEY (`PackageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `famelog`
--

DROP TABLE IF EXISTS `famelog`;
CREATE TABLE IF NOT EXISTS `famelog` (
  `famelogid` int NOT NULL AUTO_INCREMENT,
  `characterid` int NOT NULL DEFAULT '0',
  `characterid_to` int NOT NULL DEFAULT '0',
  `when` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`famelogid`),
  KEY `characterid` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `families`
--

DROP TABLE IF EXISTS `families`;
CREATE TABLE IF NOT EXISTS `families` (
  `familyid` int NOT NULL AUTO_INCREMENT,
  `leaderid` int NOT NULL DEFAULT '0',
  `notice` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`familyid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `gifts`
--

DROP TABLE IF EXISTS `gifts`;
CREATE TABLE IF NOT EXISTS `gifts` (
  `giftid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `recipient` int NOT NULL DEFAULT '0',
  `from` varchar(13) NOT NULL DEFAULT '',
  `message` varchar(255) NOT NULL DEFAULT '',
  `sn` int NOT NULL DEFAULT '0',
  `uniqueid` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`giftid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `guilds`
--

DROP TABLE IF EXISTS `guilds`;
CREATE TABLE IF NOT EXISTS `guilds` (
  `guildid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `leader` int UNSIGNED NOT NULL DEFAULT '0',
  `GP` int NOT NULL DEFAULT '0',
  `logo` int UNSIGNED DEFAULT NULL,
  `logoColor` smallint UNSIGNED NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL,
  `rank1title` varchar(45) NOT NULL DEFAULT 'Master',
  `rank2title` varchar(45) NOT NULL DEFAULT 'Jr. Master',
  `rank3title` varchar(45) NOT NULL DEFAULT 'Member',
  `rank4title` varchar(45) NOT NULL DEFAULT 'Member',
  `rank5title` varchar(45) NOT NULL DEFAULT 'Member',
  `capacity` int UNSIGNED NOT NULL DEFAULT '10',
  `logoBG` int UNSIGNED DEFAULT NULL,
  `logoBGColor` smallint UNSIGNED NOT NULL DEFAULT '0',
  `notice` varchar(101) DEFAULT NULL,
  `signature` int NOT NULL DEFAULT '0',
  `alliance` int UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY (`guildid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `hiredmerch`
--

DROP TABLE IF EXISTS `hiredmerch`;
CREATE TABLE IF NOT EXISTS `hiredmerch` (
  `PackageId` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int UNSIGNED DEFAULT '0',
  `accountid` int UNSIGNED DEFAULT NULL,
  `Mesos` int UNSIGNED DEFAULT '0',
  `time` bigint UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`PackageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `inventoryequipment`
--

DROP TABLE IF EXISTS `inventoryequipment`;
CREATE TABLE IF NOT EXISTS `inventoryequipment` (
  `inventoryequipmentid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int UNSIGNED NOT NULL DEFAULT '0',
  `upgradeslots` int NOT NULL DEFAULT '0',
  `level` int NOT NULL DEFAULT '0',
  `str` int NOT NULL DEFAULT '0',
  `dex` int NOT NULL DEFAULT '0',
  `int` int NOT NULL DEFAULT '0',
  `luk` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `mp` int NOT NULL DEFAULT '0',
  `watk` int NOT NULL DEFAULT '0',
  `matk` int NOT NULL DEFAULT '0',
  `wdef` int NOT NULL DEFAULT '0',
  `mdef` int NOT NULL DEFAULT '0',
  `acc` int NOT NULL DEFAULT '0',
  `avoid` int NOT NULL DEFAULT '0',
  `hands` int NOT NULL DEFAULT '0',
  `speed` int NOT NULL DEFAULT '0',
  `jump` int NOT NULL DEFAULT '0',
  `ViciousHammer` int NOT NULL DEFAULT '0',
  `growthLevelUpType` int NOT NULL DEFAULT '0',
  `growthLevel` int NOT NULL DEFAULT '0',
  `growthEXP` int NOT NULL DEFAULT '0',
  `durability` int NOT NULL DEFAULT '-1',
  `enhance` int NOT NULL DEFAULT '0',
  `rank` int NOT NULL DEFAULT '0',
  `hidden` int NOT NULL DEFAULT '0',
  `potential1` int UNSIGNED NOT NULL DEFAULT '0',
  `potential2` int UNSIGNED NOT NULL DEFAULT '0',
  `potential3` int UNSIGNED NOT NULL DEFAULT '0',
  `socket1` int UNSIGNED NOT NULL DEFAULT '0',
  `socket2` int UNSIGNED NOT NULL DEFAULT '0',
  `hpR` int NOT NULL DEFAULT '0',
  `mpR` int NOT NULL DEFAULT '0',
  `incattackSpeed` int NOT NULL DEFAULT '0',
  `tuc` int NOT NULL DEFAULT '0',
  `ViciousHammerMax` int NOT NULL DEFAULT '0',
  `potentialCount` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `inventoryitems`
--

DROP TABLE IF EXISTS `inventoryitems`;
CREATE TABLE IF NOT EXISTS `inventoryitems` (
  `inventoryitemid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int DEFAULT NULL,
  `accountid` int DEFAULT NULL,
  `packageid` int DEFAULT NULL,
  `itemid` int NOT NULL DEFAULT '0',
  `inventorytype` int NOT NULL DEFAULT '0',
  `position` int NOT NULL DEFAULT '0',
  `quantity` int NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int NOT NULL DEFAULT '-1',
  `flag` int NOT NULL DEFAULT '0',
  `expiredate` bigint NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `inventoryslot`
--

DROP TABLE IF EXISTS `inventoryslot`;
CREATE TABLE IF NOT EXISTS `inventoryslot` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int UNSIGNED DEFAULT NULL,
  `equip` tinyint UNSIGNED DEFAULT NULL,
  `use` tinyint UNSIGNED DEFAULT NULL,
  `setup` tinyint UNSIGNED DEFAULT NULL,
  `etc` tinyint UNSIGNED DEFAULT NULL,
  `cash` tinyint UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `keymap`
--

DROP TABLE IF EXISTS `keymap`;
CREATE TABLE IF NOT EXISTS `keymap` (
  `id` int NOT NULL AUTO_INCREMENT,
  `characterid` int NOT NULL DEFAULT '0',
  `key` tinyint UNSIGNED NOT NULL DEFAULT '0',
  `type` tinyint UNSIGNED NOT NULL DEFAULT '0',
  `action` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `keymap_ibfk_1` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `monsterbook`
--

DROP TABLE IF EXISTS `monsterbook`;
CREATE TABLE IF NOT EXISTS `monsterbook` (
  `id` int NOT NULL AUTO_INCREMENT,
  `charid` int UNSIGNED NOT NULL DEFAULT '0',
  `cardid` int UNSIGNED NOT NULL DEFAULT '0',
  `level` tinyint UNSIGNED DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `charid` (`charid`,`cardid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `mountdata`
--

DROP TABLE IF EXISTS `mountdata`;
CREATE TABLE IF NOT EXISTS `mountdata` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int UNSIGNED DEFAULT NULL,
  `Level` int UNSIGNED NOT NULL DEFAULT '0',
  `Exp` int UNSIGNED NOT NULL DEFAULT '0',
  `Fatigue` int UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `mtsequipment`
--

DROP TABLE IF EXISTS `mtsequipment`;
CREATE TABLE IF NOT EXISTS `mtsequipment` (
  `inventoryequipmentid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int UNSIGNED NOT NULL DEFAULT '0',
  `upgradeslots` int NOT NULL DEFAULT '0',
  `level` int NOT NULL DEFAULT '0',
  `str` int NOT NULL DEFAULT '0',
  `dex` int NOT NULL DEFAULT '0',
  `int` int NOT NULL DEFAULT '0',
  `luk` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `mp` int NOT NULL DEFAULT '0',
  `watk` int NOT NULL DEFAULT '0',
  `matk` int NOT NULL DEFAULT '0',
  `wdef` int NOT NULL DEFAULT '0',
  `mdef` int NOT NULL DEFAULT '0',
  `acc` int NOT NULL DEFAULT '0',
  `avoid` int NOT NULL DEFAULT '0',
  `hands` int NOT NULL DEFAULT '0',
  `speed` int NOT NULL DEFAULT '0',
  `jump` int NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint NOT NULL DEFAULT '0',
  `growthLevelUpType` int NOT NULL DEFAULT '0',
  `growthLevel` int NOT NULL DEFAULT '0',
  `growthEXP` int NOT NULL DEFAULT '0',
  `durability` int NOT NULL DEFAULT '-1',
  `enhance` tinyint NOT NULL DEFAULT '0',
  `rank` int NOT NULL DEFAULT '0',
  `hidden` int NOT NULL DEFAULT '0',
  `potential1` smallint NOT NULL DEFAULT '0',
  `potential2` smallint NOT NULL DEFAULT '0',
  `potential3` smallint NOT NULL DEFAULT '0',
  `socket1` smallint NOT NULL DEFAULT '0',
  `socket2` smallint NOT NULL DEFAULT '0',
  `hpR` smallint NOT NULL DEFAULT '0',
  `mpR` smallint NOT NULL DEFAULT '0',
  `incattackSpeed` int NOT NULL DEFAULT '0',
  `tuc` int NOT NULL DEFAULT '0',
  `ViciousHammerMax` int NOT NULL DEFAULT '0',
  `potentialCount` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `mtsitems`
--

DROP TABLE IF EXISTS `mtsitems`;
CREATE TABLE IF NOT EXISTS `mtsitems` (
  `inventoryitemid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int DEFAULT NULL,
  `accountid` int DEFAULT NULL,
  `packageId` int DEFAULT NULL,
  `itemid` int NOT NULL DEFAULT '0',
  `inventorytype` int NOT NULL DEFAULT '0',
  `position` int NOT NULL DEFAULT '0',
  `quantity` int NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int NOT NULL DEFAULT '-1',
  `flag` int NOT NULL DEFAULT '0',
  `expiredate` bigint NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `characterid_2` (`characterid`,`inventorytype`),
  KEY `packageid` (`packageId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `mtstransfer`
--

DROP TABLE IF EXISTS `mtstransfer`;
CREATE TABLE IF NOT EXISTS `mtstransfer` (
  `inventoryitemid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int DEFAULT NULL,
  `accountid` int DEFAULT NULL,
  `packageid` int DEFAULT NULL,
  `itemid` int NOT NULL DEFAULT '0',
  `inventorytype` int NOT NULL DEFAULT '0',
  `position` int NOT NULL DEFAULT '0',
  `quantity` int NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int NOT NULL DEFAULT '-1',
  `flag` int NOT NULL DEFAULT '0',
  `expiredate` bigint NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `mtstransferequipment`
--

DROP TABLE IF EXISTS `mtstransferequipment`;
CREATE TABLE IF NOT EXISTS `mtstransferequipment` (
  `inventoryequipmentid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int UNSIGNED NOT NULL DEFAULT '0',
  `upgradeslots` int NOT NULL DEFAULT '0',
  `level` int NOT NULL DEFAULT '0',
  `str` int NOT NULL DEFAULT '0',
  `dex` int NOT NULL DEFAULT '0',
  `int` int NOT NULL DEFAULT '0',
  `luk` int NOT NULL DEFAULT '0',
  `hp` int NOT NULL DEFAULT '0',
  `mp` int NOT NULL DEFAULT '0',
  `watk` int NOT NULL DEFAULT '0',
  `matk` int NOT NULL DEFAULT '0',
  `wdef` int NOT NULL DEFAULT '0',
  `mdef` int NOT NULL DEFAULT '0',
  `acc` int NOT NULL DEFAULT '0',
  `avoid` int NOT NULL DEFAULT '0',
  `hands` int NOT NULL DEFAULT '0',
  `speed` int NOT NULL DEFAULT '0',
  `jump` int NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint NOT NULL DEFAULT '0',
  `growthLevelUpType` int NOT NULL DEFAULT '0',
  `growthLevel` int NOT NULL DEFAULT '0',
  `growthEXP` int NOT NULL DEFAULT '0',
  `durability` int NOT NULL DEFAULT '-1',
  `enhance` tinyint NOT NULL DEFAULT '0',
  `rank` int NOT NULL DEFAULT '0',
  `hidden` int NOT NULL DEFAULT '0',
  `potential1` smallint NOT NULL DEFAULT '0',
  `potential2` smallint NOT NULL DEFAULT '0',
  `potential3` smallint NOT NULL DEFAULT '0',
  `socket1` smallint NOT NULL DEFAULT '0',
  `socket2` smallint NOT NULL DEFAULT '0',
  `hpR` smallint NOT NULL DEFAULT '0',
  `mpR` smallint NOT NULL DEFAULT '0',
  `incattackSpeed` int NOT NULL DEFAULT '0',
  `tuc` int NOT NULL DEFAULT '0',
  `ViciousHammerMax` int NOT NULL DEFAULT '0',
  `potentialCount` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `mts_cart`
--

DROP TABLE IF EXISTS `mts_cart`;
CREATE TABLE IF NOT EXISTS `mts_cart` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int NOT NULL DEFAULT '0',
  `itemid` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `mts_items`
--

DROP TABLE IF EXISTS `mts_items`;
CREATE TABLE IF NOT EXISTS `mts_items` (
  `id` int NOT NULL,
  `tab` tinyint(1) NOT NULL DEFAULT '1',
  `price` int NOT NULL DEFAULT '0',
  `characterid` int NOT NULL DEFAULT '0',
  `seller` varchar(13) NOT NULL DEFAULT '',
  `expiration` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `notes`
--

DROP TABLE IF EXISTS `notes`;
CREATE TABLE IF NOT EXISTS `notes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `to` varchar(13) NOT NULL DEFAULT '',
  `from` varchar(13) NOT NULL DEFAULT '',
  `message` text NOT NULL,
  `timestamp` bigint UNSIGNED NOT NULL,
  `gift` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `nxcode`
--

DROP TABLE IF EXISTS `nxcode`;
CREATE TABLE IF NOT EXISTS `nxcode` (
  `code` varchar(15) NOT NULL,
  `valid` int NOT NULL DEFAULT '1',
  `user` varchar(13) DEFAULT NULL,
  `type` int NOT NULL DEFAULT '0',
  `item` int NOT NULL DEFAULT '10000',
  PRIMARY KEY (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `pets`
--

DROP TABLE IF EXISTS `pets`;
CREATE TABLE IF NOT EXISTS `pets` (
  `petid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(13) DEFAULT NULL,
  `level` int UNSIGNED NOT NULL,
  `closeness` int UNSIGNED NOT NULL,
  `fullness` int UNSIGNED NOT NULL,
  `seconds` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`petid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `playernpcs`
--

DROP TABLE IF EXISTS `playernpcs`;
CREATE TABLE IF NOT EXISTS `playernpcs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(13) NOT NULL,
  `hair` int NOT NULL,
  `face` int NOT NULL,
  `skin` int NOT NULL,
  `x` int NOT NULL DEFAULT '0',
  `y` int NOT NULL DEFAULT '0',
  `map` int NOT NULL,
  `charid` int NOT NULL,
  `scriptid` int NOT NULL,
  `foothold` int NOT NULL,
  `dir` tinyint(1) NOT NULL DEFAULT '0',
  `gender` tinyint(1) NOT NULL DEFAULT '0',
  `pets` varchar(25) DEFAULT '0,0,0',
  PRIMARY KEY (`id`),
  KEY `scriptid` (`scriptid`),
  KEY `playernpcs_ibfk_1` (`charid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `questinfo`
--

DROP TABLE IF EXISTS `questinfo`;
CREATE TABLE IF NOT EXISTS `questinfo` (
  `questinfoid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int NOT NULL DEFAULT '0',
  `quest` int NOT NULL DEFAULT '0',
  `customData` varchar(555) DEFAULT NULL,
  PRIMARY KEY (`questinfoid`),
  KEY `characterid` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `queststatus`
--

DROP TABLE IF EXISTS `queststatus`;
CREATE TABLE IF NOT EXISTS `queststatus` (
  `queststatusid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `characterid` int NOT NULL DEFAULT '0',
  `quest` int NOT NULL DEFAULT '0',
  `status` tinyint NOT NULL DEFAULT '0',
  `time` int NOT NULL DEFAULT '0',
  `forfeited` int NOT NULL DEFAULT '0',
  `customData` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`queststatusid`),
  KEY `characterid` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `queststatusmobs`
--

DROP TABLE IF EXISTS `queststatusmobs`;
CREATE TABLE IF NOT EXISTS `queststatusmobs` (
  `queststatusmobid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `queststatusid` int UNSIGNED NOT NULL DEFAULT '0',
  `mob` int NOT NULL DEFAULT '0',
  `count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`queststatusmobid`),
  KEY `queststatusid` (`queststatusid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `reactordrops`
--

DROP TABLE IF EXISTS `reactordrops`;
CREATE TABLE IF NOT EXISTS `reactordrops` (
  `reactordropid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `reactorid` int NOT NULL,
  `itemid` int NOT NULL,
  `chance` int NOT NULL,
  `questid` int NOT NULL DEFAULT '-1',
  PRIMARY KEY (`reactordropid`),
  KEY `reactorid` (`reactorid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 PACK_KEYS=1;

-- --------------------------------------------------------

--
-- テーブルの構造 `regrocklocations`
--

DROP TABLE IF EXISTS `regrocklocations`;
CREATE TABLE IF NOT EXISTS `regrocklocations` (
  `trockid` int NOT NULL AUTO_INCREMENT,
  `characterid` int DEFAULT NULL,
  `mapid` int DEFAULT NULL,
  PRIMARY KEY (`trockid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `rings`
--

DROP TABLE IF EXISTS `rings`;
CREATE TABLE IF NOT EXISTS `rings` (
  `ringid` int NOT NULL AUTO_INCREMENT,
  `partnerRingId` int NOT NULL DEFAULT '0',
  `partnerChrId` int NOT NULL DEFAULT '0',
  `itemid` int NOT NULL DEFAULT '0',
  `partnername` varchar(255) NOT NULL,
  PRIMARY KEY (`ringid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `savedlocations`
--

DROP TABLE IF EXISTS `savedlocations`;
CREATE TABLE IF NOT EXISTS `savedlocations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `characterid` int NOT NULL,
  `locationtype` int NOT NULL DEFAULT '0',
  `map` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `savedlocations_ibfk_1` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `shopitems`
--

DROP TABLE IF EXISTS `shopitems`;
CREATE TABLE IF NOT EXISTS `shopitems` (
  `shopitemid` int NOT NULL AUTO_INCREMENT,
  `shopid` int NOT NULL,
  `itemid` int NOT NULL DEFAULT '0',
  `price` int NOT NULL DEFAULT '0',
  `position` int NOT NULL DEFAULT '0',
  `reqitem` int NOT NULL DEFAULT '0',
  `reqitemq` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`shopitemid`),
  KEY `shopid` (`shopid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `shops`
--

DROP TABLE IF EXISTS `shops`;
CREATE TABLE IF NOT EXISTS `shops` (
  `shopid` int NOT NULL DEFAULT '0',
  `npcid` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`shopid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `skillmacros`
--

DROP TABLE IF EXISTS `skillmacros`;
CREATE TABLE IF NOT EXISTS `skillmacros` (
  `id` int NOT NULL AUTO_INCREMENT,
  `characterid` int NOT NULL DEFAULT '0',
  `position` tinyint(1) NOT NULL DEFAULT '0',
  `skill1` int NOT NULL DEFAULT '0',
  `skill2` int NOT NULL DEFAULT '0',
  `skill3` int NOT NULL DEFAULT '0',
  `name` varchar(30) DEFAULT NULL,
  `shout` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `skills`
--

DROP TABLE IF EXISTS `skills`;
CREATE TABLE IF NOT EXISTS `skills` (
  `id` int NOT NULL AUTO_INCREMENT,
  `skillid` int NOT NULL DEFAULT '0',
  `characterid` int NOT NULL DEFAULT '0',
  `skilllevel` tinyint NOT NULL DEFAULT '0',
  `masterlevel` tinyint NOT NULL DEFAULT '0',
  `expiration` bigint NOT NULL DEFAULT '-1',
  PRIMARY KEY (`id`),
  KEY `skills_ibfk_1` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- テーブルの構造 `storages`
--

DROP TABLE IF EXISTS `storages`;
CREATE TABLE IF NOT EXISTS `storages` (
  `storageid` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `accountid` int NOT NULL DEFAULT '0',
  `slots` int NOT NULL DEFAULT '0',
  `meso` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`storageid`),
  KEY `accountid` (`accountid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `trocklocations`
--

DROP TABLE IF EXISTS `trocklocations`;
CREATE TABLE IF NOT EXISTS `trocklocations` (
  `trockid` int NOT NULL AUTO_INCREMENT,
  `characterid` int DEFAULT NULL,
  `mapid` int DEFAULT NULL,
  PRIMARY KEY (`trockid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `wishlist`
--

DROP TABLE IF EXISTS `wishlist`;
CREATE TABLE IF NOT EXISTS `wishlist` (
  `characterid` int NOT NULL,
  `sn` int NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- テーブルの構造 `__root`
--

DROP TABLE IF EXISTS `__root`;
CREATE TABLE IF NOT EXISTS `__root` (
  `id` int NOT NULL AUTO_INCREMENT,
  `maple_id` int DEFAULT NULL,
  `character_id` int DEFAULT NULL,
  `data_name` varchar(64) DEFAULT NULL,
  `value_int` int DEFAULT NULL,
  `value_str` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
