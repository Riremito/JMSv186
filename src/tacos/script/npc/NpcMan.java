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
                // 104000000, 港口 200000200:オルビス公園
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
            case 1013207 -> {
                // 1013207, クン
                // 200090080, 용이 잠든 섬으로 200090090:리스항구행 914100000:臨時港
                npc_script = new NpcOlafTemporaryHarbor();
            }
            case 1022000 -> {
                // 1022000, コブシを開いて立て
                // 102000003, 戦士の聖殿
                npc_script = new NpcDancesWithBalrog();
            }
            case 1032003 -> {
                // 1032003, シェイン
                // 101000000, エリニア
                npc_script = new NpcShane();
            }
            case 1032004 -> {
                // 1032004, ルイス
                // 101000100, 忍耐の森-1段階 101000101:忍耐の森-2段階 101000102:忍耐の森-3段階 101000103:忍耐の森-4段階 101000104:忍耐の森-5段階
                npc_script = new NpcLouis();
            }
            case 1032008 -> {
                // 1032008, チェリ
                // 101000300, エリニアステーション
                npc_script = new NpcCherry();
            }
            case 1052001 -> {
                // 1052001, ダークロード
                // 103000003, 盗賊のアジト
                npc_script = new NpcDarkLord();
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
            case 1052125 -> {
                // 1052125, ガードマン
                // 103040400, 7階 8階 A区域
                npc_script = new NpcJune();
            }
            case 1057002 -> {
                // 1057002, 睦実
                // 103050100, 秘桜蔭 2階
                npc_script = new NpcYun();
            }
            case 1061100 -> {
                // 1061100, ホテルガイド
                // 105040400, スリーピーホテルロビー
                npc_script = new NpcHotelReceptionist();
            }
            case 1072000 -> {
                // 1072000, 戦士転職教官
                // 102020300, 西の岩山4
                npc_script = new NpcWarriorJobInstructor();
            }
            case 1072004 -> {
                // 1072004, 戦士転職教官
                // 108000300, 戦士の岩山1 108000301:戦士の岩山2 108000302:戦士の岩山3
                npc_script = new NpcWarriorJobInstructorTest();
            }
            case 1090000 -> {
                // 1090000, カイリン
                // 120000101, 航海室 912010200:カイリンの訓練場
                npc_script = new NpcKyrin();
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
            case 1092019 -> {
                // 1092019, ジョナサン
                // 120000102, ジョナサンの部屋
                npc_script = new NpcLordJonathan();
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
            case 1102002 -> {
                // 1102002, キリド
                // 130010220, 孵化場
                npc_script = new NpcKiridu();
            }
            case 1102003 -> {
                // 1102003, キダン
                // 130000100, 騎士の殿堂 130000101:騎士の殿堂
                npc_script = new NpcKidan();
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
            case 2010000 -> {
                // 2010000, チャーリー軍曹
                // 200000000, オルビス
                npc_script = new NpcStaffSergeantCharlie();
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
            case 2010009 -> {
                // 2010009, レナリウ
                // 200000301, ギルド本部<英雄の殿堂>
                npc_script = new NpcLenario();
            }
            case 2012001 -> {
                // 2012001, リニ
                // 200000111, ステーション<エリニア行き>
                npc_script = new NpcRini();
            }
            case 2012006 -> {
                // 2012006, イス
                // 200000100, オルビスチケット売場
                npc_script = new NpcIsaTheStationGuide();
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
            case 2012025 -> {
                // 2012025, ゼラス
                // 200000151, ステーション<アリアント行き>
                npc_script = new NpcGeras();
            }
            case 2013000 -> {
                // 2013000, 妖精ウィンキー
                // 200080101, 見知らぬ塔 920010000:入口
                npc_script = new NpcWonkyTheFairy();
            }
            case 2013001 -> {
                // 2013001, 侍従イク
                // 920010100, 中央塔 920010200:散歩路 920010300:倉庫 920010400:休憩室 920010500:封印された部屋 920010600:ラウンジ 920010700:登り道 920010800:庭園 920010900:罪人の部屋 920011000:暗黒の部屋 920011200:出口
                npc_script = new NpcChamberlainEak();
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
            case 2023000 -> {
                // 2023000, 危険地域弾丸タクシー
                // 211000000, エルナス
                // 220000000, ルディブリアム
                // 240000000, リプレ
                npc_script = new NpcDangerZoneTaxi();
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
            case 2040002 -> {
                // 2040002, レフトくん
                // 221024400, エオス塔100階
                npc_script = new NpcOlsonTheToySoldier();
            }
            case 2040024 -> {
                // 2040024, 一番目のエオス石
                // 221024400, エオス塔100階
                npc_script = new NpcFirstEosRock();
            }
            case 2040027 -> {
                // 2040027, 四番目のエオス石
                // 221020000, エオス塔1階
                npc_script = new NpcFourthEosRock();
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
            case 2041000 -> {
                // 2041000, ティティアン
                // 220000110, ステーション<オルビス行き>
                npc_script = new NpcTian();
            }
            case 2041025 -> {
                // 2041025, 機械装置
                // 220080001, 時計塔の深層部
                npc_script = new NpcMachineApparatus();
            }
            case 2060009 -> {
                // 2060009, イルカ
                // 230000000, アクアリウム
                // 251000100, 海側波止場
                npc_script = new NpcDolphin();
            }
            case 2081005 -> {
                // 2081005, ケロベン
                // 240040700, 生命の洞窟入口
                npc_script = new NpcKeroben();
            }
            case 2082001 -> {
                // 2082001, タミ
                // 240000110, ステーション<オルビス行き>
                npc_script = new NpcTommie();
            }
            case 2082003 -> {
                // 2082003, コルバ
                // 240000110, ステーション<オルビス行き>
                npc_script = new NpcCorba();
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
            case 2091005 -> {
                // 2091005, 素公パンダ
                // 925020001, 武陵道場入口 925020100:武陵道場 1階 925020200:武陵道場 2階 925020300:武陵道場 3階 925020400:武陵道場 4階 925020500:武陵道場 5階 925020600:武陵道場 6階 925020700:武陵道場 7階 925020800:武陵道場 8階 925020900:武陵道場 9階 925021000:武陵道場 10階 925021100:武陵道場 11階 925021200:武陵道場 12階 925021300:武陵道場 13階 925021400:武陵道場 14階 925021500:武陵道場 15階 925021600:武陵道場 16階 925021700:武陵道場 17階 925021800:武陵道場 18階 925021900:武陵道場 19階 925022000:武陵道場 20階 925022100:武陵道場 21階 925022200:武陵道場 22階 925022300:武陵道場 23階 925022400:武陵道場 24階 925022500:武陵道場 25階 925022600:武陵道場 26階 925022700:武陵道場 27階 925022800:武陵道場 28階 925022900:武陵道場 29階 925023000:武陵道場 30階 925023100:武陵道場 31階 925023200:武陵道場 32階 925023300:武陵道場 33階 925023400:武陵道場 34階 925023500:武陵道場 35階 925023600:武陵道場 36階 925023700:武陵道場 37階 925023800:武陵道場 38階
                npc_script = new NpcSoGong();
            }
            case 2100006 -> {
                // 2100006, マズラ
                // 260000000, アリアント
                npc_script = new NpcMazra();
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
            case 2110005 -> {
                // 2110005, ラクダ
                // 260020000, アリアント北門の外
                // 260020700, サヘル地帯1
                npc_script = new NpcCamel();
            }
            case 2141002 -> {
                // 2141002, 忘れられた神殿管理人
                // 270050100, 神々の黄昏 270050200:失われた黄昏
                npc_script = new NpcForgottenTempleKeeper();
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
                // 100000200, 広場 101000000:エリニア 102000000:ぺリオン 103000000:カニングシティー 104000000:港口 105040300:スリーピーウッド 120000000:ノーチラス
                npc_script = new NpcDalair();
            }
            case 9000041 -> {
                // 9000041, 寄付
                // 100000200, 広場 101000000:エリニア 102000000:ぺリオン 103000000:カニングシティー 104000000:港口 105040300:スリーピーウッド 120000000:ノーチラス
                npc_script = new NpcDonationBox();
            }
            case 9001107 -> {
                // 9001107, 案内うさぎ
                // 922231000, 月うさぎの遊び場 922231001:月うさぎの遊び場
                npc_script = new NpcGuideMoonBunny();
            }
            case 9001108 -> {
                // 9001108, 案内うさぎ
                // 922230000, 月の国 922230100:灯る月花庭園 922230101:灯る月花庭園 922230102:灯る月花庭園 922230103:灯る月花庭園 922230104:灯る月花庭園 922230105:灯る月花庭園 922230106:灯る月花庭園 922230200:光る月花庭園 922230201:光る月花庭園 922230202:光る月花庭園 922230203:光る月花庭園 922230204:光る月花庭園 922230205:光る月花庭園 922230206:光る月花庭園 922230300:煌く月花庭園 922230301:煌く月花庭園 922230302:煌く月花庭園 922230303:煌く月花庭園 922230304:煌く月花庭園 922230305:煌く月花庭園 922230306:煌く月花庭園
                npc_script = new NpcGatekeeperMoonBunny();
            }
            case 9020000 -> {
                // 9020000, ラケリース
                // 103000000, カニングシティー
                npc_script = new NpcLakelis();
            }
            case 9020001 -> {
                // 9020001, クロート
                // 103000800, 一つ目の同行<1st> 103000801:一つ目の同行<2nd> 103000802:一つ目の同行<3rd> 103000803:一つ目の同行<4th> 103000804:一つ目の同行<Last> 910340100:첫번째동행<1관문> 910340200:첫번째동행<2관문> 910340300:첫번째동행<3관문> 910340400:첫번째동행<4관문> 910340500:첫번째동행<마지막관문> 910340501:첫번째동행<마지막관문>
                npc_script = new NpcCloto();
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
                // 120000200, 中央廊下 809000101:銭湯（男）
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
            case 9120105 -> {
                // 9120105, キャサリン
                // 100000000, ヘネシス 101000000:エリニア 102000000:ぺリオン 120000000:ノーチラス 200000000:オルビス 211000000:エルナス 221000000:地球防衛本部 222000000:下町 230000000:アクアリウム 240000000:リプレ 250000000:武陵 251000000:白草村 260000000:アリアント 261000000:マガティア 800000000:キノコ神社
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
            case 9120201 -> {
                // 9120201, コンペイ
                // 801040004, 武器庫
                npc_script = new NpcKonpeiBodyguard();
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
                // 502010200, 墜落した宇宙船の深海 502010300:化石鉱物の鉱山 502010600:地下道集結地 502010700:輸送路の果て 502010800:安全な防御船
                npc_script = new NpcOS4ShuttleExit();
            }
            case 9250129 -> {
                // 9250129, OS4シャトル
                // 502010000, OSSS秘密基地格納庫
                npc_script = new NpcResearcherS();
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
