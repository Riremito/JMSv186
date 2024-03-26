-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- ホスト: localhost
-- 生成日時: 2024 年 3 月 26 日 04:08
-- サーバのバージョン: 5.6.12-log
-- PHP のバージョン: 5.4.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- データベース: `v186`
--
CREATE DATABASE IF NOT EXISTS `v186` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `v186`;

-- --------------------------------------------------------

--
-- テーブルの構造 `accounts`
--

CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(13) NOT NULL DEFAULT '',
  `password` varchar(128) NOT NULL DEFAULT '',
  `salt` varchar(32) DEFAULT NULL,
  `2ndpassword` varchar(134) DEFAULT NULL,
  `salt2` varchar(32) DEFAULT NULL,
  `loggedin` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `lastlogin` timestamp NULL DEFAULT NULL,
  `createdat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `birthday` date NOT NULL DEFAULT '0000-00-00',
  `banned` tinyint(1) NOT NULL DEFAULT '0',
  `banreason` text,
  `gm` tinyint(1) NOT NULL DEFAULT '0',
  `email` tinytext,
  `macs` tinytext,
  `tempban` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `greason` tinyint(4) unsigned DEFAULT NULL,
  `ACash` int(11) DEFAULT NULL,
  `mPoints` int(11) DEFAULT NULL,
  `gender` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `SessionIP` varchar(64) DEFAULT NULL,
  `points` int(11) NOT NULL DEFAULT '0',
  `vpoints` int(11) NOT NULL DEFAULT '0',
  `monthvotes` int(11) NOT NULL DEFAULT '0',
  `totalvotes` int(11) NOT NULL DEFAULT '0',
  `lastvote` int(11) NOT NULL DEFAULT '0',
  `lastvote2` int(11) NOT NULL DEFAULT '0',
  `lastlogon` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `ranking1` (`id`,`banned`,`gm`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=6 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `achievements`
--

CREATE TABLE IF NOT EXISTS `achievements` (
  `achievementid` int(9) NOT NULL DEFAULT '0',
  `charid` int(9) NOT NULL DEFAULT '0',
  `accountid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`achievementid`,`charid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- テーブルの構造 `alliances`
--

CREATE TABLE IF NOT EXISTS `alliances` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(13) NOT NULL,
  `leaderid` int(11) NOT NULL,
  `guild1` int(11) NOT NULL,
  `guild2` int(11) NOT NULL,
  `guild3` int(11) NOT NULL DEFAULT '0',
  `guild4` int(11) NOT NULL DEFAULT '0',
  `guild5` int(11) NOT NULL DEFAULT '0',
  `rank1` varchar(13) NOT NULL DEFAULT 'Master',
  `rank2` varchar(13) NOT NULL DEFAULT 'Jr.Master',
  `rank3` varchar(13) NOT NULL DEFAULT 'Member',
  `rank4` varchar(13) NOT NULL DEFAULT 'Member',
  `rank5` varchar(13) NOT NULL DEFAULT 'Member',
  `capacity` int(11) NOT NULL DEFAULT '2',
  `notice` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `auth_server_channel`
--

CREATE TABLE IF NOT EXISTS `auth_server_channel` (
  `channelid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `world` int(11) NOT NULL DEFAULT '0',
  `number` int(11) DEFAULT NULL,
  `key` varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`channelid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `auth_server_channel_ip`
--

CREATE TABLE IF NOT EXISTS `auth_server_channel_ip` (
  `channelconfigid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `channelid` int(10) unsigned NOT NULL DEFAULT '0',
  `name` tinytext NOT NULL,
  `value` tinytext NOT NULL,
  PRIMARY KEY (`channelconfigid`),
  KEY `channelid` (`channelid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `auth_server_cs`
--

CREATE TABLE IF NOT EXISTS `auth_server_cs` (
  `CashShopServerId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(40) NOT NULL,
  `world` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`CashShopServerId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `auth_server_login`
--

CREATE TABLE IF NOT EXISTS `auth_server_login` (
  `loginserverid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(40) NOT NULL DEFAULT '',
  `world` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`loginserverid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `auth_server_mts`
--

CREATE TABLE IF NOT EXISTS `auth_server_mts` (
  `MTSServerId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(40) NOT NULL,
  `world` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`MTSServerId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `bbs_replies`
--

CREATE TABLE IF NOT EXISTS `bbs_replies` (
  `replyid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `threadid` int(10) unsigned NOT NULL,
  `postercid` int(10) unsigned NOT NULL,
  `timestamp` bigint(20) unsigned NOT NULL,
  `content` varchar(26) NOT NULL DEFAULT '',
  `guildid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`replyid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `bbs_threads`
--

CREATE TABLE IF NOT EXISTS `bbs_threads` (
  `threadid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `postercid` int(10) unsigned NOT NULL,
  `name` varchar(26) NOT NULL DEFAULT '',
  `timestamp` bigint(20) unsigned NOT NULL,
  `icon` smallint(5) unsigned NOT NULL,
  `startpost` text NOT NULL,
  `guildid` int(10) unsigned NOT NULL,
  `localthreadid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`threadid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `bosslog`
--

CREATE TABLE IF NOT EXISTS `bosslog` (
  `bosslogid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(10) unsigned NOT NULL,
  `bossid` varchar(20) NOT NULL,
  `lastattempt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`bosslogid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `buddies`
--

CREATE TABLE IF NOT EXISTS `buddies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL,
  `buddyid` int(11) NOT NULL,
  `pending` tinyint(4) NOT NULL DEFAULT '0',
  `groupname` varchar(16) NOT NULL DEFAULT 'ETC',
  PRIMARY KEY (`id`),
  KEY `buddies_ibfk_1` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `cashshop_limit_sell`
--

CREATE TABLE IF NOT EXISTS `cashshop_limit_sell` (
  `serial` int(11) NOT NULL,
  `amount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`serial`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- テーブルの構造 `cashshop_modified_items`
--

CREATE TABLE IF NOT EXISTS `cashshop_modified_items` (
  `serial` int(11) NOT NULL,
  `discount_price` int(11) NOT NULL DEFAULT '-1',
  `mark` tinyint(1) NOT NULL DEFAULT '-1',
  `showup` tinyint(1) NOT NULL DEFAULT '0',
  `itemid` int(11) NOT NULL DEFAULT '0',
  `priority` tinyint(3) NOT NULL DEFAULT '0',
  `package` tinyint(1) NOT NULL DEFAULT '0',
  `period` tinyint(3) NOT NULL DEFAULT '0',
  `gender` tinyint(1) NOT NULL DEFAULT '0',
  `count` tinyint(3) NOT NULL DEFAULT '0',
  `meso` int(11) NOT NULL DEFAULT '0',
  `unk_1` tinyint(1) NOT NULL DEFAULT '0',
  `unk_2` tinyint(1) NOT NULL DEFAULT '0',
  `unk_3` tinyint(1) NOT NULL DEFAULT '0',
  `extra_flags` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`serial`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- テーブルの構造 `characters`
--

CREATE TABLE IF NOT EXISTS `characters` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL DEFAULT '0',
  `world` tinyint(1) NOT NULL DEFAULT '0',
  `name` varchar(13) NOT NULL DEFAULT '',
  `level` int(11) NOT NULL DEFAULT '0',
  `exp` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `maxhp` int(11) NOT NULL DEFAULT '0',
  `maxmp` int(11) NOT NULL DEFAULT '0',
  `meso` int(11) NOT NULL DEFAULT '0',
  `tama` int(11) NOT NULL DEFAULT '0',
  `hpApUsed` int(11) NOT NULL DEFAULT '0',
  `job` int(11) NOT NULL DEFAULT '0',
  `skincolor` tinyint(1) NOT NULL DEFAULT '0',
  `gender` tinyint(1) NOT NULL DEFAULT '0',
  `fame` int(11) NOT NULL DEFAULT '0',
  `hair` int(11) NOT NULL DEFAULT '0',
  `face` int(11) NOT NULL DEFAULT '0',
  `ap` int(11) NOT NULL DEFAULT '0',
  `map` int(11) NOT NULL DEFAULT '0',
  `spawnpoint` int(3) NOT NULL DEFAULT '0',
  `gm` int(3) NOT NULL DEFAULT '0',
  `party` int(11) NOT NULL DEFAULT '0',
  `buddyCapacity` int(3) NOT NULL DEFAULT '25',
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `guildid` int(10) unsigned NOT NULL DEFAULT '0',
  `guildrank` tinyint(1) unsigned NOT NULL DEFAULT '5',
  `allianceRank` tinyint(1) unsigned NOT NULL DEFAULT '5',
  `monsterbookcover` int(11) unsigned NOT NULL DEFAULT '0',
  `dojo_pts` int(11) unsigned NOT NULL DEFAULT '0',
  `dojoRecord` tinyint(2) unsigned NOT NULL DEFAULT '0',
  `pets` varchar(13) NOT NULL DEFAULT '-1,-1,-1',
  `sp` varchar(255) NOT NULL DEFAULT '0,0,0,0,0,0,0,0,0,0',
  `subcategory` int(11) NOT NULL DEFAULT '0',
  `Jaguar` int(3) NOT NULL DEFAULT '0',
  `rank` int(11) NOT NULL DEFAULT '1',
  `rankMove` int(11) NOT NULL DEFAULT '0',
  `jobRank` int(11) NOT NULL DEFAULT '1',
  `jobRankMove` int(11) NOT NULL DEFAULT '0',
  `marriageId` int(11) NOT NULL DEFAULT '0',
  `familyid` int(11) NOT NULL DEFAULT '0',
  `seniorid` int(11) NOT NULL DEFAULT '0',
  `junior1` int(11) NOT NULL DEFAULT '0',
  `junior2` int(11) NOT NULL DEFAULT '0',
  `currentrep` int(11) NOT NULL DEFAULT '0',
  `totalrep` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `accountid` (`accountid`),
  KEY `party` (`party`),
  KEY `ranking1` (`level`,`exp`),
  KEY `ranking2` (`gm`,`job`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `character_slots`
--

CREATE TABLE IF NOT EXISTS `character_slots` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accid` int(11) NOT NULL DEFAULT '0',
  `worldid` int(11) NOT NULL DEFAULT '0',
  `charslots` int(11) NOT NULL DEFAULT '6',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `cheatlog`
--

CREATE TABLE IF NOT EXISTS `cheatlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `offense` tinytext NOT NULL,
  `count` int(11) NOT NULL DEFAULT '0',
  `lastoffensetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `param` tinytext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cid` (`characterid`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `csequipment`
--

CREATE TABLE IF NOT EXISTS `csequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `watk` int(11) NOT NULL DEFAULT '0',
  `matk` int(11) NOT NULL DEFAULT '0',
  `wdef` int(11) NOT NULL DEFAULT '0',
  `mdef` int(11) NOT NULL DEFAULT '0',
  `acc` int(11) NOT NULL DEFAULT '0',
  `avoid` int(11) NOT NULL DEFAULT '0',
  `hands` int(11) NOT NULL DEFAULT '0',
  `speed` int(11) NOT NULL DEFAULT '0',
  `jump` int(11) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` int(11) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `csitems`
--

CREATE TABLE IF NOT EXISTS `csitems` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageid` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `drop_data`
--

CREATE TABLE IF NOT EXISTS `drop_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dropperid` int(11) NOT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `minimum_quantity` int(11) NOT NULL DEFAULT '1',
  `maximum_quantity` int(11) NOT NULL DEFAULT '1',
  `questid` int(11) NOT NULL DEFAULT '0',
  `chance` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `mobid` (`dropperid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=41307 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `drop_data_global`
--

CREATE TABLE IF NOT EXISTS `drop_data_global` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `continent` int(11) NOT NULL,
  `dropType` tinyint(1) NOT NULL DEFAULT '0',
  `itemid` int(11) NOT NULL DEFAULT '0',
  `minimum_quantity` int(11) NOT NULL DEFAULT '1',
  `maximum_quantity` int(11) NOT NULL DEFAULT '1',
  `questid` int(11) NOT NULL DEFAULT '0',
  `chance` int(11) NOT NULL DEFAULT '0',
  `comments` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mobid` (`continent`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `drop_data_vana`
--

CREATE TABLE IF NOT EXISTS `drop_data_vana` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dropperid` int(11) NOT NULL,
  `flags` set('is_mesos') NOT NULL DEFAULT '',
  `itemid` int(11) NOT NULL DEFAULT '0',
  `minimum_quantity` int(11) NOT NULL DEFAULT '1',
  `maximum_quantity` int(11) NOT NULL DEFAULT '1',
  `questid` int(11) NOT NULL DEFAULT '0',
  `chance` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `mobid` (`dropperid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `dueyequipment`
--

CREATE TABLE IF NOT EXISTS `dueyequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `watk` int(11) NOT NULL DEFAULT '0',
  `matk` int(11) NOT NULL DEFAULT '0',
  `wdef` int(11) NOT NULL DEFAULT '0',
  `mdef` int(11) NOT NULL DEFAULT '0',
  `acc` int(11) NOT NULL DEFAULT '0',
  `avoid` int(11) NOT NULL DEFAULT '0',
  `hands` int(11) NOT NULL DEFAULT '0',
  `speed` int(11) NOT NULL DEFAULT '0',
  `jump` int(11) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` int(11) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `dueyitems`
--

CREATE TABLE IF NOT EXISTS `dueyitems` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageid` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `dueypackages`
--

CREATE TABLE IF NOT EXISTS `dueypackages` (
  `PackageId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `RecieverId` int(10) NOT NULL,
  `SenderName` varchar(13) NOT NULL,
  `Mesos` int(10) unsigned DEFAULT '0',
  `TimeStamp` bigint(20) unsigned DEFAULT NULL,
  `Checked` tinyint(1) unsigned DEFAULT '1',
  `Type` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY (`PackageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `eventstats`
--

CREATE TABLE IF NOT EXISTS `eventstats` (
  `eventstatid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event` varchar(30) NOT NULL,
  `instance` varchar(30) NOT NULL,
  `characterid` int(11) NOT NULL,
  `channel` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`eventstatid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `famelog`
--

CREATE TABLE IF NOT EXISTS `famelog` (
  `famelogid` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `characterid_to` int(11) NOT NULL DEFAULT '0',
  `when` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`famelogid`),
  KEY `characterid` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `families`
--

CREATE TABLE IF NOT EXISTS `families` (
  `familyid` int(11) NOT NULL AUTO_INCREMENT,
  `leaderid` int(11) NOT NULL DEFAULT '0',
  `notice` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`familyid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `game_poll_reply`
--

CREATE TABLE IF NOT EXISTS `game_poll_reply` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AccountId` int(10) unsigned NOT NULL,
  `SelectAns` tinyint(5) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `gifts`
--

CREATE TABLE IF NOT EXISTS `gifts` (
  `giftid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `recipient` int(11) NOT NULL DEFAULT '0',
  `from` varchar(13) NOT NULL DEFAULT '',
  `message` varchar(255) NOT NULL DEFAULT '',
  `sn` int(11) NOT NULL DEFAULT '0',
  `uniqueid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`giftid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `gmlog`
--

CREATE TABLE IF NOT EXISTS `gmlog` (
  `gmlogid` int(11) NOT NULL AUTO_INCREMENT,
  `cid` int(11) NOT NULL DEFAULT '0',
  `command` tinytext NOT NULL,
  `mapid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`gmlogid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `guilds`
--

CREATE TABLE IF NOT EXISTS `guilds` (
  `guildid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `leader` int(10) unsigned NOT NULL DEFAULT '0',
  `GP` int(11) NOT NULL DEFAULT '0',
  `logo` int(10) unsigned DEFAULT NULL,
  `logoColor` smallint(5) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL,
  `rank1title` varchar(45) NOT NULL DEFAULT 'Master',
  `rank2title` varchar(45) NOT NULL DEFAULT 'Jr. Master',
  `rank3title` varchar(45) NOT NULL DEFAULT 'Member',
  `rank4title` varchar(45) NOT NULL DEFAULT 'Member',
  `rank5title` varchar(45) NOT NULL DEFAULT 'Member',
  `capacity` int(10) unsigned NOT NULL DEFAULT '10',
  `logoBG` int(10) unsigned DEFAULT NULL,
  `logoBGColor` smallint(5) unsigned NOT NULL DEFAULT '0',
  `notice` varchar(101) DEFAULT NULL,
  `signature` int(11) NOT NULL DEFAULT '0',
  `alliance` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`guildid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `hiredmerch`
--

CREATE TABLE IF NOT EXISTS `hiredmerch` (
  `PackageId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(10) unsigned DEFAULT '0',
  `accountid` int(10) unsigned DEFAULT NULL,
  `Mesos` int(10) unsigned DEFAULT '0',
  `time` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`PackageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `hiredmerchequipment`
--

CREATE TABLE IF NOT EXISTS `hiredmerchequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `watk` int(11) NOT NULL DEFAULT '0',
  `matk` int(11) NOT NULL DEFAULT '0',
  `wdef` int(11) NOT NULL DEFAULT '0',
  `mdef` int(11) NOT NULL DEFAULT '0',
  `acc` int(11) NOT NULL DEFAULT '0',
  `avoid` int(11) NOT NULL DEFAULT '0',
  `hands` int(11) NOT NULL DEFAULT '0',
  `speed` int(11) NOT NULL DEFAULT '0',
  `jump` int(11) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` int(11) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `hiredmerchitems`
--

CREATE TABLE IF NOT EXISTS `hiredmerchitems` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageid` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `htsquads`
--

CREATE TABLE IF NOT EXISTS `htsquads` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `channel` int(10) unsigned NOT NULL,
  `leaderid` int(10) unsigned NOT NULL DEFAULT '0',
  `status` int(10) unsigned NOT NULL DEFAULT '0',
  `members` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `inventoryequipment`
--

CREATE TABLE IF NOT EXISTS `inventoryequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `str` smallint(6) NOT NULL DEFAULT '0',
  `dex` smallint(6) NOT NULL DEFAULT '0',
  `int` smallint(6) NOT NULL DEFAULT '0',
  `luk` smallint(6) NOT NULL DEFAULT '0',
  `hp` smallint(6) NOT NULL DEFAULT '0',
  `mp` smallint(6) NOT NULL DEFAULT '0',
  `watk` smallint(6) NOT NULL DEFAULT '0',
  `matk` smallint(6) NOT NULL DEFAULT '0',
  `wdef` smallint(6) NOT NULL DEFAULT '0',
  `mdef` smallint(6) NOT NULL DEFAULT '0',
  `acc` smallint(6) NOT NULL DEFAULT '0',
  `avoid` smallint(6) NOT NULL DEFAULT '0',
  `hands` smallint(6) NOT NULL DEFAULT '0',
  `speed` smallint(6) NOT NULL DEFAULT '0',
  `jump` smallint(6) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` mediumint(9) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=12094 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `inventoryitems`
--

CREATE TABLE IF NOT EXISTS `inventoryitems` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageid` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=44020 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `inventorylog`
--

CREATE TABLE IF NOT EXISTS `inventorylog` (
  `inventorylogid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `msg` tinytext NOT NULL,
  PRIMARY KEY (`inventorylogid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `inventoryslot`
--

CREATE TABLE IF NOT EXISTS `inventoryslot` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(10) unsigned DEFAULT NULL,
  `equip` tinyint(3) unsigned DEFAULT NULL,
  `use` tinyint(3) unsigned DEFAULT NULL,
  `setup` tinyint(3) unsigned DEFAULT NULL,
  `etc` tinyint(3) unsigned DEFAULT NULL,
  `cash` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=719 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `ipbans`
--

CREATE TABLE IF NOT EXISTS `ipbans` (
  `ipbanid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`ipbanid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `ipvotelog`
--

CREATE TABLE IF NOT EXISTS `ipvotelog` (
  `vid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `accid` varchar(45) NOT NULL DEFAULT '0',
  `ipaddress` varchar(30) NOT NULL DEFAULT '127.0.0.1',
  `votetime` varchar(100) NOT NULL DEFAULT '0',
  `votetype` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`vid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `keymap`
--

CREATE TABLE IF NOT EXISTS `keymap` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `key` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `type` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `action` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `keymap_ibfk_1` (`characterid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=29001 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `macbans`
--

CREATE TABLE IF NOT EXISTS `macbans` (
  `macbanid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mac` varchar(30) NOT NULL,
  PRIMARY KEY (`macbanid`),
  UNIQUE KEY `mac_2` (`mac`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `macfilters`
--

CREATE TABLE IF NOT EXISTS `macfilters` (
  `macfilterid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filter` varchar(30) NOT NULL,
  PRIMARY KEY (`macfilterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `monsterbook`
--

CREATE TABLE IF NOT EXISTS `monsterbook` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `charid` int(10) unsigned NOT NULL DEFAULT '0',
  `cardid` int(10) unsigned NOT NULL DEFAULT '0',
  `level` tinyint(2) unsigned DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=31 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `mountdata`
--

CREATE TABLE IF NOT EXISTS `mountdata` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(10) unsigned DEFAULT NULL,
  `Level` int(3) unsigned NOT NULL DEFAULT '0',
  `Exp` int(10) unsigned NOT NULL DEFAULT '0',
  `Fatigue` int(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `mtsequipment`
--

CREATE TABLE IF NOT EXISTS `mtsequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `watk` int(11) NOT NULL DEFAULT '0',
  `matk` int(11) NOT NULL DEFAULT '0',
  `wdef` int(11) NOT NULL DEFAULT '0',
  `mdef` int(11) NOT NULL DEFAULT '0',
  `acc` int(11) NOT NULL DEFAULT '0',
  `avoid` int(11) NOT NULL DEFAULT '0',
  `hands` int(11) NOT NULL DEFAULT '0',
  `speed` int(11) NOT NULL DEFAULT '0',
  `jump` int(11) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` int(11) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `mtsitems`
--

CREATE TABLE IF NOT EXISTS `mtsitems` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageId` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `characterid_2` (`characterid`,`inventorytype`),
  KEY `packageid` (`packageId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `mtstransfer`
--

CREATE TABLE IF NOT EXISTS `mtstransfer` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageid` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `mtstransferequipment`
--

CREATE TABLE IF NOT EXISTS `mtstransferequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `watk` int(11) NOT NULL DEFAULT '0',
  `matk` int(11) NOT NULL DEFAULT '0',
  `wdef` int(11) NOT NULL DEFAULT '0',
  `mdef` int(11) NOT NULL DEFAULT '0',
  `acc` int(11) NOT NULL DEFAULT '0',
  `avoid` int(11) NOT NULL DEFAULT '0',
  `hands` int(11) NOT NULL DEFAULT '0',
  `speed` int(11) NOT NULL DEFAULT '0',
  `jump` int(11) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` int(11) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `mts_cart`
--

CREATE TABLE IF NOT EXISTS `mts_cart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `itemid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `mts_items`
--

CREATE TABLE IF NOT EXISTS `mts_items` (
  `id` int(11) NOT NULL,
  `tab` tinyint(1) NOT NULL DEFAULT '1',
  `price` int(11) NOT NULL DEFAULT '0',
  `characterid` int(11) NOT NULL DEFAULT '0',
  `seller` varchar(13) NOT NULL DEFAULT '',
  `expiration` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED;

-- --------------------------------------------------------

--
-- テーブルの構造 `mulungdojo`
--

CREATE TABLE IF NOT EXISTS `mulungdojo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `charid` int(11) NOT NULL DEFAULT '0',
  `stage` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `notes`
--

CREATE TABLE IF NOT EXISTS `notes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `to` varchar(13) NOT NULL DEFAULT '',
  `from` varchar(13) NOT NULL DEFAULT '',
  `message` text NOT NULL,
  `timestamp` bigint(20) unsigned NOT NULL,
  `gift` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `nxcode`
--

CREATE TABLE IF NOT EXISTS `nxcode` (
  `code` varchar(15) NOT NULL,
  `valid` int(11) NOT NULL DEFAULT '1',
  `user` varchar(13) DEFAULT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `item` int(11) NOT NULL DEFAULT '10000',
  PRIMARY KEY (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- テーブルの構造 `pets`
--

CREATE TABLE IF NOT EXISTS `pets` (
  `petid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(13) DEFAULT NULL,
  `level` int(3) unsigned NOT NULL,
  `closeness` int(6) unsigned NOT NULL,
  `fullness` int(3) unsigned NOT NULL,
  `seconds` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`petid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=41 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `playernpcs`
--

CREATE TABLE IF NOT EXISTS `playernpcs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(13) NOT NULL,
  `hair` int(11) NOT NULL,
  `face` int(11) NOT NULL,
  `skin` int(11) NOT NULL,
  `x` int(11) NOT NULL DEFAULT '0',
  `y` int(11) NOT NULL DEFAULT '0',
  `map` int(11) NOT NULL,
  `charid` int(11) NOT NULL,
  `scriptid` int(11) NOT NULL,
  `foothold` int(11) NOT NULL,
  `dir` tinyint(1) NOT NULL DEFAULT '0',
  `gender` tinyint(1) NOT NULL DEFAULT '0',
  `pets` varchar(25) DEFAULT '0,0,0',
  PRIMARY KEY (`id`),
  KEY `scriptid` (`scriptid`),
  KEY `playernpcs_ibfk_1` (`charid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `playernpcs_equip`
--

CREATE TABLE IF NOT EXISTS `playernpcs_equip` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `npcid` int(11) NOT NULL,
  `equipid` int(11) NOT NULL,
  `equippos` int(11) NOT NULL,
  `charid` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `playernpcs_equip_ibfk_1` (`charid`),
  KEY `playernpcs_equip_ibfk_2` (`npcid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `questactions`
--

CREATE TABLE IF NOT EXISTS `questactions` (
  `questactionid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `questid` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `data` blob NOT NULL,
  PRIMARY KEY (`questactionid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `questinfo`
--

CREATE TABLE IF NOT EXISTS `questinfo` (
  `questinfoid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `quest` int(6) NOT NULL DEFAULT '0',
  `customData` varchar(555) DEFAULT NULL,
  PRIMARY KEY (`questinfoid`),
  KEY `characterid` (`characterid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=653 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `questrequirements`
--

CREATE TABLE IF NOT EXISTS `questrequirements` (
  `questrequirementid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `questid` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `data` blob NOT NULL,
  PRIMARY KEY (`questrequirementid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `queststatus`
--

CREATE TABLE IF NOT EXISTS `queststatus` (
  `queststatusid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `quest` int(6) NOT NULL DEFAULT '0',
  `status` tinyint(4) NOT NULL DEFAULT '0',
  `time` int(11) NOT NULL DEFAULT '0',
  `forfeited` int(11) NOT NULL DEFAULT '0',
  `customData` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`queststatusid`),
  KEY `characterid` (`characterid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=12227 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `queststatusmobs`
--

CREATE TABLE IF NOT EXISTS `queststatusmobs` (
  `queststatusmobid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `queststatusid` int(10) unsigned NOT NULL DEFAULT '0',
  `mob` int(11) NOT NULL DEFAULT '0',
  `count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`queststatusmobid`),
  KEY `queststatusid` (`queststatusid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `reactordrops`
--

CREATE TABLE IF NOT EXISTS `reactordrops` (
  `reactordropid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `reactorid` int(11) NOT NULL,
  `itemid` int(11) NOT NULL,
  `chance` int(11) NOT NULL,
  `questid` int(5) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`reactordropid`),
  KEY `reactorid` (`reactorid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 PACK_KEYS=1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `readable_cheatlog`
--

CREATE TABLE IF NOT EXISTS `readable_cheatlog` (
  `accountname` varchar(13) DEFAULT NULL,
  `accountid` int(11) DEFAULT NULL,
  `name` varchar(13) DEFAULT NULL,
  `characterid` int(11) DEFAULT NULL,
  `offense` tinytext,
  `count` int(11) DEFAULT NULL,
  `lastoffensetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `param` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- テーブルの構造 `readable_last_hour_cheatlog`
--

CREATE TABLE IF NOT EXISTS `readable_last_hour_cheatlog` (
  `accountname` varchar(13) DEFAULT NULL,
  `accountid` int(11) DEFAULT NULL,
  `name` varchar(13) DEFAULT NULL,
  `characterid` int(11) DEFAULT NULL,
  `numrepos` decimal(32,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- テーブルの構造 `regrocklocations`
--

CREATE TABLE IF NOT EXISTS `regrocklocations` (
  `trockid` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `mapid` int(11) DEFAULT NULL,
  PRIMARY KEY (`trockid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `reports`
--

CREATE TABLE IF NOT EXISTS `reports` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `reporttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reporterid` int(11) NOT NULL,
  `victimid` int(11) NOT NULL,
  `reason` tinyint(4) NOT NULL,
  `chatlog` text NOT NULL,
  `status` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `rings`
--

CREATE TABLE IF NOT EXISTS `rings` (
  `ringid` int(11) NOT NULL AUTO_INCREMENT,
  `partnerRingId` int(11) NOT NULL DEFAULT '0',
  `partnerChrId` int(11) NOT NULL DEFAULT '0',
  `itemid` int(11) NOT NULL DEFAULT '0',
  `partnername` varchar(255) NOT NULL,
  PRIMARY KEY (`ringid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `savedlocations`
--

CREATE TABLE IF NOT EXISTS `savedlocations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL,
  `locationtype` int(11) NOT NULL DEFAULT '0',
  `map` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `savedlocations_ibfk_1` (`characterid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=244 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `shopitems`
--

CREATE TABLE IF NOT EXISTS `shopitems` (
  `shopitemid` int(10) NOT NULL AUTO_INCREMENT,
  `shopid` int(10) NOT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `price` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `reqitem` int(11) NOT NULL DEFAULT '0',
  `reqitemq` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`shopitemid`),
  KEY `shopid` (`shopid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `shops`
--

CREATE TABLE IF NOT EXISTS `shops` (
  `shopid` int(10) NOT NULL DEFAULT '0',
  `npcid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`shopid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- テーブルの構造 `skillmacros`
--

CREATE TABLE IF NOT EXISTS `skillmacros` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `position` tinyint(1) NOT NULL DEFAULT '0',
  `skill1` int(11) NOT NULL DEFAULT '0',
  `skill2` int(11) NOT NULL DEFAULT '0',
  `skill3` int(11) NOT NULL DEFAULT '0',
  `name` varchar(30) DEFAULT NULL,
  `shout` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=251 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `skills`
--

CREATE TABLE IF NOT EXISTS `skills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `skillid` int(11) NOT NULL DEFAULT '0',
  `characterid` int(11) NOT NULL DEFAULT '0',
  `skilllevel` tinyint(4) NOT NULL DEFAULT '0',
  `masterlevel` tinyint(4) NOT NULL DEFAULT '0',
  `expiration` bigint(20) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`id`),
  KEY `skills_ibfk_1` (`characterid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=9480 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `skills_cooldowns`
--

CREATE TABLE IF NOT EXISTS `skills_cooldowns` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `charid` int(11) NOT NULL,
  `SkillID` int(11) NOT NULL,
  `length` bigint(20) NOT NULL,
  `StartTime` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `speedruns`
--

CREATE TABLE IF NOT EXISTS `speedruns` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(13) NOT NULL,
  `leader` varchar(13) NOT NULL,
  `timestring` varchar(1024) NOT NULL,
  `time` bigint(20) NOT NULL DEFAULT '0',
  `members` varchar(1024) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `storages`
--

CREATE TABLE IF NOT EXISTS `storages` (
  `storageid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL DEFAULT '0',
  `slots` int(11) NOT NULL DEFAULT '0',
  `meso` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`storageid`),
  KEY `accountid` (`accountid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=6 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `trocklocations`
--

CREATE TABLE IF NOT EXISTS `trocklocations` (
  `trockid` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `mapid` int(11) DEFAULT NULL,
  PRIMARY KEY (`trockid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=123 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `wishlist`
--

CREATE TABLE IF NOT EXISTS `wishlist` (
  `characterid` int(11) NOT NULL,
  `sn` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED;

-- --------------------------------------------------------

--
-- テーブルの構造 `wz_oxdata`
--

CREATE TABLE IF NOT EXISTS `wz_oxdata` (
  `questionset` smallint(6) NOT NULL DEFAULT '0',
  `questionid` smallint(6) NOT NULL DEFAULT '0',
  `question` varchar(200) NOT NULL DEFAULT '',
  `display` varchar(200) NOT NULL DEFAULT '',
  `answer` enum('o','x') NOT NULL,
  PRIMARY KEY (`questionset`,`questionid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- テーブルの構造 `zaksquads`
--

CREATE TABLE IF NOT EXISTS `zaksquads` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `channel` int(10) unsigned NOT NULL,
  `leaderid` int(10) unsigned NOT NULL DEFAULT '0',
  `status` int(10) unsigned NOT NULL DEFAULT '0',
  `members` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- テーブルの構造 `__root`
--

CREATE TABLE IF NOT EXISTS `__root` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `maple_id` int(11) DEFAULT NULL,
  `character_id` int(11) DEFAULT NULL,
  `data_name` varchar(64) DEFAULT NULL,
  `value_int` int(11) DEFAULT NULL,
  `value_str` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=21 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
