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
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : party3_enter (JMS131-302), 会話文は lua_script_jp (party3_enter) を利用
// lua の party3_enter は処理全体がコメントアウトされている (jkによりブロック 20080730) ため、コメントアウトされた処理を利用
public class Npc_Orbis_WonkyTheFairy extends NpcScriptMan {

    private static final int MENU_ENTER = 0;
    private static final int MENU_TOWER = 1;
    private static final int MENU_FOOD = 2;
    private static final int MENU_FEATHER = 3;
    private final String field_set_name = "Party5";
    private final int map_strange_tower = 200080101; // オルビス塔 : 見知らぬ塔
    private final int map_entrance = 920010000; // 女神の塔 : 入口
    private final int map_exit = 920011200; // 女神の塔 : 出口
    private final int party_member = 6;
    private final int level_min = 51;
    private final int level_max = 70;
    private final int item_takeaway_first = 4001044; // 回収するアイテム (4001044 - 4001063)
    private final int item_takeaway_last = 4001063;
    private final int item_takeaway_extra = 4001074; // 透明アイテム
    private int last_status = 0;
    private boolean leaving = false;

    @Override
    public boolean action(int status, int answer) {
        if (hasPages()) {
            return nextPage();
        }
        // 入口
        if (getMapId() == this.map_entrance) {
            if (status == 0) {
                if (!isPartyBoss()) {
                    // 翻訳
                    say("よく考えて、決めたら話しかけてね。");
                    return false;
                }
                // TODO : lua の clearmission (ウィンキのお願い) は未実装
                this.last_status = status;
                // 翻訳
                askYesNo("パーティークエストを途中でやめて、ここから出たいの？出たら、また最初からやり直しだよ…");
                return true;
            }
            if (this.leaving) {
                warp(this.map_exit, "st00");
                return false;
            }
            if (status < this.last_status) {
                // 翻訳
                say("よく考えて、決めたら話しかけてね。");
                return false;
            }
            this.leaving = true;
            // 翻訳
            say("もう一度試してね～", false, true);
            return true;
        }
        if (getMapId() != this.map_strange_tower) {
            return false;
        }
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("お～、会えて嬉しいよ！女神の塔を冒険したいの？グループに戦士、魔法使い、盗賊、弓使いが各1名以上いるとウィンキの祝福をかけてあげるわ。");
                nt.addMenu(MENU_ENTER, "入場を申し込む。");
                nt.addMenu(MENU_TOWER, "女神の塔について聞く。");
                nt.addMenu(MENU_FOOD, "ウィンキに食べ物を渡す。");
                nt.addMenu(MENU_FEATHER, "女神の羽について聞く。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case MENU_ENTER -> {
                        if (!isPartyBoss()) {
                            // 翻訳
                            say("ねえ、パーティーリーダーに話しかけてほしいんだ。ほかの人じゃだめだよ。");
                            return false;
                        }
                        int res;
                        // lua では FieldSet の入場条件で判定しているが、FieldSet のデータに条件がないため NPC で判定する
                        if (TacosFieldSet.getPartyMembers(getPlayer()).size() != this.party_member) {
                            res = TacosFieldSet.ENTER_PARTY_MEMBER;
                        } else {
                            res = TacosFieldSet.ENTER_SUCCESS;
                            for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
                                if (member.getLevel() < this.level_min || this.level_max < member.getLevel()) {
                                    res = TacosFieldSet.ENTER_PARTY_LEVEL;
                                    break;
                                }
                            }
                            if (res == TacosFieldSet.ENTER_SUCCESS) {
                                res = enterFieldSetParty(this.field_set_name, 0);
                            }
                        }
                        // 翻訳
                        switch (res) {
                            case TacosFieldSet.ENTER_SUCCESS -> {
                                // TODO : lua の premission (ウィンキのお願い) と givePartyBuff (妖精ウィンキの祝福 2022090 - 2022093) は未実装
                                TacosFieldSet field_set = getFieldSet(this.field_set_name);
                                if (field_set != null) {
                                    // 宝箱のリアクター
                                    // TODO : lua の setReactorState の第4引数 (0) の用途は不明
                                    int rand_a1 = random(1, 10);
                                    int rand_a2 = random(1, 10);
                                    int rand_a3 = random(1, 10);
                                    if (rand_a1 == 1 || rand_a1 == 2) {
                                        field_set.setReactorState(16, "party3_box01", 1);
                                    }
                                    if (rand_a2 == 3 || rand_a2 == 4) {
                                        field_set.setReactorState(19, "party3_box02", 1);
                                    }
                                    if (rand_a3 == 5 || rand_a3 == 6) {
                                        field_set.setReactorState(22, "party3_box03", 1);
                                    }
                                }
                                // lua の party3_takeawayitem
                                for (int item_id = this.item_takeaway_first; item_id <= this.item_takeaway_last; item_id++) {
                                    removeItemAll(item_id);
                                }
                                removeItemAll(this.item_takeaway_extra);
                            }
                            case TacosFieldSet.ENTER_NO_PARTY -> {
                                say("パーティーに入っていないね。これをするにはパーティーに入っていないとだめだよ！");
                            }
                            case TacosFieldSet.ENTER_PARTY_MEMBER -> {
                                say("パーティーが6人じゃないよ！6人必要なんだ！！");
                            }
                            case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                                say("パーティーの中にレベル51～70ではない人がいるよ。パーティーをレベル範囲内にしてね！");
                            }
                            case TacosFieldSet.ENTER_USING -> {
                                say("すでに別のパーティーがクエストのクリアを目指して入っているよ。しばらくしてからもう一度試してね。");
                            }
                            default -> {
                                say("理由があって入れてあげられないんだ。しばらくしてからもう一度試してね。");
                            }
                        }
                        return false;
                    }
                    case MENU_TOWER -> {
                        // 翻訳
                        return sayPages(
                                "数日前、大きな嵐の後で、オルビスの塔の上にある#b女神ミネルバの像#kの後ろに、雲に覆われた新しい道が現れたんだ。雲が晴れると、謎めいた新しい塔が姿を現したんだよ。",
                                "僕たちが探している塔は、オルビスを陰で支えている存在、#b女神ミネルバ#kの塔だという結論に達したんだ。#b女神ミネルバ#kは塔のどこかに閉じ込められているに違いないよ。どうかな？塔を探検して、女神の痕跡を見つけてくれない？"
                        );
                    }
                    default -> {
                        // TODO : ウィンキに食べ物を渡す (lua の feellike), 女神の羽について聞く
                        return false;
                    }
                }
            }
            default -> {
            }
        }
        return false;
    }
}
