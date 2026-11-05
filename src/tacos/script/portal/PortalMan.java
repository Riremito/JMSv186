/*
 * Copyright (C) 2026 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package tacos.script.portal;

import tacos.client.TacosClient;
import tacos.script.js.TacosScriptPortal;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
// ポータルスクリプト (Map.wz の portal の script) の Java の実装
public class PortalMan {

    // Java の実装を優先し、存在しない場合は js を利用する
    public static boolean enter(TacosClient client, TacosPortal portal) {
        PortalScriptMan portal_script = create(portal.getScriptName());
        if (portal_script == null) {
            return TacosScriptPortal.getInstance().enter(portal, client);
        }
        client.getPlayer().DebugMsg("Portal (Java) : " + portal.getScriptName());
        return portal_script.start(client, portal);
    }

    private static PortalScriptMan create(String script_name) {
        switch (script_name) {
            case "aMatchMove2" -> {
                // 980010000, アリアント闘技大会 : 闘技場の控え室
                return new Portal_MonsterCarnival_AMatchMove2();
            }
            case "amoria_out" -> {
                // 680000000, ウェディングタウン : ウェディングタウン
                return new Portal_Amoria_Amoria_out();
            }
            case "aranTutorAloneX" -> {
                // 914000000, ブラックロード : 負傷兵の幕舍
                return new Portal_Rien_AranTutorAloneX();
            }
            case "aranTutorArrow0" -> {
                // 914000100, ブラックロード : 避難準備中
                return new Portal_Rien_AranTutorArrow0();
            }
            case "aranTutorArrow1" -> {
                // 914000200, ブラックロード : 燃える森1
                return new Portal_Rien_AranTutorArrow1();
            }
            case "aranTutorArrow2" -> {
                // 914000210, ブラックロード : 燃える森2
                return new Portal_Rien_AranTutorArrow2();
            }
            case "aranTutorArrow3" -> {
                // 914000220, ブラックロード : 燃える森3
                return new Portal_Rien_AranTutorArrow3();
            }
            case "aranTutorGuide0" -> {
                // 914000200, ブラックロード : 燃える森1
                return new Portal_Rien_AranTutorGuide0();
            }
            case "aranTutorGuide1" -> {
                // 914000210, ブラックロード : 燃える森2
                return new Portal_Rien_AranTutorGuide1();
            }
            case "aranTutorGuide2" -> {
                // 914000220, ブラックロード : 燃える森3
                return new Portal_Rien_AranTutorGuide2();
            }
            case "aranTutorLost" -> {
                // 914000300, ブラックロード : 森の最奥
                return new Portal_Rien_AranTutorLost();
            }
            case "aranTutorMono0" -> {
                // 914000000, ブラックロード : 負傷兵の幕舍
                return new Portal_Rien_AranTutorMono0();
            }
            case "aranTutorMono1" -> {
                // 914000000, ブラックロード : 負傷兵の幕舍
                return new Portal_Rien_AranTutorMono1();
            }
            case "aranTutorMono2" -> {
                // 914000000, ブラックロード : 負傷兵の幕舍
                return new Portal_Rien_AranTutorMono2();
            }
            case "aranTutorMono3" -> {
                // 914000200, ブラックロード : 燃える森1
                return new Portal_Rien_AranTutorMono3();
            }
            case "aranTutorOut1" -> {
                // 914000100, ブラックロード : 避難準備中
                return new Portal_Rien_AranTutorOut1();
            }
            case "aranTutorOut2" -> {
                // 914000200, ブラックロード : 燃える森1
                return new Portal_Rien_AranTutorOut2();
            }
            case "aranTutorOut3" -> {
                // 914000210, ブラックロード : 燃える森2
                return new Portal_Rien_AranTutorOut3();
            }
            case "ariant_Agit" -> {
                // 260000200, アリアント村 : アリアント集落地
                return new Portal_Ariant_Ariant_Agit();
            }
            case "ariant_castle" -> {
                // 260000300, アリアント : アリアント宮殿
                return new Portal_Ariant_Ariant_castle();
            }
            case "ariant_queens" -> {
                // 260000301, アリアント宮殿 : アリアント宮殿&lt;庭園&gt;
                // 260000302, アリアント宮殿 : アリアント宮殿&lt;廊下&gt;
                // 260000303, アリアント宮殿 : アリアント宮殿&lt;王室&gt;
                return new Portal_Ariant_Ariant_queens();
            }
            case "ariantMout" -> {
                // 980010010, アリアント闘技大会 : 王の部屋
                return new Portal_MonsterCarnival_AriantMout();
            }
            case "ariantMout2" -> {
                // 980010020, アリアント闘技大会 : 闘技場出口
                return new Portal_MonsterCarnival_AriantMout2();
            }
            case "babyPigOut" -> {
                // 900020110, 隠された通り : 鬱蒼とした森
                return new Portal_Henesys_BabyPigOut();
            }
            case "balog_end" -> {
                // 105100301
                // 105100401
                return new Portal_Sleepywood_Balog_end();
            }
            case "balogTemple" -> {
                // 105090200, 中央ダンジョン : 別世界への扉
                return new Portal_Sleepywood_BalogTemple();
            }
            case "bingcube_EnterPT" -> {
                // 502029000, 墜落した宇宙船入口 : 墜落した宇宙船入口
                return new Portal_OSSS_Bingcube_EnterPT();
            }
            case "BlackHoleReturn" -> {
                // 502040100, 過去のその日 : 墜落した宇宙船の深海
                // 502040200, 過去のその日 : 化石鉱物の鉱山
                // 502040300, 過去のその日 : 加工工場輸送路
                // ...
                return new Portal_OSSS_BlackHoleReturn();
            }
            case "catPriest_map" -> {
                // 250010504, 武陵 : 妖怪の森2
                return new Portal_MuLung_CatPriest_map();
            }
            case "cellar" -> {
                // 300000010, エリン森 : キャンプ会議場
                return new Portal_Ellin_Cellar();
            }
            case "contactDragon" -> {
                // 900010100, 夢の中 : 夢見る一本道
                return new Portal_Henesys_ContactDragon();
            }
            case "crane_MR" -> {
                // 200090300, 航海中 : 武陵行き
                return new Portal_Orbis_Crane();
            }
            case "crane_SS" -> {
                // 200090310, オルビス : 航海中&lt;オルビス行き&gt;
                return new Portal_Orbis_Crane();
            }
            case "Crimsonhuji00" -> {
                // 803000202
                return new Portal_Zipangu_Crimsonhuji00();
            }
            case "Crimsonhuji01" -> {
                // 803000304
                return new Portal_Zipangu_Crimsonhuji01();
            }
            case "Crimsonhuji02" -> {
                // 803000400
                return new Portal_Zipangu_Crimsonhuji02();
            }
            case "curseforest" -> {
                // 100040105, 隠れ道 : 邪気の森1
                return new Portal_Henesys_Curseforest();
            }
            case "davy2_hd1" -> {
                // 925100200,  : 甲板突破1
                return new Portal_HerbTown_DavyHidden();
            }
            case "davy3_hd1" -> {
                // 925100300,  : 甲板突破2
                return new Portal_HerbTown_DavyHidden();
            }
            case "davy_next0" -> {
                // 925100000,  : 海賊船への道
                return new Portal_HerbTown_DavyNext();
            }
            case "davy_next1" -> {
                // 925100100,  : 船首突破
                return new Portal_HerbTown_Davy_next1();
            }
            case "davy_next2" -> {
                // 925100200,  : 甲板突破1
                return new Portal_HerbTown_DavyNext();
            }
            case "davy_next3" -> {
                // 925100300,  : 甲板突破2
                return new Portal_HerbTown_DavyNext();
            }
            case "davy_next4" -> {
                // 925100400,  : 海賊退治!
                return new Portal_HerbTown_Davy_next4();
            }
            case "Depart_goBack00" -> {
                // 103040420, カニングスクエア : 7階 8階 C区域
                return new Portal_Kerning_Depart_goBack00();
            }
            case "Depart_goBack01" -> {
                // 103040420, カニングスクエア : 7階 8階 C区域
                // 103040430, カニングスクエア : 7階 8階 D区域
                return new Portal_Kerning_Depart_goBack01();
            }
            case "Depart_goFoward0" -> {
                // 103040410, カニングスクエア : 7階 8階 B区域
                return new Portal_Kerning_Depart_goFoward0();
            }
            case "Depart_goFoward1" -> {
                // 103040410, カニングスクエア : 7階 8階 B区域
                // 103040420, カニングスクエア : 7階 8階 C区域
                return new Portal_Kerning_Depart_goFoward1();
            }
            case "Depart_ToKerning" -> {
                // 103000310, カニングシティー地下鉄 : カニングスクエア駅
                return new Portal_Kerning_Depart_ToKerning();
            }
            case "Depart_topFloor" -> {
                // 103040400, カニングスクエア : 7階 8階 A区域
                return new Portal_Kerning_Depart_topFloor();
            }
            case "Depart_topOut" -> {
                // 103040400, カニングスクエア : 7階 8階 A区域
                return new Portal_Kerning_Depart_topOut();
            }
            case "dojang_exit" -> {
                // 925020000, 武陵道場 : 武陵道場
                return new Portal_MuLung_Dojang_exit();
            }
            case "dojang_next" -> {
                // 925020100, 武陵道場 : 武陵道場 1階
                // 925020200, 武陵道場 : 武陵道場 2階
                // 925020300, 武陵道場 : 武陵道場 3階
                // ...
                return new Portal_MuLung_Dojang();
            }
            case "dojang_tuto" -> {
                // 925020010, 武陵道場 : 武陵道場修練場
                return new Portal_MuLung_Dojang();
            }
            case "dojang_up" -> {
                // 925020100, 武陵道場 : 武陵道場 1階
                // 925020200, 武陵道場 : 武陵道場 2階
                // 925020300, 武陵道場 : 武陵道場 3階
                // ...
                return new Portal_MuLung_Dojang();
            }
            case "dracoout" -> {
                // 240000110, リプレ : ステーション&lt;オルビス行き&gt;
                return new Portal_Leafre_Dracoout();
            }
            case "DragonEggNotice" -> {
                // 900020110, 隠された通り : 鬱蒼とした森
                return new Portal_Henesys_DragonEggNotice();
            }
            case "dragoneyes" -> {
                // 900010200, 夢の中 : 夢見る森
                return new Portal_Henesys_Dragoneyes();
            }
            case "dragonNest" -> {
                // 240040610, リプレ : 危険な巣の下
                return new Portal_Leafre_DragonNest();
            }
            case "dual_ballRoom" -> {
                // 103050100, 秘桜蔭 : 秘桜蔭 2階
                return new Portal_Kerning_Dual_ballRoom();
            }
            case "dual_lv20" -> {
                // 103050300, 秘桜蔭 : 秘桜蔭地下室
                return new Portal_Kerning_Dual_lv20();
            }
            case "dual_lv25" -> {
                // 103050300, 秘桜蔭 : 秘桜蔭地下室
                return new Portal_Kerning_Dual_lv25();
            }
            case "dual_lv30" -> {
                // 103050300, 秘桜蔭 : 秘桜蔭地下室
                return new Portal_Kerning_Dual_lv30();
            }
            case "Dual_moveGate" -> {
                // 103000000, カニングシティー : カニングシティー
                return new Portal_Kerning_Dual_moveGate();
            }
            case "elevator" -> {
                // 222020100,  : ヘリオス塔2階
                // 222020200,  : ヘリオス塔99階
                return new Portal_Ludibrium_Elevator();
            }
            case "eliza_Garden" -> {
                // 200010300, オルビス : 空の階段2
                return new Portal_Orbis_Eliza_Garden();
            }
            case "enter_earth00" -> {
                // 120000101, ノーチラス : 航海室
                return new Portal_Nautilus_Enter_earth00();
            }
            case "enter_earth01" -> {
                // 221000300, ルディブリアム : 司令室
                return new Portal_Ludibrium_Enter_earth01();
            }
            case "enterAchter" -> {
                // 100000200, ヘネシス : 広場
                return new Portal_Henesys_EnterAchter();
            }
            case "enterBackStreet" -> {
                // 925020000, 武陵道場 : 武陵道場
                return new Portal_MuLung_EnterBackStreet();
            }
            case "enterBlackBC" -> {
                // 220011000, ルディブリアム : 空のテラス5
                return new Portal_Ludibrium_EnterBlackBC();
            }
            case "enterBlackFrog" -> {
                // 220000300, ルディブリアム : ルディブリアム住宅街
                return new Portal_Ludibrium_EnterBlackFrog();
            }
            case "enterBlackRoom" -> {
                // 200080600, オルビス : オルビス塔&lt;16層&gt;
                return new Portal_Orbis_EnterBlackRoom();
            }
            case "enterDisguise0" -> {
                // 130000200, クィーンズロード : エレヴの分かれ道
                return new Portal_Ereve_EnterDisguise0();
            }
            case "enterDisguise1" -> {
                // 130010000, クィーンズロード : 修行の森1
                return new Portal_Ereve_EnterDisguise();
            }
            case "enterDisguise2" -> {
                // 130010000, クィーンズロード : 修行の森1
                return new Portal_Ereve_EnterDisguise();
            }
            case "enterDisguise3" -> {
                // 130010100, クィーンズロード : 修行の森2
                return new Portal_Ereve_EnterDisguise();
            }
            case "enterDisguise4" -> {
                // 130010100, クィーンズロード : 修行の森2
                return new Portal_Ereve_EnterDisguise();
            }
            case "enterDisguise5" -> {
                // 130010200, クィーンズロード : 修行の森3
                return new Portal_Ereve_EnterDisguise();
            }
            case "enterDollcave" -> {
                // 105070300, 中央ダンジョン : エビルアイの巣３
                return new Portal_Sleepywood_EnterDollcave();
            }
            case "enterEvanRoom" -> {
                // 100030101, 一軒家 : 一軒家
                return new Portal_Henesys_EnterEvanRoom();
            }
            case "enterExitMirror" -> {
                // 809060000, 隠れ道 : 金箔寺
                // 950100000, 隠れ道 : 金箔寺
                return new Portal_Common_EnterExitMirror();
            }
            case "enterFirstDH" -> {
                // 130020000, クィーンズロード : 訓練場の入口
                return new Portal_Ereve_EnterDH();
            }
            case "enterfourthDH" -> {
                // 130020000, クィーンズロード : 訓練場の入口
                return new Portal_Ereve_EnterDH();
            }
            case "enterGym" -> {
                // 140010100, スノーアイランド : リエン修行場入口
                return new Portal_Rien_EnterGym();
            }
            case "enterInfo" -> {
                // 104000000, リス : 港口
                return new Portal_LithHarbor_EnterInfo();
            }
            case "enterMagiclibrar" -> {
                // 101000000, エリニア : エリニア
                return new Portal_Ellinia_EnterMagiclibrar();
            }
            case "enterMCave" -> {
                // 140030000, スノーアイランド : 鏡洞窟
                return new Portal_Rien_EnterMCave();
            }
            case "enterNepenthes" -> {
                // 200060000, オルビス : 散歩道2
                return new Portal_Orbis_EnterNepenthes();
            }
            case "enterPort" -> {
                // 140020200, スノーアイランド : 雪に覆われた原野 3
                return new Portal_Rien_EnterPort();
            }
            case "enterRider" -> {
                // 211050000, エルナス : 凍てつく野原
                return new Portal_ElNath_EnterRider();
            }
            case "enterRienFirst" -> {
                // 140010000, スノーアイランド : リエン西のフィールド
                return new Portal_Rien_EnterRienFirst();
            }
            case "enterSDI" -> {
                // 922030000, 隠された通り : 蛙口の家
                return new Portal_Ludibrium_EnterSDI();
            }
            case "enterSecondDH" -> {
                // 130020000, クィーンズロード : 訓練場の入口
                return new Portal_Ereve_EnterSecondDH();
            }
            case "enterSnowDragon" -> {
                // 914100010, 眠った島 : 雪に覆われた森
                return new Portal_Rien_EnterSnowDragon();
            }
            case "enterthirdDH" -> {
                // 130020000, クィーンズロード : 訓練場の入口
                return new Portal_Ereve_EnterDH();
            }
            case "entertraining" -> {
                // 1010000, メイプルロード : 冒険者の修練場入口
                return new Portal_MapleIsland_Entertraining();
            }
            case "enterWarehouse" -> {
                // 300000010, エリン森 : キャンプ会議場
                return new Portal_Ellin_EnterWarehouse();
            }
            case "enterWitch" -> {
                // 240040510, リプレ : 死んだ龍の巣
                return new Portal_Leafre_EnterWitch();
            }
            case "EvanDbPortaljp" -> {
                // 103050101, 秘桜蔭 : 千火の部屋
                return new Portal_Kerning_EvanDbPortaljp();
            }
            case "evanDollGR" -> {
                // 106010102, へネシス : ゴーレム寺院の入口
                return new Portal_Victoria_EvanDollGR();
            }
            case "evanEntrance" -> {
                // 100030320, フォレストストリート : 大きい一本道 2
                return new Portal_Henesys_EvanEntrance();
            }
            case "evanFall" -> {
                // 900020100, 隠された通り : 鬱蒼とした森
                return new Portal_Henesys_EvanFall();
            }
            case "evanFarmCT" -> {
                // 100030300, フォレストストリート : ウェタンギル中心地
                return new Portal_Henesys_EvanFarmCT();
            }
            case "evanGarden0" -> {
                // 100030102, 一軒家 : 前庭
                return new Portal_Henesys_EvanGarden0();
            }
            case "evanGarden1" -> {
                // 100030102, 一軒家 : 前庭
                return new Portal_Henesys_EvanGarden1();
            }
            case "evanGolemDoor" -> {
                // 106010101, 隠れ道 : 息をする岩
                return new Portal_Victoria_EvanGolemDoor();
            }
            case "evanlivingRoom" -> {
                // 100030101, 一軒家 : 一軒家
                return new Portal_Henesys_EvanlivingRoom();
            }
            case "evanRoom0" -> {
                // 100030100, 一軒家 : 人里離れた森の中
                return new Portal_Henesys_EvanRoom0();
            }
            case "evanRoom1" -> {
                // 100030100, 一軒家 : 人里離れた森の中
                return new Portal_Henesys_EvanRoom1();
            }
            case "evantalk00" -> {
                // 900010000, 夢の中 : 夢見る森入口
                return new Portal_Henesys_Evantalk00();
            }
            case "evantalk01" -> {
                // 900010000, 夢の中 : 夢見る森入口
                return new Portal_Henesys_Evantalk01();
            }
            case "evantalk02" -> {
                // 900010000, 夢の中 : 夢見る森入口
                return new Portal_Henesys_Evantalk02();
            }
            case "evantalk10" -> {
                // 900010100, 夢の中 : 夢見る一本道
                return new Portal_Henesys_Evantalk10();
            }
            case "evantalk11" -> {
                // 900010100, 夢の中 : 夢見る一本道
                return new Portal_Henesys_Evantalk11();
            }
            case "evantalk20" -> {
                // 900010200, 夢の中 : 夢見る森
                return new Portal_Henesys_Evantalk20();
            }
            case "evantalk21" -> {
                // 900010200, 夢の中 : 夢見る森
                return new Portal_Henesys_Evantalk21();
            }
            case "evantalk40" -> {
                // 900020200, 隠された通り : 忘れてしまった森入口
                return new Portal_Henesys_Evantalk40();
            }
            case "evantalk41" -> {
                // 900020200, 隠された通り : 忘れてしまった森入口
                return new Portal_Henesys_Evantalk41();
            }
            case "evantalk42" -> {
                // 900020200, 隠された通り : 忘れてしまった森入口
                return new Portal_Henesys_Evantalk42();
            }
            case "evantalk50" -> {
                // 900020210, 隠された通り : 忘れてしまった一本道
                return new Portal_Henesys_Evantalk50();
            }
            case "evantalk60" -> {
                // 900020220, 隠された通り : 忘れてしまった森
                return new Portal_Henesys_Evantalk60();
            }
            case "fishing_out" -> {
                // 741000200,  : 釣り場
                return new Portal_Taiwan_Fishing_out();
            }
            case "foxLaidy_map" -> {
                // 222010300, 下町 : 狐の丘
                return new Portal_Ludibrium_FoxLaidy_map();
            }
            case "ghostgate_open" -> {
                // 990000700,  : シャレン3世の墓
                return new Portal_GuildQuest_GateOpen();
            }
            case "ghostShip_next" -> {
                // 923020110, 霧の海 : 第1作戦室
                // 923020111, 霧の海 : 第2作戦室
                // 923020112, 霧の海 : 第3作戦室
                // ...
                return new Portal_Aquarium_GhostShip_next();
            }
            case "glpqPortal00" -> {
                // 803001100
                return new Portal_Zipangu_GlpqPortal();
            }
            case "glpqPortal01" -> {
                // 803001100
                return new Portal_Zipangu_GlpqPortal();
            }
            case "glpqPortal02" -> {
                // 803001100
                return new Portal_Zipangu_GlpqPortal();
            }
            case "glpqPortal03" -> {
                // 803001100
                return new Portal_Zipangu_GlpqPortal();
            }
            case "glpqPortal04" -> {
                // 803001100
                return new Portal_Zipangu_GlpqPortal();
            }
            case "go_secretroom" -> {
                // 106021000, キノコ城 : 摩天楼3
                return new Portal_Victoria_Go_secretroom();
            }
            case "goback" -> {
                // 809060000, 隠れ道 : 金箔寺
                // 950100000, 隠れ道 : 金箔寺
                return new Portal_Common_Goback();
            }
            case "goGoblin" -> {
                // 809060000, 隠れ道 : 金箔寺
                // 950100000, 隠れ道 : 金箔寺
                return new Portal_Common_GoGoblin();
            }
            case "goldenShrine_Out" -> {
                // 809061000, 隠れ道 : 封印された神殿の入口
                // 950101000, 隠れ道 : 封印された神殿の入口
                return new Portal_Common_GoldenShrine_Out();
            }
            case "goldkey1" -> {
                // 980041000, 魔女の塔 : 魔女の塔1階
                // 980042000, 魔女の塔 : 魔女の塔1階
                // 980043000, 魔女の塔 : 魔女の塔1階
                // ...
                return new Portal_MonsterCarnival_Goldkey();
            }
            case "goldkey10" -> {
                // 980041100, 魔女の塔 : 魔女の塔2階
                // 980042100, 魔女の塔 : 魔女の塔2階
                // 980043100, 魔女の塔 : 魔女の塔2階
                // ...
                return new Portal_MonsterCarnival_Goldkey();
            }
            case "goldkey2" -> {
                // 980041000, 魔女の塔 : 魔女の塔1階
                // 980042000, 魔女の塔 : 魔女の塔1階
                // 980043000, 魔女の塔 : 魔女の塔1階
                // ...
                return new Portal_MonsterCarnival_Goldkey();
            }
            case "goldkey3" -> {
                // 980041000, 魔女の塔 : 魔女の塔1階
                // 980042000, 魔女の塔 : 魔女の塔1階
                // 980043000, 魔女の塔 : 魔女の塔1階
                // ...
                return new Portal_MonsterCarnival_Goldkey();
            }
            case "goldkey4" -> {
                // 980041000, 魔女の塔 : 魔女の塔1階
                // 980042000, 魔女の塔 : 魔女の塔1階
                // 980043000, 魔女の塔 : 魔女の塔1階
                // ...
                return new Portal_MonsterCarnival_Goldkey();
            }
            case "goldkey5" -> {
                // 980041000, 魔女の塔 : 魔女の塔1階
                // 980042000, 魔女の塔 : 魔女の塔1階
                // 980043000, 魔女の塔 : 魔女の塔1階
                // ...
                return new Portal_MonsterCarnival_Goldkey();
            }
            case "goldkey6" -> {
                // 980041000, 魔女の塔 : 魔女の塔1階
                // 980042000, 魔女の塔 : 魔女の塔1階
                // 980043000, 魔女の塔 : 魔女の塔1階
                // ...
                return new Portal_MonsterCarnival_Goldkey();
            }
            case "goldkey7" -> {
                // 980041100, 魔女の塔 : 魔女の塔2階
                // 980042100, 魔女の塔 : 魔女の塔2階
                // 980043100, 魔女の塔 : 魔女の塔2階
                // ...
                return new Portal_MonsterCarnival_Goldkey();
            }
            case "goldkey8" -> {
                // 980041100, 魔女の塔 : 魔女の塔2階
                // 980042100, 魔女の塔 : 魔女の塔2階
                // 980043100, 魔女の塔 : 魔女の塔2階
                // ...
                return new Portal_MonsterCarnival_Goldkey();
            }
            case "goldkey9" -> {
                // 980041100, 魔女の塔 : 魔女の塔2階
                // 980042100, 魔女の塔 : 魔女の塔2階
                // 980043100, 魔女の塔 : 魔女の塔2階
                // ...
                return new Portal_MonsterCarnival_Goldkey();
            }
            case "goMonkey" -> {
                // 809060000, 隠れ道 : 金箔寺
                // 950100000, 隠れ道 : 金箔寺
                return new Portal_Common_GoMonkey();
            }
            case "goShrine" -> {
                // 809060000, 隠れ道 : 金箔寺
                // 950100000, 隠れ道 : 金箔寺
                return new Portal_Common_GoShrine();
            }
            case "gryphius" -> {
                // 240020100, リプレ : 火と闇の戦場
                return new Portal_Leafre_Gryphius();
            }
            case "GS_backSpace" -> {
                // 923020111, 霧の海 : 第2作戦室
                // 923020112, 霧の海 : 第3作戦室
                // 923020113, 霧の海 : 第4作戦室
                // ...
                return new Portal_Aquarium_GS_backSpace();
            }
            case "GS_stage5_water" -> {
                // 923020114, 霧の海 : 第5作戦室
                return new Portal_Aquarium_GS_stage5_water();
            }
            case "GS_switchChat" -> {
                // 923020114, 霧の海 : 第5作戦室
                return new Portal_Aquarium_GS_switchChat();
            }
            case "guardmap_pt_researcher" -> {
                // 502010100, 現在のある日 : 秘密基地地下道
                return new Portal_OSSS_Guardmap_pt_researcher();
            }
            case "guardmap_pt_shuttle" -> {
                // 502010400, 現在のある日 : 加工工場輸送路
                return new Portal_OSSS_Guardmap_pt_shuttle();
            }
            case "guild1F00" -> {
                // 990000700,  : シャレン3世の墓
                return new Portal_GuildQuest_Guild1F00();
            }
            case "guild1F01" -> {
                // 990000611,  : 迷路の終りA
                return new Portal_GuildQuest_Guild1F();
            }
            case "guild1F02" -> {
                // 990000620,  : 水路の迷路2
                return new Portal_GuildQuest_Guild1F();
            }
            case "guild1F03" -> {
                // 990000631,  : 迷路の終りB
                return new Portal_GuildQuest_Guild1F();
            }
            case "guild1F04" -> {
                // 990000641,  : 迷路の終りC
                return new Portal_GuildQuest_Guild1F();
            }
            case "halloween_out" -> {
                // 229010000, ファントムマンション : 庭園
                return new Portal_HauntedHouse_Halloween_out();
            }
            case "hidenPT_0" -> {
                // 100000000, ヘネシス : ヘネシス
                return new Portal_Henesys_HidenPT_0();
            }
            case "hidenPT_1" -> {
                // 220000000, ルディブリアム : ルディブリアム
                return new Portal_Ludibrium_HidenPT_1();
            }
            case "hidenPT_2" -> {
                // 200000000, オルビス : オルビス
                return new Portal_Orbis_HidenPT_2();
            }
            case "hidenPT_3" -> {
                // 221000000, ルディブリアム : 地球防衛本部
                return new Portal_Ludibrium_HidenPT_3();
            }
            case "highposition" -> {
                // 101010103, ビクトリアロード : 渓流&lt;バンジージャンプ台&gt;
                // 102000000, ぺリオン : ぺリオン
                // 103000000, カニングシティー : カニングシティー
                // ...
                return new Portal_Common_Highposition();
            }
            case "hontale_Bopen" -> {
                // 240050101, リプレ : 一番目の迷路部屋
                // 240050102, リプレ : 二番目の迷路部屋
                // 240050103, リプレ : 三番目の迷路部屋
                // ...
                return new Portal_Leafre_Hontale_Bopen();
            }
            case "hontale_boss1" -> {
                // 240060000, リプレ : 試験の洞窟1
                return new Portal_Leafre_HontaleBoss();
            }
            case "hontale_boss2" -> {
                // 240060100, リプレ : 試験の洞窟2
                return new Portal_Leafre_HontaleBoss();
            }
            case "hontale_BR" -> {
                // 240060000, リプレ : 試験の洞窟1
                // 240060100, リプレ : 試験の洞窟2
                return new Portal_Leafre_Hontale_BR();
            }
            case "hontale_BtoB1" -> {
                // 240050100, リプレ : 迷路部屋
                return new Portal_Leafre_Hontale_BtoB1();
            }
            case "hontale_C" -> {
                // 240050200, リプレ : 選択の洞窟
                return new Portal_Leafre_Hontale_C();
            }
            case "hontale_morph" -> {
                // 240040700, リプレ : 生命の洞窟入口
                return new Portal_Leafre_Hontale_morph();
            }
            case "hontale_morph2" -> {
                // 240040700, リプレ : 生命の洞窟入口
                return new Portal_Leafre_Hontale_morph2();
            }
            case "hontale_out1" -> {
                // 240050600, リプレ : ホーンテイルの洞窟
                return new Portal_Leafre_Hontale_out1();
            }
            case "inDragonEgg" -> {
                // 100030300, フォレストストリート : ウェタンギル中心地
                return new Portal_Henesys_InDragonEgg();
            }
            case "inERShip" -> {
                // 101000000, エリニア : エリニア
                return new Portal_Ellinia_InERShip();
            }
            case "infoAttack" -> {
                // 40000, メイプルロード : 小さな森
                return new Portal_MapleIsland_InfoAttack();
            }
            case "infoMinimap" -> {
                // 10000, メイプルロード : キノコの丘
                return new Portal_MapleIsland_InfoMinimap();
            }
            case "infoPickup" -> {
                // 40000, メイプルロード : 小さな森
                return new Portal_MapleIsland_InfoPickup();
            }
            case "infoReactor" -> {
                // 1000000, レインボーストリート : アムホスト
                return new Portal_MapleIsland_InfoReactor();
            }
            case "infoSkill" -> {
                // 40000, メイプルロード : 小さな森
                return new Portal_MapleIsland_InfoSkill();
            }
            case "infoWorldmap" -> {
                // 50000, メイプルロード : 危険な森
                return new Portal_MapleIsland_InfoWorldmap();
            }
            case "inhalloween" -> {
                // 229010000, ファントムマンション : 庭園
                return new Portal_HauntedHouse_Inhalloween();
            }
            case "inNix1" -> {
                // 240020101, リプレ : グリフォンの森
                // 240020102, リプレ : グリフォンの森2
                return new Portal_Leafre_InNix1();
            }
            case "inNix2" -> {
                // 240020401, リプレ : ドラゴンの森
                // 240020402, リプレ : レッドドラゴンの森
                return new Portal_Leafre_InNix2();
            }
            case "investigate1" -> {
                // 106020300, キノコ城 : 奥深きキノコの森
                return new Portal_Victoria_Investigate1();
            }
            case "jnr12_in" -> {
                // 926110400,  : 中央研究室入口
                return new Portal_Ariant_Jnr12_in();
            }
            case "jnr1_out" -> {
                // 926110001,  : 暗い通路
                return new Portal_Ariant_Jnr1_out();
            }
            case "jnr3_in0" -> {
                // 926110200,  : 特殊な実験室
                return new Portal_Ariant_Jnr3_in();
            }
            case "jnr3_in1" -> {
                // 926110200,  : 特殊な実験室
                return new Portal_Ariant_Jnr3_in();
            }
            case "jnr_201_0" -> {
                // 926110201,  : 暗い研究室1
                return new Portal_Ariant_Jnr3_in();
            }
            case "jnr_202" -> {
                // 926110202,  : 暗い研究室2
                return new Portal_Ariant_Jnr3_in();
            }
            case "kinggate2_open" -> {
                // 990000800,  : 王の回廊
                return new Portal_GuildQuest_GateOpen();
            }
            case "kinggate_open" -> {
                // 990000800,  : 王の回廊
                return new Portal_GuildQuest_GateOpen();
            }
            case "ludi021" -> {
                // 922000009,  : 秘密通路
                return new Portal_Ludibrium_Ludi021();
            }
            case "magatia_dark0" -> {
                // 261020600, アルカドノ研究所 : 研究所B-3区域
                return new Portal_Magatia_Magatia_dark0();
            }
            case "mapleTree_out" -> {
                // 970010000, 隠れ道 : 紅葉の木の庭園
                return new Portal_Event_MapleTree_out();
            }
            case "mayong" -> {
                // 240020400, リプレ : フリーズケンタウロスの領域
                return new Portal_Leafre_Mayong();
            }
            case "mc_out" -> {
                // 980000000, モンスターカーニバル : モンスターカーニバル入口
                // 980030000, モンスターカーニバル2nd : シュピゲルマンの事務室
                return new Portal_MonsterCarnival_Mc_out();
            }
            case "MCrevive1" -> {
                // 980000102, モンスターカーニバル : カーニバルフィールド1&lt;再生の間&gt;
                return new Portal_MonsterCarnival_MCrevive();
            }
            case "MCrevive2" -> {
                // 980000202, モンスターカーニバル : カーニバルフィールド2&lt;再生の間&gt;
                return new Portal_MonsterCarnival_MCrevive();
            }
            case "MCrevive3" -> {
                // 980000302, モンスターカーニバル : カーニバルフィールド3&lt;再生の間&gt;
                return new Portal_MonsterCarnival_MCrevive();
            }
            case "MCrevive4" -> {
                // 980000402, モンスターカーニバル : カーニバルフィールド4&lt;再生の間&gt;
                return new Portal_MonsterCarnival_MCrevive();
            }
            case "MCrevive5" -> {
                // 980000502, モンスターカーニバル : カーニバルフィールド5&lt;再生の間&gt;
                return new Portal_MonsterCarnival_MCrevive();
            }
            case "MCrevive6" -> {
                // 980000602, モンスターカーニバル : カーニバルフィールド6&lt;再生の間&gt;
                return new Portal_MonsterCarnival_MCrevive();
            }
            case "MD_drakeroom" -> {
                // 105090311, 中央ダンジョン : 冷たい揺りかご
                // 105090320, ミニダンジョン : ドレイクの青い洞窟
                return new Portal_Common_MD();
            }
            case "MD_error" -> {
                // 261020300, アルカドノ研究所 : 研究所C-1区域
                // 261020301, マガティア : 禁断の実験室
                return new Portal_Common_MD();
            }
            case "MD_golem" -> {
                // 105040304, 中央ダンジョン : スリーピーダンジョン4
                // 105040320, スリーピーウッド : 崩れたゴーレムの城跡
                return new Portal_Common_MD();
            }
            case "MD_mushroom" -> {
                // 105050100, 中央ダンジョン : アリの巣2
                // 105050101, スリーピーウッド : キノコ栽培洞窟
                return new Portal_Common_MD();
            }
            case "MD_pig" -> {
                // 100020000, ヘネシス : 東の草むら
                // 100020100, ヘネシス : 豚農場
                return new Portal_Common_MD();
            }
            case "MD_protect" -> {
                // 240040520, リプレ : 壊れた龍の巣
                // 240040900, ミニダンジョン : ニュート保護区域
                return new Portal_Common_MD();
            }
            case "MD_rabbit" -> {
                // 221023400, ルディブリアム : エオス塔76階-90階
                // 221023401, ルディブリアム : ビートラビットの隠れ家
                return new Portal_Common_MD();
            }
            case "MD_remember" -> {
                // 240040511, リプレ : 残された龍の巣
                // 240040800, ミニダンジョン : 復活する記憶
                return new Portal_Common_MD();
            }
            case "MD_roundTable" -> {
                // 240020500, リプレ : 火と水の戦場
                // 240020501, ミニダンジョン : ケンタウロスの円卓
                return new Portal_Common_MD();
            }
            case "MD_sand" -> {
                // 260020600, サンセットロード : サヘル地帯2
                // 260020630, アリアント : 砂塵の吹く丘
                return new Portal_Common_MD();
            }
            case "MD_treasure" -> {
                // 251010402, 白草村 : 赤鼻海賊団の盗掘2
                // 251010410, ミニダンジョン : 宝島の掠奪
                return new Portal_Common_MD();
            }
            case "met_in" -> {
                // 102040000, カニングシティー : 工事現場-北
                return new Portal_Perion_Met_in();
            }
            case "met_out" -> {
                // 910320000, ヒドゥンストリート : 捨てられた地下鉄の歴史
                return new Portal_Victoria_Met_out();
            }
            case "metalgate_open" -> {
                // 990000430,  : 信念の部屋
                return new Portal_GuildQuest_GateOpen();
            }
            case "metro_Chat00" -> {
                // 910320000, ヒドゥンストリート : 捨てられた地下鉄の歴史
                return new Portal_Victoria_Metro_Chat00();
            }
            case "metro_in00" -> {
                // 910320000, ヒドゥンストリート : 捨てられた地下鉄の歴史
                return new Portal_Victoria_Metro_in00();
            }
            case "minar_elli" -> {
                // 101010000, ビクトリアロード : エリニアの北側フィールド
                // 240010100, リプレ : ミナル西側の境界
                return new Portal_Common_Minar_elli();
            }
            case "minar_job4" -> {
                // 240010500, リプレ : 動物の谷
                return new Portal_Leafre_Minar_job4();
            }
            case "mirtalk00" -> {
                // 900010000, 夢の中 : 夢見る森入口
                return new Portal_Henesys_Mirtalk();
            }
            case "mirtalk01" -> {
                // 900010100, 夢の中 : 夢見る一本道
                return new Portal_Henesys_Mirtalk();
            }
            case "move_elin" -> {
                // 222020400, ルディブリアム : 時間制御室
                return new Portal_Ludibrium_Move_elin();
            }
            case "moveBefore" -> {
                // 108000710, アランの過去 : 鍛冶屋外部
                return new Portal_Victoria_MoveBefore();
            }
            case "moveNext" -> {
                // 108000700, アランの過去 : 大将翁の鍛冶屋
                return new Portal_Victoria_MoveNext();
            }
            case "nets_in" -> {
                // 260020500, サンセットロード : サヘル地帯3
                return new Portal_Ariant_Nets_in();
            }
            case "nets_out" -> {
                // 926010000, 隠れ道 : ピラミッドの丘
                return new Portal_Ariant_Nets_out();
            }
            case "ninja_Boss" -> {
                // 800040401, ジパング : 楓城 天守閣2階
                return new Portal_Zipangu_Ninja_Boss();
            }
            case "nooutShip" -> {
                // 914000500, ブラックロード : 避難準備完了
                return new Portal_Rien_NooutShip();
            }
            case "obstacle" -> {
                // 106020300, キノコ城 : 奥深きキノコの森
                return new Portal_Victoria_Obstacle();
            }
            case "outBabyBird" -> {
                // 910020000, 隠れ道 : ヒナの巣
                return new Portal_Victoria_OutBabyBird();
            }
            case "outChild" -> {
                // 914000300, ブラックロード : 森の最奥
                return new Portal_Rien_OutChild();
            }
            case "outDarkEreb" -> {
                // 913030000, 隠れ道 : エレヴ
                return new Portal_Ereve_OutDarkEreb();
            }
            case "outNix1" -> {
                // 240020600, 隠れ道 : 人里離れた森
                return new Portal_Leafre_OutNix1();
            }
            case "outNix2" -> {
                // 240020600, 隠れ道 : 人里離れた森
                return new Portal_Leafre_OutNix2();
            }
            case "OutPerrion_1" -> {
                // 105100100
                return new Portal_Sleepywood_OutPerrion_1();
            }
            case "OutPerrion_2" -> {
                // 105100100
                return new Portal_Sleepywood_OutPerrion_2();
            }
            case "outTemple" -> {
                // 270000100, タイムロード : 時間の神殿
                return new Portal_TimeTemple_OutTemple();
            }
            case "pachinko" -> {
                // 809030000,  : パチンコ屋
                return new Portal_Zipangu_Pachinko();
            }
            case "pachinkoEnter" -> {
                // 801000300, ジパング : ショーワ町通り
                // 810000000, ルディブリアム : パチンコ屋入口
                return new Portal_Zipangu_PachinkoEnter();
            }
            case "party3_gardenin" -> {
                // 920010800, 女神の塔 : 庭園
                return new Portal_Orbis_Party3_gardenin();
            }
            case "party3_jail1" -> {
                // 920010900, 女神の塔 : 罪人の部屋
                return new Portal_Orbis_Party3_jail();
            }
            case "party3_jail2" -> {
                // 920010900, 女神の塔 : 罪人の部屋
                return new Portal_Orbis_Party3_jail();
            }
            case "party3_jail3" -> {
                // 920010900, 女神の塔 : 罪人の部屋
                return new Portal_Orbis_Party3_jail();
            }
            case "party3_jailin" -> {
                // 920010910, 女神の塔 : 監獄１
                // 920010920, 女神の塔 : 監獄2
                // 920010930, 女神の塔 : 監獄3
                return new Portal_Orbis_Party3_jailin();
            }
            case "party3_r4pt" -> {
                // 920010600, 女神の塔 : ラウンジ
                return new Portal_Orbis_Party3_r4pt();
            }
            case "party3_r6pt" -> {
                // 920010700, 女神の塔 : 登り道
                return new Portal_Orbis_Party3_r6pt();
            }
            case "party3_room1" -> {
                // 920010100, 女神の塔 : 中央塔
                return new Portal_Orbis_Party3_room();
            }
            case "party3_room2" -> {
                // 920010100, 女神の塔 : 中央塔
                return new Portal_Orbis_Party3_room();
            }
            case "party3_room3" -> {
                // 920010100, 女神の塔 : 中央塔
                return new Portal_Orbis_Party3_room();
            }
            case "party3_room4" -> {
                // 920010100, 女神の塔 : 中央塔
                return new Portal_Orbis_Party3_room();
            }
            case "party3_room5" -> {
                // 920010100, 女神の塔 : 中央塔
                return new Portal_Orbis_Party3_room();
            }
            case "party3_room6" -> {
                // 920010100, 女神の塔 : 中央塔
                return new Portal_Orbis_Party3_room();
            }
            case "party3_room8" -> {
                // 920010100, 女神の塔 : 中央塔
                return new Portal_Orbis_Party3_room8();
            }
            case "party3_roomout" -> {
                // 920010200, 女神の塔 : 散歩路
                // 920010300, 女神の塔 : 倉庫
                // 920010400, 女神の塔 : 休憩室
                // ...
                return new Portal_Orbis_Party3_roomout();
            }
            case "party6_out" -> {
                // 930000600, エリン森 : 毒の森
                return new Portal_Ellinia_Party6();
            }
            case "party6_stage" -> {
                // 930000000, エリン森 : 森の前
                // 930000010, エリン森 : 森入口
                // 930000100, エリン森 : 森の端
                // ...
                return new Portal_Ellinia_Party6();
            }
            case "party6_stage501" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage501();
            }
            case "party6_stage502" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage502();
            }
            case "party6_stage503" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage503();
            }
            case "party6_stage504" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage504();
            }
            case "party6_stage505" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage505();
            }
            case "party6_stage506" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage506();
            }
            case "party6_stage507" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage507();
            }
            case "party6_stage508" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage508();
            }
            case "party6_stage509" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage509();
            }
            case "party6_stage510" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage510();
            }
            case "party6_stage511" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage511();
            }
            case "party6_stage512" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage512();
            }
            case "party6_stage513" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage513();
            }
            case "party6_stage514" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage514();
            }
            case "party6_stage515" -> {
                // 930000300, エリン森 : 霧の森
                return new Portal_Ellinia_Party6_stage515();
            }
            case "party6_stage800" -> {
                // 930000800, エリン森 : 森出口
                return new Portal_Ellinia_Party6_stage800();
            }
            case "PB_wich" -> {
                // 980040000, 魔女の塔 : 魔女の塔入口
                return new Portal_MonsterCarnival_PB_wich();
            }
            case "pField_out" -> {
                // 390009999, ユニークロード : 宝倉庫出口
                return new Portal_Event_PField_out();
            }
            case "photoOut" -> {
                // 970000000, メイプルスタジオ : メイプルスタジオ
                return new Portal_Event_PhotoOut();
            }
            case "Pianus" -> {
                // 230040410, アクアロード : 危険な洞窟
                return new Portal_Aquarium_Pianus();
            }
            case "piramid_Chat00" -> {
                // 926010000, 隠れ道 : ピラミッドの丘
                return new Portal_Ariant_Piramid_Chat00();
            }
            case "piramid_in00" -> {
                // 926010000, 隠れ道 : ピラミッドの丘
                return new Portal_Ariant_Piramid_in00();
            }
            case "Populatus00" -> {
                // 220080000, ルディブリアム : 時計塔の奥
                return new Portal_Ludibrium_Populatus00();
            }
            case "PPinkOut" -> {
                // 270050300, 神殿の奥 : 黄昏の黎明の間
                return new Portal_TimeTemple_PPinkOut();
            }
            case "pqEnterPortal" -> {
                // 803000502
                return new Portal_Zipangu_PqEnterPortal();
            }
            case "q2073" -> {
                // 100030000, ヘネシス : 東の森
                return new Portal_Henesys_Q2073();
            }
            case "rand_ola" -> {
                // 109030001, 隠れ道 : のぼれ～のぼれ～＜1段階＞
                // 109030002, 隠れ道 : のぼれ～のぼれ～＜２段階＞
                // 109030003, 隠れ道 : のぼれ～のぼれ～＜３段階＞
                return new Portal_Event_Rand_ola();
            }
            case "rankRoom" -> {
                // 100000201, ヘネシス : 弓使い学院
                // 101000003, エリニア : 魔法図書館
                // 102000003, ぺリオン : 戦士の聖殿
                // ...
                return new Portal_Common_RankRoom();
            }
            case "reundodraco" -> {
                // 240000110, リプレ : ステーション&lt;オルビス行き&gt;
                return new Portal_Leafre_Reundodraco();
            }
            case "rienCaveEnter" -> {
                // 140010200, スノーアイランド : 氷原野
                return new Portal_Rien_RienCaveEnter();
            }
            case "rienTutor1" -> {
                // 140090100, スノーアイランド : 冷たい森1
                return new Portal_Rien_RienTutor1();
            }
            case "rienTutor2" -> {
                // 140090200, スノーアイランド : 冷たい森2
                return new Portal_Rien_RienTutor2();
            }
            case "rienTutor3" -> {
                // 140090300, スノーアイランド : 冷たい森3
                return new Portal_Rien_RienTutor3();
            }
            case "rienTutor4" -> {
                // 140090400, スノーアイランド : 冷たい森4
                return new Portal_Rien_RienTutor4();
            }
            case "rienTutor5" -> {
                // 140090500, スノーアイランド : 冷たい森5
                return new Portal_Rien_RienTutor5();
            }
            case "rienTutor6" -> {
                // 140090500, スノーアイランド : 冷たい森5
                return new Portal_Rien_RienTutor6();
            }
            case "rienTutor7" -> {
                // 140010000, スノーアイランド : リエン西のフィールド
                return new Portal_Rien_RienTutor7();
            }
            case "rienTutor8" -> {
                // 140000000, スノーアイランド : リエン村
                return new Portal_Rien_RienTutor8();
            }
            case "rnj12_in" -> {
                // 926100400,  : 中央研究室入口
                return new Portal_Ariant_Rnj12_in();
            }
            case "rnj1_out" -> {
                // 926100001,  : 暗い通路
                return new Portal_Ariant_Jnr1_out();
            }
            case "rnj3_in0" -> {
                // 926100200,  : 特殊な実験室
                return new Portal_Ariant_Jnr3_in();
            }
            case "rnj3_in1" -> {
                // 926100200,  : 特殊な実験室
                return new Portal_Ariant_Jnr3_in();
            }
            case "rnj_201_0" -> {
                // 926100201,  : 暗い研究室1
                return new Portal_Ariant_Jnr3_in();
            }
            case "rnj_202" -> {
                // 926100202,  : 暗い研究室2
                return new Portal_Ariant_Jnr3_in();
            }
            case "rnj_exit" -> {
                // 926100700,  : 出口
                // 926110700,  : 出口
                return new Portal_Ariant_Rnj_exit();
            }
            case "ropeEarth1" -> {
                // 922230000, 隠れ道 : 月の国
                return new Portal_Ludibrium_RopeEarth1();
            }
            case "ropeEarth2" -> {
                // 922230001, 隠れ道 : 月の穴
                return new Portal_Ludibrium_RopeEarth2();
            }
            case "ropeMoon1" -> {
                // 922230001, 隠れ道 : 月の穴
                return new Portal_Ludibrium_RopeMoon1();
            }
            case "ropeMoon2" -> {
                // 922230002, 隠れ道 : 月の下
                return new Portal_Ludibrium_RopeMoon2();
            }
            case "s4berserk" -> {
                // 105090800, 中央ダンジョン : 神殿の入口4
                return new Portal_Sleepywood_S4berserk();
            }
            case "s4berserk_move" -> {
                // 910500200,  : 忘れられた神殿
                return new Portal_Victoria_S4berserk_move();
            }
            case "s4common1_clear" -> {
                // 921100300,  : タイラスを守れ
                return new Portal_ElNath_S4common1_clear();
            }
            case "s4firehawk" -> {
                // 211042200, エルナス : 試練の洞窟3
                return new Portal_ElNath_S4firehawk();
            }
            case "s4hitman" -> {
                // 101030104, ぺリオン : 遺跡発掘ベースキャンプ
                return new Portal_Ellinia_S4hitman();
            }
            case "s4iceeagle" -> {
                // 211040700, エルナス : 危ない絶壁
                return new Portal_ElNath_S4iceeagle();
            }
            case "s4nest" -> {
                // 240010700, リプレ : 空の巣1
                return new Portal_Leafre_S4nest();
            }
            case "s4resur_enter" -> {
                // 220070400, ルディブリアム : 忘れられた回廊
                return new Portal_Ludibrium_S4resur_enter();
            }
            case "s4resur_out" -> {
                // 922020000,  : 忘れられた闇
                return new Portal_Ludibrium_S4resur_out();
            }
            case "s4resurrection" -> {
                // 230040300, アクアロード : 危険な海峡2
                return new Portal_Aquarium_S4resurrection();
            }
            case "s4rush" -> {
                // 105090700, 中央ダンジョン : 神殿の入口3
                return new Portal_Sleepywood_S4rush();
            }
            case "s4tornado_enter" -> {
                // 220010001, ルディブリアム : 雲のバルコニー
                return new Portal_Ludibrium_S4tornado_enter();
            }
            case "secretDoor" -> {
                // 261010000, ジェニミスト研究所 : 研究所1階廊下
                // 261020200, アルカドノ研究所 : 研究所B-1区域
                return new Portal_Magatia_SecretDoor();
            }
            case "secretgate1_open" -> {
                // 990000610,  : 水路の迷路1
                return new Portal_GuildQuest_GateOpen();
            }
            case "secretgate2_open" -> {
                // 990000630,  : 水路の迷路3
                return new Portal_GuildQuest_GateOpen();
            }
            case "secretgate3_open" -> {
                // 990000640,  : 水路の迷路4
                return new Portal_GuildQuest_GateOpen();
            }
            case "Sky_BossOut" -> {
                // 240080800, リプレ : 天空の巣
                return new Portal_Leafre_Sky_BossOut();
            }
            case "Sky_BossSummon" -> {
                // 240080800, リプレ : 天空の巣
                return new Portal_Leafre_Sky_BossSummon();
            }
            case "Sky_Next" -> {
                // 240080100, リプレ : 天空地域1
                // 240080200, リプレ : 天空地域2
                // 240080300, リプレ : 天空地域3
                // ...
                return new Portal_Leafre_Sky_Next();
            }
            case "Sky_Previous" -> {
                // 240080200, リプレ : 天空地域2
                // 240080300, リプレ : 天空地域3
                // 240080400, リプレ : 天空地域4
                // ...
                return new Portal_Leafre_Sky_Previous();
            }
            case "Sky_ReviveOut" -> {
                // 240080040, リプレ : 天空の復活場
                // 240080041, リプレ : 天空の復活場
                // 240080050, リプレ : 亡子の洞窟
                // ...
                return new Portal_Leafre_Sky_ReviveOut();
            }
            case "speargate_open" -> {
                // 990000400,  : 騎士のホール
                return new Portal_GuildQuest_GateOpen();
            }
            case "statuegate_open" -> {
                // 990000300,  : シャレニアン城門
                return new Portal_GuildQuest_GateOpen();
            }
            case "stonegate_open" -> {
                // 990000400,  : 騎士のホール
                return new Portal_GuildQuest_GateOpen();
            }
            case "stopIceWall" -> {
                // 914100020, 眠った島 : 眠った洞窟
                return new Portal_Rien_StopIceWall();
            }
            case "stopIceWall2" -> {
                // 914100022, 眠った島 : 眠った洞窟
                return new Portal_Rien_StopIceWall2();
            }
            case "StudioZone_Out" -> {
                // 970000100, 隠れ道 : スタジオ控え室
                return new Portal_Event_StudioZone_Out();
            }
            case "subway_in2" -> {
                // 103000100, 地下鉄 : 切符売り場
                return new Portal_Kerning_Subway_in2();
            }
            case "summondragon" -> {
                // 240040611, リプレ : ナインスピリット
                return new Portal_Leafre_Summondragon();
            }
            case "tamepig_out2" -> {
                // 923010100,  : 飼育室通路
                return new Portal_Aquarium_Tamepig_out2();
            }
            case "TD_MC_first" -> {
                // 100000002, 隠れ道 : 空き家
                return new Portal_Henesys_TD_MC_first();
            }
            case "templeenter" -> {
                // 200090510, 飛行中 : ミナルへの道
                return new Portal_Orbis_Templeenter();
            }
            case "tH_Out" -> {
                // 980040000, 魔女の塔 : 魔女の塔入口
                return new Portal_MonsterCarnival_TH_Out();
            }
            case "thief_in1" -> {
                // 260010401, バーニングロード : 岩坂
                return new Portal_Ariant_Thief_in1();
            }
            case "timeQuest" -> {
                // 270010100, タイムロード : 思い出の道1
                // 270010200, タイムロード : 思い出の道2
                // 270010300, タイムロード : 思い出の道3
                // ...
                return new Portal_TimeTemple_TimeQuest();
            }
            case "TokPQ1" -> {
                // 802000801, ジパング : 六本木モール2102年(エントランス)
                return new Portal_Zipangu_TokPQ1();
            }
            case "Tokyoboss1_go" -> {
                // 802000209, ジパング : お台場 2100年
                return new Portal_Zipangu_Tokyoboss1_go();
            }
            case "Tokyoboss2_go" -> {
                // 802000309, ジパング : 公園 2095年
                return new Portal_Zipangu_Tokyoboss2_go();
            }
            case "Tokyoboss3_go" -> {
                // 802000409, ジパング : 秋葉原 2102年
                return new Portal_Zipangu_Tokyoboss3_go();
            }
            case "Tokyoboss4_go" -> {
                // 802000609, ジパング : 旗艦ファイア・オールドフォックス甲板 2102年
                return new Portal_Zipangu_Tokyoboss4_go();
            }
            case "Tokyoboss5_go" -> {
                // 802000109, ジパング : カムナ
                return new Portal_Zipangu_Tokyoboss5_go();
            }
            case "Tokyoboss6_go" -> {
                // 802000709, ジパング : 渋谷2102年
                return new Portal_Zipangu_Tokyoboss6_go();
            }
            case "Tokyoboss7_go" -> {
                // 802000819, ジパング : 六本木モール最上階2102年
                return new Portal_Zipangu_Tokyoboss7_go();
            }
            case "tristanEnter" -> {
                // 105100100
                return new Portal_Sleepywood_TristanEnter();
            }
            case "tutoChatNPC" -> {
                // 10000, メイプルロード : キノコの丘
                return new Portal_MapleIsland_TutoChatNPC();
            }
            case "tutorHelper" -> {
                // 130030000, クィーンズロード : 開始の森1
                return new Portal_Ereve_TutorHelper();
            }
            case "tutorialNPC" -> {
                // 100000201, ヘネシス : 弓使い学院
                // 101000003, エリニア : 魔法図書館
                // 102000003, ぺリオン : 戦士の聖殿
                // ...
                return new Portal_Victoria_TutorialNPC();
            }
            case "tutorMinimap" -> {
                // 130030001, クィーンズロード : 開始の森2
                return new Portal_Ereve_TutorMinimap();
            }
            case "tutorquest" -> {
                // 130030001, クィーンズロード : 開始の森2
                // 130030002, クィーンズロード : 開始の森3
                // 130030003, クィーンズロード : 開始の森4
                // ...
                return new Portal_Ereve_Tutorquest();
            }
            case "tutorWorldmap" -> {
                // 130030006, クィーンズロード : 小さな橋
                return new Portal_Ereve_TutorWorldmap();
            }
            case "under30gate" -> {
                // 990000600,  : 地下水路
                return new Portal_GuildQuest_Under30gate();
            }
            case "undodraco" -> {
                // 200090500, 飛行中 : 時間の神殿への道
                // 200090510, 飛行中 : ミナルへの道
                return new Portal_Orbis_Undodraco();
            }
            case "unityPortal2" -> {
                // 100000000, ヘネシス : ヘネシス
                // 101000000, エリニア : エリニア
                // 102000000, ぺリオン : ぺリオン
                // ...
                return new Portal_Common_UnityPortal2();
            }
            case "visitor_energy" -> {
                // 502022010, 未来のある日 : エネルギー研究所への道
                return new Portal_OSSS_Visitor_energy();
            }
            case "visitor_jinbee82" -> {
                // 502021010, 未来のある日 : 未来のヘネシス外郭
                return new Portal_OSSS_Visitor_jinbee82();
            }
            case "visitor_time_p" -> {
                // 502021010, 未来のある日 : 未来のヘネシス外郭
                return new Portal_OSSS_Visitor_time_p();
            }
            case "visitorhidenPT" -> {
                // 502010010, 現在のある日 : 地下道入口
                return new Portal_OSSS_VisitorhidenPT();
            }
            case "visitorPT_R" -> {
                // 502030000, 母船 : 進入キューブ
                // 502030001, 母船 : 次元キューブ
                // 502030003, 母船 : エネルギーキューブ
                // ...
                return new Portal_OSSS_VisitorPT_R();
            }
            case "warpEarth" -> {
                // 922230002, 隠れ道 : 月の下
                return new Portal_Ludibrium_WarpEarth();
            }
            case "warpMoon" -> {
                // 922231000, 隠れ道 : 月うさぎの遊び場
                return new Portal_Ludibrium_WarpMoon();
            }
            case "watergate_open" -> {
                // 990000500,  : 賢者の噴水
                return new Portal_GuildQuest_GateOpen();
            }
            case "Zakum03" -> {
                // 280010000, 閉鉱 : 知られざる閉鉱
                return new Portal_ElNath_Zakum03();
            }
            case "Zakum05" -> {
                // 211042300, エルナス : ジャクムへの門
                return new Portal_ElNath_Zakum05();
            }
            default -> {
                return null;
            }
        }
    }
}
