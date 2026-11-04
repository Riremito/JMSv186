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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : s4blocking (JMS131-302), 会話文は lua_script_jp (s4blocking) を利用
public class NpcMooseTraining extends NpcScriptMan {

    private static final int MAP_FOREST = 240010400; // リプレ : 森の分かれ道
    private static final int MAP_ROAD = 924000000; // 修練場への道
    private static final int MAP_TRAINING = 924000001; // ムスの修練場
    private static final int MAP_EXIT = 924000002; // 修練場出口
    private final int quest_power_of_shield = 6180; // 盾の力
    private final int item_shield = 1092041; // スキル習得用盾

    @Override
    public boolean action(int status, int answer) {
        switch (getMapId()) {
            case MAP_FOREST -> {
                if (getPlayer().getQuestStatus(this.quest_power_of_shield) != 1) {
                    return false;
                }
                if (status == 0) {
                    // 翻訳
                    say("よし。盾の修行場へ送ってやろう。もう一度話しかけてくれ。", false, true);
                    return true;
                }
                warp(MAP_ROAD);
                return false;
            }
            case MAP_ROAD -> {
                switch (status) {
                    case 0 -> {
                        // 翻訳
                        say("修行場へ送る前に、一つ伝えておかなければならないことがある。盾の修行場では、私が渡した#b#t" + this.item_shield + "##kを装備していなければならない。そうでなければ命はないぞ。", false, true);
                        return true;
                    }
                    case 1 -> {
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("向こうに着く前に#r盾を装備する#kことを忘れるな！\r\n#b#L0# #t" + this.item_shield + "#がほしい。#l\r\n#L1# #m" + MAP_TRAINING + "#に入れて。#l\r\n#L2# 外に出して。#l", false);
                        askMenu(nt);
                        return true;
                    }
                    case 2 -> {
                        switch (answer) {
                            case 0 -> {
                                if (0 < getPlayer().getItemQuantity(this.item_shield, false)) {
                                    // 翻訳
                                    say("すでに#t" + this.item_shield + "##kを持っているな。これ以上は必要ない。");
                                    return false;
                                }
                                // TODO : 装備中の盾の削除 (動作未確認)
                                if (MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.EQUIPPED, this.item_shield, 1, true, false)) {
                                    // 翻訳
                                    getPlayer().dropMessage(OpsBroadcastMsg.BM_EVENT.get(), "スキル習得用の盾は削除されました。");
                                }
                                if (!exchange(0, this.item_shield, 1)) {
                                    // 翻訳
                                    say("装備ウィンドウに空きがないので、#t" + this.item_shield + "##kを渡せなかった。空きを作ってからもう一度試してくれ。");
                                    return false;
                                }
                                // 翻訳
                                say("#t" + this.item_shield + "#を渡した。インベントリを確認してくれ。必ず装備しておくんだぞ！");
                                return false;
                            }
                            case 1 -> {
                                if (enterFieldSet("S4blocking", 0) != TacosFieldSet.ENTER_SUCCESS) {
                                    // 翻訳
                                    say("ほかのキャラクターが依頼を受けています。入場できません。");
                                }
                                return false;
                            }
                            case 2 -> {
                                warp(MAP_EXIT);
                                return false;
                            }
                            default -> {
                            }
                        }
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case MAP_TRAINING -> {
                if (status == 0) {
                    // 翻訳
                    askYesNo("ここから出たいか？");
                    return true;
                }
                if (status == 1) {
                    warp(MAP_EXIT);
                }
                return false;
            }
            case MAP_EXIT -> {
                if (status == 0) {
                    // 翻訳
                    say("外に出してやろう。#t" + this.item_shield + "#は返してもらうぞ。", false, true);
                    return true;
                }
                // TODO : 装備中の盾の削除 (動作未確認)
                if (MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.EQUIPPED, this.item_shield, 1, true, false)) {
                    // 翻訳
                    getPlayer().dropMessage(OpsBroadcastMsg.BM_EVENT.get(), "スキル習得用の盾は削除されました。");
                }
                if (0 < getPlayer().getItemQuantity(this.item_shield, false)) {
                    exchange(0, this.item_shield, -1);
                }
                warp(MAP_FOREST, "st00");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
