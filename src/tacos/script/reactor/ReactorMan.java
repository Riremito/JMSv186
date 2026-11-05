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
package tacos.script.reactor;

import tacos.client.TacosCharacter;
import tacos.script.js.TacosScriptReactor;
import tacos.server.map.object.TacosReactor;

/**
 *
 * @author Riremito
 */
public class ReactorMan {

    // Java の処理を優先し、存在しない場合は js を利用する
    public static boolean act(TacosCharacter chr, TacosReactor reactor) {
        if (start(chr, reactor)) {
            chr.DebugMsg("ReactorMan : " + reactor.getId());
            return true;
        }
        return TacosScriptReactor.getInstance().act(chr.getClient(), reactor);
    }

    public static boolean start(TacosCharacter chr, TacosReactor reactor) {
        ReactorScriptMan reactor_script = create(reactor.getId());
        if (reactor_script == null) {
            return false;
        }
        reactor_script.start(chr, reactor);
        return true;
    }

    // Reactor の状態が変化した (破壊されていない) 場合の処理, Java のみ
    public static boolean changeState(TacosCharacter chr, TacosReactor reactor, int old_state) {
        ReactorScriptMan reactor_script = create(reactor.getId());
        if (reactor_script == null) {
            return false;
        }
        reactor_script.startChangeState(chr, reactor, old_state);
        return true;
    }

