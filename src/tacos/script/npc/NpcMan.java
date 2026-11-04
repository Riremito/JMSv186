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
package tacos.script.npc;

import tacos.client.TacosCharacter;
import tacos.wz.NpcWz.NpcInfo;

/**
 *
 * @author Riremito
 */
public class NpcMan {

    public static boolean start(TacosCharacter chr, NpcInfo npc_info, int npc_script_id, int npc_id) {
        NpcScriptMan npc_script;
        switch (npc_script_id) {
            case 2003 -> {
                // 2003, ロビン
                npc_script = new NpcRobin();
            }
            case 2007 -> {
                // 2007, イベントガイド
                // 10000, メイプルロード : キノコの丘
                npc_script = new NpcEventGuide();
            }
            case 10200 -> {
                // 10200, ヘレナ
                npc_script = new NpcAthenaPierceTutorial();
            }
            case 10201 -> {
                // 10201, ハインズ
                npc_script = new NpcGrendelTheReallyOldTutorial();
            }
            case 10202 -> {
                // 10202, コブシを開いて立て
                npc_script = new NpcDancesWithBalrogTutorial();
            }
            case 10203 -> {
                // 10203, ダークロード
                npc_script = new NpcDarkLordTutorial();
            }
            case 10204 -> {
                // 10204, カイリン
                npc_script = new NpcKyrinTutorial();
            }
            case 12100 -> {
                // 12100, マイ
                npc_script = new NpcMai();
            }
            case 12101 -> {
                // 12101, レイン
                npc_script = new NpcRain();
            }
            case 22000 -> {
                // 22000, シャンクス
                npc_script = new NpcShanks();
            }
            case 1002000 -> {
                // 1002000, ピル
                // 104000000, 港口
                npc_script = new NpcPhil();
            }
            case 1002002 -> {
                // 1002002, ペィソン
                // 104000000, 港口
                npc_script = new NpcPason();
            }
            case 1002003 -> {
                // 1002003, モンロンジジ
                // 104000000, 港口
                npc_script = new NpcMrGoldstein();
            }
            case 1002004, 1032005 -> {
                // 1002004, リス港口高級タクシー
                // 104000000, 港口
                // 1032005, エリニア高級タクシー
                // 101000000, エリニア
                npc_script = new NpcVIPCab();
            }
            case 1002006 -> {
                // 1002006, チェフ
                // 104000000, 港口
                npc_script = new NpcChef();
            }
            case 1002007 -> {
                // 1002007, リス港口中型タクシー
                // 104000000, 港口
                npc_script = new NpcRegularCabAtLithHarbor();
            }
            case 1002100 -> {
                // 1002100, ジェーン
                // 104000000, 港口
                npc_script = new NpcJane();
            }
            case 1002101 -> {
                // 1002101, クン
                // 104000000, 港口
                npc_script = new NpcOlaf();
            }
            case 1002103 -> {
                // 1002103, アール
                // 104000000, 港口
                // 200000200, オルビス公園
                npc_script = new NpcCaptainAl();
            }
            case 1012000, 1022001, 1032000, 1052016 -> {
                // 1012000, メイプル運輸大型タクシー
                // 100000000, ヘネシス
                // 101010000, エリニアの北側フィールド
                // 101010100, そびえ立つ木1
                // 101010101, そびえ立つ木2
                // 101010102, そびえ立つ木3
                // 101020000, 北の森
                // 1022001, メイプル運輸大型タクシー
                // 101030101, 遺跡発掘地1
                // 102000000, ぺリオン
                // 1032000, メイプル運輸大型タクシー
                // 101000000, エリニア
                // 1052016, メイプル運輸大型タクシー
                // 103000000, カニングシティー
                npc_script = new NpcRegularCab();
            }
            case 1012005 -> {
                // 1012005, クロイ
                // 100000200, 広場
                npc_script = new NpcCloy();
            }
            case 1012007 -> {
                // 1012007, プロド
                // 100000202, ヘネシス : ペットの散歩路
                npc_script = new NpcTrainerFrod();
            }
            case 1012008 -> {
                // 1012008, カイジ
                npc_script = new NpcCasey();
            }
            case 1012100 -> {
                // 1012100, ヘレナ
                // 100000201, 弓使い学院
                npc_script = new NpcAthenaPierce();
            }
            case 1012103 -> {
                // 1012103, ナタリー
                // 100000104, ヘネシス : 美容院
                npc_script = new NpcNatalie();
            }
            case 1012104 -> {
                // 1012104, ブリトニー
                // 100000104, ヘネシス : 美容院
                npc_script = new NpcBrittany();
            }
            case 1012105 -> {
                // 1012105, 私美女
                // 100000105, ヘネシス : スキンケアーショップ
                npc_script = new NpcMsTan();
            }
            case 1012115 -> {
                // 1012115, ヘネシス草むら
                // 100020000, ヘネシス : 東の草むら
                npc_script = new NpcHenesysForest();
            }
            case 1012116 -> {
                // 1012116, ヘネシス草むら
                // 100020000, ヘネシス : 東の草むら
                npc_script = new NpcHenesysForest();
            }
            case 1012119 -> {
                // 1012119, ヘンケル
                // 100010000, ヘネシス : 東の丘
                npc_script = new NpcHenkel();
            }
            case 1013001 -> {
                // 1013001, ドラゴン
                // 900010200, 夢の中 : 夢見る森
                npc_script = new NpcDragon();
            }
            case 1013002 -> {
                // 1013002, ドラゴンの巣
                // 900020220, 隠された通り : 忘れてしまった森
                npc_script = new NpcDragonNest();
            }
            case 1013104 -> {
                // 1013104, 卵の箱
                // 100030102, 一軒家 : 前庭
                npc_script = new NpcHen();
            }
            case 1013200 -> {
                // 1013200, 赤ちゃんオオカミ
                // 900020100, 隠された通り : 鬱蒼とした森
                npc_script = new NpcPiglet();
            }
            case 1013207 -> {
                // 1013207, クン
                // 200090080, 용이 잠든 섬으로
                // 200090090, 리스항구행
                // 914100000, 臨時港
                npc_script = new NpcOlafTemporaryHarbor();
            }
            case 1022000 -> {
                // 1022000, コブシを開いて立て
                // 102000003, 戦士の聖殿
                npc_script = new NpcDancesWithBalrog();
            }
            case 1022105 -> {
                // 1022105, ヘンケル
                // 101040000, ぺリオン : 分かれ道-東
                npc_script = new NpcHenkel();
            }
            case 1022107 -> {
                // 1022107, ペリオン警告板
                // 101030000, ぺリオン : 東の峰
                npc_script = new NpcPerionWarningPost();
            }
            case 1032001 -> {
                // 1032001, ハインズ
                // 101000003, 魔法図書館
                npc_script = new NpcGrendelTheReallyOld();
            }
            case 1032003 -> {
                // 1032003, シェイン
                // 101000000, エリニア
                npc_script = new NpcShane();
            }
            case 1032004 -> {
                // 1032004, ルイス
                // 101000100, 忍耐の森-1段階
                // 101000101, 忍耐の森-2段階
                // 101000102, 忍耐の森-3段階
                // 101000103, 忍耐の森-4段階
                // 101000104, 忍耐の森-5段階
                npc_script = new NpcLouis();
            }
            case 1032007 -> {
                // 1032007, ジョエル
                // 101000300, ビクトリアロード : エリニアステーション
                npc_script = new NpcShipTicketSeller();
            }
            case 1032008 -> {
                // 1032008, チェリ
                // 101000300, エリニアステーション
                npc_script = new NpcCherry();
            }
            case 1032009 -> {
                // 1032009, プリン
                // 101000301, ビクトリアロード : 控え室<オルビス行き>
                npc_script = new NpcWaitingRoomExit();
            }
            case 1032100 -> {
                // 1032100, 妖精アルウェン
                // 101000000, エリニア
                npc_script = new NpcArwenTheFairy();
            }
            case 1032102 -> {
                // 1032102, 妖精マル
                npc_script = new NpcMarTheFairy();
            }
            case 1032111 -> {
                // 1032111, 小さい切り株
                // 101010103, ビクトリアロード : 渓流<バンジージャンプ台>
                npc_script = new NpcSmallTreeStump();
            }
            case 1032113 -> {
                // 1032113, ヘンケル
                // 910120000, ビクトリアロード : 魔法使い修行場
                npc_script = new NpcHenkel();
            }
            case 1032114 -> {
                // 1032114, ヘンケル
                // 100050000, エリニア : 南フィールド
                npc_script = new NpcHenkel();
            }
            case 1040000 -> {
                // 1040000, ルーク
                // 106010100, へネシス : ダンジョンの入口
                npc_script = new NpcLuke();
            }
            case 1040001 -> {
                // 1040001, マイク
                // 106000300, ダンジョンの入口
                npc_script = new NpcMike();
            }
            case 1043000 -> {
                // 1043000, 花の摘み
                // 101000101, 忍耐の森-2段階
                npc_script = new NpcPileOfFlowers();
            }
            case 1043001 -> {
                // 1043001, 薬草の藪
                // 101000104, 忍耐の森-5段階
                npc_script = new NpcPileOfHerbs();
            }
            case 1052001 -> {
                // 1052001, ダークロード
                // 103000003, 盗賊のアジト
                npc_script = new NpcDarkLord();
            }
            case 1052004 -> {
                // 1052004, 院長デンマ
                // 100000103, ヘネシス : 整形外科
                npc_script = new NpcDenma();
            }
            case 1052005 -> {
                // 1052005, 医者ヘッポコ
                // 100000103, ヘネシス : 整形外科
                npc_script = new NpcDrFeeble();
            }
            case 1052006 -> {
                // 1052006, ウンイ
                // 103000100, 切符売り場
                npc_script = new NpcSubwayTicketSeller();
            }
            case 1052007 -> {
                // 1052007, 改札口
                npc_script = new NpcTheTicketGate();
            }
            case 1052008, 1052009, 1052010 -> {
                // 1052008, 宝箱
                // 103000902, B1<地下鉄基地>
                // 1052009, 宝箱
                // 103000905, B2<地下鉄基地>
                // 1052010, 宝箱
                // 103000909, B3<地下鉄基地>
                npc_script = new NpcSubwayTreasureBox();
            }
            case 1052011 -> {
                // 1052011, 出口
                // 103000900, 103000901, 103000903, 103000904, 103000906, 103000907, 103000908
                npc_script = new NpcSubwayExit();
            }
            case 1052012 -> {
                // 1052012, モンロン
                // 103000000, カニングシティー
                npc_script = new NpcMongFromKong();
            }
            case 1052013 -> {
                // 1052013, パソコン
                // 193000000, ネットカフェ
                npc_script = new NpcComputer();
            }
            case 1052100 -> {
                // 1052100, ドンジオバネ
                // 103000005, カニングシティー : 美容院
                npc_script = new NpcDonGiovanni();
            }
            case 1052101 -> {
                // 1052101, アンドレア
                // 103000005, カニングシティー : 美容院
                npc_script = new NpcAndre();
            }
            case 1052107 -> {
                // 1052107, 小さな街灯
                // 103000105, 地下鉄 : 1号線-4区
                npc_script = new NpcSmallStreetLight();
            }
            case 1052113 -> {
                // 1052113, ヘンケル
                // 910310000, ビクトリアロード : 盗賊修行場
                npc_script = new NpcHenkel();
            }
            case 1052114 -> {
                // 1052114, ヘンケル
                // 103010000, カニングシティー : 工事現場
                npc_script = new NpcHenkel();
            }
            case 1052115 -> {
                // 1052115, 林次長
                npc_script = new NpcMrLim();
            }
            case 1052125 -> {
                // 1052125, ガードマン
                // 103040400, 7階 8階 A区域
                npc_script = new NpcJune();
            }
            case 1057001 -> {
                // 1057001, 紅
                // 103000000, カニングシティー : カニングシティー
                npc_script = new NpcRyden();
            }
            case 1057002 -> {
                // 1057002, 睦実
                // 103050100, 秘桜蔭 2階
                npc_script = new NpcYun();
            }
            case 1061006 -> {
                // 1061006, 変な形の石像
                // 105040300, スリーピーウッド
                npc_script = new NpcMysteriousStatue();
            }
            case 1061007 -> {
                // 1061007, 崩れている石像
                npc_script = new NpcCrumblingStatue();
            }
            case 1061009 -> {
                // 1061009, 次元の扉
                // 100040106, 隠れ道 : 邪気の森2
                // 105040305, 中央ダンジョン : スリーピーダンジョン5
                // 105070001, 中央ダンジョン : アリの巣-広場
                // 105070200, 中央ダンジョン : エビルアイの巣２
                // 107000402, 隠れ道 : ルーパン沼2
                npc_script = new NpcDoorOfDimension();
            }
            case 1061010 -> {
                // 1061010, 光る水晶
                // 108010101, 隠れ道 : 次元の世界
                // 108010201, 隠れ道 : 次元の世界
                // 108010301, 隠れ道 : 次元の世界
                // 108010401, 隠れ道 : 次元の世界
                // 108010501, 隠れ道 : 次元の世界
                // 804000710, 隠れ道 : 次元の世界
                npc_script = new NpcGlimmeringCrystal();
            }
            case 1061100 -> {
                // 1061100, ホテルガイド
                // 105040400, スリーピーホテルロビー
                npc_script = new NpcHotelReceptionist();
            }
            case 1063000, 1063001, 1063002 -> {
                // 1063000, 桃色の花山
                // 105040311, 忍耐の森「2段階」
                // 1063001, 青色の花山
                // 105040313, 忍耐の森「4段階」
                // 1063002, 白色の花山
                // 105040316, 忍耐の森「7段階」
                npc_script = new NpcPileOfViolas();
            }
            case 1072000 -> {
                // 1072000, 戦士転職教官
                // 102020300, 西の岩山4
                npc_script = new NpcWarriorJobInstructor();
            }
            case 1072001 -> {
                // 1072001, 魔法使い転職教官
                // 101020000, 北の森
                npc_script = new NpcMagicianJobInstructor();
            }
            case 1072002 -> {
                // 1072002, 弓使い転職教官
                // 106010000, ダンジョンへの道
                npc_script = new NpcBowmanJobInstructor();
            }
            case 1072003 -> {
                // 1072003, 盗賊転職教官
                // 102040000, 工事現場-北
                npc_script = new NpcThiefJobInstructor();
            }
            case 1072004 -> {
                // 1072004, 戦士転職教官
                // 108000300, 戦士の岩山1
                // 108000301, 戦士の岩山2
                // 108000302, 戦士の岩山3
                npc_script = new NpcWarriorJobInstructorTest();
            }
            case 1072005 -> {
                // 1072005, 魔法使い転職教官
                // 108000200, 木のダンジョン1
                // 108000201, 木のダンジョン2
                // 108000202, 木のダンジョン3
                npc_script = new NpcMagicianJobInstructorTest();
            }
            case 1072006 -> {
                // 1072006, 弓使い転職教官
                // 108000100, アリの巣1
                // 108000101, アリの巣2
                // 108000102, アリの巣3
                npc_script = new NpcBowmanJobInstructorTest();
            }
            case 1072007 -> {
                // 1072007, 盗賊転職教官
                // 108000400, 盗賊の工事現場1
                // 108000401, 盗賊の工事現場2
                // 108000402, 盗賊の工事現場3
                npc_script = new NpcThiefJobInstructorTest();
            }
            case 1072008 -> {
                // 1072008, カイリン
                // 108000500, 隠れ道 : 海賊のテスト場
                npc_script = new NpcKyrinTestRoom();
            }
            case 1081001 -> {
                // 1081001, パイソン
                npc_script = new NpcPison();
            }
            case 1090000 -> {
                // 1090000, カイリン
                // 120000101, 航海室
                // 912010200, カイリンの訓練場
                npc_script = new NpcKyrin();
            }
            case 1092000 -> {
                // 1092000, タンユン
                // 120000103, ノーチラス : 食堂
                npc_script = new NpcTangyoon();
            }
            case 1092007 -> {
                // 1092007, ムラト
                // 120000100, 上階廊下
                npc_script = new NpcMuirhat();
            }
            case 1092008 -> {
                // 1092008, シュリンツ
                // 120000104, 訓練場
                npc_script = new NpcShulynch();
            }
            case 1092010 -> {
                // 1092010, ジャック
                // 120000100, ノーチラス : 上階廊下
                npc_script = new NpcJackNautilus();
            }
            case 1092014 -> {
                // 1092014, ノーチラス大型タクシー
                // 120000000, ノーチラス
                npc_script = new NpcNautilusMidSizedTaxi();
            }
            case 1092016 -> {
                // 1092016, 輝く石
                // 120000301, 動力室
                npc_script = new NpcShinyStone();
            }
            case 1092018 -> {
                // 1092018, ゴミ箱
                // 120000100, ノーチラス : 上階廊下
                npc_script = new NpcNautilusTrashCan();
            }
            case 1092019 -> {
                // 1092019, ジョナサン
                // 120000102, ジョナサンの部屋
                npc_script = new NpcLordJonathan();
            }
            case 1094002 -> {
                // 1094002, 草原
                // 120000000, ノーチラス船着場 : ノーチラス
                npc_script = new NpcNautilusBush();
            }
            case 1094003 -> {
                // 1094003, 草原
                // 120000000, ノーチラス船着場 : ノーチラス
                npc_script = new NpcNautilusBush();
            }
            case 1094004 -> {
                // 1094004, 草原
                // 120000000, ノーチラス船着場 : ノーチラス
                npc_script = new NpcNautilusBush();
            }
            case 1094005 -> {
                // 1094005, 草原
                // 120000000, ノーチラス船着場 : ノーチラス
                npc_script = new NpcNautilusBush();
            }
            case 1094006 -> {
                // 1094006, 草原
                // 120000000, ノーチラス船着場 : ノーチラス
                npc_script = new NpcNautilusBush();
            }
            case 1095001 -> {
                // 1095001, ヘンケル
                // 912030000, ビクトリアロード : 海賊修行場
                npc_script = new NpcHenkel();
            }
            case 1095002 -> {
                // 1095002, ヘンケル
                // 120010000, ビクトリアロード : 船着場への道
                npc_script = new NpcHenkel();
            }
            case 1100003 -> {
                // 1100003, キリル
                // 130000210, ステーション
                npc_script = new NpcKiriru();
            }
            case 1100004 -> {
                // 1100004, キル
                // 130000210, ステーション
                npc_script = new NpcKiru();
            }
            case 1100007 -> {
                // 1100007, キリル(エリニア)
                // 101000400, ステーション<エレヴ行き>
                npc_script = new NpcKiriruEllinia();
            }
            case 1100008 -> {
                // 1100008, キル(オルビス)
                // 200000161, ステーション<エレヴ行き>
                npc_script = new NpcKiruOrbis();
            }
            case 1101001 -> {
                // 1101001, 神獣
                // 130000000, エレヴ
                npc_script = new NpcShinsoo();
            }
            case 1102001 -> {
                // 1102001, キリコ
                // 108000600, 隠れ道 : 第2訓練場
                npc_script = new NpcKiriko();
            }
            case 1102002 -> {
                // 1102002, キリド
                // 130010220, 孵化場
                npc_script = new NpcKiridu();
            }
            case 1102003 -> {
                // 1102003, キダン
                // 130000100, 騎士の殿堂
                // 130000101, 騎士の殿堂
                npc_script = new NpcKidan();
            }
            case 1103005 -> {
                // 1103005, ナインハート
                // 913040006, オープニング : シグナス騎士団
                npc_script = new NpcNineheartIntro();
            }
            case 1200003 -> {
                // 1200003, プロ
                // 140020300, ペンギン港
                npc_script = new NpcPuroRien();
            }
            case 1200004 -> {
                // 1200004, プロ
                // 104000000, 港口
                npc_script = new NpcPuroLith();
            }
            case 1201000 -> {
                // 1201000, リリン
                npc_script = new NpcLilin();
            }
            case 1202009 -> {
                // 1202009, 番人ヴォルフ
                npc_script = new NpcWolfGuard();
            }
            case 1202010 -> {
                // 1202010, プディン
                npc_script = new NpcPudin();
            }
            case 1204001 -> {
                // 1204001, フランシス
                // 910510200, 隠れ道 : 人形使いの洞窟
                npc_script = new NpcFrancis();
            }
            case 1300014 -> {
                // 1300014, セルフ
                // 106020300, キノコ城 : 奥深きキノコの森
                npc_script = new NpcSelf();
            }
            case 2010000 -> {
                // 2010000, チャーリー軍曹
                // 200000000, オルビス
                npc_script = new NpcStaffSergeantCharlie();
            }
            case 2010001 -> {
                // 2010001, ミノ
                // 200000202, オルビス : オルビスヘアーショップ
                npc_script = new NpcMino();
            }
            case 2010002 -> {
                // 2010002, プランツ
                // 200000201, オルビス : 整形外科
                npc_script = new NpcFranz();
            }
            case 2010005 -> {
                // 2010005, ガイドシュリ
                // 200000000, オルビス
                npc_script = new NpcShuri();
            }
            case 2010007 -> {
                // 2010007, ヘラクル
                // 200000301, ギルド本部<英雄の殿堂>
                npc_script = new NpcHeracle();
            }
            case 2010008 -> {
                // 2010008, レア
                // 200000301, ギルド本部<英雄の殿堂>
                npc_script = new NpcLea();
            }
            case 2010009 -> {
                // 2010009, レナリウ
                // 200000301, ギルド本部<英雄の殿堂>
                npc_script = new NpcLenario();
            }
            case 2012000 -> {
                // 2012000, イフ
                // 200000100, オルビスチケット売場
                npc_script = new NpcAgatha();
            }
            case 2012001 -> {
                // 2012001, リニ
                // 200000111, ステーション<エリニア行き>
                npc_script = new NpcRini();
            }
            case 2012002 -> {
                // 2012002, エリン
                // 200000112, オルビス : 控え室<エリニア行き>
                npc_script = new NpcWaitingRoomExit();
            }
            case 2012006 -> {
                // 2012006, イス
                // 200000100, オルビスチケット売場
                npc_script = new NpcIsaTheStationGuide();
            }
            case 2012007 -> {
                // 2012007, リンス
                // 200000202, オルビス : オルビスヘアーショップ
                npc_script = new NpcRinz();
            }
            case 2012008 -> {
                // 2012008, ロミ
                // 200000203, オルビス : オルビススキンケアショップ
                npc_script = new NpcRomi();
            }
            case 2012009 -> {
                // 2012009, 助手リーザ
                // 200000201, オルビス : 整形外科
                npc_script = new NpcRiza();
            }
            case 2012012 -> {
                // 2012012, リーサ
                // 200000000, オルビス : オルビス
                npc_script = new NpcLisa();
            }
            case 2012013 -> {
                // 2012013, スナ
                // 200000121, ステーション<ルディブリアム行き>
                npc_script = new NpcSunny();
            }
            case 2012014 -> {
                // 2012014, オルビス魔法石
                // 200080200, オルビス塔<20層>
                npc_script = new NpcOrbisMagicSpot();
            }
            case 2012015 -> {
                // 2012015, エルナス魔法石
                // 200082100, オルビス塔<1層>
                npc_script = new NpcElNathMagicSpot();
            }
            case 2012021 -> {
                // 2012021, ラミニ
                // 200000131, ステーション<リプレ行き>
                npc_script = new NpcRamini();
            }
            case 2012022 -> {
                // 2012022, ぺラス
                // 200000132, オルビス : 控え室<リプレ行き>
                npc_script = new NpcWaitingRoomExit();
            }
            case 2012023 -> {
                // 2012023, 紅葉玉
                // 200000300, オルビス : 出会いの丘
                npc_script = new NpcMapleLeafMarble();
            }
            case 2012024 -> {
                // 2012024, イグネト
                // 200000152, オルビス : 控え室<アリアント行き>
                npc_script = new NpcWaitingRoomExit();
            }
            case 2012025 -> {
                // 2012025, ゼラス
                // 200000151, ステーション<アリアント行き>
                npc_script = new NpcGeras();
            }
            case 2013000 -> {
                // 2013000, 妖精ウィンキー
                // 200080101, 見知らぬ塔
                // 920010000, 入口
                npc_script = new NpcWonkyTheFairy();
            }
            case 2013001 -> {
                // 2013001, 侍従イク
                // 920010100, 中央塔
                // 920010200, 散歩路
                // 920010300, 倉庫
                // 920010400, 休憩室
                // 920010500, 封印された部屋
                // 920010600, ラウンジ
                // 920010700, 登り道
                // 920010800, 庭園
                // 920010900, 罪人の部屋
                // 920011000, 暗黒の部屋
                // 920011200, 出口
                npc_script = new NpcChamberlainEak();
            }
            case 2020005 -> {
                // 2020005, アルケスタ
                // 211000100, 市場
                npc_script = new NpcAlcaster();
            }
            case 2020008 -> {
                // 2020008, タイラス
                // 211000001, 長老の官邸
                npc_script = new NpcTylus();
            }
            case 2020009 -> {
                // 2020009, ロベイラ
                // 211000001, 長老の官邸
                npc_script = new NpcRobeira();
            }
            case 2020010 -> {
                // 2020010, レネ
                // 211000001, 長老の官邸
                npc_script = new NpcRene();
            }
            case 2020011 -> {
                // 2020011, アレク
                // 211000001, 長老の官邸
                npc_script = new NpcArec();
            }
            case 2020013 -> {
                // 2020013, ペドロ
                // 211000001, 長老の官邸
                npc_script = new NpcPedro();
            }
            case 2022004 -> {
                // 2022004, タイラス
                // 921100301, タイラス護衛完遂
                npc_script = new NpcTylusEscort();
            }
            case 2023000 -> {
                // 2023000, 危険地域弾丸タクシー
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 240000000, リプレ
                npc_script = new NpcDangerZoneTaxi();
            }
            case 2030000 -> {
                // 2030000, ジェフ
                // 211040200, 氷の谷2
                npc_script = new NpcJeff();
            }
            case 2030006 -> {
                // 2030006, 聖なる岩
                // 211040401, 雪原の聖地
                npc_script = new NpcHolyStone();
            }
            case 2030008 -> {
                // 2030008, アドビス
                // 211042300, ジャクムへの門
                npc_script = new NpcAdobis();
            }
            case 2030010 -> {
                // 2030010, アーモン
                // 280020000, 閉鉱 : 火山の息<1段階>
                npc_script = new NpcAmon();
            }
            case 2030011 -> {
                // 2030011, アーリ
                // 280090000, 閉鉱 : 悲恋の部屋
                npc_script = new NpcAli();
            }
            case 2030014 -> {
                // 2030014, 古代氷石
                // 921100100, 氷の谷
                npc_script = new NpcAncientIcyStone();
            }
            case 2032001 -> {
                // 2032001, スピルナ
                // 200050001, 老婆の家
                npc_script = new NpcSpiruna();
            }
            case 2032003 -> {
                // 2032003, リラー
                // 280020001, 閉鉱 : 火山の息<2段階>
                npc_script = new NpcLira();
            }
            case 2040000 -> {
                // 2040000, モル
                // 220000100, ルディブリアム : チケット売場
                npc_script = new NpcShipTicketSeller();
            }
            case 2040002 -> {
                // 2040002, レフトくん
                // 221024400, エオス塔100階
                npc_script = new NpcOlsonTheToySoldier();
            }
            case 2040014 -> {
                // 2040014, チコ
                npc_script = new NpcChico();
            }
            case 2040019 -> {
                // 2040019, エバー
                // 220000003, ルディブリアム : 整形外科
                npc_script = new NpcEverton();
            }
            case 2040024 -> {
                // 2040024, 一番目のエオス石
                // 221024400, エオス塔100階
                npc_script = new NpcFirstEosRock();
            }
            case 2040025 -> {
                // 2040025, 二番目のエオス石
                npc_script = new NpcSecondEosRock();
            }
            case 2040026 -> {
                // 2040026, 三番目のエオス石
                npc_script = new NpcThirdEosRock();
            }
            case 2040027 -> {
                // 2040027, 四番目のエオス石
                // 221020000, エオス塔1階
                npc_script = new NpcFourthEosRock();
            }
            case 2040028 -> {
                // 2040028, マークくん
                // 922000010, 人形の家
                npc_script = new NpcMarkTheToySoldier();
            }
            case 2040031 -> {
                // 2040031, 文書束
                // 220000304, ルディブリアム : クロイの家
                npc_script = new NpcDocumentRoll();
            }
            case 2040032 -> {
                // 2040032, ウィーバー
                // 220000006, ルディブリアム : ルディブリアムの散歩路
                npc_script = new NpcWeaver();
            }
            case 2040033 -> {
                // 2040033, ネル
                // 220000006, ルディブリアム : ルディブリアムの散歩路
                npc_script = new NpcNeru();
            }
            case 2040046 -> {
                // 2040046, ハリ
                // 220000000, ルディブリアム
                npc_script = new NpcRobertHolly();
            }
            case 2040048 -> {
                // 2040048, ナナ
                // 220000000, ルディブリアム
                npc_script = new NpcNara();
            }
            case 2040050 -> {
                // 2040050, 風来坊錬金術師
                // 105040300, スリーピーウッド
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 240000000, リプレ
                npc_script = new NpcEurekTheAlchemist();
            }
            case 2040052 -> {
                // 2040052, 司書ウィズ
                // 222020000, ヘリオス塔の図書館
                npc_script = new NpcLibrarianWiz();
            }
            case 2041000 -> {
                // 2041000, ティティアン
                // 220000110, ステーション<オルビス行き>
                npc_script = new NpcTian();
            }
            case 2041001 -> {
                // 2041001, エリン
                // 200000122, オルビス : 控え室<ルディブリアム行き>
                npc_script = new NpcWaitingRoomExit();
            }
            case 2041007 -> {
                // 2041007, ミユ
                // 220000004, ルディブリアム : 美容院
                npc_script = new NpcMiyu();
            }
            case 2041009 -> {
                // 2041009, ミニ
                // 220000004, ルディブリアム : 美容院
                npc_script = new NpcMini();
            }
            case 2041010 -> {
                // 2041010, エル
                // 220000003, ルディブリアム : 整形外科
                npc_script = new NpcEllie();
            }
            case 2041013 -> {
                // 2041013, ジナ
                // 220000005, ルディブリアム : スキンケアーショップ
                npc_script = new NpcGina();
            }
            case 2041025 -> {
                // 2041025, 機械装置
                // 220080001, 時計塔の深層部
                npc_script = new NpcMachineApparatus();
            }
            case 2050014 -> {
                // 2050014, 隕石1
                // 221040000, ルディブリアム : クーラン草原1
                npc_script = new NpcMeteorite();
            }
            case 2050015 -> {
                // 2050015, 隕石2
                // 221040200, ルディブリアム : クーラン草原3
                npc_script = new NpcMeteorite();
            }
            case 2050016 -> {
                // 2050016, 隕石3
                // 221040300, ルディブリアム : クーラン草原4
                npc_script = new NpcMeteorite();
            }
            case 2050017 -> {
                // 2050017, 隕石4
                // 221040100, ルディブリアム : クーラン草原2
                npc_script = new NpcMeteorite();
            }
            case 2050018 -> {
                // 2050018, 隕石5
                // 221040201, ルディブリアム : バーナードの草原
                npc_script = new NpcMeteorite();
            }
            case 2050019 -> {
                // 2050019, 隕石6
                // 221040400, ルディブリアム : クーラン草原5
                npc_script = new NpcMeteorite();
            }
            case 2060005 -> {
                // 2060005, ケンタ
                npc_script = new NpcKenta();
            }
            case 2060009 -> {
                // 2060009, イルカ
                // 230000000, アクアリウム
                // 251000100, 海側波止場
                npc_script = new NpcDolphin();
            }
            case 2060010 -> {
                // 2060010, イルカ
                // 923020000, 霧の海 : 座礁された幽霊船
                npc_script = new NpcFoggySeaDolphin();
            }
            case 2081004 -> {
                // 2081004, ファム
                // 240000006, リプレ : ファムの家
                npc_script = new NpcPam();
            }
            case 2081005 -> {
                // 2081005, ケロベン
                // 240040700, 生命の洞窟入口
                npc_script = new NpcKeroben();
            }
            case 2081009 -> {
                // 2081009, ムス
                // 240010400, リプレ : 森の分かれ道
                npc_script = new NpcMoose();
            }
            case 2081100 -> {
                // 2081100, ハルモニア
                // 240010501, リプレ : 師弟の森
                npc_script = new NpcHarmonia();
            }
            case 2081200 -> {
                // 2081200, グリト
                // 240010501, リプレ : 師弟の森
                npc_script = new NpcGritto();
            }
            case 2081300 -> {
                // 2081300, レゴル
                // 240010501, リプレ : 師弟の森
                npc_script = new NpcLegor();
            }
            case 2081400 -> {
                // 2081400, ヘリン
                // 240010501, リプレ : 師弟の森
                npc_script = new NpcHellin();
            }
            case 2082000 -> {
                // 2082000, ミュ
                // 240000100, リプレ : チケット売場
                npc_script = new NpcShipTicketSeller();
            }
            case 2082001 -> {
                // 2082001, タミ
                // 240000110, ステーション<オルビス行き>
                npc_script = new NpcTommie();
            }
            case 2082002 -> {
                // 2082002, ハリモ
                // 240000111, リプレ : 控え室<オルビス行き>
                npc_script = new NpcWaitingRoomExit();
            }
            case 2082003 -> {
                // 2082003, コルバ
                // 240000110, ステーション<オルビス行き>
                npc_script = new NpcCorba();
            }
            case 2083002 -> {
                // 2083002, 木の根の水晶
                // 240050100, リプレ : 迷路部屋
                npc_script = new NpcCrystalOfRoots();
            }
            case 2083005 -> {
                // 2083005, 生命の泉
                // 240050400, リプレ : ホーンテイルの洞窟入口
                npc_script = new NpcFountainOfLife();
            }
            case 2084000 -> {
                // 2084000, ゴールドコンパス
                npc_script = new NpcGoldCompass();
            }
            case 2090004 -> {
                // 2090004, チエル
                // 250000000, 武陵
                npc_script = new NpcMrDo();
            }
            case 2090005 -> {
                // 2090005, ツノーレ
                // 200000141, ステーション通路<武陵行き>
                // 250000100, 武陵神社
                // 251000000, 白草村
                npc_script = new NpcCrane();
            }
            case 2090100 -> {
                // 2090100, ルオさん
                // 250000003, 武陵 : 武陵美容室
                npc_script = new NpcGrandpaLuo();
            }
            case 2090101 -> {
                // 2090101, リシュ
                // 250000003, 武陵 : 武陵美容室
                npc_script = new NpcLilishu();
            }
            case 2090102 -> {
                // 2090102, ナラン
                // 250000000, 武陵 : 武陵
                npc_script = new NpcNaran();
            }
            case 2090103 -> {
                // 2090103, パター
                // 250000000, 武陵 : 武陵
                npc_script = new NpcPata();
            }
            case 2090104 -> {
                // 2090104, ノマン
                // 250000000, 武陵 : 武陵
                npc_script = new NpcNoma();
            }
            case 2091005 -> {
                // 2091005, 素公パンダ
                // 925020001, 武陵道場入口
                // 925020100, 武陵道場 1階
                // 925020200, 武陵道場 2階
                // 925020300, 武陵道場 3階
                // 925020400, 武陵道場 4階
                // 925020500, 武陵道場 5階
                // 925020600, 武陵道場 6階
                // 925020700, 武陵道場 7階
                // 925020800, 武陵道場 8階
                // 925020900, 武陵道場 9階
                // 925021000, 武陵道場 10階
                // 925021100, 武陵道場 11階
                // 925021200, 武陵道場 12階
                // 925021300, 武陵道場 13階
                // 925021400, 武陵道場 14階
                // 925021500, 武陵道場 15階
                // 925021600, 武陵道場 16階
                // 925021700, 武陵道場 17階
                // 925021800, 武陵道場 18階
                // 925021900, 武陵道場 19階
                // 925022000, 武陵道場 20階
                // 925022100, 武陵道場 21階
                // 925022200, 武陵道場 22階
                // 925022300, 武陵道場 23階
                // 925022400, 武陵道場 24階
                // 925022500, 武陵道場 25階
                // 925022600, 武陵道場 26階
                // 925022700, 武陵道場 27階
                // 925022800, 武陵道場 28階
                // 925022900, 武陵道場 29階
                // 925023000, 武陵道場 30階
                // 925023100, 武陵道場 31階
                // 925023200, 武陵道場 32階
                // 925023300, 武陵道場 33階
                // 925023400, 武陵道場 34階
                // 925023500, 武陵道場 35階
                // 925023600, 武陵道場 36階
                // 925023700, 武陵道場 37階
                // 925023800, 武陵道場 38階
                npc_script = new NpcSoGong();
            }
            case 2091006 -> {
                // 2091006, 武陵道場掲示板
                // 250000100, 武陵 : 武陵神社
                npc_script = new NpcMuLungDojoBulletinBoard();
            }
            case 2096000 -> {
                // 2096000, 練習記録帳
                npc_script = new NpcPracticeChart();
            }
            case 2100006 -> {
                // 2100006, マズラ
                // 260000000, アリアント
                npc_script = new NpcMazra();
            }
            case 2101001 -> {
                // 2101001, ジユル
                // 260000200, アリアント村 : アリアント集落地
                npc_script = new NpcJiyur();
            }
            case 2101003 -> {
                // 2101003, アディン
                // 260000200, アリアント村 : アリアント集落地
                npc_script = new NpcArdin();
            }
            case 2101011 -> {
                // 2101011, セザン
                // 260000200, アリアント村 : アリアント集落地
                npc_script = new NpcSejan();
            }
            case 2101013 -> {
                // 2101013, カルタサ
                // 260010600, バーニングロード : 流浪団のテント
                npc_script = new NpcKarcasa();
            }
            case 2101014 -> {
                // 2101014, セザール
                // 980010000, 闘技場の控え室
                npc_script = new NpcCesar();
            }
            case 2102000 -> {
                // 2102000, アセソン
                // 260000100, アリアント乗降場
                npc_script = new NpcAsesson();
            }
            case 2102001 -> {
                // 2102001, シュリン
                // 260000110, アリアント : 控え室<オルビス行き>
                npc_script = new NpcWaitingRoomExit();
            }
            case 2102002 -> {
                // 2102002, シラス
                // 260000100, アリアント : アリアント乗降場
                npc_script = new NpcShipTicketSeller();
            }
            case 2110005 -> {
                // 2110005, ラクダ
                // 260020000, アリアント北門の外
                // 260020700, サヘル地帯1
                npc_script = new NpcCamel();
            }
            case 2111000 -> {
                // 2111000, カソン
                npc_script = new NpcCarson();
            }
            case 2111003 -> {
                // 2111003, ヒュモノイドA
                // 261000000, マガティア
                npc_script = new NpcHumanoidA();
            }
            case 2111006 -> {
                // 2111006, ファウェン
                // 261020401, アルカドノ研究所 : 関係者以外出入禁止区域
                npc_script = new NpcParwen();
            }
            case 2111025 -> {
                // 2111025, 制御装置
                // 261020401, アルカドノ研究所 : 関係者以外出入禁止区域
                npc_script = new NpcSecurityControl();
            }
            case 2111026 -> {
                // 2111026, 未完成魔法陣
                npc_script = new NpcIncompleteMagicSquare();
            }
            case 2141000 -> {
                // 2141000, キルストン
                npc_script = new NpcKirston();
            }
            case 2141002 -> {
                // 2141002, 忘れられた神殿管理人
                // 270050100, 神々の黄昏
                // 270050200, 失われた黄昏
                npc_script = new NpcForgottenTempleKeeper();
            }
            case 2161000 -> {
                // 2161000, ヴァンレオン
                // 211070100, 謁見室
                npc_script = new NpcVonLeon();
            }
            case 9000008 -> {
                // 9000008, 開け屋
                // 103000000, カニングシティー
                npc_script = new NpcMrPickall();
            }
            case 9000009 -> {
                // 9000009, バイキン
                // 104000000, 港口
                npc_script = new NpcVikin();
            }
            case 9000018 -> {
                // 9000018, マチルダ
                // 100000000, ヘネシス
                // 101000000, エリニア
                // 102000000, ぺリオン
                // 103000000, カニングシティー
                // 200000000, オルビス
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 221000000, 地球防衛本部
                // 240000000, リプレ
                // 250000000, 武陵
                // 260000000, アリアント
                // 261000000, マガティア
                npc_script = new NpcMatilda();
            }
            case 9000020 -> {
                // 9000020, スピネル
                // 100000000, ヘネシス
                // 101000000, エリニア
                // 102000000, ぺリオン
                // 103000000, カニングシティー
                // 104000000, 港口
                // 200000000, オルビス
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 240000000, リプレ
                // 250000000, 武陵
                // 251000000, 白草村
                // 260000000, アリアント
                // 261000000, マガティア
                // 500000000, 水上市場
                // 701000000, 上海ワイタン
                // 702000000, 宋山里
                // 740000000, 台湾西門町
                // 800000000, キノコ神社
                // 950000000, 여행을 끝내고
                npc_script = new NpcSpinel();
            }
            case 9000040 -> {
                // 9000040, ダリア
                // 100000200, 広場
                // 101000000, エリニア
                // 102000000, ぺリオン
                // 103000000, カニングシティー
                // 104000000, 港口
                // 105040300, スリーピーウッド
                // 120000000, ノーチラス
                npc_script = new NpcDalair();
            }
            case 9000041 -> {
                // 9000041, 寄付
                // 100000200, 広場
                // 101000000, エリニア
                // 102000000, ぺリオン
                // 103000000, カニングシティー
                // 104000000, 港口
                // 105040300, スリーピーウッド
                // 120000000, ノーチラス
                npc_script = new NpcDonationBox();
            }
            case 9000059 -> {
                // 9000059, ジンジャーマン
                // 980044200, 魔女の塔 : 魔女の塔最上階
                npc_script = new NpcGingerman();
            }
            case 9000060 -> {
                // 9000060, ジンジャーマン
                // 980041200, 魔女の塔 : 魔女の塔最上階
                npc_script = new NpcGingerman();
            }
            case 9000075 -> {
                // 9000075, チャン
                // 809060000, 隠れ道 : 金箔寺
                npc_script = new NpcChan();
            }
            case 9000078 -> {
                // 9000078, ユース
                // 809060000, 隠れ道 : 金箔寺
                npc_script = new NpcMrYoo();
            }
            case 9000080 -> {
                // 9000080, ダオ
                // 809060000, 隠れ道 : 金箔寺
                npc_script = new NpcDao();
            }
            case 9000082 -> {
                // 9000082, ポン
                // 809061010, 隠れ道 : 悪霊の神殿
                npc_script = new NpcPong();
            }
            case 9001107 -> {
                // 9001107, 案内うさぎ
                // 922231000, 月うさぎの遊び場
                // 922231001, 月うさぎの遊び場
                npc_script = new NpcGuideMoonBunny();
            }
            case 9001108 -> {
                // 9001108, 案内うさぎ
                // 922230000, 月の国
                // 922230100, 灯る月花庭園
                // 922230101, 灯る月花庭園
                // 922230102, 灯る月花庭園
                // 922230103, 灯る月花庭園
                // 922230104, 灯る月花庭園
                // 922230105, 灯る月花庭園
                // 922230106, 灯る月花庭園
                // 922230200, 光る月花庭園
                // 922230201, 光る月花庭園
                // 922230202, 光る月花庭園
                // 922230203, 光る月花庭園
                // 922230204, 光る月花庭園
                // 922230205, 光る月花庭園
                // 922230206, 光る月花庭園
                // 922230300, 煌く月花庭園
                // 922230301, 煌く月花庭園
                // 922230302, 煌く月花庭園
                // 922230303, 煌く月花庭園
                // 922230304, 煌く月花庭園
                // 922230305, 煌く月花庭園
                // 922230306, 煌く月花庭園
                npc_script = new NpcGatekeeperMoonBunny();
            }
            case 9010010 -> {
                // 9010010, カサンドラ
                // 100000000, ヘネシス
                // 102000000, ぺリオン
                // 104000000, 港口
                // 105040300, スリーピーウッド
                // 120000200, 中央廊下
                // 200000000, オルビス
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 221000000, 地球防衛本部
                // 222000000, 下町
                // 240000000, リプレ
                // 250000000, 武陵
                // 251000000, 白草村
                // 260000000, アリアント
                // 261000000, マガティア
                // 800000000, キノコ神社
                npc_script = new NpcCassandra();
            }
            case 9010022 -> {
                // 9010022, 次元の鏡
                // 100000000, ヘネシス
                // 101000000, エリニア
                // 102000000, ぺリオン
                // 103000000, カニングシティー
                // 105040300, スリーピーウッド
                // 120000000, ノーチラス
                // 200000200, オルビス公園
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 221000000, 地球防衛本部
                // 222000000, 下町
                // 230000000, アクアリウム
                // 240000000, リプレ
                // 250000000, 武陵
                // 251000000, 白草村
                // 260000000, アリアント
                // 261000000, マガティア
                // 800000000, キノコ神社
                npc_script = new NpcDimensionalMirror();
            }
            case 9020000 -> {
                // 9020000, ラケリース
                // 103000000, カニングシティー
                npc_script = new NpcLakelis();
            }
            case 9020001 -> {
                // 9020001, クロート
                // 103000800, 一つ目の同行<1st>
                // 103000801, 一つ目の同行<2nd>
                // 103000802, 一つ目の同行<3rd>
                // 103000803, 一つ目の同行<4th>
                // 103000804, 一つ目の同行<Last>
                // 910340100, 첫번째동행<1관문>
                // 910340200, 첫번째동행<2관문>
                // 910340300, 첫번째동행<3관문>
                // 910340400, 첫번째동행<4관문>
                // 910340500, 첫번째동행<마지막관문>
                // 910340501, 첫번째동행<마지막관문>
                npc_script = new NpcCloto();
            }
            case 9060000 -> {
                // 9060000, ケンタ
                // 923010000, ケンタの飼育室
                npc_script = new NpcKentaBreedingRoom();
            }
            case 9100100, 9100101, 9100102, 9100103, 9100104, 9100105, 9100106, 9100107, 9100108, 9100109, 9100110, 9100111, 9100113, 9100114, 9100115, 9100116, 9100117 -> {
                // 9100100, ガシャポン
                // 101000000, エリニア
                // 9100101, ガシャポン
                // 100000100, 市場
                // 9100102, ガシャポン
                // 102000000, ぺリオン
                // 9100103, ガシャポン
                // 103000000, カニングシティー
                // 9100104, ガシャポン
                // 105040300, スリーピーウッド
                // 9100105, ガシャポン
                // 211000100, 市場
                // 9100106, ガシャポン
                // 800000000, キノコ神社
                // 9100107, ガシャポン
                // 120000200, 中央廊下
                // 809000101, 銭湯（男）
                // 9100108, ガシャポン
                // 809000201, 銭湯（女）
                // 9100109, ガシャポン(パチ)
                // 809030000, パチンコ屋
                // 9100110, ガシャポン(ネット)
                // 193000000, ネットカフェ
                // 9100111, ガシャポン(キャンペーン)
                // 104000000, 港口
                // 9100113, ガシャポン(モバイル)
                // 100000000, ヘネシス
                // 9100114, ファンタ用ガシャ
                // 9100115, アクエリアス用ガシャ
                // 9100116, コカ･コーラ ゼロ用ガシャ
                // 9100117, ノーチラスガシャポン
                npc_script = new NpcGachapon();
            }
            case 9100112 -> {
                // 9100112, ガシャポン（兵法書）
                // 104000000, 港口
                npc_script = new NpcExpGachapon();
            }
            case 9100200, 9100201, 9100202, 9100203, 9100204, 9100205 -> {
                // 9100200, パチンコ1
                // 809030000, パチンコ屋
                // 9100201, パチンコ2
                // 809030000, パチンコ屋
                // 9100202, パチンコ3
                // 809030000, パチンコ屋
                // 9100203, パチンコ4
                // 809030000, パチンコ屋
                // 9100204, パチンコ5
                // 809030000, パチンコ屋
                // 9100205, パチンコ6
                // 809030000, パチンコ屋
                npc_script = new NpcPachinko();
            }
            case 9103001 -> {
                // 9103001, ガイドモモ
                // 220000000, ルディブリアム
                npc_script = new NpcRolly();
            }
            case 9105007 -> {
                // 9105007, ゲンタ
                npc_script = new NpcGenta();
            }
            case 9110000, 9110008 -> {
                // 9110000, ペリー
                // 103000000, カニングシティー
                // 9110008, ペリー
                // 800000000, キノコ神社
                npc_script = new NpcPerry();
            }
            case 9110010 -> {
                // 9110010, キノコの像
                // 800000000, キノコ神社
                npc_script = new NpcGachaponStatue();
            }
            case 9110107 -> {
                // 9110107, 駕籠
                // 800000000, キノコ神社
                // 800040000, 楓城 天下泰平
                npc_script = new NpcPalakeen();
            }
            case 9120003 -> {
                // 9120003, ヒカリ
                // 801000000, ショーワ町
                npc_script = new NpcHikari();
            }
            case 9120008 -> {
                // 9120008, ツーリ
                // 801000300, ショーワ町通り
                npc_script = new NpcTsuri();
            }
            case 9120010 -> {
                // 9120010, ファイト
                // 801000300, ショーワ町通り
                npc_script = new NpcFaito();
            }
            case 9120013 -> {
                // 9120013, ボス猫
                // 801000000, ショーワ町
                npc_script = new NpcBossKitty();
            }
            case 9120015 -> {
                // 9120015, コンペイ
                // 801000000, ショーワ町
                npc_script = new NpcKonpei();
            }
            case 9120026 -> {
                // 9120026, クリスタル
                // 800040000, 楓城 天下泰平
                // 802000101, カムナ
                // 802000313, 公園 2095年
                npc_script = new NpcCrystal();
            }
            case 9120040 -> {
                // 9120040, ポニチャル
                npc_script = new NpcPonicharu();
            }
            case 9120100 -> {
                // 9120100, 美容師テッペイ
                // 801000001, ジパング : 美容院
                npc_script = new NpcTepei();
            }
            case 9120101 -> {
                // 9120101, 助手みどり
                // 801000001, ジパング : 美容院
                npc_script = new NpcMidori();
            }
            case 9120102 -> {
                // 9120102, ヒゲクロ先生
                // 801000002, ジパング : 整形外科
                npc_script = new NpcHikekuro();
            }
            case 9120103 -> {
                // 9120103, 助手サエコ
                // 801000002, ジパング : 整形外科
                npc_script = new NpcSaeko();
            }
            case 9120105 -> {
                // 9120105, キャサリン
                // 100000000, ヘネシス
                // 101000000, エリニア
                // 102000000, ぺリオン
                // 120000000, ノーチラス
                // 200000000, オルビス
                // 211000000, エルナス
                // 221000000, 地球防衛本部
                // 222000000, 下町
                // 230000000, アクアリウム
                // 240000000, リプレ
                // 250000000, 武陵
                // 251000000, 白草村
                // 260000000, アリアント
                // 261000000, マガティア
                // 800000000, キノコ神社
                npc_script = new NpcCatherine();
            }
            case 9120106 -> {
                // 9120106, パチンコ玉交換機
                // 809030000, パチンコ屋
                npc_script = new NpcPachinkoBallExchanger();
            }
            case 9120107 -> {
                // 9120107, キャサリン
                // 809030000, パチンコ屋
                npc_script = new NpcCatherinePachinkoParlor();
            }
            case 9120200 -> {
                // 9120200, コンペイ
                // 801040000, ジパング : アジト前
                npc_script = new NpcKonpeiHideout();
            }
            case 9120201 -> {
                // 9120201, コンペイ
                // 801040004, 武器庫
                npc_script = new NpcKonpeiBodyguard();
            }
            case 9120202 -> {
                // 9120202, コンペイ
                // 801040100, ジパング : 悪夢の果て
                npc_script = new NpcKonpeiHideout();
            }
            case 9120203 -> {
                // 9120203, コンペイ
                // 801040101, ジパング : アジト前(天晴れ)
                npc_script = new NpcKonpeiHideout();
            }
            case 9201015 -> {
                // 9201015, ジュリアス
                // 680000002, ウェディングタウン : 美容院
                npc_script = new NpcJulius();
            }
            case 9201016 -> {
                // 9201016, シェイマス
                // 680000002, ウェディングタウン : 美容院
                npc_script = new NpcSeamus();
            }
            case 9201018 -> {
                // 9201018, アルバーツ
                // 680000003, ウェディングタウン : 整形外科
                npc_script = new NpcAlberts();
            }
            case 9201019 -> {
                // 9201019, シャキ
                // 680000003, ウェディングタウン : 整形外科
                npc_script = new NpcShaki();
            }
            case 9201022 -> {
                // 9201022, トーマス
                // 680000500, ウェディングタウン : 出口
                npc_script = new NpcThomasSwift();
            }
            case 9201023 -> {
                // 9201023, ナナ
                // 100000200, ヘネシス : 広場
                npc_script = new NpcNana();
            }
            case 9201051 -> {
                // 9201051, ジョン・バリケード
                // 104000000, 港口
                npc_script = new NpcJohnBarricade();
            }
            case 9201052 -> {
                // 9201052, フォックスウィット
                // 104000000, 港口
                npc_script = new NpcProfessorFoxwit();
            }
            case 9201112 -> {
                // 9201112, ジャック
                // 803000502, クリムゾン砦3
                npc_script = new NpcJack();
            }
            case 9201115 -> {
                // 9201115, 戦女神の像
                // 803100000, 支配者の秘密の間-孤高の戦場-
                npc_script = new NpcBattleStatue();
            }
            case 9250121 -> {
                // 9250121, ごみ箱おじさん
                // 100000000, ヘネシス
                // 200000000, オルビス
                // 220000000, ルディブリアム
                // 221000000, 地球防衛本部
                npc_script = new NpcDirtyBum();
            }
            case 9250125 -> {
                // 9250125, ドクタービン
                // 502010030, OSSS秘密基地ドクタービンの部屋
                npc_script = new NpcDoctorBing();
            }
            case 9250127 -> {
                // 9250127, OS4シャトル
                // 502010000, OSSS秘密基地格納庫
                npc_script = new NpcOS4Shuttle();
            }
            case 9250128 -> {
                // 9250128, OS4シャトル
                // 502010200, 墜落した宇宙船の深海
                // 502010300, 化石鉱物の鉱山
                // 502010600, 地下道集結地
                // 502010700, 輸送路の果て
                // 502010800, 安全な防御船
                npc_script = new NpcOS4ShuttleExit();
            }
            case 9250129 -> {
                // 9250129, OS4シャトル
                // 502010000, OSSS秘密基地格納庫
                npc_script = new NpcResearcherS();
            }
            case 9250138 -> {
                // 9250138, ブラックホール生成器
                npc_script = new NpcBlackHoleActivator();
            }
            case 9250144 -> {
                // 9250144, タイムマシーン
                // 502010030, OSSS秘密基地ドクタービンの部屋
                npc_script = new NpcTimeMachine();
            }
            case 9250152 -> {
                // 9250152, OS3Aマシーン
                // 502010010, 地下道入口
                npc_script = new NpcOS3AMachine();
            }
            case 9250155 -> {
                // 9250155, OSSS研究員
                // 502029000, 墜落した宇宙船入口
                npc_script = new NpcOSSSResearcher();
            }
            case 9310004 -> {
                // 9310004, 婦人警官ポリン
                // 701010320, 中原山岳地帯2
                npc_script = new NpcOfficerLim();
            }
            case 9310005 -> {
                // 9310005, 警察官ミャオ
                // 701010321, ブラックシープンの領域
                npc_script = new NpcOfficerChung();
            }
            case 9310006 -> {
                // 9310006, 警察官ミカーファイ
                // 701010322, 抜け道
                npc_script = new NpcOfficerHuh();
            }
            case 9310007 -> {
                // 9310007, 警察官ハーク
                // 701010322, 抜け道
                // 701010323, 危険な丘陵
                // 701010324, 悲鳴の丘陵
                npc_script = new NpcOfficerKang();
            }
            case 9310039 -> {
                // 9310039, 掛け軸
                // 702070400, 蔵経閣7階
                npc_script = new NpcHangingScrollEnter();
            }
            case 9310044 -> {
                // 9310044, 掛け軸
                // 702060000, 修行の間
                npc_script = new NpcHangingScrollExit();
            }
            case 9330028 -> {
                // 9330028, メロノン
                // 741020100, 夜市場分かれ道2
                npc_script = new NpcMelonon();
            }
            case 9330032 -> {
                // 9330032, 果物屋トレホレ
                // 741020101, 夜市場裏道1
                // 741020102, 夜市場裏道2
                npc_script = new NpcFruitStandGramps();
            }
            case 9330045 -> {
                // 9330045, 釣り場管理人
                // 100000000, ヘネシス
                // 101000000, エリニア
                // 102000000, ぺリオン
                // 103000000, カニングシティー
                // 105040300, スリーピーウッド
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 701000000, 上海ワイタン
                // 702000000, 宋山里
                // 740000000, 台湾西門町
                // 741000000, 夜市場
                // 800000000, キノコ神社
                // 801000000, ショーワ町
                npc_script = new NpcKedrick();
            }
            default -> {
                return false;
            }
        }
        npc_script.setNpcId(npc_script_id);
        npc_script.start(chr);
        return true;
    }
}