    private static ReactorScriptMan create(int reactor_id) {
        switch (reactor_id) {
            case 2000 -> {
                // 2000, mBoxItem0
                // 200000300, オルビス : 出会いの丘
                return new Reactor_Orbis_MBoxItem0();
            }
            case 2001 -> {
                // 2001, mBoxItem0
                // 1000000, レインボーストリート : アムホスト
                // 1000002, レインボーストリート : アムホストの民家
                return new Reactor_MapleIsland_MBoxItem0();
            }
            case 1002008 -> {
                // 1002008, farmItem0
                // 100030300, フォレストストリート : ウェタンギル中心地
                return new Reactor_Common_DropItems();
            }
            case 1012000 -> {
                // 1012000, vFlowerItem0
                // 101010000, ビクトリアロード : エリニアの北側フィールド
                // 101010100, エリニア : そびえ立つ木1
                // 101010101, エリニア : そびえ立つ木2
                // 101010102, エリニア : そびえ立つ木3
                // 101020000, エリニア : 北の森
                return new Reactor_Ellinia_VFlowerItem0();
            }
            case 1020000 -> {
                // 1020000, s4hitmanMap0
                // 910200000,  : 未公開の遺跡１
                return new Reactor_Victoria_S4hitmanMap0();
            }
            case 1020001 -> {
                // 1020001, s4hitmanMap1
                // 910200000,  : 未公開の遺跡１
                return new Reactor_Victoria_S4hitmanMap0();
            }
            case 1020002 -> {
                // 1020002, s4hitmanMap2
                // 910200000,  : 未公開の遺跡１
                return new Reactor_Victoria_S4hitmanMap0();
            }
            case 1020003 -> {
                // 1020003, s4hitmanMap3
                // 910200001,  : 未公開の遺跡２
                return new Reactor_Victoria_S4hitmanMap();
            }
            case 1020004 -> {
                // 1020004, s4hitmanMap4
                // 910200001,  : 未公開の遺跡２
                return new Reactor_Victoria_S4hitmanMap4();
            }
            case 1020005 -> {
                // 1020005, s4hitmanMap5
                // 910200001,  : 未公開の遺跡２
                return new Reactor_Victoria_S4hitmanMap5();
            }
            case 1020006 -> {
                // 1020006, s4hitmanMap6
                // 910200002,  : 未公開の遺跡３
                return new Reactor_Victoria_S4hitmanMap();
            }
            case 1020007 -> {
                // 1020007, s4hitmanMap7
                // 910200002,  : 未公開の遺跡３
                return new Reactor_Victoria_S4hitmanMap4();
            }
            case 1020008 -> {
                // 1020008, s4hitmanMap8
                // 910200002,  : 未公開の遺跡３
                return new Reactor_Victoria_S4hitmanMap5();
            }
            case 1021000 -> {
                // 1021000, s4hitmanMob0
                // 910200000,  : 未公開の遺跡１
                // 910200001,  : 未公開の遺跡２
                // 910200002,  : 未公開の遺跡３
                return new Reactor_Victoria_S4hitmanMob();
            }
            case 1021001 -> {
                // 1021001, s4hitmanMob1
                // 910200000,  : 未公開の遺跡１
                // 910200001,  : 未公開の遺跡２
                // 910200002,  : 未公開の遺跡３
                return new Reactor_Victoria_S4hitmanMob();
            }
            case 1022000 -> {
                // 1022000, s4hitmanItem0
                // 910200000,  : 未公開の遺跡１
                // 910200001,  : 未公開の遺跡２
                // 910200002,  : 未公開の遺跡３
                return new Reactor_Common_DropItems();
            }
            case 1022001 -> {
                // 1022001, periItem0
                // 101030101, ぺリオン : 遺跡発掘地1
                return new Reactor_Common_DropItems();
            }
            case 1050000 -> {
                // 1050000, s4berserkMap0
                // 910500200,  : 忘れられた神殿
                return new Reactor_Victoria_S4berserkMap0();
            }
            case 1052000 -> {
                // 1052000, s4berserkItem0
                // 910500200,  : 忘れられた神殿
                return new Reactor_Common_DropItems();
            }
            case 1052001 -> {
                // 1052001, balogItem0
                // 105100301, 
                return new Reactor_Common_DropItems();
            }
            case 1052002 -> {
                // 1052002, balogItem0
                // 105100401, 
                return new Reactor_Common_DropItems();
            }
            case 1072000 -> {
                // 1072000, vFlowerItem1
                // 107000000, カニングシティー : さまよい沼1
                // 107000100, カニングシティー : さまよい沼2
                // 107000200, カニングシティー : さまよい沼3
                // 107000300, カニングシティー : 危険なクロコ1
                // 107000400, カニングシティー : 危険なクロコ2
                // 107000401, 隠れ道 : ルーパン沼1
                // 107000402, 隠れ道 : ルーパン沼2
                // 107000403, 隠れ道 : ルーパン沼3
                return new Reactor_Common_DropItems();
            }
            case 1102000 -> {
                // 1102000, coconut0
                // 110010000, フロリナ : 海の家
                // 110020000, フロリナ : ロランロラン
                // 110020001, 隠れ道 : ロランロランロラン
                // 110030000, フロリナ : ロラン＆クラン
                // 110030001, フロリナ : クラン＆ロラン
                // 110040000, フロリナ : 熱砂地帯
                return new Reactor_Common_DropItems();
            }
            case 1102001 -> {
                // 1102001, coconut1
                // 110010000, フロリナ : 海の家
                // 110020000, フロリナ : ロランロラン
                // 110020001, 隠れ道 : ロランロランロラン
                // 110030000, フロリナ : ロラン＆クラン
                // 110030001, フロリナ : クラン＆ロラン
                // 110040000, フロリナ : 熱砂地帯
                return new Reactor_Common_DropItems();
            }
            case 1102002 -> {
                // 1102002, coconut2
                // 110010000, フロリナ : 海の家
                // 110020000, フロリナ : ロランロラン
                // 110020001, 隠れ道 : ロランロランロラン
                // 110030000, フロリナ : ロラン＆クラン
                // 110030001, フロリナ : クラン＆ロラン
                // 110040000, フロリナ : 熱砂地帯
                return new Reactor_Common_DropItems();
            }
            case 1102003 -> {
                // 1102003, florinaBox0
                return new Reactor_Common_DropItems();
            }
            case 1302000 -> {
                // 1302000, erebItem0
                // 130030004, クィーンズロード : 開始の森5
                return new Reactor_Ereve_ErebItem0();
            }
            case 1402000 -> {
                // 1402000, rienItem0
                // 140090400, スノーアイランド : 冷たい森4
                return new Reactor_Rien_RienItem0();
            }
            case 2001000 -> {
                // 2001000, fgodMob0
                // 920010800, 女神の塔 : 庭園
                return new Reactor_Orbis_FgodMob0();
            }
            case 2001001 -> {
                // 2001001, fgodMob1
                // 920010800, 女神の塔 : 庭園
                return new Reactor_Orbis_FgodMob1();
            }
            case 2001002 -> {
                // 2001002, fgodMob2
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob2();
            }
            case 2001003 -> {
                // 2001003, fgodMob3
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob3();
            }
            case 2001004 -> {
                // 2001004, fgodMob4
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob4();
            }
            case 2001005 -> {
                // 2001005, fgodMob5
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob5();
            }
            case 2001006 -> {
                // 2001006, fgodMob6
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob6();
            }
            case 2001007 -> {
                // 2001007, fgodMob7
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob7();
            }
            case 2001008 -> {
                // 2001008, fgodMob8
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob8();
            }
            case 2001009 -> {
                // 2001009, fgodMob9
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob9();
            }
            case 2001010 -> {
                // 2001010, fgodMob10
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob10();
            }
            case 2001011 -> {
                // 2001011, fgodMob11
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob11();
            }
            case 2001012 -> {
                // 2001012, fgodMob12
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob12();
            }
            case 2001013 -> {
                // 2001013, fgodMob13
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob13();
            }
            case 2001014 -> {
                // 2001014, fgodMob14
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob14();
            }
            case 2001015 -> {
                // 2001015, fgodMob15
                // 920010300, 女神の塔 : 倉庫
                return new Reactor_Orbis_FgodMob15();
            }
            case 2001016 -> {
                // 2001016, fgodBoss
                // 920010800, 女神の塔 : 庭園
                return new Reactor_Orbis_FgodBoss();
            }
            case 2002000 -> {
                // 2002000, oBoxItem0
                // 200000000, オルビス : オルビス
                // 200000200, オルビス : オルビス公園
                return new Reactor_Orbis_OBoxItem0();
            }
            case 2002001 -> {
                // 2002001, fgodItem0
                // 920010000, 女神の塔 : 入口
                return new Reactor_Orbis_FgodItem0();
            }
            case 2002002 -> {
                // 2002002, fgodItem1
                // 920010604, 女神の塔 : 客室304号
                return new Reactor_Common_DropItems();
            }
            case 2002003 -> {
                // 2002003, fgodItem2
                // 920010800, 女神の塔 : 庭園
                return new Reactor_Common_DropItems();
            }
            case 2002004 -> {
                // 2002004, fgodItem3
                // 920010400, 女神の塔 : 休憩室
                return new Reactor_Common_DropItems();
            }
            case 2002005 -> {
                // 2002005, fgodItem4
                // 920010400, 女神の塔 : 休憩室
                return new Reactor_Common_DropItems();
            }
            case 2002006 -> {
                // 2002006, fgodItem5
                // 920010400, 女神の塔 : 休憩室
                return new Reactor_Common_DropItems();
            }
            case 2002007 -> {
                // 2002007, fgodItem6
                // 920010400, 女神の塔 : 休憩室
                return new Reactor_Common_DropItems();
            }
            case 2002008 -> {
                // 2002008, fgodItem7
                // 920010400, 女神の塔 : 休憩室
                return new Reactor_Common_DropItems();
            }
            case 2002009 -> {
                // 2002009, fgodItem8
                // 920010400, 女神の塔 : 休憩室
                return new Reactor_Common_DropItems();
            }
            case 2002010 -> {
                // 2002010, fgodItem9
                // 920010400, 女神の塔 : 休憩室
                return new Reactor_Common_DropItems();
            }
            case 2002011 -> {
                // 2002011, fgodItem10
                // 920010400, 女神の塔 : 休憩室
                return new Reactor_Common_DropItems();
            }
            case 2002012 -> {
                // 2002012, fgodItem11
                // 920010500, 女神の塔 : 封印された部屋
                return new Reactor_Common_DropItems();
            }
            case 2002013 -> {
                // 2002013, fgodItem12
                // 920010700, 女神の塔 : 登り道
                return new Reactor_Common_DropItems();
            }
            case 2002014 -> {
                // 2002014, fgodItem13
                // 920011100, 女神の塔 : 宝物倉庫
                return new Reactor_Orbis_FgodItem13();
            }
            case 2002015 -> {
                // 2002015, fgodItem14
                return new Reactor_Common_DropItems();
            }
            case 2002016 -> {
                // 2002016, fgodItem15
                // 926000010, アリアント : 王妃の宝物庫
                return new Reactor_Common_DropItems();
            }
            case 2002017 -> {
                // 2002017, fgodItem16
                // 920010911, 女神の塔 : 監獄倉庫1
                // 920010912, 女神の塔 : 監獄倉庫1
                // 920010921, 女神の塔 : 監獄倉庫2
                // 920010922, 女神の塔 : 監獄倉庫2
                // 920010931, 女神の塔 : 監獄倉庫3
                // 920010932, 女神の塔 : 監獄倉庫3
                return new Reactor_Orbis_FgodItem16();
            }
            case 2002018 -> {
                // 2002018, fgodItem17
                // 920010912, 女神の塔 : 監獄倉庫1
                // 920010922, 女神の塔 : 監獄倉庫2
                // 920010932, 女神の塔 : 監獄倉庫3
                return new Reactor_Orbis_FgodItem16();
            }
            case 2006000 -> {
                // 2006000, fgodNPC0
                // 920010000, 女神の塔 : 入口
                return new Reactor_Orbis_FgodNPC0();
            }
            case 2006001 -> {
                // 2006001, fgodNPC1
                // 920010100, 女神の塔 : 中央塔
                return new Reactor_Orbis_FgodNPC0();
            }
            case 2008000 -> {
                // 2008000, (action ãªã)
                // 920010100, 女神の塔 : 中央塔
                return new Reactor_Orbis_2008000();
            }
            case 2008001 -> {
                // 2008001, (action ãªã)
                // 920010100, 女神の塔 : 中央塔
                return new Reactor_Orbis_2008000();
            }
            case 2008002 -> {
                // 2008002, (action ãªã)
                // 920010100, 女神の塔 : 中央塔
                return new Reactor_Orbis_2008000();
            }
            case 2008003 -> {
                // 2008003, (action ãªã)
                // 920010100, 女神の塔 : 中央塔
                return new Reactor_Orbis_2008000();
            }
            case 2008004 -> {
                // 2008004, (action ãªã)
                // 920010100, 女神の塔 : 中央塔
                return new Reactor_Orbis_2008000();
            }
            case 2008005 -> {
                // 2008005, (action ãªã)
                // 920010100, 女神の塔 : 中央塔
                return new Reactor_Orbis_2008000();
            }
            case 2008006 -> {
                // 2008006, (action ãªã)
                // 920010400, 女神の塔 : 休憩室
                return new Reactor_Orbis_2008000();
            }
            case 2092000 -> {
                // 2092000, snowdrop
                return new Reactor_Common_DropItems();
            }
            case 2092001 -> {
                // 2092001, snowdrop1
                return new Reactor_Common_DropItems();
            }
            case 2110000 -> {
                // 2110000, go280010000
                // 280010011, 閉鉱 : 1-2区域
                // 280010031, 閉鉱 : 3-2区域
                // 280010041, 閉鉱 : 4-2区域
                // 280010071, 閉鉱 : 7-2区域
                // 280010081, 閉鉱 : 8-2区域
                // 280010091, 閉鉱 : 9-2区域
                // 280010110, 閉鉱 : 11-1区域
                // 280010140, 閉鉱 : 14-1区域
                // 280011000, 閉鉱 : 16区域&lt;どこかにある閉鉱&gt;
                // 280011001, 閉鉱 : 16区域1
                // 280011002, 閉鉱 : 16区域2
                // 280011003, 閉鉱 : 16区域3
                // 280011004, 閉鉱 : 16区域4
                // 280011005, 閉鉱 : 16区域5
                // 280011006, 閉鉱 : 16区域6
                return new Reactor_ElNath_Go280010000();
            }
            case 2111000 -> {
                // 2111000, boxMob0
                // 280010031, 閉鉱 : 3-2区域
                // 280010041, 閉鉱 : 4-2区域
                // 280010050, 閉鉱 : 5-1区域
                // 280010060, 閉鉱 : 6-1区域
                // 280010081, 閉鉱 : 8-2区域
                // 280010101, 閉鉱 : 10-2区域
                // 280010110, 閉鉱 : 11-1区域
                // 280010130, 閉鉱 : 13-1区域
                // 280011000, 閉鉱 : 16区域&lt;どこかにある閉鉱&gt;
                // 280011001, 閉鉱 : 16区域1
                // 280011002, 閉鉱 : 16区域2
                // 280011003, 閉鉱 : 16区域3
                // 280011005, 閉鉱 : 16区域5
                // 280011006, 閉鉱 : 16区域6
                return new Reactor_ElNath_BoxMob0();
            }
            case 2111001 -> {
                // 2111001, boss
                // 280030000, ジャクムダンジョン : ジャクムの祭壇
                return new Reactor_ElNath_Boss();
            }
            case 2111101 -> {
                // 2111101, chaosboss
                return new Reactor_ElNath_Boss();
            }
            case 2112000 -> {
                // 2112000, boxItem0
                // 280010050, 閉鉱 : 5-1区域
                // 280010081, 閉鉱 : 8-2区域
                // 280010130, 閉鉱 : 13-1区域
                // 280011000, 閉鉱 : 16区域&lt;どこかにある閉鉱&gt;
                return new Reactor_Common_DropItems();
            }
            case 2112001 -> {
                // 2112001, boxItem1
                // 280010040, 閉鉱 : 4-1区域
                // 280010091, 閉鉱 : 9-2区域
                // 280011000, 閉鉱 : 16区域&lt;どこかにある閉鉱&gt;
                // 280011002, 閉鉱 : 16区域2
                // 280011003, 閉鉱 : 16区域3
                // 280011004, 閉鉱 : 16区域4
                // 280011005, 閉鉱 : 16区域5
                return new Reactor_Common_DropItems();
            }
            case 2112002 -> {
                // 2112002, boxItem2
                // 280010020, 閉鉱 : 2-1区域
                // 280010070, 閉鉱 : 7-1区域
                // 280010101, 閉鉱 : 10-2区域
                // 280011006, 閉鉱 : 16区域6
                return new Reactor_Common_DropItems();
            }
            case 2112003 -> {
                // 2112003, boxItem3
                // 280010081, 閉鉱 : 8-2区域
                // 280011000, 閉鉱 : 16区域&lt;どこかにある閉鉱&gt;
                // 280011005, 閉鉱 : 16区域5
                return new Reactor_Common_DropItems();
            }
            case 2112004 -> {
                // 2112004, boxKey0
                // 280010091, 閉鉱 : 9-2区域
                // 280010110, 閉鉱 : 11-1区域
                // 280010140, 閉鉱 : 14-1区域
                // 280011002, 閉鉱 : 16区域2
                // 280011003, 閉鉱 : 16区域3
                return new Reactor_Common_DropItems();
            }
            case 2112005 -> {
                // 2112005, boxPaper0
                // 280011005, 閉鉱 : 16区域5
                return new Reactor_Common_DropItems();
            }
            case 2112006 -> {
                // 2112006, money10000
                // 280010031, 閉鉱 : 3-2区域
                // 280010101, 閉鉱 : 10-2区域
                return new Reactor_ElNath_Money10000();
            }
            case 2112007 -> {
                // 2112007, boxItem0
                // 280011001, 閉鉱 : 16区域1
                return new Reactor_Common_DropItems();
            }
            case 2112008 -> {
                // 2112008, boxItem1
                // 280010100, 閉鉱 : 10-1区域
                // 280010120, 閉鉱 : 12-1区域
                return new Reactor_Common_DropItems();
            }
            case 2112009 -> {
                // 2112009, boxItem2
                // 280010011, 閉鉱 : 1-2区域
                // 280010040, 閉鉱 : 4-1区域
                // 280010110, 閉鉱 : 11-1区域
                // 280011000, 閉鉱 : 16区域&lt;どこかにある閉鉱&gt;
                return new Reactor_Common_DropItems();
            }
            case 2112010 -> {
                // 2112010, boxItem3
                // 280010020, 閉鉱 : 2-1区域
                // 280011004, 閉鉱 : 16区域4
                return new Reactor_Common_DropItems();
            }
            case 2112011 -> {
                // 2112011, boxKey0
                // 280010041, 閉鉱 : 4-2区域
                // 280011005, 閉鉱 : 16区域5
                return new Reactor_Common_DropItems();
            }
            case 2112012 -> {
                // 2112012, boxPaper0
                // 280010000, 閉鉱 : 知られざる閉鉱
                // 280010010, 閉鉱 : 1-1区域
                // 280010011, 閉鉱 : 1-2区域
                // 280010020, 閉鉱 : 2-1区域
                // 280010030, 閉鉱 : 3-1区域
                // 280010031, 閉鉱 : 3-2区域
                // 280010041, 閉鉱 : 4-2区域
                // 280010050, 閉鉱 : 5-1区域
                // 280010060, 閉鉱 : 6-1区域
                // 280010070, 閉鉱 : 7-1区域
                // 280010071, 閉鉱 : 7-2区域
                // 280010080, 閉鉱 : 8-1区域
                // 280010090, 閉鉱 : 9-1区域
                // 280010091, 閉鉱 : 9-2区域
                // 280010100, 閉鉱 : 10-1区域
                // 280010101, 閉鉱 : 10-2区域
                // 280010110, 閉鉱 : 11-1区域
                // 280010120, 閉鉱 : 12-1区域
                // 280010130, 閉鉱 : 13-1区域
                // 280010140, 閉鉱 : 14-1区域
                // 280010150, 閉鉱 : 15-1区域
                // 280011004, 閉鉱 : 16区域4
                // 280011006, 閉鉱 : 16区域6
                return new Reactor_Common_DropItems();
            }
            case 2112013 -> {
                // 2112013, money100
                // 280010000, 閉鉱 : 知られざる閉鉱
                // 280010011, 閉鉱 : 1-2区域
                // 280010040, 閉鉱 : 4-1区域
                // 280010090, 閉鉱 : 9-1区域
                // 280010120, 閉鉱 : 12-1区域
                // 280010140, 閉鉱 : 14-1区域
                // 280010150, 閉鉱 : 15-1区域
                // 280011001, 閉鉱 : 16区域1
                return new Reactor_ElNath_Money100();
            }
            case 2112014 -> {
                // 2112014, boxBItem0
                // 280011005, 閉鉱 : 16区域5
                return new Reactor_Common_DropItems();
            }
            case 2112015 -> {
                // 2112015, s4frameItem0
                // 921100000,  : 溶岩の心臓部
                return new Reactor_Common_DropItems();
            }
            case 2112016 -> {
                // 2112016, s4fireHawkItem0
                // 921100200,  : フェニックスの巣
                return new Reactor_Common_DropItems();
            }
            case 2112017 -> {
                // 2112017, s4iceEagleItem0
                // 921100210,  : フリズベルクの巣
                return new Reactor_Common_DropItems();
            }
            case 2118003 -> {
                // 2118003, (action ãªã)
                return new Reactor_ElNath_2118003();
            }
            case 2119000 -> {
                // 2119000, scaScript1
                // 211041100, エルナス : 死んだ木の森1
                return new Reactor_ElNath_ScaScript();
            }
            case 2119001 -> {
                // 2119001, scaScript2
                // 211041200, エルナス : 死んだ木の森2
                return new Reactor_ElNath_ScaScript();
            }
            case 2119002 -> {
                // 2119002, scaScript3
                // 211041300, エルナス : 死んだ木の森3
                return new Reactor_ElNath_ScaScript();
            }
            case 2119003 -> {
                // 2119003, scaScript4
                // 211041400, エルナス : 死んだ木の森4
                return new Reactor_ElNath_ScaScript();
            }
            case 2119004 -> {
                // 2119004, snowscaScript0
                // 211010000, エルナス : 凍結注意！凍りの道1
                return new Reactor_ElNath_SnowscaScript();
            }
            case 2119005 -> {
                // 2119005, snowscaScript1
                // 211020000, エルナス : 凍結注意！凍りの道2
                return new Reactor_ElNath_SnowscaScript();
            }
            case 2119006 -> {
                // 2119006, snowscaScript2
                // 211050000, エルナス : 凍てつく野原
                return new Reactor_ElNath_SnowscaScript();
            }
            case 2200000 -> {
                // 2200000, go221024400
                // 922000010,  : 人形の家
                return new Reactor_Ludibrium_Go221024400();
            }
            case 2200001 -> {
                // 2200001, ludiPotal0
                // 220030200, ルディブリアム : オモチャ工場-メイン工程2
                // 220030300, ルディブリアム : オモチャ工場-2工程&lt;3 区域&gt;
                // 220030400, ルディブリアム : オモチャ工場-2工程&lt;4 区域&gt;
                return new Reactor_Ludibrium_LudiPotal0();
            }
            case 2200002 -> {
                // 2200002, go922010201
                // 922010200, ルディブリアム : 放置された塔&lt;2段階&gt;
                return new Reactor_Ludibrium_Go922010201();
            }
            case 2201000 -> {
                // 2201000, ludiMob0
                // 922000020, ルディブリアム : 秘密工程１
                return new Reactor_Ludibrium_LudiMob0();
            }
            case 2201001 -> {
                // 2201001, ludiMob1
                // 922010300, ルディブリアム : 放置された塔&lt;3段階&gt;
                return new Reactor_Ludibrium_LudiMob1();
            }
            case 2201002 -> {
                // 2201002, ludiMob2
                // 922010700, ルディブリアム : 放置された塔&lt;7段階&gt;
                return new Reactor_Ludibrium_LudiMob2();
            }
            case 2201003 -> {
                // 2201003, ludiBoss0
                // 922010900, ルディブリアム : 時空の亀裂
                return new Reactor_Ludibrium_LudiBoss0();
            }
            case 2201004 -> {
                // 2201004, boss2
                // 220080001, ルディブリアム : 時計塔の深層部
                return new Reactor_Ludibrium_Boss2();
            }
            case 2202000 -> {
                // 2202000, ludiquest0
                // 922000010,  : 人形の家
                return new Reactor_Common_DropItems();
            }
            case 2202001 -> {
                // 2202001, ludiquest1
                // 922000000,  : オモチャ工場&lt;4 区域&gt;
                return new Reactor_Common_DropItems();
            }
            case 2202002 -> {
                // 2202002, ludiquest2
                // 220020000, ルディブリアム : オモチャ工場-1工程&lt;1区域&gt;
                // 220020100, ルディブリアム : オモチャ工場-1工程&lt;2区域&gt;
                // 220020200, ルディブリアム : オモチャ工場-1工程&lt;3区域&gt;
                // 220020300, ルディブリアム : オモチャ工場-メイン工程1
                // 220020400, ルディブリアム : オモチャ工場-1工程&lt;5区域&gt;
                // 220020500, ルディブリアム : オモチャ工場-1工程&lt;6区域&gt;
                // 220020600, ルディブリアム : オモチャ工場-機械室
                // 220030000, ルディブリアム : オモチャ工場-2工程&lt;1区域&gt;
                // 220030100, ルディブリアム : オモチャ工場-2工程&lt;2区域&gt;
                // 220030200, ルディブリアム : オモチャ工場-メイン工程2
                // 220030300, ルディブリアム : オモチャ工場-2工程&lt;3 区域&gt;
                // 220030400, ルディブリアム : オモチャ工場-2工程&lt;4 区域&gt;
                // 922000021, ルディブリアム : 秘密工程２
                return new Reactor_Common_DropItems();
            }
            case 2202003 -> {
                // 2202003, ludiquest3
                // 922010200, ルディブリアム : 放置された塔&lt;2段階&gt;
                // 922010201, ルディブリアム : 塔の罠
                // 922010501, ルディブリアム : 塔の迷路１
                // 922010502, ルディブリアム : 塔の迷路２
                // 922010503, ルディブリアム : 塔の迷路３
                // 922010504, ルディブリアム : 塔の迷路４
                // 922010505, ルディブリアム : 塔の迷路５
                // 922010506, ルディブリアム : 塔の迷路６
                return new Reactor_Common_DropItems();
            }
            case 2202004 -> {
                // 2202004, ludiquest4
                // 922011000, ルディブリアム : 放置された塔&lt;ボーナス&gt;
                return new Reactor_Ludibrium_Ludiquest4();
            }
            case 2212000 -> {
                // 2212000, osquest0
                // 221030401, ルディブリアム : プラティアンの草原
                return new Reactor_Ludibrium_Osquest();
            }
            case 2212001 -> {
                // 2212001, osquest1
                // 221030501, ルディブリアム : メカティアンの草原
                return new Reactor_Ludibrium_Osquest();
            }
            case 2212002 -> {
                // 2212002, osquest2
                // 221030301, ルディブリアム : ガンティアンの草原
                return new Reactor_Ludibrium_Osquest();
            }
            case 2212003 -> {
                // 2212003, osquest3
                // 221040402, ルディブリアム : ドゴンス基地
                return new Reactor_Ludibrium_Osquest();
            }
            case 2212004 -> {
                // 2212004, osquest4
                // 221000200, ルディブリアム : 格納庫
                return new Reactor_Ludibrium_Osquest();
            }
            case 2212005 -> {
                // 2212005, osquest5
                // 221040100, ルディブリアム : クーラン草原2
                return new Reactor_Common_DropItems();
            }
            case 2221000 -> {
                // 2221000, fvMob0
                // 222010401, 下町 : 山奥の廃屋
                return new Reactor_Ludibrium_FvMob0();
            }
            case 2221001 -> {
                // 2221001, fvMob1
                // 222010401, 下町 : 山奥の廃屋
                return new Reactor_Ludibrium_FvMob1();
            }
            case 2221002 -> {
                // 2221002, fvMob2
                // 222010401, 下町 : 山奥の廃屋
                return new Reactor_Ludibrium_FvMob2();
            }
            case 2221003 -> {
                // 2221003, fvquest0
                // 222000000, 下町 : 下町
                return new Reactor_Ludibrium_Fvquest();
            }
            case 2221004 -> {
                // 2221004, fvquest1
                // 222000000, 下町 : 下町
                return new Reactor_Ludibrium_Fvquest();
            }
            case 2222000 -> {
                // 2222000, fvquest2
                // 222010000, 下町 : カラス山の入口
                // 222010001, 下町 : カラス山の麓
                // 222010002, 下町 : 小さい泉
                // 222010100, 下町 : 小池のほとり
                // 222010101, 下町 : 虎の森1
                // 222010102, 下町 : 虎の森2
                return new Reactor_Ludibrium_Fvquest2();
            }
            case 2222001 -> {
                // 2222001, fvevent0
                // 922210000,  : 涼しいスイカ畑
                // 922210100,  : おいしいスイカ畑
                // 922210200,  : 甘いスイカ畑
                // 922210400,  : 涼しいスイカ畑
                // 922210500,  : 甘いスイカ畑
                // 922210600,  : 甘いスイカ畑
                // 922210700,  : 涼しいスイカ畑
                // 922210800,  : おいしいスイカ畑
                // 922210900,  : 甘いスイカ畑
                return new Reactor_Common_DropItems();
            }
            case 2229009 -> {
                // 2229009, fvscaScript0
                // 222010300, 下町 : 狐の丘
                return new Reactor_Ludibrium_FvscaScript0();
            }
            case 2302000 -> {
                // 2302000, aquaItem0
                // 230010000, アクアロード : 海の道
                // 230010001, アクアロード : ペンギンの遊び場
                // 230010100, アクアロード : 水晶の峡谷
                // 230010200, アクアロード : 赤珊瑚の坂道
                // 230010300, アクアロード : 巻貝の丘
                // 230010400, アクアロード : 西側の分かれ道
                // 230020000, アクアロード : 東側の分かれ道
                // 230020100, アクアロード : 海草の塔
                // 230020101, アクアロード : 隠れた町
                // 230020200, アクアロード : 砂城の遊び場
                // 230020300, アクアロード : 大魚の谷
                return new Reactor_Aquarium_AquaItem();
            }
            case 2302001 -> {
                // 2302001, aquaItem1
                // 230040000, アクアロード : 深淵海峡1
                // 230040100, アクアロード : 深淵海峡2
                // 230040200, アクアロード : 危険な海峡1
                // 230040300, アクアロード : 危険な海峡2
                // 230040400, アクアロード : 難破船の墓
                // 230040410, アクアロード : 危険な洞窟
                return new Reactor_Aquarium_AquaItem();
            }
            case 2302002 -> {
                // 2302002, aquaItem2
                // 230030000, アクアロード : 青い海草の道
                // 230030001, アクアロード : 魚達の憩いの広場
                // 230030100, アクアロード : キノコ珊瑚の丘
                // 230030200, アクアロード : 四角地帯
                return new Reactor_Aquarium_AquaItem2();
            }
            case 2302003 -> {
                // 2302003, s4resurItem0
                // 923000100,  : 冷たい祭壇
                return new Reactor_Common_DropItems();
            }
            case 2302005 -> {
                // 2302005, tameItem0
                // 923010000,  : ケンタの飼育室
                return new Reactor_Common_DropItems();
            }
            case 2309000 -> {
                // 2309000, (action ãªã)
                // 923020114, 霧の海 : 第5作戦室
                return new Reactor_Aquarium_2309000();
            }
            case 2401000 -> {
                // 2401000, hontaleBoss
                // 240060200, リプレ : ホーンテイルの洞窟
                return new Reactor_Leafre_HontaleBoss();
            }
            case 2401001 -> {
                // 2401001, s4fireHawkMob0
                // 924000100,  : 空の巣の端
                return new Reactor_Leafre_S4fireHawkMob0();
            }
            case 2401002 -> {
                // 2401002, s4iceEagleMob0
                // 924000100,  : 空の巣の端
                return new Reactor_Leafre_S4iceEagleMob0();
            }
            case 2401100 -> {
                // 2401100, chaoshontaleBoss
                return new Reactor_Leafre_ChaoshontaleBoss();
            }
            case 2402000 -> {
                // 2402000, leafItem0
                // 240010000, リプレ : リプレ西側の森
                // 240010100, リプレ : ミナル西側の境界
                // 240010101, リプレ : 毛玉の森
                // 240010200, リプレ : 邪鬼の森
                // 240010300, リプレ : 険しい坂
                // 240010400, リプレ : 森の分かれ道
                // 240010600, リプレ : 空の巣2
                // 240010700, リプレ : 空の巣1
                // 240010800, リプレ : 空の巣の入口
                // 240010900, リプレ : ミナル東側の境界
                // 240011000, リプレ : リプレ東側の森
                // 240020000, リプレ : バーンケンタウロスの領域
                // 240020200, リプレ : ケンタウロスの領域
                // 240020300, リプレ : 水と闇の戦場
                // 240020500, リプレ : 火と水の戦場
                // 240020501, ミニダンジョン : ケンタウロスの円卓
                return new Reactor_Common_DropItems();
            }
            case 2402001 -> {
                // 2402001, leafItem1
                // 240010000, リプレ : リプレ西側の森
                // 240010100, リプレ : ミナル西側の境界
                // 240010101, リプレ : 毛玉の森
                // 240010200, リプレ : 邪鬼の森
                // 240010300, リプレ : 険しい坂
                // 240010400, リプレ : 森の分かれ道
                // 240010600, リプレ : 空の巣2
                // 240010700, リプレ : 空の巣1
                // 240010800, リプレ : 空の巣の入口
                // 240010900, リプレ : ミナル東側の境界
                // 240011000, リプレ : リプレ東側の森
                // 240020000, リプレ : バーンケンタウロスの領域
                // 240020200, リプレ : ケンタウロスの領域
                // 240020300, リプレ : 水と闇の戦場
                // 240020500, リプレ : 火と水の戦場
                // 240020501, ミニダンジョン : ケンタウロスの円卓
                return new Reactor_Common_DropItems();
            }
            case 2402002 -> {
                // 2402002, hontaleItem0
                // 240050100, リプレ : 迷路部屋
                return new Reactor_Common_DropItems();
            }
            case 2402003 -> {
                // 2402003, hontaleItem1
                // 240050100, リプレ : 迷路部屋
                return new Reactor_Common_DropItems();
            }
            case 2402004 -> {
                // 2402004, hontaleItem2
                // 240050100, リプレ : 迷路部屋
                return new Reactor_Common_DropItems();
            }
            case 2402005 -> {
                // 2402005, hontaleItem3
                // 240050100, リプレ : 迷路部屋
                return new Reactor_Common_DropItems();
            }
            case 2402006 -> {
                // 2402006, hontaleItem4
                // 240050310, リプレ : 闇の洞窟
                return new Reactor_Leafre_HontaleItem4();
            }
            case 2406000 -> {
                // 2406000, hontaleNPC0
                // 240040611, リプレ : ナインスピリット
                return new Reactor_Leafre_HontaleNPC0();
            }
            case 2408004 -> {
                // 2408004, (action なし)
                // 240040611, リプレ : ナインスピリット
                return new Reactor_Leafre_2408004();
            }
            case 2502000 -> {
                // 2502000, muruengItem0
                // 250010500, 武陵 : 天桃の果樹園1
                // 250010600, 武陵 : 天桃の果樹園2
                // 250010700, 武陵 : 天桃の果樹園3
                return new Reactor_Common_DropItems();
            }
            case 2502001 -> {
                // 2502001, muruengItem1
                // 250010500, 武陵 : 天桃の果樹園1
                // 250010600, 武陵 : 天桃の果樹園2
                // 250010700, 武陵 : 天桃の果樹園3
                return new Reactor_Common_DropItems();
            }
            case 2511000 -> {
                // 2511000, davyMob0
                // 925100000,  : 海賊船への道
                return new Reactor_HerbTown_DavyMob0();
            }
            case 2511001 -> {
                // 2511001, davyMob1
                // 925100200,  : 甲板突破1
                // 925100300,  : 甲板突破2
                return new Reactor_HerbTown_DavyMob1();
            }
            case 2512000 -> {
                // 2512000, davyItem0
                // 925100000,  : 海賊船への道
                // 925100200,  : 甲板突破1
                // 925100300,  : 甲板突破2
                return new Reactor_Common_DropItems();
            }
            case 2512001 -> {
                // 2512001, davyItem1
                // 925100201,  : 100年生キキョウの領域1
                // 925100301,  : 100年生キキョウの領域2
                return new Reactor_HerbTown_DavyItem1();
            }
            case 2516000 -> {
                // 2516000, davyNPC0
                // 925100500,  : 海賊船長の威厳
                return new Reactor_HerbTown_DavyNPC0();
            }
            case 2518000 -> {
                // 2518000, (action なし)
                // 925100000,  : 海賊船への道
                // 925100200,  : 甲板突破1
                // 925100300,  : 甲板突破2
                // 926100202,  : 暗い研究室2
                // 926110202,  : 暗い研究室2
                return new Reactor_Common_2518000();
            }
            case 2519000 -> {
                // 2519000, davyScript0
                // 925100400,  : 海賊退治!
                return new Reactor_HerbTown_DavyItem1();
            }
            case 2519001 -> {
                // 2519001, davyScript1
                // 925100400,  : 海賊退治!
                return new Reactor_HerbTown_DavyItem1();
            }
            case 2519002 -> {
                // 2519002, davyScript2
                // 925100400,  : 海賊退治!
                return new Reactor_HerbTown_DavyItem1();
            }
            case 2519003 -> {
                // 2519003, davyScript3
                // 925100400,  : 海賊退治!
                return new Reactor_HerbTown_DavyItem1();
            }
            case 2602000 -> {
                // 2602000, ariantItem0
                // 926000010, アリアント : 王妃の宝物庫
                return new Reactor_Common_DropItems();
            }
            case 2612000 -> {
                // 2612000, magatiaItem0
                // 926120300,  : 雪園バラが育つ地
                return new Reactor_Common_DropItems();
            }
            case 2612001 -> {
                // 2612001, rnjItem0
                // 926100202,  : 暗い研究室2
                // 926110202,  : 暗い研究室2
                return new Reactor_Common_DropItems();
            }
            case 2612002 -> {
                // 2612002, rnjItem1
                // 926100201,  : 暗い研究室1
                // 926110201,  : 暗い研究室1
                return new Reactor_Common_DropItems();
            }
            case 2612003 -> {
                // 2612003, rnjItem2
                // 926100201,  : 暗い研究室1
                // 926100202,  : 暗い研究室2
                // 926110201,  : 暗い研究室1
                // 926110202,  : 暗い研究室2
                return new Reactor_Common_DropItems();
            }
            case 2612004 -> {
                // 2612004, magatiaItem1
                // 261000010, マガティア : ジェニミスト協会
                return new Reactor_Common_DropItems();
            }
            case 2618001 -> {
                // 2618001, (action ãªã)
                // 926100200,  : 特殊な実験室
                // 926110200,  : 特殊な実験室
                return new Reactor_Magatia_2618001();
            }
            case 2618002 -> {
                // 2618002, (action ãªã)
                // 926100200,  : 特殊な実験室
                // 926110200,  : 特殊な実験室
                return new Reactor_Magatia_2618001();
            }
            case 2619003 -> {
                // 2619003, magascaScript0
                // 261020200, アルカドノ研究所 : 研究所B-1区域
                return new Reactor_Magatia_MagascaScript();
            }
            case 2619004 -> {
                // 2619004, magascaScript1
                // 261020400, アルカドノ研究所 : 研究所C-2区域
                return new Reactor_Magatia_MagascaScript();
            }
            case 2619005 -> {
                // 2619005, magascaScript2
                // 261020600, アルカドノ研究所 : 研究所B-3区域
                return new Reactor_Magatia_MagascaScript();
            }
            case 2709000 -> {
                // 2709000, PinkBeenScript0
                // 270050100, 神殿の奥 : 神々の黄昏
                return new Reactor_TimeTemple_PinkBeenScript0();
            }
            case 3001000 -> {
                // 3001000, pFBoss
                // 930000600, エリン森 : 毒の森
                return new Reactor_Ellinia_PFBoss();
            }
            case 3002000 -> {
                // 3002000, pFItem0
                // 930000200, エリン森 : 変質した森
                return new Reactor_Common_DropItems();
            }
            case 3002001 -> {
                // 3002001, pFItem1
                // 930000500, エリン森 : 森の広場
                return new Reactor_Common_DropItems();
            }
            case 6102001 -> {
                // 6102001, glpqitem2
                return new Reactor_Common_DropItems();
            }
            case 6102002 -> {
                // 6102002, glpqreward1
                // 803001300, 古代の英雄への挑戦 : 隠された兵器庫
                return new Reactor_Common_DropItems();
            }
            case 6102003 -> {
                // 6102003, glpqreward2
                // 803001300, 古代の英雄への挑戦 : 隠された兵器庫
                return new Reactor_Common_DropItems();
            }
            case 6102004 -> {
                // 6102004, glpqreward3
                // 803011300, 
                return new Reactor_Common_DropItems();
            }
            case 6102005 -> {
                // 6102005, glpqreward4
                // 803011300, 
                return new Reactor_Common_DropItems();
            }
            case 6109013 -> {
                // 6109013, glpqstrge
                // 803001000, 
                return new Reactor_Zipangu_Glpqstrge();
            }
            case 6802000 -> {
                // 6802000, weddingItem0
                // 680000401, ウェディングタウン : ウェディング披露宴会場
                // 680000600, 
                // 889300500, ウェディングタウン : ウェディング披露宴会場(ハウスウェディング)
                return new Reactor_Amoria_WeddingItem0();
            }
            case 6802001 -> {
                // 6802001, weddingItem1
                // 680000401, ウェディングタウン : ウェディング披露宴会場
                // 680000600, 
                // 889300500, ウェディングタウン : ウェディング披露宴会場(ハウスウェディング)
                return new Reactor_Amoria_WeddingItem0();
            }
            case 8001000 -> {
                // 8001000, shouwaBoss
                // 801040100, ジパング : 悪夢の果て
                return new Reactor_Zipangu_ShouwaBoss();
            }
            case 8009000 -> {
                // 8009000, JPscaScript0
                // 800020110, ジパング : 闇夜の松林
                return new Reactor_Zipangu_JPscaScript();
            }
            case 8009001 -> {
                // 8009001, JPscaScript1
                // 800020120, ジパング : 地図から消えた集落
                return new Reactor_Zipangu_JPscaScript();
            }
            case 8009002 -> {
                // 8009002, JPscaScript2
                // 800020130, ジパング : 大仏との邂逅
                return new Reactor_Zipangu_JPscaScript();
            }
            case 8021001 -> {
                // 8021001, Tok_BossMob2
                // 802000311, ジパング : 公園 2095年
                return new Reactor_Zipangu_Tok_BossMob2();
            }
            case 8091000 -> {
                // 8091000, JPludiMob0
                // 809050000,  : 迷路
                // 809050010,  : 迷路
                return new Reactor_Zipangu_JPludiMob();
            }
            case 8091001 -> {
                // 8091001, JPludiMob1
                // 809050001,  : 迷路
                // 809050009,  : 迷路
                return new Reactor_Zipangu_JPludiMob();
            }
            case 8091002 -> {
                // 8091002, JPludiMob2
                // 809050002,  : 迷路
                // 809050003,  : 迷路
                // 809050008,  : 迷路
                return new Reactor_Zipangu_JPludiMob();
            }
            case 8091003 -> {
                // 8091003, JPludiMob3
                // 809050004,  : 迷路
                // 809050007,  : 迷路
                return new Reactor_Zipangu_JPludiMob();
            }
            case 8091004 -> {
                // 8091004, JPludiMob4
                // 809050005,  : 迷路
                // 809050006,  : 迷路
                return new Reactor_Zipangu_JPludiMob();
            }
            case 8098000 -> {
                // 8098000, (action ãªã)
                // 809050000,  : 迷路
                // 809050001,  : 迷路
                // 809050002,  : 迷路
                // 809050003,  : 迷路
                // 809050004,  : 迷路
                // 809050005,  : 迷路
                // 809050006,  : 迷路
                // 809050007,  : 迷路
                // 809050008,  : 迷路
                // 809050009,  : 迷路
                // 809050010,  : 迷路
                // 809050011,  : 迷路
                // 809050012,  : 迷路
                // 809050013,  : 迷路
                // 809050014,  : 迷路
                return new Reactor_Zipangu_8098000();
            }
            case 8802001 -> {
                // 8802001, mBoxItem0
                return new Reactor_Common_DropItems();
            }
            case 9000000 -> {
                // 9000000, eventMap0
                // 109010100, 隠れ道 : 東フィールド
                return new Reactor_Event_EventMap0();
            }
            case 9000001 -> {
                // 9000001, eventMap1
                // 109010100, 隠れ道 : 東フィールド
                return new Reactor_Event_EventMap1();
            }
            case 9000002 -> {
                // 9000002, eventMap2
                // 109010200, 隠れ道 : 南フィールド
                return new Reactor_Event_EventMap2();
            }
            case 9001000 -> {
                // 9001000, eventMob0
                // 109010100, 隠れ道 : 東フィールド
                // 109010200, 隠れ道 : 南フィールド
                // 109010202, 隠れ道 : 秘密の部屋-南2
                return new Reactor_Event_EventMob0();
            }
            case 9002000 -> {
                // 9002000, eventItem0
                // 109010100, 隠れ道 : 東フィールド
                // 109010101, 隠れ道 : 秘密の部屋-東1
                // 109010102, 隠れ道 : 秘密の部屋-東2
                // 109010103, 隠れ道 : 秘密の部屋-東3
                // 109010104, 隠れ道 : 秘密の部屋-東4
                // 109010105, 隠れ道 : 秘密の部屋-東5
                // 109010106, 隠れ道 : 秘密の部屋-東6
                // 109010107, 隠れ道 : 秘密の部屋-東7
                // 109010108, 隠れ道 : 秘密の部屋-東8
                // 109010109, 隠れ道 : 秘密の部屋-東9
                // 109010110, 隠れ道 : 秘密の部屋-東10
                // 109010200, 隠れ道 : 南フィールド
                // 109010201, 隠れ道 : 秘密の部屋-南1
                // 109010202, 隠れ道 : 秘密の部屋-南2
                // 109010203, 隠れ道 : 秘密の部屋-南3
                // 109010204, 隠れ道 : 秘密の部屋-南4
                // 109010206, 隠れ道 : 秘密の部屋-南6
                return new Reactor_Common_DropItems();
            }
            case 9002001 -> {
                // 9002001, eventItem1
                // 109010100, 隠れ道 : 東フィールド
                // 109010102, 隠れ道 : 秘密の部屋-東2
                // 109010103, 隠れ道 : 秘密の部屋-東3
                // 109010104, 隠れ道 : 秘密の部屋-東4
                // 109010105, 隠れ道 : 秘密の部屋-東5
                // 109010107, 隠れ道 : 秘密の部屋-東7
                // 109010108, 隠れ道 : 秘密の部屋-東8
                // 109010109, 隠れ道 : 秘密の部屋-東9
                // 109010110, 隠れ道 : 秘密の部屋-東10
                // 109010200, 隠れ道 : 南フィールド
                // 109010201, 隠れ道 : 秘密の部屋-南1
                // 109010202, 隠れ道 : 秘密の部屋-南2
                // 109010203, 隠れ道 : 秘密の部屋-南3
                // 109010204, 隠れ道 : 秘密の部屋-南4
                // 109010205, 隠れ道 : 秘密の部屋-南5
                // 109010206, 隠れ道 : 秘密の部屋-南6
                return new Reactor_Common_DropItems();
            }
            case 9002002 -> {
                // 9002002, eventItem2
                // 109010100, 隠れ道 : 東フィールド
                // 109010101, 隠れ道 : 秘密の部屋-東1
                // 109010102, 隠れ道 : 秘密の部屋-東2
                // 109010103, 隠れ道 : 秘密の部屋-東3
                // 109010104, 隠れ道 : 秘密の部屋-東4
                // 109010105, 隠れ道 : 秘密の部屋-東5
                // 109010106, 隠れ道 : 秘密の部屋-東6
                // 109010107, 隠れ道 : 秘密の部屋-東7
                // 109010108, 隠れ道 : 秘密の部屋-東8
                // 109010109, 隠れ道 : 秘密の部屋-東9
                // 109010110, 隠れ道 : 秘密の部屋-東10
                // 109010200, 隠れ道 : 南フィールド
                // 109010201, 隠れ道 : 秘密の部屋-南1
                // 109010202, 隠れ道 : 秘密の部屋-南2
                // 109010203, 隠れ道 : 秘密の部屋-南3
                // 109010204, 隠れ道 : 秘密の部屋-南4
                // 109010205, 隠れ道 : 秘密の部屋-南5
                // 109010206, 隠れ道 : 秘密の部屋-南6
                return new Reactor_Common_DropItems();
            }
            case 9102000 -> {
                // 9102000, sBoxItem0
                // 200090011, 航海中 : 船室&lt;オルビス行き&gt;
                return new Reactor_Common_DropItems();
            }
            case 9102001 -> {
                // 9102001, sBoxItem1
                // 101000003, エリニア : 魔法図書館
                return new Reactor_Common_DropItems();
            }
            case 9102002 -> {
                // 9102002, moonItem0
                // 910010000,  : 月見草の坂
                return new Reactor_Common_DropItems();
            }
            case 9102003 -> {
                // 9102003, moonItem1
                // 910010000,  : 月見草の坂
                return new Reactor_Common_DropItems();
            }
            case 9102004 -> {
                // 9102004, moonItem2
                // 910010000,  : 月見草の坂
                return new Reactor_Common_DropItems();
            }
            case 9102005 -> {
                // 9102005, moonItem3
                // 910010000,  : 月見草の坂
                return new Reactor_Common_DropItems();
            }
            case 9102006 -> {
                // 9102006, moonItem4
                // 910010000,  : 月見草の坂
                return new Reactor_Common_DropItems();
            }
            case 9102007 -> {
                // 9102007, moonItem5
                // 910010000,  : 月見草の坂
                return new Reactor_Common_DropItems();
            }
            case 9108000 -> {
                // 9108000, (action ãªã)
                // 910010000,  : 月見草の坂
                return new Reactor_Henesys_9108000();
            }
            case 9108001 -> {
                // 9108001, (action ãªã)
                // 910010000,  : 月見草の坂
                return new Reactor_Henesys_9108000();
            }
            case 9108002 -> {
                // 9108002, (action ãªã)
                // 910010000,  : 月見草の坂
                return new Reactor_Henesys_9108000();
            }
            case 9108003 -> {
                // 9108003, (action ãªã)
                // 910010000,  : 月見草の坂
                return new Reactor_Henesys_9108000();
            }
            case 9108004 -> {
                // 9108004, (action ãªã)
                // 910010000,  : 月見草の坂
                return new Reactor_Henesys_9108000();
            }
            case 9108005 -> {
                // 9108005, (action ãªã)
                // 910010000,  : 月見草の坂
                return new Reactor_Henesys_9108000();
            }
            case 9201000 -> {
                // 9201000, syarenMob0
                // 990000630,  : 水路の迷路3
                return new Reactor_GuildQuest_SyarenMob0();
            }
            case 9201001 -> {
                // 9201001, syarenNPC0
                // 990000700,  : シャレン3世の墓
                return new Reactor_GuildQuest_SyarenNPC0();
            }
            case 9201002 -> {
                // 9201002, syarenMob1
                // 990000900,  : エレゴスの王子
                return new Reactor_GuildQuest_SyarenMob1();
            }
            case 9202000 -> {
                // 9202000, syarenItem0
                // 990000100,  : 守護の谷
                // 990000200,  : 遺跡の入口
                return new Reactor_Common_DropItems();
            }
            case 9202001 -> {
                // 9202001, syarenItem1
                // 990000410,  : 名誉の部屋
                // 990000431,  : 制約の部屋
                return new Reactor_Common_DropItems();
            }
            case 9202002 -> {
                // 9202002, syarenItem2
                // 990000430,  : 信念の部屋
                return new Reactor_Common_DropItems();
            }
            case 9202003 -> {
                // 9202003, syarenItem3
                // 990000501,  : 中央宴会室
                return new Reactor_Common_DropItems();
            }
            case 9202004 -> {
                // 9202004, syarenItem4
                // 990000502,  : 酒貯蔵庫
                return new Reactor_Common_DropItems();
            }
            case 9202005 -> {
                // 9202005, syarenItem5
                // 990000611,  : 迷路の終りA
                return new Reactor_Common_DropItems();
            }
            case 9202006 -> {
                // 9202006, syarenItem6
                // 990000620,  : 水路の迷路2
                return new Reactor_Common_DropItems();
            }
            case 9202007 -> {
                // 9202007, syarenItem7
                // 990000631,  : 迷路の終りB
                return new Reactor_Common_DropItems();
            }
            case 9202008 -> {
                // 9202008, syarenItem8
                // 990000641,  : 迷路の終りC
                return new Reactor_Common_DropItems();
            }
            case 9202009 -> {
                // 9202009, syarenItem9
                // 990000100,  : 守護の谷
                // 990000200,  : 遺跡の入口
                return new Reactor_Common_DropItems();
            }
            case 9202010 -> {
                // 9202010, syarenItem10
                // 990000501,  : 中央宴会室
                return new Reactor_Common_DropItems();
            }
            case 9202011 -> {
                // 9202011, syarenItem11
                // 990000502,  : 酒貯蔵庫
                return new Reactor_Common_DropItems();
            }
            case 9202012 -> {
                // 9202012, syarenItem12
                // 990001000,  : シャレン3世の部屋
                // 990001101,  : ギルド協会キャンプ
                return new Reactor_GuildQuest_SyarenItem12();
            }
            case 9208000 -> {
                // 9208000, (action ãªã)
                // 910210000,  : シャレニアン城門
                // 990000300,  : シャレニアン城門
                return new Reactor_GuildQuest_9208000();
            }
            case 9208001 -> {
                // 9208001, (action ãªã)
                // 910210000,  : シャレニアン城門
                // 990000300,  : シャレニアン城門
                return new Reactor_GuildQuest_9208000();
            }
            case 9208002 -> {
                // 9208002, (action ãªã)
                // 990000300,  : シャレニアン城門
                return new Reactor_GuildQuest_9208000();
            }
            case 9208004 -> {
                // 9208004, (action ãªã)
                // 990000400,  : 騎士のホール
                return new Reactor_GuildQuest_9208000();
            }
            case 9208007 -> {
                // 9208007, (action ãªã)
                // 990000440,  : 正義の部屋
                return new Reactor_GuildQuest_9208000();
            }
            case 9980000 -> {
                // 9980000, mcGuardian0
                return new Reactor_MonsterCarnival_McGuardian0();
            }
            case 9980001 -> {
                // 9980001, mcGuardian0
                return new Reactor_MonsterCarnival_McGuardian0();
            }
            default -> {
                return null;
            }
        }
    }
}
